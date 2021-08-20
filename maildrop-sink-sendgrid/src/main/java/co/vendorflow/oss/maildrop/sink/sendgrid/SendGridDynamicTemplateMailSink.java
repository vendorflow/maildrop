/*
 * Copyright 2021 Vendorflow, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.vendorflow.oss.maildrop.sink.sendgrid;

import static co.vendorflow.oss.maildrop.api.MailStatus.Lifecycle.SENT;
import static co.vendorflow.oss.maildrop.api.Recipient.Destination.BCC;
import static co.vendorflow.oss.maildrop.api.Recipient.Destination.CC;
import static co.vendorflow.oss.maildrop.api.Recipient.Destination.TO;
import static com.sendgrid.Method.POST;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGridAPI;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import co.vendorflow.oss.maildrop.api.MailMessage;
import co.vendorflow.oss.maildrop.api.MailStatus;
import co.vendorflow.oss.maildrop.api.Recipient;
import co.vendorflow.oss.maildrop.api.sink.MailSink;
import co.vendorflow.oss.maildrop.api.sink.MaildropDeliveryException;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SendGridDynamicTemplateMailSink implements MailSink {

    private static final String SEND_ENDPOINT = "mail/send";

    private static final Predicate<String> TEMPLATE_ID = Pattern.compile("d-\\p{XDigit}{32}").asMatchPredicate();

    private static final String HEADER_X_MESSAGE_ID = "X-Message-ID";

    private final SendGridAPI sg;

    private final SendGridTemplateResolver templates;

    @Override
    public MailStatus deliver(MailMessage message) throws MaildropDeliveryException {
        var body = maildropToSg(message);
        String bodyJson;
        try {
            bodyJson = body.build();
        } catch (IOException e) {
            throw new MaildropDeliveryException("could not serialize message", e);
        }

        var req = new Request();
        req.setMethod(POST);
        req.setEndpoint(SEND_ENDPOINT);
        req.setBody(bodyJson);

        Response resp;
        try {
            resp = sg.api(req);
        } catch (Exception e) {
            throw new MaildropDeliveryException("exception performing SendGrid API call", e);
        }

        if (resp.getStatusCode() >= 400) {
            throw new MaildropDeliveryException("SendGrid returned status " + resp.getStatusCode() + ": " + resp.getBody());
        }

        Map<String, Object> statusMetadata = new HashMap<>();

        findXMessageId(resp.getHeaders()).ifPresentOrElse(
                xmid -> statusMetadata.put(HEADER_X_MESSAGE_ID, xmid),
                () -> log.warn("no header " + HEADER_X_MESSAGE_ID + " found")
        );


        return new MailStatus(SENT, statusMetadata);
    }


    Mail maildropToSg(MailMessage message) {
        String templateId = Optional.ofNullable(templates.resolveTemplateId(message))
                .orElseThrow(() -> new TemplateNotResolvedException(message.getTemplateName()));

        if (!TEMPLATE_ID.test(templateId)) {
            log.warn("'{}' does not look like a valid template ID, but trying anyway", templateId);
        } else {
            log.info("sending message with template {}", templateId);
        }

        var m = new Mail();

        m.setFrom(iaToSg(message.getFrom()));
        Optional.ofNullable(message.getReplyTo()).map(SendGridDynamicTemplateMailSink::iaToSg).ifPresent(m::setReplyTo);
        m.setTemplateId(templateId);

        if (!message.getHandlingOptions().isEmpty()) {
            log.warn("don't know how to process handling options {} for {}", message.getHandlingOptions().keySet(), message.getTemplateName());
        }

        m.addPersonalization(toPersonalization(message));

        return m;
    }


    static Request mailToRequest(Mail mail) {
        var req = new Request();

        return req;
    }


    private static final Map<Recipient.Destination, BiConsumer<Personalization, Email>> ADDERS = Map.of(
            TO, Personalization::addTo,
            CC, Personalization::addCc,
            BCC, Personalization::addBcc
    );


    static Personalization toPersonalization(MailMessage message) {
        var p = new Personalization();
        message.getSubstitutions().forEach(p::addDynamicTemplateData);
        message.getMetadata().forEach((k, v) -> p.addDynamicTemplateData(k, v.toString()));


        message.getRecipients().forEach(r -> {
            log.debug("appending {} to Personalization", r.getAddress());

            var adder = ADDERS.get(r.getDestination());
            if (adder == null) {
                throw new MaildropDeliveryException("don't know how to handle unknown destination " + r.getDestination() + " for " + r.getAddress());
            }

            var e = iaToSg(r.getAddress());
            adder.accept(p, e);

            if (!r.getHandlingOptions().isEmpty()) {
                log.warn("don't know how to process handling options {} for {}", r.getHandlingOptions().keySet(), r.getAddress());
            }

            if (!r.getMetadata().isEmpty()) {
                log.warn("don't know how to process metadata options {} for {}", r.getMetadata().keySet(), r.getAddress());
            }
        });

        return p;
    }


    static Email iaToSg(InternetAddress ia) {
        return new Email(ia.getAddress(), ia.getPersonal());
    }


    static Optional<String> findXMessageId(Map<String, String> headers) {
        return headers.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(HEADER_X_MESSAGE_ID))
                .map(Map.Entry::getValue)
                .findFirst();
    }
}

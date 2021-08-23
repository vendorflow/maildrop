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

package co.vendorflow.oss.maildrop.impl;

import static co.vendorflow.oss.maildrop.api.Recipient.cc;
import static co.vendorflow.oss.maildrop.api.Recipient.to;
import static java.time.Instant.now;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextDouble;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import co.vendorflow.oss.maildrop.api.MailMessage;
import co.vendorflow.oss.maildrop.api.Recipient;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

/**
 * @author christopher
 *
 */
public class MaildropTestUtils {

    static final BiPredicate<MailMessage, MailMessage> MESSAGE_EQUALS_WITHOUT_RECIPIENT_OR_METADATA = (a, b) ->
            Objects.equals(a.getTemplateName(), b.getTemplateName())
                    && Objects.equals(a.getFrom(), b.getFrom())
                    && Objects.equals(a.getReplyTo(), b.getReplyTo())
                    && Objects.equals(a.getSubstitutions(), b.getSubstitutions())
                    && Objects.equals(a.getHandlingOptions(), b.getHandlingOptions())
                    ;


    static final BiPredicate<MailMessage, MailMessage> MESSAGE_EQUALS_WITHOUT_RECIPIENT = (a, b) -> {
            return MESSAGE_EQUALS_WITHOUT_RECIPIENT_OR_METADATA.test(a, b)
                    && Objects.equals(a.getMetadata(), b.getMetadata())
                    ;
    };


    static MailMessage buildComplexMessage() {
        try {
            String template = randomAlphanumeric(10);
            InternetAddress from = new InternetAddress(String.format("%s %s <from_%s@example.test>", randomAlphabetic(5), randomAlphabetic(6), randomAlphabetic(7)));
            InternetAddress replyTo = new InternetAddress(String.format("%s %s <replyto_%s@example.test>", randomAlphabetic(5), randomAlphabetic(6), randomAlphabetic(7)));
            InternetAddress to = new InternetAddress(String.format("%s %s <to_%s@example.test>", randomAlphabetic(5), randomAlphabetic(6), randomAlphabetic(7)));
            InternetAddress cc = new InternetAddress(String.format("%s %s <cc_%s@example.test>", randomAlphabetic(5), randomAlphabetic(6), randomAlphabetic(7)));

            Instant deliverAt = now();

            String strShared = randomAlphabetic(8);
            Double numShared = nextDouble(0.0, 1.0);

            List<String> metaVals = List.of(randomAlphanumeric(6), randomAlphanumeric(3));

            return new MailMessage() {
                @Override
                public @NotEmpty String getTemplateName() {
                    return template;
                }

                @Override
                public @Email @NotNull InternetAddress getFrom() {
                    return from;
                }

                @Override
                public @Email InternetAddress getReplyTo() {
                    return replyTo;
                }


                @Override
                public @NotNull Map<String, Object> getSubstitutions() {
                    return Map.of("strShared", strShared, "numShared", numShared);
                }


                @Override
                public @NotNull Map<String, Object> getHandlingOptions() {
                    return Map.of("deliveryTime", deliverAt, "deliveryEpoch", deliverAt.getEpochSecond());
                }


                @Override
                public @NotNull Map<String, Object> getMetadata() {
                    return Map.of("array", metaVals);
                }


                @Override
                public @NotEmpty Collection<Recipient> getRecipients() {
                    return List.of(
                            to(to, Map.of("kind", "to")),
                            cc(cc, Map.of("kind", "cc"))
                    );
                }

            };
        } catch (AddressException e) {
            throw new RuntimeException("can't happen", e);
        }
    }
}

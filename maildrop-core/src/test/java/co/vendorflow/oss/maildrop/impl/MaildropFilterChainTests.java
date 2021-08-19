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

import static co.vendorflow.oss.maildrop.api.Recipient.Destination.TO;
import static co.vendorflow.oss.maildrop.impl.MaildropTestUtils.MESSAGE_EQUALS_WITHOUT_RECIPIENT_OR_METADATA;
import static co.vendorflow.oss.maildrop.impl.TestAddress.ALICE;
import static co.vendorflow.oss.maildrop.impl.TestAddress.BOB;
import static co.vendorflow.oss.maildrop.impl.TestAddress.CUSTOMER_SERVICE;
import static co.vendorflow.oss.maildrop.impl.TestAddress.RENEE;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;

import co.vendorflow.oss.maildrop.api.MailMessage;
import co.vendorflow.oss.maildrop.api.Recipient;
import co.vendorflow.oss.maildrop.api.Recipient.Destination;
import co.vendorflow.oss.maildrop.api.filter.MaildropFilter;
import co.vendorflow.oss.maildrop.api.filter.MaildropFilterException;
import jakarta.mail.internet.InternetAddress;

public class MaildropFilterChainTests {

    @Test
    public void handlesEmptyChain() {
        var chain = new MaildropFilterChain(emptySet());

        var original = new MutableMailMessage();

        var output = chain.filter(original);

        assertThat(output).hasSize(1)
            .element(0).isSameAs(original);
    }


    @Test
    public void appliesInOrder() {
        var chain = new MaildropFilterChain(List.of(
                new CcBobFilter(),
                new SplitToSeparateMessagesFilter(),
                new MetaizeToAddressFilter()
        ));

        var original = new TestMessage();

        var output = chain.filter(original);
        assertThat(output).hasSize(2)
                .extracting(MailMessage::getRecipients).allMatch(r -> r.size() == 1);

        var reneeMsg = findByRecipient(output, RENEE.getAddress());
        assertThat(reneeMsg)
                .usingRecursiveComparison().withEqualsForType(MESSAGE_EQUALS_WITHOUT_RECIPIENT_OR_METADATA, MailMessage.class)
                .isEqualTo(original);
        assertThat(reneeMsg.getRecipients())
                .usingRecursiveComparison()
                .isEqualTo(original.getRecipients());
        assertThat(reneeMsg.getMetadata())
                .isEqualTo(Map.of("sharedMeta", "cool", "addressLength", 20));

        var bobMsg = findByRecipient(output, BOB);
        assertThat(bobMsg)
                .usingRecursiveComparison().withEqualsForType(MESSAGE_EQUALS_WITHOUT_RECIPIENT_OR_METADATA, MailMessage.class)
                .isEqualTo(original);
        assertThat(bobMsg.getMetadata())
                .isEqualTo(Map.of("sharedMeta", "cool", "addressLength", 15));
        assertThat(bobMsg.getRecipients())
                .hasSize(1)
                .element(0)
                        .matches(bob -> bob.getAddress().equals(BOB))
                        .matches(bob -> bob.getDestination() == TO);
    }


    static MailMessage findByRecipient(Collection<MailMessage> messages, InternetAddress address) {
        return messages.stream()
                .filter(m -> m.getRecipients().stream().anyMatch(r -> r.getAddress().equals(address)))
                .findFirst()
                .orElseThrow();
    }



    static class CcBobFilter extends MessageMutatingFilter {
        @Override
        protected void mutate(MutableMailMessage mutable) {
            if (mutable.getRecipients().stream().noneMatch(r -> r.getAddress().equals(BOB))) {
                mutable.getRecipients().add(Recipient.cc(BOB, emptyMap(), emptyMap()));
            }
        }
    }


    static class SplitToSeparateMessagesFilter implements MaildropFilter {
        @Override
        public List<MailMessage> filter(MailMessage original) throws MaildropFilterException {
            if (original.getRecipients().size() == 1) {
                return List.of(original);
            }

            return original.getRecipients().stream()
                    .map(r -> {
                        var to = r.copy();
                        to.setDestination(Destination.TO);
                        var copy = MutableMailMessage.copyOf(original);
                        copy.setRecipients(List.of(to));
                        return copy;
                    })
                    .collect(toList());
        }
    }


    static class MetaizeToAddressFilter extends MessageMutatingFilter {
        @Override
        protected void mutate(MutableMailMessage mutable) {
            Integer addressLength = mutable.getRecipients().get(0).getAddress().getAddress().length();
            mutable.getMetadata().put("addressLength", addressLength);
        }
    }


    public static class TestMessage implements MailMessage {
        @Override
        public @NotEmpty String getTemplateName() {
            return "mfct";
        }

        @Override
        public @Email @NotNull InternetAddress getFrom() {
            return ALICE;
        }


        @Override
        public @Email InternetAddress getReplyTo() {
            return CUSTOMER_SERVICE;
        }

        @Override
        public @NotEmpty Collection<Recipient> getRecipients() {
            return List.of(RENEE);
        }

        @Override
        public @NotNull Map<String, Object> getSubstitutions() {
            return Map.of("orderNumber", 1234);
        }

        @Override
        public @NotNull Map<String, Object> getMetadata() {
            return Map.of("sharedMeta", "cool");
        }

        @Override
        public @NotNull Map<String, Object> getHandlingOptions() {
            return Map.of("sendLater", false);
        }
    }
}

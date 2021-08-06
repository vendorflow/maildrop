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

import static co.vendorflow.oss.maildrop.impl.MaildropTestUtils.MESSAGE_EQUALS_WITHOUT_RECIPIENT;
import static co.vendorflow.oss.maildrop.impl.TestAddress.ALICE;
import static co.vendorflow.oss.maildrop.impl.TestAddress.RENEE;
import static co.vendorflow.oss.maildrop.impl.TestAddress.CUSTOMER_SERVICE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;

import co.vendorflow.oss.maildrop.api.MailMessage;
import co.vendorflow.oss.maildrop.api.Recipient;
import jakarta.mail.internet.InternetAddress;

public class MessageMutatingFilterTests {

    private static final MessageMutatingFilter TEST_FILTER = new MessageMutatingFilter() {
        @Override
        protected void mutate(MutableMailMessage mutable) {
        }
    };


    @Test
    public void mutableIsPassedThrough() {
        var original = new MutableMailMessage();

        var output = TEST_FILTER.filter(original);

        assertThat(output).hasSize(1);
        assertThat(output.get(0)).isSameAs(original);
    }



    @Test
    public void otherTypeIsCopied() {
        var original = new MailMessage() {
            @Override
            public @NotEmpty String getTemplateName() {
                return "tempName";
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
                return Set.of(RENEE);
            }

            @Override
            public @NotNull Map<String, Object> getSubstitutions() {
                return Map.of("subA", "valueA");
            }

            @Override
            public @NotNull Map<String, Object> getHandlingOptions() {
                return Map.of("handleA", 12345);
            }

            @Override
            public @NotNull Map<String, Object> getMetadata() {
                return Map.of("metaA", "atem");
            }
        };

        var output = TEST_FILTER.filter(original);

        assertThat(output).hasSize(1);
        assertThat(output.get(0))
                .isInstanceOf(MutableMailMessage.class)
                .usingRecursiveComparison().withEqualsForType(MESSAGE_EQUALS_WITHOUT_RECIPIENT, MailMessage.class).isEqualTo(original);
        assertThat(output.get(0).getRecipients())
                .hasSize(1)
                .element(0).usingRecursiveComparison().isEqualTo(RENEE);
    }
}

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

import java.util.Objects;
import java.util.function.BiPredicate;

import co.vendorflow.oss.maildrop.api.MailMessage;

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
}

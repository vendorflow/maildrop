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

package co.vendorflow.oss.maildrop.api;

import static java.util.Collections.emptyMap;

import java.util.Collection;
import java.util.Map;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import jakarta.mail.internet.InternetAddress;

/**
 * The core model of the Maildrop API, representing a message to be sent.
 *
 * @author Christopher Smith
 */
public interface MailMessage {
    /**
     * What template to use to build the body of this message.
     */
    @NotEmpty
    String getTemplateName();


    /**
     * The From address for this message.
     */
    @Email
    @NotNull
    InternetAddress getFrom();


    /**
     * A Reply-To address for this message, if different from the From address,
     * or {@code null} if there is no Reply-To address.
     */
    @Email
    InternetAddress getReplyTo();


    /**
     * Values to be substituted into the template for all recipients.
     * This map may be unmodifiable.
     */
    @NotNull
    default Map<String, Object> getSubstitutions() {
        return emptyMap();
    }


    /**
     * Instructions for the handling of this message, such as scheduled sending.
     * This map may be unmodifiable.
     */
    @NotNull
    default Map<String, Object> getHandlingOptions() {
        return emptyMap();
    }


    /**
     * Values to be attached to the message for accounting purposes
     * but not substituted into the template. Examples include A/B groups,
     * correlation IDs, and origination information.
     */
    @NotNull
    default Map<String, Object> getMetadata() {
        return emptyMap();
    }


    @NotEmpty
    Collection<Recipient> getRecipients();
}

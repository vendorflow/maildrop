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

package co.vendorflow.oss.maildrop.api.filter;

import java.util.Collection;

import co.vendorflow.oss.maildrop.api.MailMessage;

/**
 * Processes a message before it is sent.
 *
 * @author Christopher Smith
 */
@FunctionalInterface
public interface MaildropFilter {
    /**
     * Process the message, such as by inserting extra metadata. This method returns
     * {@link Collection} to support distributed business rules that might either
     * prevent messages from being sent or split messages out (e.g., by replacing
     * {@code cc} recipients with separate messages).
     *
     * @param message the message to be processed
     * @return the processed messages, which might be of a different instance or even type
     * @throws MaildropFilterException if an error is encountered while filtering the message
     */
    Collection<MailMessage> filter(MailMessage message) throws MaildropFilterException;
}

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

/**
 * Delivery implementation, responsible for handling the mail after any
 * processing that happens in the application. This sink may not be the
 * final delivery mechanism; for example, it might put the message into
 * message queue for SMTP transmission asynchronously.
 *
 * @author Christopher Smith
 */
public interface MailSink {
    MailStatus deliver(MailMessage message) throws MaildropException;
}

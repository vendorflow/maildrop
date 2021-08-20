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

package co.vendorflow.oss.maildrop.api.sink;

import co.vendorflow.oss.maildrop.api.MaildropException;

/**
 * Represents some problem identified while delivering a message.
 *
 * @author Christopher Smith
 */
public class MaildropDeliveryException extends MaildropException {
    public MaildropDeliveryException(String message) {
        super(message);
    }

    public MaildropDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}

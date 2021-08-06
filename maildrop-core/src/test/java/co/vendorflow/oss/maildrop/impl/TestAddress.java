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

import static java.util.Collections.emptyMap;

import java.io.UnsupportedEncodingException;

import co.vendorflow.oss.maildrop.api.Recipient;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

class TestAddress {
    static final InternetAddress ALICE, BOB, CUSTOMER_SERVICE;

    static final Recipient RENEE;

    static {
        try {
            ALICE = new InternetAddress("alice@sender.test", "Alice Adams");
            BOB = new InternetAddress("bob@sender.test", "Bob Brown");
            CUSTOMER_SERVICE = new InternetAddress("inbox@sender.test", "Customer Service");

            RENEE = Recipient.to(new InternetAddress("renee@recipient.test"), emptyMap(), emptyMap());
        } catch (UnsupportedEncodingException | AddressException e) {
            throw new Error(e); // because, seriously
        }
    }
}

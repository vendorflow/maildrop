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

package co.vendorflow.oss.maildrop.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

import jakarta.mail.internet.InternetAddress;

/**
 * @author Christopher Smith
 */
public class JakartaInternetAddressModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public JakartaInternetAddressModule() {
        // FIXME by replacing with the Jackson module
        super(new Version(0, 0, 1, "snap"));

        addSerializer(new JakartaInternetAddressSerializer());
        addDeserializer(InternetAddress.class, new JakartaInternetAddressDeserializer());
    }
}

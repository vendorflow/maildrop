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

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.vendorflow.oss.maildrop.jackson.JakartaInternetAddressModule;

public class MailMessageSerializationTests {


    private static final ObjectMapper JACKSON = new ObjectMapper()
            .findAndRegisterModules()
            .registerModule(new JakartaInternetAddressModule())
            .enable(INDENT_OUTPUT)
            .disable(WRITE_DATES_AS_TIMESTAMPS);

    @Test
    public void canSerialize() throws JsonProcessingException {
        var original = MaildropTestUtils.buildComplexMessage();

        var json = JACKSON.writeValueAsString(original);

        var deserialized = JACKSON.readValue(json, MutableMailMessage.class);

        assertThat(Instant.parse((String) deserialized.getHandlingOptions().get("deliveryTime")).getEpochSecond())
            .isEqualTo(((Number) deserialized.getHandlingOptions().get("deliveryEpoch")).longValue());
    }
}

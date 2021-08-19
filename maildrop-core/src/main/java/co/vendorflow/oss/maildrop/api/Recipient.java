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

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import jakarta.mail.internet.InternetAddress;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Christopher Smith
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Recipient {

    /**
     * The Destination type, as defined in RFC 2822.
     */
    @NotNull
    private Destination destination;

    /**
     * The delivery address for this particular recipient.
     */
    @Email
    @NotNull
    private InternetAddress address;

    /**
     * Values to be substituted into the template that are specific to this recipient.
     * These values will be merged with any substitutions listed at the message level,
     * with recipient-specific substitutions taking precedence.
     */
    @NotNull
    private Map<String, Object> substitutions = new HashMap<>();


    /**
     * Instructions for the handling of the message to this specific recipient,
     * such as scheduled sending. These values will be merged with any options
     * listed at the message level, with recipient-specific substitutions taking precedence.
     */
    @NotNull
    private Map<String, Object> handlingOptions = new HashMap<>();


    /**
     * Values to be attached to the message for accounting purposes
     * but not substituted into the template. These values will be merged with
     * any metadata listed at the message level, with recipient-specific metadata
     * taking precedence.
     */
    @NotNull
    private Map<String, Object> metadata = new HashMap<>();


    public Recipient copy() {
        return build(destination, address, substitutions, metadata, handlingOptions);
    }


    public enum Destination {
        TO,
        CC,
        BCC
    }


    public static Recipient to(InternetAddress address, Map<String, ?> substitutions, Map<String, ?> metadata) {
        return build(Destination.TO, address, substitutions, metadata, emptyMap());
    }

    public static Recipient cc(InternetAddress address, Map<String, ?> substitutions, Map<String, ?> metadata) {
        return build(Destination.CC, address, substitutions, metadata, emptyMap());
    }

    public static Recipient bcc(InternetAddress address, Map<String, ?> substitutions, Map<String, ?> metadata) {
        return build(Destination.BCC, address, substitutions, metadata, emptyMap());
    }


    private static Recipient build(
            Destination destination,
            InternetAddress address,
            Map<String, ?> substitutions,
            Map<String, ?> metadata,
            Map<String, ?> handlingOptions
    ) {
        var r = new Recipient();
        r.setDestination(destination);
        r.setAddress(address);
        r.setSubstitutions(new HashMap<>(substitutions));
        r.setMetadata(new HashMap<>(metadata));
        r.setHandlingOptions(new HashMap<>(handlingOptions));
        return r;
    }
}

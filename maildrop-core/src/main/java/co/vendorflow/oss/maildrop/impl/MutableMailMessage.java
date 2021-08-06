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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.vendorflow.oss.maildrop.api.MailMessage;
import co.vendorflow.oss.maildrop.api.Recipient;
import jakarta.mail.internet.InternetAddress;
import lombok.Data;

/**
 * An implementation of {@link MailMessage} where all of the properties are guaranteed
 * to be safely mutable. Used for filtering, where filters may modify a single object,
 * and as a generic deserialization target.
 *
 * @author Christopher Smith
 */
@Data
public final class MutableMailMessage implements MailMessage {
    String templateName;

    InternetAddress from;

    InternetAddress replyTo;


    final Map<String, Object> substitutions = new HashMap<>();

    public void setSubstitutions(Map<String, ?> value) {
        this.substitutions.clear();
        this.substitutions.putAll(value);
    }


    final Map<String, Object> metadata = new HashMap<>();

    public void setMetadata(Map<String, ?> value) {
        this.metadata.clear();
        this.metadata.putAll(value);
    }


    final Map<String, Object> handlingOptions = new HashMap<>();

    public void setHandlingOptions(Map<String, ?> value) {
        this.handlingOptions.clear();
        this.handlingOptions.putAll(value);
    }


    final List<Recipient> recipients = new ArrayList<>();

    public void setRecipients(Collection<? extends Recipient> value) {
        this.recipients.clear();
        this.recipients.addAll(value);
    }


    public static MutableMailMessage copyOf(MailMessage original) {
        var n = new MutableMailMessage();
        n.templateName = original.getTemplateName();
        n.from = original.getFrom();
        n.replyTo = original.getReplyTo();
        n.substitutions.putAll(original.getSubstitutions());
        n.metadata.putAll(original.getMetadata());
        n.handlingOptions.putAll(original.getHandlingOptions());
        original.getRecipients().stream().map(Recipient::copy).forEachOrdered(n.recipients::add);
        return n;
    }
}

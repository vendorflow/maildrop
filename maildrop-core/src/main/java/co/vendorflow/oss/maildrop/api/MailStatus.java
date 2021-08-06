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

import java.util.Map;

import javax.validation.constraints.NotNull;

import lombok.Value;

/**
 * @author Christopher Smith
 */
@Value
public class MailStatus {
    Lifecycle lifecycle;

    Map<String, Object> metadata;

    public MailStatus(@NotNull Lifecycle lifecycle, @NotNull Map<String, ?> metadata) {
        this.lifecycle = lifecycle;
        this.metadata = Map.copyOf(metadata);
    }


    /**
     * The lifecycle status of a message. As mail delivery is best-effort,
     * problems in deliverability may arise asynchronously at a later time,
     * but this status indicates the most advanced state the message
     * is known to have reached.
     */
    public enum Lifecycle {
        /**
         * The message has been enqueued, but it may not have been persisted anywhere.
         */
        ENQUEUED,

        /**
         * The message has been saved to persistent storage (such as a delivery queue),
         * but it may not have been sent to the recipients' mail.
         */
        PERSISTED,

        /**
         * The message has been sent to the recipient as far as this subsystem has
         * visibility. This does not guarantee delivery; for example, the message
         * may have been transmitted only to an SMTP relay in the delivery path.
         */
        SENT
    }
}

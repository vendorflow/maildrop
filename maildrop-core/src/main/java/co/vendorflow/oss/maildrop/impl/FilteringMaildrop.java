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

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PRIVATE;

import java.util.Map;

import co.vendorflow.oss.maildrop.api.MailMessage;
import co.vendorflow.oss.maildrop.api.MailSink;
import co.vendorflow.oss.maildrop.api.MailStatus;
import co.vendorflow.oss.maildrop.api.Maildrop;
import co.vendorflow.oss.maildrop.api.MaildropException;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Christopher Smith
 */
@RequiredArgsConstructor(access = PRIVATE)
@Builder
public class FilteringMaildrop implements Maildrop {

    @NonNull
    private final MailSink sink;

    @Builder.Default
    @NonNull
    private final MaildropFilterChain filterChain = new MaildropFilterChain();

    @Override
    public Map<MailMessage, MailStatus> enqueue(MailMessage message) throws MaildropException {
        var filtered = filterChain.filter(message);
        return filtered.stream().collect(toMap(identity(), sink::deliver));
    }
}

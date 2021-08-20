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

import static java.lang.System.identityHashCode;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import co.vendorflow.oss.maildrop.api.MailMessage;
import co.vendorflow.oss.maildrop.api.filter.MaildropFilter;
import co.vendorflow.oss.maildrop.api.filter.MaildropFilterException;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Composite filter representing executing multiple filters in sequence.
 *
 * @author Christopher Smith
 */
@NoArgsConstructor
public class MaildropFilterChain implements MaildropFilter {

    @Getter
    private final List<MaildropFilter> filters = new ArrayList<>();

    public MaildropFilterChain(Collection<? extends MaildropFilter> filters) {
        this.filters.addAll(filters);
    }

    public void setFilters(Collection<? extends MaildropFilter> filters) {
        this.filters.clear();
        this.filters.addAll(filters);
    }


    @Override
    public List<MailMessage> filter(final MailMessage message) throws MaildropFilterException {
        var current = List.of(message);
        for (var filter : filters) {
            current = current.stream()
                    .map(filter::filter)
                    .flatMap(Collection::stream)
                    .collect(toList());
        }
        return current;
    }


    @Override
    public String toString() {
        return new StringBuilder("MaildropFilterChain@")
                .append(Integer.toHexString(identityHashCode(this)))
                .append('[')
                .append(filters.stream().map(Object::getClass).map(Class::getSimpleName).collect(joining(", ")))
                .append(']')
                .toString();
    }
}

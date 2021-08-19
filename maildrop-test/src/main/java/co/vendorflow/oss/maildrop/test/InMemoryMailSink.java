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

package co.vendorflow.oss.maildrop.test;

import static co.vendorflow.oss.maildrop.api.MailStatus.Lifecycle.PERSISTED;
import static java.util.Collections.emptyMap;
import static java.util.Collections.synchronizedList;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import co.vendorflow.oss.maildrop.api.MailMessage;
import co.vendorflow.oss.maildrop.api.MailSink;
import co.vendorflow.oss.maildrop.api.MailStatus;
import co.vendorflow.oss.maildrop.api.MaildropException;

/**
 * An in-memory implementation of {@link MailSink} that places messages into a list.
 * This implementation is intended for testing purposes; it can be used for
 * in-process testing by injecting this sink into the test case and examining the
 * sent messages directly, or it can be used in integration testing by attaching an
 * HTTP REST or similar endpoint to the output.
 *
 * @author Christopher Smith
 */
public final class InMemoryMailSink implements MailSink {

    private static final MailStatus RETURN_STATUS = new MailStatus(PERSISTED, emptyMap());

    private final AtomicReference<List<MailMessage>> queueReference = new AtomicReference<>();


    public InMemoryMailSink() {
        drainQueue();
    }


    /**
     * Appends the supplied message to the queue. This method is synchronized with
     * {@link #drainQueue()} so that each message will be drained exactly once.
     */
    @Override
    public synchronized MailStatus deliver(MailMessage message) throws MaildropException {
        queueReference.get().add(message);
        return RETURN_STATUS;
    }


    /**
     * Provide a reference to the queue. Access to this list is synchronized, but
     * no guarantees are made about the visibility of specific messages. In particular,
     * code reading the queue might miss messages that are delivered after a subsequent
     * call to {@link #drainQueue()}, and messages might be appended to this list
     * after it is returned (potentially resulting in {@link ConcurrentModificationException}).
     * Applications that need consistent ordering should generally drain the list and,
     * if needed, append it to another data structure.
     *
     * @return a synchronized reference to the queue list
     */
    public List<MailMessage> getQueue() {
        return queueReference.get();
    }


    /**
     * Atomically replace the queue with a new one and return the previous queue.
     * This method is guaranteed to return all messages exactly once, and it is
     * synchronized with the {@link #deliver(MailMessage)} method.
     *
     * @return the queue as it appeared immediately before the call to this method
     */
    public synchronized List<MailMessage> drainQueue() {
        return queueReference.getAndSet(synchronizedList(new LinkedList<>()));
    }
}

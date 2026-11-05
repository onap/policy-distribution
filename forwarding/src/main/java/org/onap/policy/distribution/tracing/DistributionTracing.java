/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2026 Deutsche Telekom. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.distribution.tracing;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import java.util.HashMap;
import java.util.Map;

/**
 * Scopes distribution processing and propagates its W3C context to policy endpoints.
 */
public final class DistributionTracing implements AutoCloseable {
    private static final W3CTraceContextPropagator PROPAGATOR = W3CTraceContextPropagator.getInstance();
    private static final TextMapGetter<Map<String, String>> GETTER = new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(Map<String, String> carrier) {
            return carrier.keySet();
        }

        @Override
        public String get(Map<String, String> carrier, String key) {
            return carrier.get(key);
        }
    };

    private final Span span;
    private final Scope scope;

    private DistributionTracing(Span span) {
        this.span = span;
        scope = span.makeCurrent();
    }

    /**
     * Starts a consumer span using only the context of this Kafka record.
     *
     * @param headers the record headers
     * @param topic the topic, or null for notifications without record metadata
     * @param partition the record partition
     * @param offset the record offset
     * @return the processing scope
     */
    public static DistributionTracing consumer(Map<String, String> headers, String topic, int partition, long offset) {
        var parent = PROPAGATOR.extract(Context.root(), headers, GETTER);
        var builder = GlobalOpenTelemetry.getOrNoop().getTracer("org.onap.policy.distribution")
            .spanBuilder("sdc.distribution process").setSpanKind(SpanKind.CONSUMER).setParent(parent)
            .setAttribute("messaging.system", "kafka").setAttribute("messaging.operation.name", "process");
        if (topic != null) {
            builder.setAttribute("messaging.destination.name", topic)
                .setAttribute("messaging.destination.partition.id", Integer.toString(partition))
                .setAttribute("messaging.kafka.offset", offset);
        }
        return new DistributionTracing(builder.startSpan());
    }

    /**
     * Adds the current trace context to a copy of the request headers.
     *
     * @param headers the original request headers
     * @return request headers with trace context
     */
    public static Map<String, Object> headers(Map<String, Object> headers) {
        var result = new HashMap<>(headers);
        PROPAGATOR.inject(Context.current(), result, Map::put);
        return result;
    }

    /**
     * Marks the active processing span as failed, including caught business failures.
     *
     * @param failure the failure
     */
    public static void error(Throwable failure) {
        Span.current().recordException(failure).setStatus(StatusCode.ERROR);
    }

    @Override
    public void close() {
        scope.close();
        span.end();
    }
}

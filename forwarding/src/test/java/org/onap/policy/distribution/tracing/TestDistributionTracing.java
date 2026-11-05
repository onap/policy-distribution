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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestDistributionTracing {
    private final InMemorySpanExporter exporter = InMemorySpanExporter.create();
    private SdkTracerProvider provider;

    @BeforeEach
    void setUp() {
        GlobalOpenTelemetry.resetForTest();
        provider = SdkTracerProvider.builder().addSpanProcessor(SimpleSpanProcessor.create(exporter)).build();
        GlobalOpenTelemetry.set(OpenTelemetrySdk.builder().setTracerProvider(provider).build());
    }

    @AfterEach
    void tearDown() {
        provider.close();
        GlobalOpenTelemetry.resetForTest();
    }

    @Test
    void extractsW3cAndPropagatesToHttp() {
        try (var processing = DistributionTracing.consumer(Map.of("traceparent",
            "00-11111111111111111111111111111111-2222222222222222-01", "tracestate", "vendor=value"),
            "notifications", 2, 15)) {
            var headers = DistributionTracing.headers(Map.of("Accept", "application/json"));
            assertThat(headers.get("traceparent").toString()).contains("11111111111111111111111111111111")
                .contains(Span.current().getSpanContext().getSpanId());
            assertThat(headers.get("tracestate")).isEqualTo("vendor=value");
        }
        assertThat(exporter.getFinishedSpanItems()).singleElement().satisfies(span -> {
            assertThat(span.getKind()).isEqualTo(SpanKind.CONSUMER);
            assertThat(span.getParentSpanId()).isEqualTo("2222222222222222");
        });
    }

    @Test
    void startsIndependentRootsAndRestoresAmbientScopeAfterFailure() {
        var ambient = GlobalOpenTelemetry.get().getTracer("test").spanBuilder("unrelated").startSpan();
        try (var ignored = ambient.makeCurrent()) {
            for (var headers : java.util.List.of(Map.<String, String>of(), Map.of("traceparent", "malformed"))) {
                assertThatThrownBy(() -> {
                    try (var processing = DistributionTracing.consumer(headers, null, -1, -1)) {
                        var failure = new IllegalStateException("processing failed");
                        DistributionTracing.error(failure);
                        throw failure;
                    }
                }).isInstanceOf(IllegalStateException.class);
                assertThat(Span.current()).isEqualTo(ambient);
            }
        } finally {
            ambient.end();
        }
        assertThat(exporter.getFinishedSpanItems().subList(0, 2)).allSatisfy(span -> {
            assertThat(span.getParentSpanContext().isValid()).isFalse();
            assertThat(span.getTraceId()).isNotEqualTo(ambient.getSpanContext().getTraceId());
            assertThat(span.getStatus().getStatusCode()).isEqualTo(StatusCode.ERROR);
        });
        assertThat(Span.current().getSpanContext().isValid()).isFalse();
    }
}

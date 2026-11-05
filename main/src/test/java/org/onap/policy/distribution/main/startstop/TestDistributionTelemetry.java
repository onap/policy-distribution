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

package org.onap.policy.distribution.main.startstop;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpServer;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestDistributionTelemetry {
    @BeforeEach
    void setUp() {
        GlobalOpenTelemetry.resetForTest();
        System.setProperty("otel.traces.exporter", "none");
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("otel.traces.exporter");
        System.clearProperty("otel.exporter.otlp.endpoint");
        System.clearProperty("otel.exporter.otlp.protocol");
        GlobalOpenTelemetry.resetForTest();
    }

    @Test
    void ownsAndClosesApplicationSdk() {
        var telemetry = new DistributionTelemetry();
        telemetry.start();
        var tracer = GlobalOpenTelemetry.get().getTracer("test");
        var active = tracer.spanBuilder("before shutdown").startSpan();
        assertThat(active.isRecording()).isTrue();
        active.end();
        telemetry.close();
        var stopped = tracer.spanBuilder("after shutdown").startSpan();
        assertThat(stopped.isRecording()).isFalse();
        stopped.end();
    }

    @Test
    void exportsApplicationSpansOverOtlpAndFlushesOnShutdown() throws Exception {
        var exports = new CopyOnWriteArrayList<byte[]>();
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/v1/traces", exchange -> {
            exports.add(exchange.getRequestBody().readAllBytes());
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        });
        server.start();
        System.setProperty("otel.traces.exporter", "otlp");
        System.setProperty("otel.exporter.otlp.endpoint", "http://localhost:" + server.getAddress().getPort());
        try (var telemetry = new DistributionTelemetry()) {
            telemetry.start();
            GlobalOpenTelemetry.get().getTracer("test").spanBuilder("exported span").startSpan().end();
        } finally {
            server.stop(0);
        }
        assertThat(exports).singleElement().satisfies(payload -> {
            assertThat(new String(payload, StandardCharsets.ISO_8859_1))
                .contains("policy-distribution", "exported span");
        });
    }

    @Test
    void reusesExternalSdkWithoutClosingIt() {
        var exporter = InMemorySpanExporter.create();
        try (var provider = SdkTracerProvider.builder()
            .addSpanProcessor(SimpleSpanProcessor.create(exporter)).build()) {
            GlobalOpenTelemetry.set(OpenTelemetrySdk.builder().setTracerProvider(provider).build());
            try (var telemetry = new DistributionTelemetry()) {
                telemetry.start();
            }
            var span = GlobalOpenTelemetry.get().getTracer("test").spanBuilder("external").startSpan();
            assertThat(span.isRecording()).isTrue();
            span.end();
            assertThat(exporter.getFinishedSpanItems()).hasSize(1);
        }
    }
}

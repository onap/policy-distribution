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

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import java.util.Map;

/**
 * Owns telemetry when no agent or embedding application has already configured it.
 */
final class DistributionTelemetry implements AutoCloseable {
    private OpenTelemetrySdk sdk;

    void start() {
        if (!GlobalOpenTelemetry.isSet()) {
            sdk = AutoConfiguredOpenTelemetrySdk.builder()
                .addPropertiesSupplier(() -> Map.of("otel.service.name", "policy-distribution",
                    "otel.traces.exporter", "otlp", "otel.exporter.otlp.protocol", "http/protobuf",
                    "otel.metrics.exporter", "none", "otel.logs.exporter", "none",
                    "otel.propagators", "tracecontext"))
                .setResultAsGlobal().build().getOpenTelemetrySdk();
        }
    }

    @Override
    public void close() {
        if (sdk != null) {
            sdk.close();
            sdk = null;
        }
    }
}

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

package org.onap.policy.distribution.forwarding.lifecycle.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpServer;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.onap.policy.distribution.forwarding.testclasses.CommonTestData;
import org.onap.policy.distribution.tracing.DistributionTracing;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;

class TestForwarderTracePropagation {
    @Test
    void propagatesConsumerContextToApiPapAndAcmOverHttp() throws Exception {
        var requests = new CopyOnWriteArrayList<Map<String, String>>();
        var server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            requests.add(Map.of("path", exchange.getRequestURI().getPath(),
                "traceparent", exchange.getRequestHeaders().getFirst("traceparent"),
                "tracestate", exchange.getRequestHeaders().getFirst("tracestate")));
            exchange.getRequestBody().readAllBytes();
            var body = exchange.getRequestURI().getPath().equals("/policy/api/v1/policies")
                ? Files.readAllBytes(Path.of("src/test/resources/parameters/sample_policy.json"))
                : "{}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        var parameters = CommonTestData.getPolicyForwarderParameters(
            "src/test/resources/parameters/LifecycleApiPolicyForwarderParameters.json",
            LifecycleApiForwarderParameters.class);
        parameters.getApiParameters().setPort(server.getAddress().getPort());
        parameters.getPapParameters().setPort(server.getAddress().getPort());
        var acmParameters = CommonTestData.getPolicyForwarderParameters(
            "src/test/resources/parameters/LifecycleApiAutomationCompositionForwarderParameters.json",
            LifecycleApiAutomationCompositionForwarderParameters.class);
        acmParameters.getAutomationCompositionRuntimeParameters().setPort(server.getAddress().getPort());
        ParameterService.register(parameters);
        ParameterService.register(acmParameters);
        GlobalOpenTelemetry.resetForTest();
        try (var provider = SdkTracerProvider.builder().build()) {
            GlobalOpenTelemetry.set(OpenTelemetrySdk.builder().setTracerProvider(provider).build());
            try (var processing = DistributionTracing.consumer(Map.of("traceparent",
                "00-11111111111111111111111111111111-2222222222222222-01", "tracestate", "vendor=value"),
                "notifications", 0, 1)) {
                final var consumerId = Span.current().getSpanContext().getSpanId();
                var forwarder = new LifecycleApiPolicyForwarder();
                forwarder.configure(parameters.getName());
                var coder = new StandardCoder();
                forwarder.forward(List.of(coder.decode(ResourceUtils.getResourceAsString(
                    "src/test/resources/parameters/sample_policy_type.json"), ToscaServiceTemplate.class),
                    coder.decode(ResourceUtils.getResourceAsString("src/test/resources/parameters/sample_policy.json"),
                        ToscaServiceTemplate.class)));
                var acmForwarder = new LifecycleApiAutomationCompositionForwarder();
                acmForwarder.configure(acmParameters.getName());
                acmForwarder.forward(List.of(coder.decode(ResourceUtils.getResourceAsString(
                    "src/test/resources/parameters/sample_automation_composition.json"), ToscaServiceTemplate.class)));
                assertThat(requests).extracting(request -> request.get("path")).containsExactly(
                    "/policy/api/v1/policytypes", "/policy/api/v1/policies", "/policy/pap/v1/pdps/policies",
                    "/onap/acm/v2/commission");
                assertThat(requests).allSatisfy(request -> {
                    assertThat(request.get("traceparent"))
                        .isEqualTo("00-11111111111111111111111111111111-" + consumerId + "-01");
                    assertThat(request.get("tracestate")).isEqualTo("vendor=value");
                });
            }
        } finally {
            GlobalOpenTelemetry.resetForTest();
            ParameterService.deregister(parameters);
            ParameterService.deregister(acmParameters);
            server.stop(0);
        }
    }
}

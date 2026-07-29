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

package org.onap.policy.distribution.reception.handling.sdc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.sdc.impl.DistributionClientImpl;

/**
 * Integration test that drives the real, unmocked SDC distribution client against a stub SDC
 * server, i.e. the exact code path {@code SdcReceptionHandler.initializeSdcClient()} runs in
 * production.
 *
 * <p>The policy parent pom pulls in {@code sdc-distribution-client} with a blanket
 * {@code <exclusion>*:*</exclusion>}, so none of the client's own transitive dependencies land on
 * the runtime classpath. Anything it touches has to be declared explicitly by this module. When one
 * of those declarations is missing, the client blows up with a {@link NoClassDefFoundError} the
 * first time it talks to SDC — on the {@code Timer-0} background thread, so the container stays
 * "Running" and only the distribution of service models silently stops working.
 *
 * <p>{@link TestSdcReceptionHandler} cannot catch that class of regression: it mocks
 * {@code createSdcDistributionClient()}, so the real client, its HTTP stack and its response
 * parsing never load. This test deliberately uses no mocks — it starts a local HTTP server that
 * answers like SDC and lets the client parse a real response, which exercises Apache HttpClient 4.x
 * ({@code org.apache.http}), commons-io ({@code IOUtils}) and functionaljava ({@code fj.data.Either}).
 *
 * <p>The client is not expected to reach a healthy state here — there is no Kafka broker behind the
 * stub — so the assertion is deliberately not on the result status. It is that initialization fails
 * *gracefully*, returning a result instead of dying on a {@link LinkageError}.
 */
class SdcClientInitializationTest {

    private static final String ARTIFACT_TYPES_PATH = "/sdc/v1/artifactTypes";
    private static final String KAFKA_DATA_PATH = "/sdc/v1/distributionKafkaData";

    private HttpServer sdcStub;
    private SdcReceptionHandlerConfigurationParameterGroup configParameters;

    /**
     * Starts a stub SDC server on a free port and points the handler parameters at it.
     *
     * @throws IOException if the stub server cannot be started
     */
    @BeforeEach
    void startSdcStub() throws IOException {
        sdcStub = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        sdcStub.createContext(ARTIFACT_TYPES_PATH, exchange -> respond(exchange,
            "[\"TOSCA_CSAR\",\"HEAT\"]"));
        sdcStub.createContext(KAFKA_DATA_PATH, exchange -> respond(exchange,
            "{\"kafkaBootStrapServer\":\"localhost:9092\","
                + "\"distrNotificationTopicName\":\"SDC-DISTR-NOTIF-TOPIC\","
                + "\"distrStatusTopicName\":\"SDC-DISTR-STATUS-TOPIC\"}"));
        sdcStub.start();

        final var gson = new GsonBuilder().create();
        configParameters = gson.fromJson(new FileReader("src/test/resources/handling-sdc.json"),
            SdcReceptionHandlerConfigurationParameterGroup.class);
        ParameterService.register(configParameters);
    }

    @AfterEach
    void stopSdcStub() {
        ParameterService.deregister(configParameters);
        sdcStub.stop(0);
    }

    @Test
    void realSdcClientInitializesWithoutMissingClasses() {
        final var sdcConfig = new StubAddressSdcConfiguration(configParameters,
            "localhost:" + sdcStub.getAddress().getPort());
        final var distributionClient = new DistributionClientImpl();

        // Mirrors SdcReceptionHandler.initializeSdcClient(); a missing runtime dependency of
        // sdc-distribution-client surfaces here as a NoClassDefFoundError.
        assertThatCode(() -> {
            final var result = distributionClient.init(sdcConfig, notification -> { });
            assertThat(result).isNotNull();
            assertThat(result.getDistributionActionResult()).isNotNull();
        }).doesNotThrowAnyException();

        distributionClient.stop();
    }

    private static void respond(final HttpExchange exchange, final String body) throws IOException {
        final var payload = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, payload.length);
        try (var responseBody = exchange.getResponseBody()) {
            responseBody.write(payload);
        }
    }

    /**
     * Configuration that behaves exactly like {@link SdcConfiguration} but talks plain HTTP to the
     * stub server instead of the configured SDC instance.
     */
    private static class StubAddressSdcConfiguration extends SdcConfiguration {

        private final String sdcAddress;

        StubAddressSdcConfiguration(final SdcReceptionHandlerConfigurationParameterGroup configParameters,
            final String sdcAddress) {
            super(configParameters);
            this.sdcAddress = sdcAddress;
        }

        @Override
        public String getSdcAddress() {
            return sdcAddress;
        }

        @Override
        public Boolean isUseHttpsWithSDC() {
            return false;
        }

        /**
         * Supplied explicitly because the interface default reads it from the SASL_JAAS_CONFIG
         * environment variable and throws when it is unset, which would abort init() before it ever
         * calls SDC over HTTP.
         */
        @Override
        public String getKafkaSaslJaasConfig() {
            return "org.apache.kafka.common.security.scram.ScramLoginModule required "
                + "username=\"policy\" password=\"policy\";";
        }
    }
}

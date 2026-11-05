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
import static org.awaitility.Awaitility.await;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.testing.exporter.InMemorySpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.parameters.PluginHandlerParameters;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderParameters;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;
import org.onap.policy.distribution.reception.statistics.DistributionStatisticsManager;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientImpl;
import org.springframework.kafka.test.EmbeddedKafkaKraftBroker;

/**
 * Drives a whole SDC distribution through {@link SdcReceptionHandler} and the real, unmocked
 * {@link DistributionClientImpl}: an embedded Kafka broker carries the notification and status topics
 * and a stub HTTP server answers the SDC REST calls the client makes (artifact types, Kafka data and
 * the artifact download).
 */
class SdcDistributionEndToEndTest {

    private static final String NOTIFICATION_TOPIC = "SDC-DISTR-NOTIF-TOPIC";
    private static final String STATUS_TOPIC = "SDC-DISTR-STATUS-TOPIC";
    private static final String ARTIFACT_NAME = "service-Sampleservice.csar";
    private static final String ARTIFACT_URL =
        "/sdc/v1/catalog/services/Sampleservice/1.0/artifacts/" + ARTIFACT_NAME;
    private static final Duration TIMEOUT = Duration.ofSeconds(90);

    private static EmbeddedKafkaKraftBroker kafka;

    private final List<String> artifactRequestAuthorizations = new CopyOnWriteArrayList<>();
    private final List<JsonObject> statusMessages = new ArrayList<>();
    private final AtomicReference<IDistributionClientResult> stopResult = new AtomicReference<>();
    private final String distributionId = UUID.randomUUID().toString();

    private final InMemorySpanExporter exporter = InMemorySpanExporter.create();
    private SdkTracerProvider tracerProvider;
    private byte[] csar;
    private HttpServer sdcStub;
    private SdcReceptionHandlerConfigurationParameterGroup sdcParameters;
    private PluginHandlerParameters pluginParameters;
    private ReceptionHandlerParameters receptionParameters;
    private KafkaConsumer<String, String> statusConsumer;

    @BeforeAll
    static void startKafka() {
        kafka = new EmbeddedKafkaKraftBroker(1, 1, NOTIFICATION_TOPIC, STATUS_TOPIC);
        kafka.afterPropertiesSet();
    }

    @AfterAll
    static void stopKafka() {
        kafka.destroy();
    }

    /**
     * Starts the SDC stub and registers the parameter groups the reception handler is initialized from.
     *
     * @throws IOException if the stub server cannot be started or the test resources cannot be read
     */
    @BeforeEach
    void setUp() throws IOException {
        GlobalOpenTelemetry.resetForTest();
        tracerProvider = SdkTracerProvider.builder().addSpanProcessor(SimpleSpanProcessor.create(exporter)).build();
        GlobalOpenTelemetry.set(OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build());
        DistributionStatisticsManager.resetAllStatistics();
        RecordingDecoder.DECODED_CSARS.clear();
        RecordingDecoder.FAIL_NEXT.set(false);
        csar = Files.readAllBytes(Path.of("src/test/resources", ARTIFACT_NAME));

        sdcStub = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        sdcStub.createContext("/sdc/v1/artifactTypes", exchange -> respond(exchange, "application/json",
            "[\"TOSCA_CSAR\",\"HEAT\"]".getBytes(StandardCharsets.UTF_8)));
        sdcStub.createContext("/sdc/v1/distributionKafkaData", exchange -> respond(exchange, "application/json",
            ("{\"kafkaBootStrapServer\":\"" + kafka.getBrokersAsString() + "\","
                + "\"distrNotificationTopicName\":\"" + NOTIFICATION_TOPIC + "\","
                + "\"distrStatusTopicName\":\"" + STATUS_TOPIC + "\"}").getBytes(StandardCharsets.UTF_8)));
        sdcStub.createContext(ARTIFACT_URL, exchange -> {
            artifactRequestAuthorizations.add(exchange.getRequestHeaders().getFirst("Authorization"));
            respond(exchange, "application/octet-stream", csar);
        });
        sdcStub.start();

        sdcParameters = new GsonBuilder().create().fromJson(new FileReader("src/test/resources/handling-sdc.json"),
            SdcReceptionHandlerConfigurationParameterGroup.class);
        ParameterService.register(sdcParameters);

        pluginParameters = new PluginHandlerParameters(
            Map.of("RecordingDecoderKey", new PolicyDecoderParameters("RecordingDecoder",
                RecordingDecoder.class.getName(), "RecordingDecoderConfiguration")),
            Map.of("DummyForwarderKey", new PolicyForwarderParameters("DummyForwarder",
                DummyPolicyForwarder.class.getName(), "DummyConfiguration")));
        pluginParameters.setName("SdcEndToEnd");
        ParameterService.register(pluginParameters);

        receptionParameters = new ReceptionHandlerParameters("SDC", SdcReceptionHandler.class.getName(),
            sdcParameters.getName(), pluginParameters);
        receptionParameters.setName("SdcEndToEnd");
        ParameterService.register(receptionParameters);

        statusConsumer = new KafkaConsumer<>(Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBrokersAsString(),
            ConsumerConfig.GROUP_ID_CONFIG, "status-observer-" + distributionId,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"),
            new StringDeserializer(), new StringDeserializer());
        statusConsumer.subscribe(List.of(STATUS_TOPIC));
    }

    @AfterEach
    void tearDown() {
        tracerProvider.close();
        GlobalOpenTelemetry.resetForTest();
        statusConsumer.close();
        ParameterService.deregister(receptionParameters);
        ParameterService.deregister(pluginParameters);
        ParameterService.deregister(sdcParameters);
        sdcStub.stop(0);
    }

    @Test
    void distributesServiceArtifactThroughRealSdcClient() throws Exception {
        final var handler = new EmbeddedKafkaSdcReceptionHandler();
        handler.initialize(receptionParameters.getName());

        await().atMost(TIMEOUT).until(this::notificationConsumerHasCommittedPosition);
        publishNotification(distributionId, Map.of("traceparent",
            "00-11111111111111111111111111111111-2222222222222222-01"));

        await().atMost(TIMEOUT).pollInSameThread().untilAsserted(() -> {
            statusConsumer.poll(Duration.ofMillis(200))
                .forEach(message -> statusMessages.add(JsonParser.parseString(message.value()).getAsJsonObject()));
            assertThat(statusesOfThisDistribution()).contains("COMPONENT_DONE_OK");
        });

        await().atMost(TIMEOUT).untilAsserted(() -> assertThat(exporter.getFinishedSpanItems().stream()
            .filter(span -> span.getKind() == SpanKind.CONSUMER).toList()).singleElement().satisfies(span -> {
                assertThat(span.getKind()).isEqualTo(SpanKind.CONSUMER);
                assertThat(span.getTraceId()).isEqualTo("11111111111111111111111111111111");
                assertThat(span.getParentSpanId()).isEqualTo("2222222222222222");
            }));
        assertThat(statusesOfThisDistribution())
            .containsSubsequence("NOTIFIED", "DOWNLOAD_OK", "DEPLOY_OK", "COMPONENT_DONE_OK");
        assertThat(statusMessages).allSatisfy(
            message -> assertThat(message.get("consumerID").getAsString()).isEqualTo(sdcParameters.getConsumerId()));
        assertThat(artifactRequestAuthorizations).containsExactly("Basic "
            + Base64.getEncoder().encodeToString("policy:policy".getBytes(StandardCharsets.UTF_8)));
        assertThat(RecordingDecoder.DECODED_CSARS).containsExactly(csar);
        assertThat(DistributionStatisticsManager.getDistributionSuccessCount()).isEqualTo(1);
        assertThat(DistributionStatisticsManager.getDownloadSuccessCount()).isEqualTo(1);

        handler.destroy();

        await().atMost(TIMEOUT).untilAsserted(() -> assertThat(stopResult.get()).isNotNull());
        assertThat(stopResult.get().getDistributionActionResult()).hasToString("SUCCESS");
        await().atMost(TIMEOUT).until(this::notificationConsumerHasLeftGroup);
    }

    @Test
    void isolatesEachRecordContextThroughRealClientDispatch() throws Exception {
        final var handler = new EmbeddedKafkaSdcReceptionHandler();
        handler.initialize(receptionParameters.getName());
        try {
            await().atMost(TIMEOUT).until(this::notificationConsumerHasCommittedPosition);
            RecordingDecoder.FAIL_NEXT.set(true);
            publishNotification("first", Map.of("traceparent",
                "00-11111111111111111111111111111111-2222222222222222-01"));
            publishNotification("second", Map.of("traceparent",
                "00-33333333333333333333333333333333-4444444444444444-01"));
            publishNotification("headerless", Map.of());
            publishNotification("invalid", Map.of("traceparent", "invalid"));
            await().atMost(TIMEOUT).untilAsserted(() -> assertThat(exporter.getFinishedSpanItems().stream()
                .filter(span -> span.getKind() == SpanKind.CONSUMER).toList()).hasSize(4));
            var consumers = exporter.getFinishedSpanItems().stream()
                .filter(span -> span.getKind() == SpanKind.CONSUMER).toList();
            assertThat(consumers.get(0).getTraceId()).isEqualTo("11111111111111111111111111111111");
            assertThat(consumers.get(1).getTraceId()).isEqualTo("33333333333333333333333333333333");
            assertThat(consumers.subList(2, 4)).allSatisfy(span ->
                assertThat(span.getParentSpanContext().isValid()).isFalse());
            assertThat(consumers).extracting(span -> span.getTraceId()).doesNotHaveDuplicates();
            assertThat(consumers.getFirst().getStatus().getStatusCode()).isEqualTo(StatusCode.ERROR);
            assertThat(consumers.subList(1, 4)).allSatisfy(span ->
                assertThat(span.getStatus().getStatusCode()).isNotEqualTo(StatusCode.ERROR));
            assertThat(RecordingDecoder.DECODED_CSARS).hasSize(3);
            assertThat(DistributionStatisticsManager.getDistributionSuccessCount()).isEqualTo(3);
            assertThat(DistributionStatisticsManager.getDistributionFailureCount()).isEqualTo(1);
        } finally {
            handler.destroy();
            await().atMost(TIMEOUT).until(this::notificationConsumerHasLeftGroup);
        }
    }

    /**
     * The client's consumer resets to the latest offset, so a notification published before it holds a
     * position on the partition would never be delivered.
     */
    private boolean notificationConsumerHasCommittedPosition() throws ExecutionException, InterruptedException {
        try (var admin = Admin.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBrokersAsString()))) {
            return admin.listConsumerGroupOffsets(sdcParameters.getConsumerGroup()).partitionsToOffsetAndMetadata()
                .get().get(new TopicPartition(NOTIFICATION_TOPIC, 0)) != null;
        }
    }

    private boolean notificationConsumerHasLeftGroup() throws ExecutionException, InterruptedException {
        try (var admin = Admin.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBrokersAsString()))) {
            return admin.describeConsumerGroups(List.of(sdcParameters.getConsumerGroup())).all().get()
                .get(sdcParameters.getConsumerGroup()).members().isEmpty();
        }
    }

    private void publishNotification(String id, Map<String, String> headers)
        throws ExecutionException, InterruptedException, NoSuchAlgorithmException {
        final var artifact = new JsonObject();
        artifact.addProperty("artifactName", ARTIFACT_NAME);
        artifact.addProperty("artifactType", "TOSCA_CSAR");
        artifact.addProperty("artifactURL", ARTIFACT_URL);
        artifact.addProperty("artifactChecksum", Base64.getEncoder().encodeToString(
            HexFormat.of().formatHex(MessageDigest.getInstance("MD5").digest(csar)).getBytes(StandardCharsets.UTF_8)));
        artifact.addProperty("artifactDescription", "TOSCA representation of the service");
        artifact.addProperty("artifactTimeout", 0);
        artifact.addProperty("artifactUUID", UUID.randomUUID().toString());
        artifact.addProperty("artifactVersion", "1");
        final var serviceArtifacts = new JsonArray();
        serviceArtifacts.add(artifact);

        final var notification = new JsonObject();
        notification.addProperty("distributionID", id);
        notification.addProperty("serviceName", "Sampleservice");
        notification.addProperty("serviceVersion", "1.0");
        notification.addProperty("serviceUUID", UUID.randomUUID().toString());
        notification.addProperty("serviceInvariantUUID", UUID.randomUUID().toString());
        notification.addProperty("serviceDescription", "Sample service");
        notification.addProperty("workloadContext", "Production");
        notification.add("resources", new JsonArray());
        notification.add("serviceArtifacts", serviceArtifacts);

        try (var producer = new KafkaProducer<>(
            Map.<String, Object>of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBrokersAsString()),
            new StringSerializer(), new StringSerializer())) {
            var record = new ProducerRecord<String, String>(NOTIFICATION_TOPIC, notification.toString());
            headers.forEach((key, value) -> record.headers().add(key, value.getBytes(StandardCharsets.UTF_8)));
            producer.send(record).get();
        }
    }

    private List<String> statusesOfThisDistribution() {
        return statusMessages.stream()
            .filter(message -> distributionId.equals(message.get("distributionID").getAsString()))
            .map(message -> message.get("status").getAsString())
            .toList();
    }

    private static void respond(final HttpExchange exchange, final String contentType, final byte[] body)
        throws IOException {
        exchange.getResponseHeaders().add("Content-Type", contentType);
        exchange.sendResponseHeaders(200, body.length);
        try (var responseBody = exchange.getResponseBody()) {
            responseBody.write(body);
        }
    }

    private class EmbeddedKafkaSdcReceptionHandler extends SdcReceptionHandler {

        @Override
        protected IDistributionClient createSdcDistributionClient() {
            return new DistributionClientImpl() {
                /**
                 * Swaps in a configuration for the stub SDC and the plaintext embedded broker; the one the
                 * handler builds would take the Kafka security settings from the environment.
                 */
                @Override
                public synchronized IDistributionClientResult init(final IConfiguration conf,
                    final INotificationCallback callback) {
                    return super.init(new EmbeddedKafkaSdcConfiguration(sdcParameters,
                        "localhost:" + sdcStub.getAddress().getPort()), notification -> {
                            var ambient = GlobalOpenTelemetry.get().getTracer("test").spanBuilder("poller").startSpan();
                            try (var scope = ambient.makeCurrent()) {
                                callback.activateCallback(notification);
                                assertThat(Span.current()).isEqualTo(ambient);
                            } finally {
                                ambient.end();
                            }
                        });
                }

                @Override
                public synchronized IDistributionClientResult stop() {
                    final var result = super.stop();
                    stopResult.set(result);
                    return result;
                }
            };
        }
    }

    private static class EmbeddedKafkaSdcConfiguration extends SdcConfiguration {

        private final String sdcAddress;

        EmbeddedKafkaSdcConfiguration(final SdcReceptionHandlerConfigurationParameterGroup configParameters,
            final String sdcAddress) {
            super(configParameters);
            this.sdcAddress = sdcAddress;
        }

        @Override
        public String getSdcAddress() {
            return sdcAddress;
        }

        @Override
        public String getKafkaSecurityProtocolConfig() {
            return "PLAINTEXT";
        }

        /**
         * Unused over PLAINTEXT, but the client puts it into the Kafka properties regardless and a null value
         * fails there.
         */
        @Override
        public String getKafkaSaslJaasConfig() {
            return "";
        }
    }

    /**
     * Decoder that keeps the content of every CSAR it is handed; the reception handler deletes the file once
     * the policies have been forwarded.
     */
    public static class RecordingDecoder implements PolicyDecoder<Csar, DummyPolicy> {

        static final AtomicBoolean FAIL_NEXT = new AtomicBoolean();
        static final List<byte[]> DECODED_CSARS = new CopyOnWriteArrayList<>();

        @Override
        public boolean canHandle(final PolicyInput policyInput) {
            return policyInput instanceof Csar;
        }

        @Override
        public Collection<DummyPolicy> decode(final Csar input) throws PolicyDecodingException {
            if (FAIL_NEXT.getAndSet(false)) {
                throw new PolicyDecodingException("No decoder for this artifact");
            }
            try {
                DECODED_CSARS.add(Files.readAllBytes(Path.of(input.getCsarFilePath())));
            } catch (final IOException exp) {
                throw new IllegalStateException(exp);
            }
            return List.of(new DummyPolicy(input.getCsarFilePath()));
        }

        @Override
        public void configure(final String parameterGroupName) {
            // no configuration
        }
    }
}

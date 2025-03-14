/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
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

package org.onap.policy.distribution.main.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.endpoints.http.server.RestServer;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.common.utils.report.HealthCheckReport;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.parameters.CommonTestData;
import org.onap.policy.distribution.main.startstop.Main;

/**
 * Class to perform unit test of HealthCheckMonitor.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
class TestDistributionRestServer {

    private static final String NOT_ALIVE = "not alive";
    private static final String ALIVE = "alive";
    private static final String SELF = NetworkUtil.getHostname();
    private static final String NAME = "Policy SSD";

    private int port;

    @BeforeEach
    void setUp() {
        ParameterService.clear();
    }

    @Test
    void testHealthCheckSuccess() throws IOException, InterruptedException {
        final var reportString =
                        "HealthCheckReport(name=Policy SSD, url=" + SELF + ", healthy=true, code=200, message=alive)";
        final var main = startDistributionService();
        final var report = performHealthCheck();
        validateReport(true, 200, ALIVE, reportString, report);
        assertThatCode(() -> stopDistributionService(main)).doesNotThrowAnyException();
    }

    @Test
    void testHealthCheckFailure() throws IOException, InterruptedException {
        port = NetworkUtil.allocPort();
        final var reportString =
                "HealthCheckReport(name=Policy SSD, url=" + SELF + ", healthy=false, code=500, message=not alive)";
        final var restServerParams = new CommonTestData().getRestServerParameters(port);
        restServerParams.setName(CommonTestData.DISTRIBUTION_GROUP_NAME);
        final var restServer = new RestServer(restServerParams, DistributionRestController.class);
        restServer.start();
        final var report = performHealthCheck();
        validateReport(false, 500, NOT_ALIVE, reportString, report);
        assertTrue(restServer.isAlive());
        assertThat(restServer.toString()).startsWith("RestServer(servers=");
        assertThatCode(restServer::shutdown).doesNotThrowAnyException();
    }

    private Main startDistributionService() throws IOException {
        port = CommonTestData.makeConfigFile("parameters/DistributionConfigParameters.json");
        final String[] distributionConfigParameters = { "-c", CommonTestData.CONFIG_FILE };
        return new Main(distributionConfigParameters);
    }

    private void stopDistributionService(final Main main) throws PolicyDistributionException {
        main.shutdown();
    }

    private HealthCheckReport performHealthCheck() throws InterruptedException {
        final var clientConfig = new ClientConfig();

        final var feature = HttpAuthenticationFeature.basic("healthcheck", "zb!XztG34");
        clientConfig.register(feature);

        final var client = ClientBuilder.newClient(clientConfig);
        final var webTarget = client.target("http://localhost:" + port + "/healthcheck");

        final var invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        CommonTestData.awaitServer(port);
        return invocationBuilder.get(HealthCheckReport.class);
    }

    private void validateReport(final boolean healthy, final int code,
                                final String message, final String reportString, final HealthCheckReport report) {
        assertEquals(TestDistributionRestServer.NAME, report.getName());
        assertEquals(TestDistributionRestServer.SELF, report.getUrl());
        assertEquals(healthy, report.isHealthy());
        assertEquals(code, report.getCode());
        assertEquals(message, report.getMessage());
        assertEquals(reportString, report.toString());
    }
}

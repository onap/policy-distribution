/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 AT&T Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.common.utils.report.HealthCheckReport;
import org.onap.policy.common.utils.security.SelfSignedKeyStore;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.parameters.CommonTestData;
import org.onap.policy.distribution.main.startstop.Main;

/**
 * Class to perform unit test of HealthCheckMonitor.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
class TestHttpsDistributionRestServer {

    private static final String ALIVE = "alive";
    private static final String SELF = NetworkUtil.getHostname();
    private static final String NAME = "Policy SSD";

    private int port;

    @BeforeEach
    void setUp() {
        ParameterService.clear();
    }

    @Test
    void testHttpsHealthCheckSuccess() throws Exception {
        final var reportString =
                        "HealthCheckReport(name=Policy SSD, url=" + SELF + ", healthy=true, code=200, message=alive)";
        final var main = startDistributionService();
        final var report = performHealthCheck();
        validateReport(reportString, report);
        assertThatCode(() -> stopDistributionService(main)).doesNotThrowAnyException();
    }

    private Main startDistributionService() throws IOException, InterruptedException {
        final var systemProps = System.getProperties();
        systemProps.put("javax.net.ssl.keyStore", new SelfSignedKeyStore().getKeystoreName());
        systemProps.put("javax.net.ssl.keyStorePassword", SelfSignedKeyStore.KEYSTORE_PASSWORD);
        System.setProperties(systemProps);

        port = CommonTestData.makeConfigFile("parameters/DistributionConfigParameters_Https.json");
        final String[] distributionConfigParameters = { "-c", CommonTestData.CONFIG_FILE };
        return new Main(distributionConfigParameters);
    }

    private void stopDistributionService(final Main main) throws PolicyDistributionException {
        main.shutdown();
    }

    private HealthCheckReport performHealthCheck() throws Exception {

        final var noopTrustManager = NetworkUtil.getAlwaysTrustingManager();

        final var sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, noopTrustManager, new SecureRandom());
        final var clientBuilder =
                ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier((host, session) -> true);
        final var client = clientBuilder.build();
        final var feature = HttpAuthenticationFeature.basic("healthcheck", "zb!XztG34");
        client.register(feature);

        final var webTarget = client.target("https://localhost:" + port + "/healthcheck");

        final var invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        CommonTestData.awaitServer(port);
        return invocationBuilder.get(HealthCheckReport.class);
    }

    private void validateReport(final String reportString, final HealthCheckReport report) {
        assertEquals(TestHttpsDistributionRestServer.NAME, report.getName());
        assertEquals(TestHttpsDistributionRestServer.SELF, report.getUrl());
        assertTrue(report.isHealthy());
        assertEquals(200, report.getCode());
        assertEquals(TestHttpsDistributionRestServer.ALIVE, report.getMessage());
        assertEquals(reportString, report.toString());
    }
}

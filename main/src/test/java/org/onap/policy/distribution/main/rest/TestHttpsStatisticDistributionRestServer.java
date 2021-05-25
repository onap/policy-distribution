/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
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
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Properties;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.common.utils.security.SelfSignedKeyStore;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.parameters.CommonTestData;
import org.onap.policy.distribution.main.startstop.Main;

/**
 * Class to perform unit test of HealthCheckMonitor.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
public class TestHttpsStatisticDistributionRestServer {

    private int port;

    @Before
    public void setUp() {
        ParameterService.clear();
    }

    @Test
    public void testHttpsDistributionStatistic() {
        assertThatCode(() -> {
            final Main main = startDistributionService();
            final StatisticsReport report = performStatisticCheck();
            validateReport(200, 0, 0, 0, 0, 0, 0, report);
            stopDistributionService(main);
        }).doesNotThrowAnyException();
    }

    private Main startDistributionService() throws IOException, InterruptedException {
        final Properties systemProps = System.getProperties();
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

    private StatisticsReport performStatisticCheck() throws Exception {
        final TrustManager[] noopTrustManager = NetworkUtil.getAlwaysTrustingManager();

        final SSLContext sc = SSLContext.getInstance("TLSv1.2");
        sc.init(null, noopTrustManager, new SecureRandom());
        final ClientBuilder clientBuilder =
                ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier((host, session) -> true);
        final Client client = clientBuilder.build();
        final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("healthcheck", "zb!XztG34");
        client.register(feature);

        final WebTarget webTarget = client.target("https://localhost:" + port + "/statistics");

        final Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        CommonTestData.awaitServer(port);
        return invocationBuilder.get(StatisticsReport.class);
    }

    private void validateReport(final int code, final int total, final int successCount, final int failureCount,
            final int download, final int downloadSuccess, final int downloadFailure, final StatisticsReport report) {
        assertEquals(code, report.getCode());
        assertEquals(total, report.getTotalDistributionCount());
        assertEquals(successCount, report.getDistributionSuccessCount());
        assertEquals(failureCount, report.getDistributionFailureCount());
        assertEquals(download, report.getTotalDownloadCount());
        assertEquals(downloadSuccess, report.getDownloadSuccessCount());
        assertEquals(downloadFailure, report.getDownloadFailureCount());
    }
}

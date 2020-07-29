/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2019-2020 AT&T Intellectual Property. All rights reserved.
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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.onap.policy.common.endpoints.http.server.RestServer;
import org.onap.policy.common.endpoints.parameters.RestServerParameters;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.parameters.CommonTestData;
import org.onap.policy.distribution.main.startstop.Main;
import org.onap.policy.distribution.reception.statistics.DistributionStatisticsManager;

/**
 * Class to perform unit test of {@link DistributionRestController}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestDistributionStatistics {

    private int port;

    @Test
    public void testDistributionStatistics_200() {
        assertThatCode(() -> {
            final Main main = startDistributionService();
            StatisticsReport report = getDistributionStatistics();
            validateReport(report, 0, 200);
            updateDistributionStatistics();
            report = getDistributionStatistics();
            validateReport(report, 1, 200);
            stopDistributionService(main);
            DistributionStatisticsManager.resetAllStatistics();
        }).doesNotThrowAnyException();
    }

    @Test
    public void testDistributionStatistics_500() throws InterruptedException, IOException {
        port = NetworkUtil.allocPort();
        final RestServerParameters restServerParams = new CommonTestData().getRestServerParameters(false);
        Whitebox.setInternalState(restServerParams, "port", port);
        restServerParams.setName(CommonTestData.DISTRIBUTION_GROUP_NAME);
        final RestServer restServer = new RestServer(restServerParams, null, DistributionRestController.class);
        assertThatCode(() -> {
            restServer.start();
            final StatisticsReport report = getDistributionStatistics();
            validateReport(report, 0, 500);
            restServer.shutdown();
            DistributionStatisticsManager.resetAllStatistics();
        }).doesNotThrowAnyException();
    }

    private Main startDistributionService() throws IOException {
        port = CommonTestData.makeConfigFile("parameters/DistributionConfigParameters.json");
        final String[] distributionConfigParameters = { "-c", CommonTestData.CONFIG_FILE };
        return new Main(distributionConfigParameters);
    }

    private void stopDistributionService(final Main main) throws PolicyDistributionException {
        main.shutdown();
    }

    private StatisticsReport getDistributionStatistics() throws InterruptedException, IOException {
        final ClientConfig clientConfig = new ClientConfig();

        final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("healthcheck", "zb!XztG34");
        clientConfig.register(feature);

        final Client client = ClientBuilder.newClient(clientConfig);
        final WebTarget webTarget = client.target("http://localhost:" + port + "/statistics");

        final Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        CommonTestData.awaitServer(port);
        return invocationBuilder.get(StatisticsReport.class);
    }

    private void updateDistributionStatistics() {
        DistributionStatisticsManager.updateTotalDistributionCount();
        DistributionStatisticsManager.updateDistributionSuccessCount();
        DistributionStatisticsManager.updateDistributionFailureCount();
        DistributionStatisticsManager.updateTotalDownloadCount();
        DistributionStatisticsManager.updateDownloadSuccessCount();
        DistributionStatisticsManager.updateDownloadFailureCount();
    }

    private void validateReport(final StatisticsReport report, final int count, final int code) {
        assertEquals(code, report.getCode());
        assertEquals(count, report.getTotalDistributionCount());
        assertEquals(count, report.getDistributionSuccessCount());
        assertEquals(count, report.getDistributionFailureCount());
        assertEquals(count, report.getTotalDownloadCount());
        assertEquals(count, report.getDownloadSuccessCount());
        assertEquals(count, report.getDownloadFailureCount());
    }
}

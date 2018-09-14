/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Test;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.parameters.CommonTestData;
import org.onap.policy.distribution.main.parameters.RestServerParameters;
import org.onap.policy.distribution.main.startstop.Main;
import org.onap.policy.distribution.reception.statistics.DistributionStatisticsManager;

/**
 * Class to perform unit test of {@link DistributionRestController}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestDistributionStatistics {

    private static final Logger LOGGER = FlexLogger.getLogger(TestDistributionStatistics.class);


    @Test
    public void testDistributionStatistics_200() throws PolicyDistributionException, InterruptedException {
        final Main main = startDistributionService();
        StatisticsReport report = getDistributionStatistics();

        validateReport(report, 0, 200);
        updateDistributionStatistics();
        report = getDistributionStatistics();
        validateReport(report, 1, 200);
        stopDistributionService(main);
        DistributionStatisticsManager.resetAllStatistics();
    }

    @Test
    public void testDistributionStatistics_500() throws InterruptedException {
        final RestServerParameters restServerParams = new CommonTestData().getRestServerParameters(false);
        restServerParams.setName(CommonTestData.DISTRIBUTION_GROUP_NAME);

        final DistributionRestServer restServer = new DistributionRestServer(restServerParams);
        restServer.start();
        final StatisticsReport report = getDistributionStatistics();

        validateReport(report, 0, 500);
        restServer.shutdown();
        DistributionStatisticsManager.resetAllStatistics();
    }


    private Main startDistributionService() {
        final String[] distributionConfigParameters =
            { "-c", "parameters/DistributionConfigParameters.json" };
        return new Main(distributionConfigParameters);
    }

    private void stopDistributionService(final Main main) throws PolicyDistributionException {
        main.shutdown();
    }

    private StatisticsReport getDistributionStatistics() throws InterruptedException {
        StatisticsReport response = null;
        final ClientConfig clientConfig = new ClientConfig();

        final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("healthcheck", "zb!XztG34");
        clientConfig.register(feature);

        final Client client = ClientBuilder.newClient(clientConfig);
        final WebTarget webTarget = client.target("http://localhost:6969/statistics");

        final Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        while (response == null) {
            try {
                response = invocationBuilder.get(StatisticsReport.class);
            } catch (final Exception exp) {
                LOGGER.info("the server is not started yet. We will retry again");
            }
        }
        return response;
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

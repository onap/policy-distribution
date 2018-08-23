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
import static org.junit.Assert.assertTrue;

import com.jayway.awaitility.Awaitility;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Test;
import org.onap.policy.common.endpoints.report.HealthCheckReport;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.parameters.CommonTestData;
import org.onap.policy.distribution.main.parameters.RestServerParameters;
import org.onap.policy.distribution.main.startstop.Main;

/**
 * Class to perform unit test of HealthCheckMonitor.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestDistributionRestServer {

    private static final String NOT_ALIVE = "not alive";
    private static final String ALIVE = "alive";
    private static final String SELF = "self";
    private static final String NAME = "Policy SSD";

    @Test
    public void testHealthCheckSuccess() throws PolicyDistributionException, InterruptedException {
        final String reportString = "Report [name=Policy SSD, url=self, healthy=true, code=200, message=alive]";
        final Main main = startDistributionService();
        final HealthCheckReport report = performHealthCheck();
        validateReport(NAME, SELF, true, 200, ALIVE, reportString, report);
        stopDistributionService(main);
    }

    @Test
    public void testHealthCheckFailure() throws InterruptedException {
        final String reportString = "Report [name=Policy SSD, url=self, healthy=false, code=500, message=not alive]";
        final RestServerParameters restServerParams = new CommonTestData().getRestServerParameters(false);
        restServerParams.setName(CommonTestData.DISTRIBUTION_GROUP_NAME);
        final DistributionRestServer restServer = new DistributionRestServer(restServerParams);
        restServer.start();
        final HealthCheckReport report = performHealthCheck();
        validateReport(NAME, SELF, false, 500, NOT_ALIVE, reportString, report);
        assertTrue(restServer.isAlive());
        assertTrue(restServer.toString().startsWith("DistributionRestServer [servers="));
        restServer.shutdown();
    }

    private Main startDistributionService() {
        final String[] distributionConfigParameters = { "-c", "parameters/DistributionConfigParameters.json" };
        return new Main(distributionConfigParameters);
    }

    private void stopDistributionService(final Main main) throws PolicyDistributionException {
        main.shutdown();
    }

    private HealthCheckReport performHealthCheck() throws InterruptedException {
        HealthCheckReport response;
        final ClientConfig clientConfig = new ClientConfig();

        final HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("healthcheck", "zb!XztG34");
        clientConfig.register(feature);

        final Client client = ClientBuilder.newClient(clientConfig);
        final WebTarget webTarget = client.target("http://localhost:6969/healthcheck");

        final Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        try {
            response = invocationBuilder.get(HealthCheckReport.class);
        } catch (final Exception exp) {
            // may be the server is not started yet. Wait for couple of seconds and retry.
            Awaitility.await().atMost((5000), TimeUnit.MILLISECONDS)
                    .untilTrue(new TestDistributionRestServer().isServerReady(invocationBuilder));
            response = invocationBuilder.get(HealthCheckReport.class);
        }
        return response;
    }

    /**
     * @param invocationBuilder
     * @return
     */
    private AtomicBoolean isServerReady(final Invocation.Builder invocationBuilder) {
        return new AtomicBoolean(invocationBuilder.get(HealthCheckReport.class).getCode() == 200);
    }

    private void validateReport(final String name, final String url, final boolean healthy, final int code,
            final String message, final String reportString, final HealthCheckReport report) {
        assertEquals(name, report.getName());
        assertEquals(url, report.getUrl());
        assertEquals(healthy, report.isHealthy());
        assertEquals(code, report.getCode());
        assertEquals(message, report.getMessage());
        assertEquals(reportString, report.toString());
    }
}

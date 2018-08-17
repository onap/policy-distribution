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

package org.onap.policy.distribution.main.healthcheck;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.onap.policy.common.capabilities.Startable;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.distribution.main.startstop.DistributionActivator;

/**
 * Class to monitor health check of distribution service.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class HealthCheckMonitor implements Startable {

    private static final String NOT_ALIVE = "not alive";
    private static final String ALIVE = "alive";
    private static final String URL = "self";
    private static final String NAME = "Policy SSD";

    private static final Logger LOGGER = FlexLogger.getLogger(HealthCheckMonitor.class);

    public static HealthCheckMonitor healthCheckMonitor = new HealthCheckMonitor();
    private List<HttpServletServer> servers = new ArrayList<>();

    private HealthCheckMonitor() {}

    /**
     * Returns the health check report.
     *
     * @return Report containing health check status
     */
    public HealthCheckReport healthCheck() {
        final HealthCheckReport report = new HealthCheckReport();
        report.setName(NAME);
        report.setUrl(URL);
        report.setHealthy(DistributionActivator.isAlive());
        report.setCode(DistributionActivator.isAlive() ? 200 : 500);
        report.setMessage(DistributionActivator.isAlive() ? ALIVE : NOT_ALIVE);
        return report;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean start() {
        final Properties props = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = HealthCheckMonitor.class.getClassLoader().getResourceAsStream("healthcheck.properties");
            props.load(inputStream);
            this.servers = HttpServletServer.factory.build(props);
            for (final HttpServletServer server : servers) {
                startServer(server);
            }
        } catch (final Exception exp) {
            LOGGER.error("Failed to start health check monitoring service", exp);
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean stop() {
        for (final HttpServletServer server : servers) {
            try {
                server.stop();
            } catch (final Exception exp) {
                LOGGER.error("Failed to stop health check monitoring service", exp);
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        this.stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAlive() {
        return !servers.isEmpty();
    }

    /**
     * Starts the health check monitor service.
     *
     * @param server the http server
     */
    public void startServer(final HttpServletServer server) {
        try {
            server.start();
        } catch (final Exception exp) {
            LOGGER.error("Failed to start http server - " + server.toString(), exp);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("HealthCheckMonitor [servers=");
        builder.append(servers);
        builder.append("]");
        return builder.toString();
    }

}

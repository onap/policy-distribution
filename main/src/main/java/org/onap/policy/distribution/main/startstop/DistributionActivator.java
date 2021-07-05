/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2019-2020 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

package org.onap.policy.distribution.main.startstop;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.common.endpoints.http.server.RestServer;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.parameters.DistributionParameterGroup;
import org.onap.policy.distribution.main.parameters.PolicyForwarderConfigurationParameterGroup;
import org.onap.policy.distribution.main.rest.DistributionRestController;
import org.onap.policy.distribution.main.rest.aaf.AafDistributionFilter;
import org.onap.policy.distribution.reception.decoding.PluginInitializationException;
import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class wraps a distributor so that it can be activated as a complete service together with all its distribution
 * and forwarding handlers.
 */
public class DistributionActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributionActivator.class);

    // The parameters of this policy distribution activator
    @Getter
    private final DistributionParameterGroup distributionParameterGroup;

    // The map of reception handlers initialized by this distribution activator
    private final Map<String, AbstractReceptionHandler> receptionHandlersMap = new HashMap<>();

    @Getter
    @Setter
    private static boolean alive = false;

    private RestServer restServer;

    /**
     * Instantiate the activator for policy distribution as a complete service.
     *
     * @param distributionParameterGroup the parameters for the distribution service
     */
    public DistributionActivator(final DistributionParameterGroup distributionParameterGroup) {
        this.distributionParameterGroup = distributionParameterGroup;
    }

    /**
     * Initialize distribution as a complete service.
     *
     * @throws PolicyDistributionException on errors in initializing the service
     */
    @SuppressWarnings("unchecked")
    public void initialize() throws PolicyDistributionException {
        LOGGER.debug("Policy distribution starting as a service . . .");
        startDistributionRestServer();
        registerToParameterService(distributionParameterGroup);
        for (final ReceptionHandlerParameters receptionHandlerParameters : distributionParameterGroup
                .getReceptionHandlerParameters().values()) {
            try {
                final Class<AbstractReceptionHandler> receptionHandlerClass = (Class<AbstractReceptionHandler>) Class
                        .forName(receptionHandlerParameters.getReceptionHandlerClassName());
                final AbstractReceptionHandler receptionHandler =
                        receptionHandlerClass.getDeclaredConstructor().newInstance();
                receptionHandler.initialize(receptionHandlerParameters.getName());
                receptionHandlersMap.put(receptionHandlerParameters.getName(), receptionHandler);
                DistributionActivator.setAlive(true);
            } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException
                    | PluginInitializationException | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException exp) {
                throw new PolicyDistributionException(exp.getMessage(), exp);
            }
        }
        LOGGER.debug("Policy distribution started as a service");
    }

    /**
     * Starts the distribution rest server using configuration parameters.
     *
     * @throws PolicyDistributionException if server start fails
     */
    private void startDistributionRestServer() throws PolicyDistributionException {
        distributionParameterGroup.getRestServerParameters().setName(distributionParameterGroup.getName());
        restServer = new RestServer(distributionParameterGroup.getRestServerParameters(), AafDistributionFilter.class,
                        DistributionRestController.class);
        if (!restServer.start()) {
            throw new PolicyDistributionException(
                    "Failed to start distribution rest server. Check log for more details...");
        }
    }

    /**
     * Terminate policy distribution.
     *
     * @throws PolicyDistributionException on termination errors
     */
    public void terminate() throws PolicyDistributionException {
        try {
            for (final AbstractReceptionHandler handler : receptionHandlersMap.values()) {
                handler.destroy();
            }
            receptionHandlersMap.clear();
            deregisterToParameterService(distributionParameterGroup);
            DistributionActivator.setAlive(false);

            // Stop the distribution rest server
            restServer.stop();
        } catch (final Exception exp) {
            throw new PolicyDistributionException(exp.getMessage(), exp);
        }
    }

    /**
     * Method to register the parameters to Common Parameter Service.
     *
     * @param distributionParameterGroup the distribution parameter group
     */
    public void registerToParameterService(final DistributionParameterGroup distributionParameterGroup) {
        ParameterService.register(distributionParameterGroup);
        for (final ReceptionHandlerParameters params : distributionParameterGroup.getReceptionHandlerParameters()
                .values()) {
            params.setName(distributionParameterGroup.getName());
            params.getPluginHandlerParameters().setName(distributionParameterGroup.getName());
            ParameterService.register(params);
            ParameterService.register(params.getPluginHandlerParameters());
        }
        //@formatter:off
        for (final Entry<String, PolicyForwarderConfigurationParameterGroup> forwarderConfiguration
                : distributionParameterGroup.getPolicyForwarderConfigurationParameters().entrySet()) {
            forwarderConfiguration.getValue().setName(forwarderConfiguration.getKey());
            ParameterService.register(forwarderConfiguration.getValue());
        }
        for (final Entry<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfiguration
                : distributionParameterGroup.getReceptionHandlerConfigurationParameters().entrySet()) {
            receptionHandlerConfiguration.getValue().setName(receptionHandlerConfiguration.getKey());
            ParameterService.register(receptionHandlerConfiguration.getValue());
        }
        for (final Entry<String, PolicyDecoderConfigurationParameterGroup> decoderConfiguration
                : distributionParameterGroup.getPolicyDecoderConfigurationParameters().entrySet()) {
            decoderConfiguration.getValue().setName(decoderConfiguration.getKey());
            ParameterService.register(decoderConfiguration.getValue());
        }
        //@formatter:on
    }

    /**
     * Method to deregister the parameters from Common Parameter Service.
     *
     * @param distributionParameterGroup the distribution parameter group
     */
    public void deregisterToParameterService(final DistributionParameterGroup distributionParameterGroup) {
        ParameterService.deregister(distributionParameterGroup.getName());
        for (final ReceptionHandlerParameters params : distributionParameterGroup.getReceptionHandlerParameters()
                .values()) {
            ParameterService.deregister((params.getName()));
            ParameterService.deregister((params.getPluginHandlerParameters().getName()));
        }
        //@formatter:off
        for (final Entry<String, PolicyForwarderConfigurationParameterGroup> forwarderConfiguration
                : distributionParameterGroup.getPolicyForwarderConfigurationParameters().entrySet()) {
            forwarderConfiguration.getValue().setName(forwarderConfiguration.getKey());
            ParameterService.deregister(forwarderConfiguration.getKey());
        }
        for (final Entry<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfiguration
                : distributionParameterGroup.getReceptionHandlerConfigurationParameters().entrySet()) {
            receptionHandlerConfiguration.getValue().setName(receptionHandlerConfiguration.getKey());
            ParameterService.deregister(receptionHandlerConfiguration.getKey());
        }
        for (final Entry<String, PolicyDecoderConfigurationParameterGroup> decoderConfiguration
                : distributionParameterGroup.getPolicyDecoderConfigurationParameters().entrySet()) {
            decoderConfiguration.getValue().setName(decoderConfiguration.getKey());
            ParameterService.deregister(decoderConfiguration.getKey());
        }
        //@formatter:on
    }
}

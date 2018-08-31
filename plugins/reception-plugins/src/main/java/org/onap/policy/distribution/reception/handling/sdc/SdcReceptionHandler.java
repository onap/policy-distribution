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

package org.onap.policy.distribution.reception.handling.sdc;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.reception.decoding.PluginInitializationException;
import org.onap.policy.distribution.reception.decoding.PluginTerminationException;
import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;
import org.onap.policy.distribution.reception.parameters.PssdConfigurationParametersGroup;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientFactory;
import org.onap.sdc.utils.DistributionActionResultEnum;

/**
 * Handles reception of inputs from ONAP Service Design and Creation (SDC) from which policies may be decoded.
 */
public class SdcReceptionHandler extends AbstractReceptionHandler {

    private static final Logger LOGGER = FlexLogger.getLogger(SdcReceptionHandler.class);
    private SdcReceptionHandlerStatus sdcReceptionHandlerStatus = SdcReceptionHandlerStatus.STOPPED;
    private PssdConfigurationParametersGroup handlerParameters;
    private IDistributionClient distributionClient;
    private volatile int nbOfNotificationsOngoing = 0;

    @Override
    protected void initializeReception(final String parameterGroupName) throws PluginInitializationException {
        handlerParameters = (PssdConfigurationParametersGroup) ParameterService.get(parameterGroupName);
        initializeSdcClient();
        startSdcClient();
    }

    // Add functionality for receiving SDC distibutions and invoking AbstractReceptionHandler
    // inputReceived()

    @Override
    public void destroy() throws PluginTerminationException {
        LOGGER.debug("Going to stop the SDC Client...");
        if (distributionClient != null) {
            final IDistributionClientResult clientResult = distributionClient.stop();
            if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
                final String message =
                        "SDC client stop failed with reason:" + clientResult.getDistributionMessageResult();
                LOGGER.error(message);
                throw new PluginTerminationException(message);
            }
        }
        changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.STOPPED);
        LOGGER.debug("SDC Client is stopped successfully");
    }

    /**
     * Method to change the status of this reception handler instance.
     *
     * @param newStatus the new status
     */
    protected synchronized final void changeSdcReceptionHandlerStatus(final SdcReceptionHandlerStatus newStatus) {
        switch (newStatus) {
            case INIT:
            case STOPPED:
                sdcReceptionHandlerStatus = newStatus;
                break;
            case IDLE:
                if (nbOfNotificationsOngoing > 1) {
                    --nbOfNotificationsOngoing;
                } else {
                    nbOfNotificationsOngoing = 0;
                    sdcReceptionHandlerStatus = newStatus;
                }
                break;
            case BUSY:
                ++nbOfNotificationsOngoing;
                sdcReceptionHandlerStatus = newStatus;
                break;
        }
    }

    /**
     * Creates an instance of {@link IDistributionClient} from {@link DistributionClientFactory}.
     *
     * @return the {@link IDistributionClient} instance
     */
    protected IDistributionClient createSdcDistributionClient() {
        return DistributionClientFactory.createDistributionClient();
    }

    /**
     * Method to initialize the SDC client.
     *
     * @throws PluginInitializationException if the initialization of SDC Client fails
     */
    private void initializeSdcClient() throws PluginInitializationException {

        LOGGER.debug("Going to initialize the SDC Client...");
        if (sdcReceptionHandlerStatus != SdcReceptionHandlerStatus.STOPPED) {
            final String message = "The SDC Client is already initialized";
            LOGGER.error(message);
            throw new PluginInitializationException(message);
        }
        final SdcConfiguration pssdConfig = new SdcConfiguration(handlerParameters);
        distributionClient = createSdcDistributionClient();
        final IDistributionClientResult clientResult =
                distributionClient.init(pssdConfig, new SdcNotificationCallBack());
        if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.STOPPED);
            final String message =
                    "SDC client initialization failed with reason:" + clientResult.getDistributionMessageResult();
            LOGGER.error(message);
            throw new PluginInitializationException(message);
        }
        LOGGER.debug("SDC Client is initialized successfully");
        this.changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.INIT);
    }

    /**
     * Method to start the SDC client.
     *
     * @param configParameter the configuration parameters
     * @throws PluginInitializationException if the start of SDC Client fails
     */
    private void startSdcClient() throws PluginInitializationException {

        LOGGER.debug("Going to start the SDC Client...");
        final IDistributionClientResult clientResult = distributionClient.start();
        if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.STOPPED);
            final String message = "SDC client start failed with reason:" + clientResult.getDistributionMessageResult();
            LOGGER.error(message);
            throw new PluginInitializationException(message);
        }
        LOGGER.debug("SDC Client is started successfully");
        this.changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.IDLE);
    }
}

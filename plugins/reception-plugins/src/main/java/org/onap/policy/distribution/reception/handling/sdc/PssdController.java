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
import org.onap.policy.distribution.reception.handling.sdc.exceptions.PssdControllerException;
import org.onap.policy.distribution.reception.parameters.PssdConfigurationParametersGroup;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientFactory;
import org.onap.sdc.utils.DistributionActionResultEnum;

/**
 * Class to control the life cycle of SDC Client.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class PssdController {

    private static final Logger LOGGER = FlexLogger.getLogger(PssdController.class);

    private PssdControllerStatus controllerStatus = PssdControllerStatus.STOPPED;
    private PssdConfiguration pssdConfig;
    private IDistributionClient distributionClient;
    private IDistributionClientResult clientResult;
    private int nbOfNotificationsOngoing = 0;

    /**
     * Returns the count of notifications ongoing currently.
     *
     * @return the count
     */
    public int getNbOfNotificationsOngoing() {
        return nbOfNotificationsOngoing;
    }

    /**
     * Returns the current status of this controller instance.
     *
     * @return the current status
     */
    protected synchronized final PssdControllerStatus getControllerStatus() {
        return controllerStatus;
    }

    /**
     * Returns the distribution client associated with this controller instance.
     *
     * @return the distribution client
     */
    public IDistributionClient getDistributionClient() {
        return distributionClient;
    }

    /**
     * Method to change the status of this controller instance.
     *
     * @param newControllerStatus the new status
     */
    protected synchronized final void changePssdControllerStatus(final PssdControllerStatus newControllerStatus) {
        switch (newControllerStatus) {
            case INIT:
                controllerStatus = newControllerStatus;
                break;
            case IDLE:
                if (nbOfNotificationsOngoing > 1) {
                    --nbOfNotificationsOngoing;
                } else {
                    nbOfNotificationsOngoing = 0;
                    controllerStatus = newControllerStatus;
                }
                break;
            case BUSY:
                ++nbOfNotificationsOngoing;
                controllerStatus = newControllerStatus;
                break;
            case STOPPED:
                if (nbOfNotificationsOngoing == 0) {
                    controllerStatus = newControllerStatus;
                }
        }
    }

    /**
     * Returns an instance of {@link IDistributionClient} from {@link DistributionClientFactory}.
     *
     * @return the {@link IDistributionClient} instance
     */
    protected IDistributionClient getSdcDistributionClient() {
        distributionClient = DistributionClientFactory.createDistributionClient();
        return distributionClient;
    }

    /**
     * Method to initialize the SDC client.
     *
     * @param configParameter the configuration parameters
     * @throws PssdControllerException if the initialization of SDC Client fails
     */
    protected void initializeSdcClient(final PssdConfigurationParametersGroup configParameter)
            throws PssdControllerException {

        LOGGER.debug("Going to initialize the SDC Client...");
        if (getControllerStatus() != PssdControllerStatus.STOPPED) {
            final String message = "The SDC Client is already initialized";
            LOGGER.error(message);
            throw new PssdControllerException(message);
        }
        pssdConfig = new PssdConfiguration(configParameter);
        distributionClient = getSdcDistributionClient();
        clientResult = distributionClient.init(pssdConfig, new PssdNotificationCallBack());
        if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            pssdConfig = null;
            changePssdControllerStatus(PssdControllerStatus.STOPPED);
            final String message =
                    "SDC client initialization failed with reason:" + clientResult.getDistributionMessageResult();
            LOGGER.error(message);
            throw new PssdControllerException(message);
        }
        LOGGER.debug("SDC Client is initialized successfully");
        this.changePssdControllerStatus(PssdControllerStatus.INIT);
    }

    /**
     * Method to start the SDC client.
     *
     * @param configParameter the configuration parameters
     * @throws PssdControllerException if the start of SDC Client fails
     */
    public void startSdcClient() throws PssdControllerException {

        LOGGER.debug("Going to start the SDC Client...");
        if (getControllerStatus() != PssdControllerStatus.INIT) {
            final String message = "The SDC Client is already started";
            LOGGER.error(message);
            throw new PssdControllerException(message);
        }
        clientResult = distributionClient.start();
        if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            pssdConfig = null;
            changePssdControllerStatus(PssdControllerStatus.STOPPED);
            final String message = "SDC client start failed with reason:" + clientResult.getDistributionMessageResult();
            LOGGER.error(message);
            throw new PssdControllerException(message);
        }
        LOGGER.debug("SDC Client is started successfully");
        this.changePssdControllerStatus(PssdControllerStatus.IDLE);
    }

    /**
     * Method to stop the SDC Client.
     *
     * @throws PssdControllerException if the stop of SDC Client fails
     */
    public void stopSdcClient() throws PssdControllerException {

        LOGGER.debug("Going to stop the SDC Client...");
        if (getControllerStatus() == PssdControllerStatus.BUSY) {
            final String message = "Cannot stop SDC Client as PssdController status is BUSY";
            LOGGER.error(message);
            throw new PssdControllerException(message);
        }
        if (distributionClient != null) {
            clientResult = distributionClient.stop();
            if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
                final String message =
                        "SDC client stop failed with reason:" + clientResult.getDistributionMessageResult();
                LOGGER.error(message);
                throw new PssdControllerException(message);
            }
            pssdConfig = null;
            distributionClient = null;
        }
        changePssdControllerStatus(PssdControllerStatus.STOPPED);
        LOGGER.debug("SDC Client is stopped successfully");
    }
}

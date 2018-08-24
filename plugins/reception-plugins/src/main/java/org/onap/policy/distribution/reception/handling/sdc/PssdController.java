/*-
 * ============LICENSE_STARn=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.distribution.reception.handling.sdc.PssdConfiguration;
import org.onap.policy.distribution.reception.handling.sdc.PssdControllerStatus;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.PssdControllerException;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.PssdParametersException;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.ArtifactInstallerException;
import org.onap.policy.distribution.reception.parameters.PssdConfigurationParametersGroup;

import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientFactory;
import org.onap.sdc.utils.DistributionActionResultEnum;

/**
 * The Controller logic of Pssd, how to init/start/close
 *
 */
public class PssdController {
    private static final Logger LOGGER = FlexLogger.getLogger(PssdController.class);
    protected String controllerName;
    protected int nbOfNotificationsOngoing = 0;

    public int getNbOfNotificationsOngoing () {
        return nbOfNotificationsOngoing;
    }

    private PssdControllerStatus controllerStatus = PssdControllerStatus.STOPPED;

    protected synchronized final void changeControllerStatus (PssdControllerStatus newControllerStatus) {
        switch (newControllerStatus) {

            case BUSY:
                ++this.nbOfNotificationsOngoing;
                this.controllerStatus = newControllerStatus;
                break;

            case IDLE:
                if (this.nbOfNotificationsOngoing > 1) {
                    --this.nbOfNotificationsOngoing;
                } else {
                    this.nbOfNotificationsOngoing = 0;
                    this.controllerStatus = newControllerStatus;
                }

                break;
            default:
                this.controllerStatus = newControllerStatus;
                break;

        }
    }

    public synchronized final PssdControllerStatus getControllerStatus () {
        return this.controllerStatus;
    }

    // ***** END of Controller STATUS code
    protected PssdConfiguration pssdConfig;
    private IDistributionClient distributionClient;
    
    /**
     * Constructor
     * 
     * @param controllerName used to indicate the name of such controller
     */ 
    public PssdController (String controllerName) {
        this.controllerName = controllerName;
        distributionClient = null;
    }

    /**
     * Constructor
     * 
     * @param controllerName used to indicate the name of such controller
     * @param distributionClient mainly used for test. could be assigned from outside
     */ 
    public PssdController (String controllerName, IDistributionClient distributionClient) {
        this.controllerName = controllerName;
        this.distributionClient = distributionClient;
    }

    public IDistributionClient getDistributionClient () {
        return distributionClient;
    }

    /**
     * This method initializes the Pssd Controller and the Pssd Client.
     *
     * @throws PssdControllerException It throws an exception if the Pssd Client cannot be instantiated or if an init
     *         attempt is done when already initialized
     * @throws PssdParametersException If there is an issue with the parameters provided
     * @throws IOException In case of issues when trying to load the key file
     */
    public void initPssd (PssdConfigurationParametersGroup configParameter) throws PssdControllerException, PssdParametersException, IOException {
        String event = "Initialize the Pssd Controller";
        LOGGER.debug (event);
        if (this.getControllerStatus () != PssdControllerStatus.STOPPED) {
            String endEvent = "The controller is already initialized, call the closePssd method first";
            LOGGER.debug (endEvent);
            throw new PssdControllerException (endEvent);
        }
    
        if (configParameter.validate().isValid() != true){
            String endEvent = "The input configParameter is not valid, validate it before init Pssd";
            LOGGER.debug (endEvent);
            throw new PssdParametersException (endEvent);
        }
        LOGGER.debug ("the input configParameter is fine");

        if (pssdConfig == null) {
            pssdConfig = new PssdConfiguration (configParameter);
        }

        if (this.distributionClient == null) {
            distributionClient = DistributionClientFactory.createDistributionClient ();
        }
        long initStartTime = System.currentTimeMillis ();
        
        //Initialize the distribution client
        //- fetch the DMAAP server list from PssdConfig
        //- create keys in DMAAP
        //- register for topics 
        //- set the notification callback
        IDistributionClientResult result = this.distributionClient.init (pssdConfig,
                                                new PssdNotificationCallBack(this,pssdConfig, distributionClient));
        if (!result.getDistributionActionResult ().equals (DistributionActionResultEnum.SUCCESS)) {
            String endEvent = "Pssd distribution client init failed with reason:"
                              + result.getDistributionMessageResult ();
            pssdConfig = null;
            LOGGER.debug (endEvent);
            this.changeControllerStatus (PssdControllerStatus.STOPPED);
            throw new PssdControllerException ("Initialization of the Pssd Controller failed with reason: "
                                               + result.getDistributionMessageResult ());
        }

        LOGGER.info ("Pssd init successfully changeControllerStatus");
        long clientstartStartTime = System.currentTimeMillis ();
        
        //start polling notification topic
        result = this.distributionClient.start ();
        if (!result.getDistributionActionResult ().equals (DistributionActionResultEnum.SUCCESS)) {
            String endEvent = "Pssd distribution client start failed with reason:"
                              + result.getDistributionMessageResult ();
            pssdConfig = null;
            LOGGER.debug (endEvent);
            this.changeControllerStatus (PssdControllerStatus.STOPPED);
            throw new PssdControllerException ("Startup of the Pssd Controller failed with reason: "
                                               + result.getDistributionMessageResult ());
        }


        this.changeControllerStatus (PssdControllerStatus.IDLE);
        LOGGER.info ("Pssd start successfully change ControllerStatus to IDLE");
    }

    /**
     * This method closes the Pssd Controller and the Pssd Client.
     *
     * @throws PssdControllerException It throws an exception if the Pssd Client cannot be closed because
     *         it's currently BUSY in processing notifications.
     */
    public void closePssd () throws PssdControllerException {

        if (this.getControllerStatus () == PssdControllerStatus.BUSY) {
            throw new PssdControllerException ("Cannot close the Pssd controller as it's currently in BUSY state");
        }
        if (this.distributionClient != null) {
            //stop polling notification topic
            //unregister topics(via ASDC)
            this.distributionClient.stop ();
            // Next init will initialize it with a new Pssd Client
            this.distributionClient = null;
        }
        this.changeControllerStatus (PssdControllerStatus.STOPPED);
    }

    private static final String UNKNOWN="Unknown";

    /**
     * @return the address of the ASDC we are connected to.
     */
    public String getAddress () {
        if (pssdConfig != null) {
            return pssdConfig.getAsdcAddress ();
        }
        return UNKNOWN;
    }

    /**
     * @return the environment name of the ASDC we are connected to.
     */
    public String getEnvironment () {
        if (pssdConfig != null) {
            return pssdConfig.getEnvironmentName ();
        }
        return UNKNOWN;
    }
    
}

/*-
 * ============LICENSE_START=======================================================
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.PssdDownloadException;

import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.IFinalDistrStatusMessage;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.consumer.IStatusCallback;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IResourceInstance;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.DistributionStatusEnum;

/**
 * Notification callback to implement INotificationCallback 
 *
 */
public final class PssdNotificationCallBack implements INotificationCallback {
    private static final Logger LOGGER = FlexLogger.getLogger(PssdNotificationCallBack.class);

    private PssdController pssdController;
    private PssdConfiguration pssdConfig;
    private IDistributionClient distributionClient;

    /** 
     * Constructor
     * 
     * @param pssdController the controller of this notification call back 
     * @param pssdConfig  the configuraiton of this connection
     * @param distributionClient the distribution client which will do interactive with DMAAP/SDC
     */
    PssdNotificationCallBack(PssdController pssdController, PssdConfiguration pssdConfig, IDistributionClient distributionClient) {
        this.pssdController = pssdController;
        this.pssdConfig = pssdConfig;
        this.distributionClient = distributionClient;
    }

    /**
     * This method can be called multiple times at the same moment.
     * The controller must be thread safe !
     */
    @Override
    public void activateCallback(INotificationData iNotif) {
        String event = "Receive a callback notification in Pssd, nb of resources: " + iNotif.getResources().size();
        LOGGER.debug(event);
        treatNotification(iNotif);
    }

    /**
     * This function deal with the NotificationData.
     *
     */
    public void treatNotification(INotificationData iNotif) {

        int noOfArtifacts = 0;
        for (IResourceInstance resource : iNotif.getResources()) {
            noOfArtifacts += resource.getArtifacts().size();
        }
        LOGGER.info (String.valueOf(noOfArtifacts)+ iNotif.getServiceUUID()+ "Pssd treatNotification");

        try {
            pssdController.changeControllerStatus(PssdControllerStatus.BUSY);

            for (IResourceInstance resource : iNotif.getResources()) {
                // We process only VNF(VF) and Network(VL) resources on Policy Side
                if ("VF".equals(resource.getResourceType()) || "VL".equals(resource.getResourceType()) ){
                    this.processResourceNotification(iNotif,resource);
                }

            }

            //Handle services without any resources
            if (iNotif.getResources() == null || iNotif.getResources().size() < 1){

                LOGGER.info ("No resources found in Pssd treatNotification");
            }
        
            // Process only the interested artifact like TOSCA_CAR
            // To add code  later
        } catch (Exception e) {
            
            sendFinalDistributionStatus(iNotif.getDistributionID(), DistributionStatusEnum.DISTRIBUTION_COMPLETE_ERROR, 
                e.getMessage());
            
        } finally {
            sendFinalDistributionStatus(iNotif.getDistributionID(), DistributionStatusEnum.DISTRIBUTION_COMPLETE_OK,
                null);
            pssdController.changeControllerStatus (PssdControllerStatus.IDLE);
        }
    }

    /**
     * Build and publish Distribution Final Status event to Distribution
     * status Topics w/ w/o errorReason
     *
     * @param distributionID published in the distribution notification
     * @param status the message
     * @param errorReason null or concrete error reason
     */
    private void sendFinalDistributionStatus (
            String distributionID,
            DistributionStatusEnum status,
            String errorReason) {

        long subStarttime = System.currentTimeMillis();
        try {
            
            
            IFinalDistrStatusMessage finalDistribution = new FinalDistributionStatusMessage(distributionID,status,
                                                               subStarttime, pssdConfig.getConsumerID());
            
            if(errorReason == null){
                this.distributionClient.sendFinalDistrStatus(finalDistribution);
            }else{
                this.distributionClient.sendFinalDistrStatus(finalDistribution, errorReason);
            }
            
 
        } catch (RuntimeException e) {
            // TODO: May be a list containing the unsent notification should be
            // kept
            LOGGER.debug ("Exception caught in sendFinalDistributionStatus " + e.getMessage());
        }
    }

 
    /**
     * Go through each Resource, download and deploy the artifacts if necessnary
     * 
     * @param iNotif the service notification
     * @param resource Resource representer
     */   
    private void processResourceNotification (INotificationData iNotif,IResourceInstance resource) {

        return;
    }
}


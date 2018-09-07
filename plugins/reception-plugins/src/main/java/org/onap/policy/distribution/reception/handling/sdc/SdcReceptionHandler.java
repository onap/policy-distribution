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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PluginInitializationException;
import org.onap.policy.distribution.reception.decoding.PluginTerminationException;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.ArtifactDownloadException;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.IComponentDoneStatusMessage;
import org.onap.sdc.api.consumer.IDistributionStatusMessage;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.DistributionClientFactory;
import org.onap.sdc.utils.DistributionActionResultEnum;
import org.onap.sdc.utils.DistributionStatusEnum;

/**
 * Handles reception of inputs from ONAP Service Design and Creation (SDC) from which policies may be decoded.
 */
public class SdcReceptionHandler extends AbstractReceptionHandler implements INotificationCallback {

    private static final Logger LOGGER = FlexLogger.getLogger(SdcReceptionHandler.class);

    private SdcReceptionHandlerStatus sdcReceptionHandlerStatus = SdcReceptionHandlerStatus.STOPPED;
    private SdcReceptionHandlerConfigurationParameterGroup handlerParameters;
    private IDistributionClient distributionClient;
    private SdcConfiguration sdcConfig;
    private volatile int nbOfNotificationsOngoing = 0;

    private enum DistributionStatusType {
        DOWNLOAD, DEPLOY
    }

    @Override
    protected void initializeReception(final String parameterGroupName) throws PluginInitializationException {
        handlerParameters = (SdcReceptionHandlerConfigurationParameterGroup) ParameterService.get(parameterGroupName);
        initializeSdcClient();
        startSdcClient();
    }

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

    @Override
    public void activateCallback(final INotificationData notificationData) {
        LOGGER.debug("Receieved the notification from SDC with ID: " + notificationData.getDistributionID());
        changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.BUSY);
        processCsarServiceArtifacts(notificationData);
        changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.IDLE);
        LOGGER.debug("Processed the notification from SDC with ID: " + notificationData.getDistributionID());
    }

    /**
     * Method to change the status of this reception handler instance.
     *
     * @param newStatus the new status
     */
    private final synchronized void changeSdcReceptionHandlerStatus(final SdcReceptionHandlerStatus newStatus) {
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

        LOGGER.debug("Initializing the SDC Client...");
        if (sdcReceptionHandlerStatus != SdcReceptionHandlerStatus.STOPPED) {
            final String message = "The SDC Client is already initialized";
            LOGGER.error(message);
            throw new PluginInitializationException(message);
        }
        sdcConfig = new SdcConfiguration(handlerParameters);
        distributionClient = createSdcDistributionClient();
        final IDistributionClientResult clientResult = distributionClient.init(sdcConfig, this);
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

    /**
     * Method to process csar service artifacts from incoming SDC notification.
     *
     * @param notificationData the notification from SDC
     */
    public void processCsarServiceArtifacts(final INotificationData notificationData) {
        boolean artifactsProcessedSuccessfully = true;

        for (final IArtifactInfo artifact : notificationData.getServiceArtifacts()) {
            try {
                final IDistributionClientDownloadResult resultArtifact =
                        downloadTheArtifact(artifact, notificationData);
                final Path filePath = writeArtifactToFile(artifact, resultArtifact);
                final Csar csarObject = new Csar(filePath.toString());
                inputReceived(csarObject);
                sendDistributionStatus(DistributionStatusType.DEPLOY, artifact.getArtifactURL(),
                        sdcConfig.getConsumerID(), notificationData.getDistributionID(),
                        DistributionStatusEnum.DEPLOY_OK, null);
                deleteArtifactFile(filePath);
            } catch (final ArtifactDownloadException | PolicyDecodingException exp) {
                LOGGER.error("Failed to process csar service artifacts ", exp);
                artifactsProcessedSuccessfully = false;
                sendDistributionStatus(DistributionStatusType.DEPLOY, artifact.getArtifactURL(),
                        sdcConfig.getConsumerID(), notificationData.getDistributionID(),
                        DistributionStatusEnum.DEPLOY_ERROR,
                        "Failed to deploy the artifact due to: " + exp.getMessage());
            }
        }
        if (artifactsProcessedSuccessfully) {
            sendComponentDoneStatus(notificationData.getDistributionID(), DistributionStatusEnum.COMPONENT_DONE_OK,
                    null);
        } else {
            sendComponentDoneStatus(notificationData.getDistributionID(), DistributionStatusEnum.COMPONENT_DONE_ERROR,
                    "Failed to process the artifact");
        }
    }

    /**
     * Method to download the distribution artifact.
     *
     * @param artifact the artifact
     * @return the download result
     * @throws ArtifactDownloadException if download fails
     */
    private IDistributionClientDownloadResult downloadTheArtifact(final IArtifactInfo artifact,
            final INotificationData notificationData) throws ArtifactDownloadException {

        final IDistributionClientDownloadResult downloadResult = distributionClient.download(artifact);
        if (!downloadResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            final String message = "Failed to download artifact with name: " + artifact.getArtifactName() + " due to: "
                    + downloadResult.getDistributionMessageResult();
            LOGGER.error(message);
            sendDistributionStatus(DistributionStatusType.DOWNLOAD, artifact.getArtifactURL(),
                    sdcConfig.getConsumerID(), notificationData.getDistributionID(),
                    DistributionStatusEnum.DOWNLOAD_ERROR, message);
            throw new ArtifactDownloadException(message);
        }
        sendDistributionStatus(DistributionStatusType.DOWNLOAD, artifact.getArtifactURL(), sdcConfig.getConsumerID(),
                notificationData.getDistributionID(), DistributionStatusEnum.DOWNLOAD_OK, null);
        return downloadResult;
    }

    /**
     * Method to write the downloaded distribution artifact to local file system.
     *
     * @param artifact the notification artifact
     * @param resultArtifact the download result artifact
     * @return the local path of written file
     * @throws ArtifactDownloadException if error occurs while writing the artifact
     */
    private Path writeArtifactToFile(final IArtifactInfo artifact,
            final IDistributionClientDownloadResult resultArtifact) throws ArtifactDownloadException {
        try {
            final byte[] payloadBytes = resultArtifact.getArtifactPayload();
            final File tempArtifactFile = File.createTempFile(artifact.getArtifactName(), null);
            try (FileOutputStream fileOutputStream = new FileOutputStream(tempArtifactFile)) {
                fileOutputStream.write(payloadBytes, 0, payloadBytes.length);
                return tempArtifactFile.toPath();
            }
        } catch (final Exception exp) {
            final String message = "Failed to write artifact to local repository";
            LOGGER.error(message, exp);
            throw new ArtifactDownloadException(message, exp);
        }
    }

    /**
     * Method to delete the downloaded notification artifact from local file system.
     *
     * @param filePath the path of file
     */
    private void deleteArtifactFile(final Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (final IOException exp) {
            LOGGER.error("Failed to delete the downloaded artifact file", exp);
        }
    }

    /**
     * Sends the distribution status to SDC using the input values.
     *
     * @param statusType the status type
     * @param artifactUrl the artifact url
     * @param consumerId the consumer id
     * @param distributionId the distribution id
     * @param status the status
     * @param errorReason the error reason
     */
    private void sendDistributionStatus(final DistributionStatusType statusType, final String artifactUrl,
            final String consumerId, final String distributionId, final DistributionStatusEnum status,
            final String errorReason) {

        IDistributionClientResult clientResult = null;
        final IDistributionStatusMessage message = new DistributionStatusMessage(artifactUrl, consumerId,
                distributionId, status, System.currentTimeMillis());
        switch (statusType) {
            case DOWNLOAD:
                if (errorReason != null) {
                    clientResult = distributionClient.sendDownloadStatus(message, errorReason);
                } else {
                    clientResult = distributionClient.sendDownloadStatus(message);
                }
                break;
            case DEPLOY:
                if (errorReason != null) {
                    clientResult = distributionClient.sendDeploymentStatus(message, errorReason);
                } else {
                    clientResult = distributionClient.sendDeploymentStatus(message);
                }
        }

        final StringBuilder loggerMessage = new StringBuilder();
        loggerMessage.append("distribution status to SDC with values - ").append("DistributionId")
                .append(distributionId).append(" Artifact: ").append(artifactUrl).append(" StatusType: ")
                .append(statusType.name()).append(" Status: ").append(status.name());
        if (errorReason != null) {
            loggerMessage.append(" ErrorReason: ").append(errorReason);
        }
        if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            loggerMessage.insert(0, "Failed sending ");
            LOGGER.debug(loggerMessage);
        } else {
            loggerMessage.insert(0, "Successfully Sent ");
            LOGGER.debug(loggerMessage);
        }
    }

    /**
     * Sends the component done status to SDC using the input values.
     *
     * @param distributionId the distribution Id
     * @param status the distribution status
     * @param errorReason the error reason
     */
    private void sendComponentDoneStatus(final String distributionId, final DistributionStatusEnum status,
            final String errorReason) {
        IDistributionClientResult clientResult = null;
        final IComponentDoneStatusMessage finalDistributionStatusMessage = new ComponentDoneStatusMessage(
                distributionId, status, System.currentTimeMillis(), sdcConfig.getConsumerID());
        if (errorReason == null) {
            clientResult = distributionClient.sendComponentDoneStatus(finalDistributionStatusMessage);
        } else {
            clientResult = distributionClient.sendComponentDoneStatus(finalDistributionStatusMessage, errorReason);
        }

        final StringBuilder loggerMessage = new StringBuilder();
        loggerMessage.append("component done status to SDC with values - ").append("DistributionId")
                .append(distributionId).append(" Status: ").append(status.name());
        if (errorReason != null) {
            loggerMessage.append(" ErrorReason: ").append(errorReason);
        }
        if (!clientResult.getDistributionActionResult().equals(DistributionActionResultEnum.SUCCESS)) {
            loggerMessage.insert(0, "Failed sending ");
            LOGGER.debug(loggerMessage);
        } else {
            loggerMessage.insert(0, "Successfully Sent ");
            LOGGER.debug(loggerMessage);
        }
    }
}

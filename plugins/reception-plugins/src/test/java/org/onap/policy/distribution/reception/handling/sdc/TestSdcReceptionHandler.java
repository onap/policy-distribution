/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
 *  Copyright (C) 2019, 2022 Nordix Foundation.
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

package org.onap.policy.distribution.reception.handling.sdc;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static  org.mockito.Mockito.any;
import static  org.mockito.Mockito.mock;
import static  org.mockito.Mockito.spy;
import static  org.mockito.Mockito.when;

import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PluginInitializationException;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;
import org.onap.policy.distribution.reception.handling.PluginHandler;
import org.onap.policy.distribution.reception.parameters.PluginHandlerParameters;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderParameters;
import org.onap.policy.distribution.reception.statistics.DistributionStatisticsManager;
import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.utils.DistributionActionResultEnum;

/**
 * Class to perform unit test of {@link SdcReceptionHandler}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
class TestSdcReceptionHandler {

    private static final String DUMMY_SERVICE_CSAR = "dummyService.csar";

    private final IDistributionClientResult successfulClientInitResult = mock(IDistributionClientResult.class);
    private final IDistributionClientResult failureClientInitResult = mock(IDistributionClientResult.class);
    private final IDistributionClient distributionClient = mock(IDistributionClient.class);
    private final IDistributionClientDownloadResult successfulClientDownloadResult
        = mock(IDistributionClientDownloadResult.class);
    private final INotificationData notificationData = mock(INotificationData.class);
    private final IArtifactInfo artifactInfo = mock(IArtifactInfo.class);

    private SdcReceptionHandlerConfigurationParameterGroup pssdConfigParameters;
    private SdcReceptionHandler sypHandler;


    /**
     * Setup for the test cases.
     *
     * @throws IOException              if it occurs
     * @throws SecurityException        if it occurs
     * @throws IllegalArgumentException if it occurs
     */
    @BeforeEach
    final void init() throws IOException, SecurityException, IllegalArgumentException {
        DistributionStatisticsManager.resetAllStatistics();
        final var gson = new GsonBuilder().create();
        pssdConfigParameters = gson.fromJson(new FileReader("src/test/resources/handling-sdc.json"),
            SdcReceptionHandlerConfigurationParameterGroup.class);
        ParameterService.register(pssdConfigParameters);
        final var sdcHandler = new SdcReceptionHandler();
        sypHandler = spy(sdcHandler);

        when(sypHandler.createSdcDistributionClient()).thenReturn(distributionClient);
        when(distributionClient.init(any(), any())).thenReturn(successfulClientInitResult);
        when(distributionClient.start()).thenReturn(successfulClientInitResult);
        when(distributionClient.stop()).thenReturn(successfulClientInitResult);
        when(distributionClient.sendComponentDoneStatus(any())).thenReturn(successfulClientInitResult);
        when(distributionClient.sendComponentDoneStatus(any(), any())).thenReturn(successfulClientInitResult);
        when(distributionClient.sendDownloadStatus(any())).thenReturn(successfulClientInitResult);
        when(distributionClient.sendDownloadStatus(any(), any())).thenReturn(successfulClientInitResult);
        when(distributionClient.sendDeploymentStatus(any())).thenReturn(successfulClientInitResult);
        when(distributionClient.sendDeploymentStatus(any(), any())).thenReturn(successfulClientInitResult);
        when(distributionClient.download(any())).thenReturn(successfulClientDownloadResult);
        when(notificationData.getServiceArtifacts()).thenReturn(List.of(artifactInfo));
        when(artifactInfo.getArtifactName()).thenReturn(DUMMY_SERVICE_CSAR);
        when(successfulClientDownloadResult.getArtifactPayload()).thenReturn(new byte[1]);
        when(successfulClientInitResult.getDistributionActionResult())
            .thenReturn(DistributionActionResultEnum.SUCCESS);
        when(successfulClientDownloadResult.getDistributionActionResult())
            .thenReturn(DistributionActionResultEnum.SUCCESS);
        when(failureClientInitResult.getDistributionActionResult())
            .thenReturn(DistributionActionResultEnum.FAIL);

    }

    @AfterEach
    void teardown() {
        ParameterService.deregister(pssdConfigParameters);
    }

    @Test
    final void testInitializeSdcClient() {
        assertThatCode(() -> sypHandler.initializeReception(pssdConfigParameters.getName()))
            .doesNotThrowAnyException();
    }

    @Test
    final void testInitializeSdcClient_Failure() {

        when(successfulClientInitResult.getDistributionActionResult())
            .thenReturn(DistributionActionResultEnum.FAIL).thenReturn(DistributionActionResultEnum.SUCCESS);
        assertThatCode(() -> sypHandler.initializeReception(pssdConfigParameters.getName()))
            .doesNotThrowAnyException();
    }

    @Test
    final void testStartSdcClient_Failure() {
        assertThatCode(() -> {
            when(distributionClient.start()).thenReturn(failureClientInitResult)
                .thenReturn(successfulClientInitResult);
            sypHandler.initializeReception(pssdConfigParameters.getName());
        }).doesNotThrowAnyException();
    }

    @Test
    final void testStopSdcClient() {
        assertThatCode(() -> {
            sypHandler.initializeReception(pssdConfigParameters.getName());
            sypHandler.destroy();
        }).doesNotThrowAnyException();
    }

    @Test
    final void testStopSdcClient_Failure() {
        sypHandler.initializeReception(pssdConfigParameters.getName());
        when(distributionClient.stop()).thenReturn(failureClientInitResult)
            .thenReturn(successfulClientInitResult);
        assertThatCode(() -> sypHandler.destroy()).doesNotThrowAnyException();
    }

    @Test
    final void testStopSdcClientWithoutStart() {
        assertThatCode(() -> sypHandler.destroy()).doesNotThrowAnyException();
    }

    @Test
    void testNotificationCallBack() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
        IllegalAccessException, PluginInitializationException {

        final var policyDecoder = new DummyDecoder();
        final Collection<PolicyDecoder<Csar, DummyPolicy>> policyDecoders = new ArrayList<>();
        policyDecoders.add(policyDecoder);

        final var policyForwarder = new DummyPolicyForwarder();
        final Collection<PolicyForwarder> policyForwarders = new ArrayList<>();
        policyForwarders.add(policyForwarder);

        setUpPlugins(sypHandler, policyDecoders, policyForwarders);
        sypHandler.initializeReception(pssdConfigParameters.getName());
        sypHandler.activateCallback(notificationData);

        assertTrue(policyDecoder.getDecodedPolicy().getName().contains(DUMMY_SERVICE_CSAR));
        assertEquals(1, policyForwarder.getNumberOfPoliciesReceived());
        assertTrue(policyForwarder.receivedPolicyWithGivenType(DUMMY_SERVICE_CSAR));
        assertEquals(1, DistributionStatisticsManager.getTotalDistributionCount());
        assertEquals(1, DistributionStatisticsManager.getDistributionSuccessCount());
        assertEquals(0, DistributionStatisticsManager.getDistributionFailureCount());
        assertEquals(1, DistributionStatisticsManager.getTotalDownloadCount());
        assertEquals(1, DistributionStatisticsManager.getDownloadSuccessCount());
        assertEquals(0, DistributionStatisticsManager.getDownloadFailureCount());
    }

    @Test
    void testDownloadArtifactFailure() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
        IllegalAccessException, PluginInitializationException {

        when(successfulClientDownloadResult.getDistributionActionResult())
            .thenReturn(DistributionActionResultEnum.FAIL);

        final var policyDecoder = new DummyDecoder();
        final Collection<PolicyDecoder<Csar, DummyPolicy>> policyDecoders = new ArrayList<>();
        policyDecoders.add(policyDecoder);

        final var policyForwarder = new DummyPolicyForwarder();
        final Collection<PolicyForwarder> policyForwarders = new ArrayList<>();
        policyForwarders.add(policyForwarder);

        setUpPlugins(sypHandler, policyDecoders, policyForwarders);
        sypHandler.initializeReception(pssdConfigParameters.getName());
        sypHandler.activateCallback(notificationData);

        assertNull(policyDecoder.getDecodedPolicy());
        assertEquals(0, policyForwarder.getNumberOfPoliciesReceived());
        assertEquals(1, DistributionStatisticsManager.getTotalDistributionCount());
        assertEquals(0, DistributionStatisticsManager.getDistributionSuccessCount());
        assertEquals(1, DistributionStatisticsManager.getDistributionFailureCount());
        assertEquals(1, DistributionStatisticsManager.getTotalDownloadCount());
        assertEquals(0, DistributionStatisticsManager.getDownloadSuccessCount());
        assertEquals(1, DistributionStatisticsManager.getDownloadFailureCount());
    }

    @Test
    void testSendDistributionStatusFailure() throws NoSuchFieldException, SecurityException,
        IllegalArgumentException, IllegalAccessException, PluginInitializationException {

        when(successfulClientDownloadResult.getDistributionActionResult())
            .thenReturn(DistributionActionResultEnum.FAIL);
        when(distributionClient.sendDownloadStatus(any(), any())).thenReturn(failureClientInitResult);
        when(distributionClient.sendDeploymentStatus(any(), any())).thenReturn(failureClientInitResult);
        when(distributionClient.sendComponentDoneStatus(any(), any())).thenReturn(failureClientInitResult);

        final var policyDecoder = new DummyDecoder();
        final Collection<PolicyDecoder<Csar, DummyPolicy>> policyDecoders = new ArrayList<>();
        policyDecoders.add(policyDecoder);

        final var policyForwarder = new DummyPolicyForwarder();
        final Collection<PolicyForwarder> policyForwarders = new ArrayList<>();
        policyForwarders.add(policyForwarder);

        setUpPlugins(sypHandler, policyDecoders, policyForwarders);
        sypHandler.initializeReception(pssdConfigParameters.getName());
        sypHandler.activateCallback(notificationData);

        assertNull(policyDecoder.getDecodedPolicy());
        assertEquals(0, policyForwarder.getNumberOfPoliciesReceived());
    }

    private void setUpPlugins(final AbstractReceptionHandler receptionHandler,
                              final Collection<PolicyDecoder<Csar, DummyPolicy>> decoders,
                              final Collection<PolicyForwarder> forwarders)
        throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
        PluginInitializationException {
        final var pluginParameters = getPluginHandlerParameters();
        pluginParameters.setName("DummyDistributionGroup");
        ParameterService.register(pluginParameters);
        final var pluginHandler = new PluginHandler(pluginParameters.getName());

        final var decodersField = pluginHandler.getClass().getDeclaredField("policyDecoders");
        decodersField.setAccessible(true);
        decodersField.set(pluginHandler, decoders);

        final var forwardersField = pluginHandler.getClass().getDeclaredField("policyForwarders");
        forwardersField.setAccessible(true);
        forwardersField.set(pluginHandler, forwarders);

        final var pluginHandlerField = AbstractReceptionHandler.class.getDeclaredField("pluginHandler");
        pluginHandlerField.setAccessible(true);
        pluginHandlerField.set(receptionHandler, pluginHandler);
        ParameterService.deregister(pluginParameters.getName());
    }

    private PluginHandlerParameters getPluginHandlerParameters() {
        final var policyDecoders = getPolicyDecoders();
        final var policyForwarders = getPolicyForwarders();
        return new PluginHandlerParameters(policyDecoders, policyForwarders);
    }

    private Map<String, PolicyDecoderParameters> getPolicyDecoders() {
        final Map<String, PolicyDecoderParameters> policyDecoders = new HashMap<>();
        final var pDParameters = new PolicyDecoderParameters("DummyDecoder",
            "org.onap.policy.distribution.reception.handling.sdc.DummyDecoder", "DummyDecoderConfiguration");
        policyDecoders.put("DummyDecoderKey", pDParameters);
        return policyDecoders;
    }

    private Map<String, PolicyForwarderParameters> getPolicyForwarders() {
        final Map<String, PolicyForwarderParameters> policyForwarders = new HashMap<>();
        final var pFParameters = new PolicyForwarderParameters("DummyForwarder",
            "org.onap.policy.distribution.reception.handling.sdc.DummyPolicyForwarder", "DummyConfiguration");
        policyForwarders.put("DummyForwarderKey", pFParameters);
        return policyForwarders;
    }
}

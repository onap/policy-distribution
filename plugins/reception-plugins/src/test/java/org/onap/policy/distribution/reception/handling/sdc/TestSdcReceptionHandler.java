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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
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
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.mock.DistributionClientStubImpl;
import org.onap.sdc.utils.DistributionActionResultEnum;

/**
 * Class to perform unit test of {@link SdcReceptionHandler}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class TestSdcReceptionHandler {

    private static final Logger LOGGER = FlexLogger.getLogger(TestSdcReceptionHandler.class);
    private static final String DUMMY_SERVICE_CSAR = "dummyService.csar";

    @Mock
    private IDistributionClientResult successfulClientInitResult;
    @Mock
    private IDistributionClientResult failureClientInitResult;
    @Mock
    private DistributionClientStubImpl distributionClient;
    @Mock
    private IDistributionClientDownloadResult successfulClientDownloadResult;
    @Mock
    private INotificationData notificationData;
    @Mock
    private IArtifactInfo artifactInfo;

    private SdcReceptionHandlerConfigurationParameterGroup pssdConfigParameters;
    private SdcReceptionHandler sypHandler;


    /**
     * Setup for the test cases.
     *
     * @throws IOException if it occurs
     */
    @Before
    public final void init() throws IOException {
        DistributionStatisticsManager.resetAllStatistics();
        final Gson gson = new GsonBuilder().create();
        pssdConfigParameters = gson.fromJson(new FileReader("src/test/resources/handling-sdc.json"),
                SdcReceptionHandlerConfigurationParameterGroup.class);
        ParameterService.register(pssdConfigParameters);
        final SdcReceptionHandler sdcHandler = new SdcReceptionHandler();
        sypHandler = Mockito.spy(sdcHandler);

        Mockito.when(sypHandler.createSdcDistributionClient()).thenReturn(distributionClient);
        Mockito.when(distributionClient.init(any(), any())).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.start()).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.stop()).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.sendComponentDoneStatus(any())).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.sendComponentDoneStatus(any(), any())).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.sendDownloadStatus(any())).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.sendDownloadStatus(any(), any())).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.sendDeploymentStatus(any())).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.sendDeploymentStatus(any(), any())).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.download(any())).thenReturn(successfulClientDownloadResult);
        Mockito.when(notificationData.getServiceArtifacts()).thenReturn(Arrays.asList(artifactInfo));
        Mockito.when(artifactInfo.getArtifactName()).thenReturn(DUMMY_SERVICE_CSAR);
        Mockito.when(successfulClientDownloadResult.getArtifactPayload()).thenReturn(new byte[1]);
        Mockito.when(successfulClientInitResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.SUCCESS);
        Mockito.when(successfulClientDownloadResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.SUCCESS);
        Mockito.when(failureClientInitResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.FAIL);

    }

    @After
    public void teardown() {
        ParameterService.deregister(pssdConfigParameters);
    }

    @Test
    public final void testInitializeSdcClient() {
        try {
            sypHandler.initializeReception(pssdConfigParameters.getName());
            verify(distributionClient, times(1)).init(anyObject(), anyObject());
            verify(distributionClient, times(1)).start();
        } catch (final PluginInitializationException exp) {
            LOGGER.error(exp);
            fail("Test should not throw any exception");
        }
    }

    @Test
    public final void testInitializeSdcClient_Again() throws PluginInitializationException {
        sypHandler.initializeReception(pssdConfigParameters.getName());
        try {
            sypHandler.initializeReception(pssdConfigParameters.getName());
            fail("Test must throw an exception here");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("The SDC Client is already initialized"));
        }
    }

    @Test
    public final void testInitializeSdcClient_Failure() throws PluginInitializationException {

        Mockito.when(successfulClientInitResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.FAIL).thenReturn(DistributionActionResultEnum.SUCCESS);
        try {
            sypHandler.initializeReception(pssdConfigParameters.getName());
            verify(distributionClient, times(2)).init(anyObject(), anyObject());
        } catch (final Exception exp) {
            LOGGER.error(exp);
            fail("Test should not throw any exception");
        }
    }

    @Test
    public final void testStartSdcClient_Failure() throws PluginInitializationException {
        try {
            Mockito.when(distributionClient.start()).thenReturn(failureClientInitResult)
                    .thenReturn(successfulClientInitResult);
            sypHandler.initializeReception(pssdConfigParameters.getName());
            verify(distributionClient, times(2)).start();
        } catch (final Exception exp) {
            LOGGER.error(exp);
            fail("Test should not throw any exception");
        }
    }

    @Test
    public final void testStopSdcClient() {
        try {
            sypHandler.initializeReception(pssdConfigParameters.getName());
            sypHandler.destroy();
            verify(distributionClient, times(1)).stop();
        } catch (final Exception exp) {
            LOGGER.error(exp);
            fail("Test should not throw any exception");
        }

    }

    @Test
    public final void testStopSdcClientWithoutStart() {
        try {
            sypHandler.destroy();
            verify(distributionClient, times(0)).stop();
        } catch (final Exception exp) {
            LOGGER.error(exp);
            fail("Test should not throw any exception");
        }

    }

    @Test
    public final void testStopSdcClient_Failure() throws PluginInitializationException {

        sypHandler.initializeReception(pssdConfigParameters.getName());
        Mockito.when(successfulClientInitResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.FAIL).thenReturn(DistributionActionResultEnum.SUCCESS);
        try {
            sypHandler.destroy();
            verify(distributionClient, times(2)).stop();
        } catch (final Exception exp) {
            LOGGER.error(exp);
            fail("Test should not throw any exception");
        }
    }

    @Test
    public void testNotificationCallBack() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException, PluginInitializationException {

        final DummyDecoder policyDecoder = new DummyDecoder();
        final Collection<PolicyDecoder<Csar, DummyPolicy>> policyDecoders = new ArrayList<>();
        policyDecoders.add(policyDecoder);

        final DummyPolicyForwarder policyForwarder = new DummyPolicyForwarder();
        final Collection<PolicyForwarder> policyForwarders = new ArrayList<>();
        policyForwarders.add(policyForwarder);

        setUpPlugins(sypHandler, policyDecoders, policyForwarders);
        sypHandler.initializeReception(pssdConfigParameters.getName());
        sypHandler.activateCallback(notificationData);

        assertEquals(DummyDecoder.DUMMY_POLICY, policyDecoder.getDecodedPolicy().getPolicyType());
        assertTrue(policyDecoder.getDecodedPolicy().getPolicyName().contains(DUMMY_SERVICE_CSAR));
        assertEquals(1, policyForwarder.getNumberOfPoliciesReceived());
        assertTrue(policyForwarder.receivedPolicyWithGivenType(DummyDecoder.DUMMY_POLICY));
        assertEquals(1, DistributionStatisticsManager.getTotalDistributionCount());
        assertEquals(1, DistributionStatisticsManager.getDistributionSuccessCount());
        assertEquals(0, DistributionStatisticsManager.getDistributionFailureCount());
        assertEquals(1, DistributionStatisticsManager.getTotalDownloadCount());
        assertEquals(1, DistributionStatisticsManager.getDownloadSuccessCount());
        assertEquals(0, DistributionStatisticsManager.getDownloadFailureCount());
    }

    @Test
    public void testDownloadArtifactFailure() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException, PluginInitializationException {

        Mockito.when(successfulClientDownloadResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.FAIL);

        final DummyDecoder policyDecoder = new DummyDecoder();
        final Collection<PolicyDecoder<Csar, DummyPolicy>> policyDecoders = new ArrayList<>();
        policyDecoders.add(policyDecoder);

        final DummyPolicyForwarder policyForwarder = new DummyPolicyForwarder();
        final Collection<PolicyForwarder> policyForwarders = new ArrayList<>();
        policyForwarders.add(policyForwarder);

        setUpPlugins(sypHandler, policyDecoders, policyForwarders);
        sypHandler.initializeReception(pssdConfigParameters.getName());
        sypHandler.activateCallback(notificationData);

        assertEquals(null, policyDecoder.getDecodedPolicy());
        assertEquals(0, policyForwarder.getNumberOfPoliciesReceived());
        assertEquals(1, DistributionStatisticsManager.getTotalDistributionCount());
        assertEquals(0, DistributionStatisticsManager.getDistributionSuccessCount());
        assertEquals(1, DistributionStatisticsManager.getDistributionFailureCount());
        assertEquals(1, DistributionStatisticsManager.getTotalDownloadCount());
        assertEquals(0, DistributionStatisticsManager.getDownloadSuccessCount());
        assertEquals(1, DistributionStatisticsManager.getDownloadFailureCount());
    }

    @Test
    public void testSendDistributionStatusFailure() throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, PluginInitializationException {

        Mockito.when(successfulClientDownloadResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.FAIL);
        Mockito.when(distributionClient.sendDownloadStatus(any(), any())).thenReturn(failureClientInitResult);
        Mockito.when(distributionClient.sendDeploymentStatus(any(), any())).thenReturn(failureClientInitResult);
        Mockito.when(distributionClient.sendComponentDoneStatus(any(), any())).thenReturn(failureClientInitResult);

        final DummyDecoder policyDecoder = new DummyDecoder();
        final Collection<PolicyDecoder<Csar, DummyPolicy>> policyDecoders = new ArrayList<>();
        policyDecoders.add(policyDecoder);

        final DummyPolicyForwarder policyForwarder = new DummyPolicyForwarder();
        final Collection<PolicyForwarder> policyForwarders = new ArrayList<>();
        policyForwarders.add(policyForwarder);

        setUpPlugins(sypHandler, policyDecoders, policyForwarders);
        sypHandler.initializeReception(pssdConfigParameters.getName());
        sypHandler.activateCallback(notificationData);

        assertEquals(null, policyDecoder.getDecodedPolicy());
        assertEquals(0, policyForwarder.getNumberOfPoliciesReceived());
    }

    private void setUpPlugins(final AbstractReceptionHandler receptionHandler,
            final Collection<PolicyDecoder<Csar, DummyPolicy>> decoders, final Collection<PolicyForwarder> forwarders)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException,
            PluginInitializationException {
        final PluginHandlerParameters pluginParameters = getPluginHandlerParameters();
        pluginParameters.setName("DummyDistributionGroup");
        ParameterService.register(pluginParameters);
        final PluginHandler pluginHandler = new PluginHandler(pluginParameters.getName());

        final Field decodersField = pluginHandler.getClass().getDeclaredField("policyDecoders");
        decodersField.setAccessible(true);
        decodersField.set(pluginHandler, decoders);

        final Field forwardersField = pluginHandler.getClass().getDeclaredField("policyForwarders");
        forwardersField.setAccessible(true);
        forwardersField.set(pluginHandler, forwarders);

        final Field pluginHandlerField = AbstractReceptionHandler.class.getDeclaredField("pluginHandler");
        pluginHandlerField.setAccessible(true);
        pluginHandlerField.set(receptionHandler, pluginHandler);
        ParameterService.deregister(pluginParameters.getName());
    }

    private PluginHandlerParameters getPluginHandlerParameters() {
        final Map<String, PolicyDecoderParameters> policyDecoders = getPolicyDecoders();
        final Map<String, PolicyForwarderParameters> policyForwarders = getPolicyForwarders();
        final PluginHandlerParameters pluginHandlerParameters =
                new PluginHandlerParameters(policyDecoders, policyForwarders);
        return pluginHandlerParameters;
    }

    private Map<String, PolicyDecoderParameters> getPolicyDecoders() {
        final Map<String, PolicyDecoderParameters> policyDecoders = new HashMap<String, PolicyDecoderParameters>();
        final PolicyDecoderParameters pDParameters = new PolicyDecoderParameters("DummyDecoder",
                "org.onap.policy.distribution.reception.handling.sdc.DummyDecoder", "DummyDecoderConfiguration");
        policyDecoders.put("DummyDecoderKey", pDParameters);
        return policyDecoders;
    }

    private Map<String, PolicyForwarderParameters> getPolicyForwarders() {
        final Map<String, PolicyForwarderParameters> policyForwarders =
                new HashMap<String, PolicyForwarderParameters>();
        final PolicyForwarderParameters pFParameters = new PolicyForwarderParameters("DummyForwarder",
                "org.onap.policy.distribution.reception.handling.sdc.DummyPolicyForwarder", "DummyConfiguration");
        policyForwarders.put("DummyForwarderKey", pFParameters);
        return policyForwarders;
    }
}

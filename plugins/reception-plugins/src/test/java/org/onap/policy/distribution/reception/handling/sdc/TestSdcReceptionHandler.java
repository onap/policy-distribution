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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.reception.decoding.PluginInitializationException;
import org.onap.policy.distribution.reception.parameters.PssdConfigurationParametersGroup;
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

    @Mock
    private IDistributionClientResult successfulClientInitResult;
    @Mock
    private IDistributionClientResult failureClientInitResult;
    @Mock
    private DistributionClientStubImpl distributionClient;

    private PssdConfigurationParametersGroup pssdConfigParameters;
    private SdcReceptionHandler sypHandler;

    /**
     * Setup for the test cases.
     *
     * @throws IOException if it occurs
     */
    @Before
    public final void init() throws IOException {
        final Gson gson = new GsonBuilder().create();
        pssdConfigParameters = gson.fromJson(new FileReader("src/test/resources/handling-sdc.json"),
                PssdConfigurationParametersGroup.class);
        ParameterService.register(pssdConfigParameters);
        final SdcReceptionHandler sdcHandler = new SdcReceptionHandler();
        sypHandler = Mockito.spy(sdcHandler);
        Mockito.when(sypHandler.getSdcDistributionClient()).thenReturn(distributionClient);
        Mockito.when(distributionClient.init(any(), any())).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.start()).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.stop()).thenReturn(successfulClientInitResult);
        Mockito.when(successfulClientInitResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.SUCCESS);
    }

    @After
    public void teardown() {
        ParameterService.deregister(pssdConfigParameters);
    }

    @Test
    public final void testInitializeSdcClient() throws PluginInitializationException {
        sypHandler.initializeReception(pssdConfigParameters.getName());
        assertEquals(SdcReceptionHandlerStatus.IDLE, sypHandler.getSDCReceptionHandlerStatus());
        assertEquals(0, sypHandler.getNbOfNotificationsOngoing());
        assertEquals(distributionClient, sypHandler.getDistributionClient());
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
                .thenReturn(DistributionActionResultEnum.FAIL);
        try {
            sypHandler.initializeReception(pssdConfigParameters.getName());
            fail("Test must throw an exception here");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("SDC client initialization failed with reason"));
        }
    }

    @Test
    public final void testStartSdcClient_Again() throws PluginInitializationException {
        sypHandler.initializeReception(pssdConfigParameters.getName());
        try {
            sypHandler.startSdcClient();
            fail("Test must throw an exception here");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("The SDC Client is already started"));
        }
    }

    @Test
    public final void testStartSdcClient_Failure() throws PluginInitializationException {

        sypHandler.initializeReception(pssdConfigParameters.getName());
        Mockito.when(successfulClientInitResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.FAIL);
        try {
            sypHandler.changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.INIT);
            sypHandler.startSdcClient();
            fail("Test must throw an exception here");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("SDC client start failed with reason"));
        }
    }

    @Test
    public final void testStopSdcClient() throws PluginInitializationException {
        sypHandler.initializeReception(pssdConfigParameters.getName());
        sypHandler.destroy();
        assertEquals(SdcReceptionHandlerStatus.STOPPED, sypHandler.getSDCReceptionHandlerStatus());
    }

    @Test
    public final void testStopSdcClient_Busy() throws PluginInitializationException {

        sypHandler.initializeReception(pssdConfigParameters.getName());
        sypHandler.changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.BUSY);
        assertEquals(1, sypHandler.getNbOfNotificationsOngoing());
        try {
            sypHandler.destroy();
            fail("Test must throw an exception here");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("Cannot stop SDC Client as SDC reception handler status is BUSY"));
        }
    }

    @Test
    public final void testStopSdcClient_Failure() throws PluginInitializationException {

        sypHandler.initializeReception(pssdConfigParameters.getName());
        Mockito.when(successfulClientInitResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.FAIL);
        try {
            sypHandler.destroy();
            fail("Test must throw an exception here");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("SDC client stop failed with reason"));
        }
    }

    @Test
    public final void testSdcReceptionHandlerStatus() throws PluginInitializationException {
        sypHandler.initializeReception(pssdConfigParameters.getName());
        assertEquals(SdcReceptionHandlerStatus.IDLE, sypHandler.getSDCReceptionHandlerStatus());
        assertEquals(0, sypHandler.getNbOfNotificationsOngoing());
        sypHandler.changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.BUSY);
        assertEquals(1, sypHandler.getNbOfNotificationsOngoing());
        sypHandler.changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.BUSY);
        assertEquals(2, sypHandler.getNbOfNotificationsOngoing());
        sypHandler.changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.STOPPED);
        assertEquals(2, sypHandler.getNbOfNotificationsOngoing());
        sypHandler.changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.IDLE);
        assertEquals(1, sypHandler.getNbOfNotificationsOngoing());
        sypHandler.changeSdcReceptionHandlerStatus(SdcReceptionHandlerStatus.IDLE);
        assertEquals(0, sypHandler.getNbOfNotificationsOngoing());
    }

}

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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.PssdControllerException;
import org.onap.policy.distribution.reception.parameters.PssdConfigurationParametersGroup;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.mock.DistributionClientStubImpl;
import org.onap.sdc.utils.DistributionActionResultEnum;

/**
 * Class to perform unit test of {@link PssdController}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class PssdControllerTest {

    @Mock
    private IDistributionClientResult successfulClientInitResult;
    @Mock
    private IDistributionClientResult failureClientInitResult;
    @Mock
    private DistributionClientStubImpl distributionClient;

    private PssdConfigurationParametersGroup pssdConfigParameters;
    private PssdController sypController;

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

        final PssdController pssdController = new PssdController();
        sypController = Mockito.spy(pssdController);
        Mockito.when(sypController.getSdcDistributionClient()).thenReturn(distributionClient);
        Mockito.when(distributionClient.init(any(), any())).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.start()).thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.stop()).thenReturn(successfulClientInitResult);
        Mockito.when(successfulClientInitResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.SUCCESS);
    }

    @Test
    public final void testInitializeSdcClient() throws PssdControllerException {
        sypController.initializeSdcClient(pssdConfigParameters);
        assertEquals(PssdControllerStatus.INIT, sypController.getControllerStatus());
        assertEquals(0, sypController.getNbOfNotificationsOngoing());
        assertEquals(distributionClient, sypController.getDistributionClient());
    }

    @Test
    public final void testInitializeSdcClient_Again() throws PssdControllerException {
        sypController.initializeSdcClient(pssdConfigParameters);
        try {
            sypController.initializeSdcClient(pssdConfigParameters);
            fail("Test must throw an exception here");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("The SDC Client is already initialized"));
        }
    }

    @Test
    public final void testInitializeSdcClient_Failure() throws PssdControllerException {

        Mockito.when(successfulClientInitResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.FAIL);
        try {
            sypController.initializeSdcClient(pssdConfigParameters);
            fail("Test must throw an exception here");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("SDC client initialization failed with reason"));
        }
    }

    @Test
    public final void testStartSdcClient() throws PssdControllerException {
        sypController.initializeSdcClient(pssdConfigParameters);
        sypController.startSdcClient();
        assertEquals(PssdControllerStatus.IDLE, sypController.getControllerStatus());
        assertEquals(0, sypController.getNbOfNotificationsOngoing());
    }

    @Test
    public final void testStartSdcClient_Again() throws PssdControllerException {
        sypController.initializeSdcClient(pssdConfigParameters);
        sypController.startSdcClient();
        try {
            sypController.startSdcClient();
            fail("Test must throw an exception here");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("The SDC Client is already started"));
        }
    }

    @Test
    public final void testStartSdcClient_Failure() throws PssdControllerException {

        sypController.initializeSdcClient(pssdConfigParameters);
        Mockito.when(successfulClientInitResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.FAIL);
        try {
            sypController.startSdcClient();
            fail("Test must throw an exception here");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("SDC client start failed with reason"));
        }
    }

    @Test
    public final void testStopSdcClient() throws PssdControllerException {
        sypController.initializeSdcClient(pssdConfigParameters);
        sypController.stopSdcClient();
        assertEquals(PssdControllerStatus.STOPPED, sypController.getControllerStatus());
    }

    @Test
    public final void testStopSdcClient_Busy() throws PssdControllerException {

        sypController.initializeSdcClient(pssdConfigParameters);
        sypController.startSdcClient();
        sypController.changePssdControllerStatus(PssdControllerStatus.BUSY);
        assertEquals(1, sypController.getNbOfNotificationsOngoing());
        try {
            sypController.stopSdcClient();
            fail("Test must throw an exception here");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("Cannot stop SDC Client as PssdController status is BUSY"));
        }
    }

    @Test
    public final void testStopSdcClient_Failure() throws PssdControllerException {

        sypController.initializeSdcClient(pssdConfigParameters);
        sypController.startSdcClient();
        Mockito.when(successfulClientInitResult.getDistributionActionResult())
                .thenReturn(DistributionActionResultEnum.FAIL);
        try {
            sypController.stopSdcClient();
            fail("Test must throw an exception here");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("SDC client stop failed with reason"));
        }
    }

    @Test
    public final void testPssdControllerStatus() throws PssdControllerException {
        sypController.initializeSdcClient(pssdConfigParameters);
        sypController.startSdcClient();
        assertEquals(PssdControllerStatus.IDLE, sypController.getControllerStatus());
        assertEquals(0, sypController.getNbOfNotificationsOngoing());
        sypController.changePssdControllerStatus(PssdControllerStatus.BUSY);
        assertEquals(1, sypController.getNbOfNotificationsOngoing());
        sypController.changePssdControllerStatus(PssdControllerStatus.BUSY);
        assertEquals(2, sypController.getNbOfNotificationsOngoing());
        sypController.changePssdControllerStatus(PssdControllerStatus.IDLE);
        assertEquals(1, sypController.getNbOfNotificationsOngoing());
        sypController.changePssdControllerStatus(PssdControllerStatus.IDLE);
        assertEquals(0, sypController.getNbOfNotificationsOngoing());
    }

}

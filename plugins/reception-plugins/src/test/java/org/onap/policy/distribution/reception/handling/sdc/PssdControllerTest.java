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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.ArtifactInstallerException;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.PssdControllerException;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.PssdParametersException;
import org.onap.policy.distribution.reception.parameters.PssdConfigurationParametersGroup;

import org.onap.sdc.api.IDistributionClient;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.notification.IArtifactInfo;
import org.onap.sdc.api.notification.INotificationData;
import org.onap.sdc.api.notification.IResourceInstance;
import org.onap.sdc.api.results.IDistributionClientDownloadResult;
import org.onap.sdc.api.results.IDistributionClientResult;
import org.onap.sdc.impl.mock.DistributionClientStubImpl;
import org.onap.sdc.utils.DistributionActionResultEnum;


/**
 * THis class tests the Pssd Controller by using the Pssd Mock CLient.
 *
 *
 */
public class PssdControllerTest {

    private static INotificationData iNotif;

    private static IDistributionClientDownloadResult downloadResult;
    private static IDistributionClientDownloadResult downloadCorruptedResult;

    private static IDistributionClientResult successfulClientInitResult;
    private static IDistributionClientResult unsuccessfulClientInitResult;

    private static IArtifactInfo artifactInfo1;

    private static IResourceInstance resource1;

    private static PssdConfigurationParametersGroup pssdConfigParameters;
    
    /**
     * Prepare Mock Notification to used for Controller Test.
     */
    @BeforeClass
    public static final void prepareMockNotification() throws IOException, URISyntaxException, 
                                        NoSuchAlgorithmException, ArtifactInstallerException  {

        iNotif = Mockito.mock(INotificationData.class);

        // Create fake ArtifactInfo
        artifactInfo1 = Mockito.mock(IArtifactInfo.class);
        Mockito.when(artifactInfo1.getArtifactName()).thenReturn("artifact1");
        Mockito.when(artifactInfo1.getArtifactType()).thenReturn("TOSCA_CSAR");
        Mockito.when(artifactInfo1.getArtifactURL()).thenReturn("https://localhost:8080/v1/catalog/services/srv1/2.0/resources/aaa/1.0/artifacts/aaa.yml");
        Mockito.when(artifactInfo1.getArtifactUUID()).thenReturn("UUID1");
        Mockito.when(artifactInfo1.getArtifactDescription()).thenReturn("testos artifact1");

        // Now provision the NotificationData mock
        List<IArtifactInfo> listArtifact = new ArrayList<>();
        listArtifact.add(artifactInfo1);

        // Create fake resource Instance
        resource1 = Mockito.mock(IResourceInstance.class);
        Mockito.when(resource1.getResourceType()).thenReturn("VF");
        Mockito.when(resource1.getResourceName()).thenReturn("resourceName");
        Mockito.when(resource1.getArtifacts()).thenReturn(listArtifact);

        List<IResourceInstance> resources = new ArrayList<>();
        resources.add(resource1);

        Mockito.when(iNotif.getResources()).thenReturn(resources);
        Mockito.when(iNotif.getDistributionID()).thenReturn("distributionID1");
        Mockito.when(iNotif.getServiceName()).thenReturn("serviceName1");
        Mockito.when(iNotif.getServiceUUID()).thenReturn("serviceNameUUID1");
        Mockito.when(iNotif.getServiceVersion()).thenReturn("1.0");

        // Mock now the Pssd distribution client behavior
        successfulClientInitResult = Mockito.mock(IDistributionClientResult.class);
        Mockito.when(successfulClientInitResult.getDistributionActionResult())
            .thenReturn(DistributionActionResultEnum.SUCCESS);

        unsuccessfulClientInitResult = Mockito.mock(IDistributionClientResult.class);
        Mockito.when(unsuccessfulClientInitResult.getDistributionActionResult())
            .thenReturn(DistributionActionResultEnum.GENERAL_ERROR);

    }
    
    /**
     * Prepare pssd configuration parameters to used for each Controller Test.
     */
    @Before
    public final void initBeforeEachTest() throws IOException {
        // load the config
        final Gson gson = new GsonBuilder().create();
        pssdConfigParameters = gson.fromJson(
            new FileReader("src/test/resources/handling-sdc.json"), PssdConfigurationParametersGroup.class);
        final GroupValidationResult validationResult = pssdConfigParameters.validate();
        assertTrue(validationResult.isValid());
        
        PssdConfiguration config = new PssdConfiguration(pssdConfigParameters);
        assertEquals(20, config.getPollingInterval());
        assertEquals(30, config.getPollingTimeout());
    }

    @AfterClass
    public static final void kill() {
        pssdConfigParameters = null;
    }

    @Test
    public final void testTheInitWithPssdStub() throws PssdControllerException, PssdParametersException, IOException {

        PssdController pssdController = new PssdController("handling-sdc",new DistributionClientStubImpl());
        pssdController.initPssd(pssdConfigParameters);
        assertTrue(pssdController.getControllerStatus() == PssdControllerStatus.IDLE);
        assertTrue(pssdController.getNbOfNotificationsOngoing() == 0);
    }

    @Test
    public final void testASecondInit() throws PssdControllerException, PssdParametersException, IOException {
        PssdController pssdController = new PssdController("handling-sdc",new DistributionClientStubImpl());
        pssdController.initPssd(pssdConfigParameters);
        // try to send a notif, this should fail internally, we just want to ensure that in case of crash, 
        //controller status goes to IDLE

        assertTrue(pssdController.getControllerStatus() == PssdControllerStatus.IDLE);
        assertTrue(pssdController.getNbOfNotificationsOngoing() == 0);

        try {
            pssdController.initPssd(pssdConfigParameters);
            fail("PssdControllerException should have been raised for the init");
        } catch (PssdControllerException e) {
            assertTrue("The controller is already initialized, call the closePssd method first".equals(e.getMessage()));
        }

        // No changes expected on the controller state
        assertTrue(pssdController.getControllerStatus() == PssdControllerStatus.IDLE);
        assertTrue(pssdController.getNbOfNotificationsOngoing() == 0);
    }

    @Test
    public final void testInitCrashWithMockitoClient() throws PssdParametersException, IOException {

        IDistributionClient distributionClient;
        // First case for init method
        distributionClient = Mockito.mock(IDistributionClient.class);
        Mockito.when(distributionClient.download(artifactInfo1))
            .thenThrow(new RuntimeException("ASDC Client not initialized"));
        Mockito.when(distributionClient.init(any(PssdConfiguration.class),any(INotificationCallback.class)))
            .thenReturn(unsuccessfulClientInitResult);
        Mockito.when(distributionClient.start()).thenReturn(unsuccessfulClientInitResult);

        PssdController pssdController = new PssdController("handling-sdc", distributionClient);

        // This should return an exception
        try {
            pssdController.initPssd(pssdConfigParameters);
            fail("PssdControllerException should have been raised for the init");
        } catch (PssdControllerException e) {
            assertTrue(e.getMessage().contains("Initialization of the Pssd Controller failed with reason"));
        }

        assertTrue(pssdController.getControllerStatus() == PssdControllerStatus.STOPPED);
        assertTrue(pssdController.getNbOfNotificationsOngoing() == 0);

        // Second case for start method

        Mockito.when(distributionClient.init(any(PssdConfiguration.class),any(INotificationCallback.class)))
            .thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.start()).thenReturn(unsuccessfulClientInitResult);

        // This should return an exception
        try {
            pssdController.initPssd(pssdConfigParameters);
            fail("PssdControllerException should have been raised for the init");
        } catch (PssdControllerException e) {
            assertTrue(e.getMessage().contains("Startup of the Pssd Controller failed with reason"));
        }

        assertTrue(pssdController.getControllerStatus() == PssdControllerStatus.STOPPED);
        assertTrue(pssdController.getNbOfNotificationsOngoing() == 0);
    }

    @Test
    public final void testTheStop() throws PssdControllerException, PssdParametersException, IOException {

        PssdController pssdController = new PssdController("handling-sdc",new DistributionClientStubImpl());

        pssdController.closePssd();
        assertTrue(pssdController.getControllerStatus() == PssdControllerStatus.STOPPED);


        pssdController = new PssdController("handling-sdc",new DistributionClientStubImpl());
        pssdController.initPssd(pssdConfigParameters);
        pssdController.closePssd();
        assertTrue(pssdController.getControllerStatus() == PssdControllerStatus.STOPPED);
    }

    @Test
    public final void testConfigAccess() throws PssdControllerException, PssdParametersException, IOException {
        IDistributionClient distributionClient;
        distributionClient = Mockito.mock(IDistributionClient.class);
        Mockito.when(distributionClient.download(artifactInfo1)).thenReturn(downloadResult);
        Mockito.when(distributionClient.init(any(PssdConfiguration.class),any(INotificationCallback.class)))
            .thenReturn(successfulClientInitResult);
        Mockito.when(distributionClient.start()).thenReturn(successfulClientInitResult);

        PssdController pssdController = new PssdController("handling-sdc",distributionClient);
        assertTrue("Unknown".equals(pssdController.getAddress()));
        assertTrue("Unknown".equals(pssdController.getEnvironment()));
        
        pssdController.initPssd(pssdConfigParameters);
        assertTrue("localhost".equals(pssdController.getAddress()));
        assertTrue("environmentName".equals(pssdController.getEnvironment()));
        
    }

}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Class to perform unit test of {@link SdcConfiguration}.
 *
 */
public class TestSdcReceptionHandlerConfigurationParameterGroup {

    @Test
    public void testSdcConfiguration() throws IOException {
        SdcReceptionHandlerConfigurationParameterGroup configParameters = null;
        final Gson gson = new GsonBuilder().create();
        configParameters = gson.fromJson(new FileReader("src/test/resources/handling-sdc.json"),
               SdcReceptionHandlerConfigurationParameterGroup.class);

        final GroupValidationResult validationResult = configParameters.validate();
        assertTrue(validationResult.isValid());
        final SdcConfiguration config = new SdcConfiguration(configParameters);
        assertEquals(Arrays.asList("a.com", "b.com", "c.com"), config.getMsgBusAddress());
        assertEquals(Arrays.asList("TOSCA_CSAR", "HEAT"), config.getRelevantArtifactTypes());
        assertEquals("localhost", config.getAsdcAddress());
        assertEquals("policy", config.getUser());
        assertEquals("policy", config.getPassword());
        assertEquals(20, config.getPollingInterval());
        assertEquals(30, config.getPollingTimeout());
        assertEquals("policy-id", config.getConsumerID());
        assertEquals("policy-group", config.getConsumerGroup());
        assertEquals("TEST", config.getEnvironmentName());
        assertEquals("null", config.getKeyStorePath());
        assertEquals("null", config.getKeyStorePassword());
        assertEquals(false, config.activateServerTLSAuth());
        assertEquals(true, config.isFilterInEmptyResources());
        assertEquals(false, config.isUseHttpsWithDmaap());
    }

    @Test
    public void testInvalidSdcConfiguration() throws IOException {
        SdcReceptionHandlerConfigurationParameterGroup configParameters = null;

        final Gson gson = new GsonBuilder().create();
        configParameters = gson.fromJson(new FileReader("src/test/resources/handling-sdcInvalid.json"),
                SdcReceptionHandlerConfigurationParameterGroup.class);

        final GroupValidationResult validationResult = configParameters.validate();
        assertFalse(validationResult.isValid());

    }

    @Test
    public void testEmptyParameters() {
        final SdcReceptionHandlerConfigurationParameterGroup configurationParameters =
                CommonTestData.getPolicyDecoderParameters("src/test/resources/parameters/EmptyParameters.json",
                        SdcReceptionHandlerConfigurationParameterGroup.class);

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }
}

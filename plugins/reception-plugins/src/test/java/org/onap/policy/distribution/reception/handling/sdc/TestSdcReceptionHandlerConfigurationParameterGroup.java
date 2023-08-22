/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
 *  Modifications Copyright (C) 2019-2022 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Class to perform unit test of {@link SdcConfiguration}.
 *
 */
class TestSdcReceptionHandlerConfigurationParameterGroup {

    @Test
    void testSdcConfiguration() throws IOException {
        final var gson = new GsonBuilder().create();
        var configParameters = gson.fromJson(new FileReader("src/test/resources/handling-sdc.json"),
               SdcReceptionHandlerConfigurationParameterGroup.class);

        final var validationResult = configParameters.validate();
        assertTrue(validationResult.isValid());
        final var config = new SdcConfiguration(configParameters);
        assertEquals(Arrays.asList("TOSCA_CSAR", "HEAT"), config.getRelevantArtifactTypes());
        assertEquals("localhost", config.getSdcAddress());
        assertEquals("policy", config.getUser());
        assertEquals("policy", config.getPassword());
        assertEquals(20, config.getPollingInterval());
        assertEquals(30, config.getPollingTimeout());
        assertEquals("policy-id", config.getConsumerID());
        assertEquals("policy-group", config.getConsumerGroup());
        assertEquals("TEST", config.getEnvironmentName());
        assertEquals("null", config.getKeyStorePath());
        assertEquals("null", config.getKeyStorePassword());
        assertFalse(config.activateServerTLSAuth());
        assertTrue(config.isFilterInEmptyResources());
        assertFalse(config.isUseHttpsWithSDC());
    }

    @Test
    void testSdcConfigurationNullParameters() throws IOException {
        final var gson = new GsonBuilder().create();
        var configParameters = gson.fromJson(new FileReader("src/test/resources/handling-sdc-null-parameters.json"),
               SdcReceptionHandlerConfigurationParameterGroup.class);

        final var validationResult = configParameters.validate();
        assertTrue(validationResult.isValid());
        final var config = new SdcConfiguration(configParameters);
        assertThat(config.getKeyStorePath()).isNull();
        assertThat(config.getKeyStorePassword()).isNull();
        //if boolean parameters are null they are set to false
        assertFalse(config.activateServerTLSAuth());
        assertFalse(config.isFilterInEmptyResources());
    }

    @Test
    void testInvalidSdcConfiguration() throws IOException {
        final var gson = new GsonBuilder().create();
        var configParameters = gson.fromJson(new FileReader("src/test/resources/handling-sdcInvalid.json"),
                SdcReceptionHandlerConfigurationParameterGroup.class);

        final var validationResult = configParameters.validate();
        assertFalse(validationResult.isValid());

    }

    @Test
    void testEmptyParameters() {
        final var configurationParameters =
                CommonTestData.getPolicyDecoderParameters("src/test/resources/parameters/EmptyParameters.json",
                        SdcReceptionHandlerConfigurationParameterGroup.class);

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }
}

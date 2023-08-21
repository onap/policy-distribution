/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.distribution.main.parameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

/**
 * Class to perform unit test of ReceptionHandlerParameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
class TestReceptionHandlerParameters {
    CommonTestData commonTestData = new CommonTestData();

    @Test
    void testReceptionHandlerParameters() {
        final var pHParameters = commonTestData.getPluginHandlerParameters(false);
        final var rHParameters = new ReceptionHandlerParameters(
                CommonTestData.RECEPTION_HANDLER_TYPE, CommonTestData.RECEPTION_HANDLER_CLASS_NAME,
                CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        final var validationResult = rHParameters.validate();
        assertEquals(CommonTestData.RECEPTION_HANDLER_TYPE, rHParameters.getReceptionHandlerType());
        assertEquals(CommonTestData.RECEPTION_HANDLER_CLASS_NAME, rHParameters.getReceptionHandlerClassName());
        assertEquals(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS,
                rHParameters.getReceptionHandlerConfigurationName());
        assertEquals(pHParameters, rHParameters.getPluginHandlerParameters());
        assertTrue(validationResult.isValid());
    }

    @Test
    void testReceptionHandlerParameters_NullReceptionHandlerType() {
        final var pHParameters = commonTestData.getPluginHandlerParameters(false);
        final var rHParameters =
                new ReceptionHandlerParameters(null, CommonTestData.RECEPTION_HANDLER_CLASS_NAME,
                        CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        final var validationResult = rHParameters.validate();
        assertThat(rHParameters.getReceptionHandlerType()).isNull();
        assertEquals(CommonTestData.RECEPTION_HANDLER_CLASS_NAME, rHParameters.getReceptionHandlerClassName());
        assertEquals(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS,
                rHParameters.getReceptionHandlerConfigurationName());
        assertEquals(pHParameters, rHParameters.getPluginHandlerParameters());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"receptionHandlerType\" value \"null\" INVALID, is null");
    }

    @Test
    void testReceptionHandlerParameters_NullReceptionHandlerClassName() {
        final var pHParameters = commonTestData.getPluginHandlerParameters(false);
        final var rHParameters =
                new ReceptionHandlerParameters(CommonTestData.RECEPTION_HANDLER_TYPE, null,
                        CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        final var validationResult = rHParameters.validate();
        assertEquals(CommonTestData.RECEPTION_HANDLER_TYPE, rHParameters.getReceptionHandlerType());
        assertThat(rHParameters.getReceptionHandlerClassName()).isNull();
        assertEquals(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS,
                rHParameters.getReceptionHandlerConfigurationName());
        assertEquals(pHParameters, rHParameters.getPluginHandlerParameters());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult())
                        .contains("\"receptionHandlerClassName\" value \"null\" INVALID, is null");
    }

    @Test
    void testReceptionHandlerParameters_EmptyReceptionHandlerType() {
        final var pHParameters = commonTestData.getPluginHandlerParameters(false);
        final var rHParameters =
                new ReceptionHandlerParameters("", CommonTestData.RECEPTION_HANDLER_CLASS_NAME,
                        CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        final var validationResult = rHParameters.validate();
        assertEquals("", rHParameters.getReceptionHandlerType());
        assertEquals(CommonTestData.RECEPTION_HANDLER_CLASS_NAME, rHParameters.getReceptionHandlerClassName());
        assertEquals(pHParameters, rHParameters.getPluginHandlerParameters());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"receptionHandlerType\" value \"\" INVALID, is blank");
    }

    @Test
    void testReceptionHandlerParameters_EmptyReceptionHandlerClassName() {
        final var pHParameters = commonTestData.getPluginHandlerParameters(false);
        final var rHParameters =
                new ReceptionHandlerParameters(CommonTestData.RECEPTION_HANDLER_TYPE, "",
                        CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        final var validationResult = rHParameters.validate();
        assertEquals(CommonTestData.RECEPTION_HANDLER_TYPE, rHParameters.getReceptionHandlerType());
        assertEquals("", rHParameters.getReceptionHandlerClassName());
        assertEquals(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS,
                rHParameters.getReceptionHandlerConfigurationName());
        assertEquals(pHParameters, rHParameters.getPluginHandlerParameters());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"receptionHandlerClassName\" value \"\" INVALID, is blank");
    }

    @Test
    void testReceptionHandlerParameters_EmptyPluginHandler() {
        final var pHParameters = commonTestData.getPluginHandlerParameters(true);
        final var rHParameters = new ReceptionHandlerParameters(
                CommonTestData.RECEPTION_HANDLER_TYPE, CommonTestData.RECEPTION_HANDLER_CLASS_NAME,
                CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        var result = rHParameters.validate();
        assertFalse(result.isValid());
        assertThat(result.getResult()).contains("\"policyForwarders\"", "minimum");
    }

    @Test
    void testReceptionHandlerParameters_InvalidReceptionHandlerClass() {
        final var pHParameters = commonTestData.getPluginHandlerParameters(false);

        final var rHParameters = new ReceptionHandlerParameters(
                CommonTestData.RECEPTION_HANDLER_TYPE, CommonTestData.RECEPTION_HANDLER_CLASS_NAME + "Invalid",
                CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        final var validationResult = rHParameters.validate();
        assertEquals(CommonTestData.RECEPTION_HANDLER_TYPE, rHParameters.getReceptionHandlerType());
        assertEquals(CommonTestData.RECEPTION_HANDLER_CLASS_NAME + "Invalid",
                rHParameters.getReceptionHandlerClassName());
        assertEquals(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS,
                rHParameters.getReceptionHandlerConfigurationName());
        assertEquals(pHParameters, rHParameters.getPluginHandlerParameters());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"receptionHandlerClassName\"", "classpath");
    }
}

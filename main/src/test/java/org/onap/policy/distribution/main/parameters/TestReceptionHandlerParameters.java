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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.distribution.reception.parameters.PluginHandlerParameters;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

/**
 * Class to perform unit test of ReceptionHandlerParameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestReceptionHandlerParameters {
    CommonTestData commonTestData = new CommonTestData();

    @Test
    public void testReceptionHandlerParameters() {
        final PluginHandlerParameters pHParameters = commonTestData.getPluginHandlerParameters(false);
        final ReceptionHandlerParameters rHParameters = new ReceptionHandlerParameters(
                CommonTestData.RECEPTION_HANDLER_TYPE, CommonTestData.RECEPTION_HANDLER_CLASS_NAME,
                CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        final ValidationResult validationResult = rHParameters.validate();
        assertEquals(CommonTestData.RECEPTION_HANDLER_TYPE, rHParameters.getReceptionHandlerType());
        assertEquals(CommonTestData.RECEPTION_HANDLER_CLASS_NAME, rHParameters.getReceptionHandlerClassName());
        assertEquals(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS,
                rHParameters.getReceptionHandlerConfigurationName());
        assertEquals(pHParameters, rHParameters.getPluginHandlerParameters());
        assertTrue(validationResult.isValid());
    }

    @Test
    public void testReceptionHandlerParameters_NullReceptionHandlerType() {
        final PluginHandlerParameters pHParameters = commonTestData.getPluginHandlerParameters(false);
        final ReceptionHandlerParameters rHParameters =
                new ReceptionHandlerParameters(null, CommonTestData.RECEPTION_HANDLER_CLASS_NAME,
                        CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        final ValidationResult validationResult = rHParameters.validate();
        assertEquals(null, rHParameters.getReceptionHandlerType());
        assertEquals(CommonTestData.RECEPTION_HANDLER_CLASS_NAME, rHParameters.getReceptionHandlerClassName());
        assertEquals(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS,
                rHParameters.getReceptionHandlerConfigurationName());
        assertEquals(pHParameters, rHParameters.getPluginHandlerParameters());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"receptionHandlerType\" value \"null\" INVALID, is null");
    }

    @Test
    public void testReceptionHandlerParameters_NullReceptionHandlerClassName() {
        final PluginHandlerParameters pHParameters = commonTestData.getPluginHandlerParameters(false);
        final ReceptionHandlerParameters rHParameters =
                new ReceptionHandlerParameters(CommonTestData.RECEPTION_HANDLER_TYPE, null,
                        CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        final ValidationResult validationResult = rHParameters.validate();
        assertEquals(CommonTestData.RECEPTION_HANDLER_TYPE, rHParameters.getReceptionHandlerType());
        assertEquals(null, rHParameters.getReceptionHandlerClassName());
        assertEquals(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS,
                rHParameters.getReceptionHandlerConfigurationName());
        assertEquals(pHParameters, rHParameters.getPluginHandlerParameters());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult())
                        .contains("\"receptionHandlerClassName\" value \"null\" INVALID, is null");
    }

    @Test
    public void testReceptionHandlerParameters_EmptyReceptionHandlerType() {
        final PluginHandlerParameters pHParameters = commonTestData.getPluginHandlerParameters(false);
        final ReceptionHandlerParameters rHParameters =
                new ReceptionHandlerParameters("", CommonTestData.RECEPTION_HANDLER_CLASS_NAME,
                        CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        final ValidationResult validationResult = rHParameters.validate();
        assertEquals("", rHParameters.getReceptionHandlerType());
        assertEquals(CommonTestData.RECEPTION_HANDLER_CLASS_NAME, rHParameters.getReceptionHandlerClassName());
        assertEquals(pHParameters, rHParameters.getPluginHandlerParameters());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"receptionHandlerType\" value \"\" INVALID, is blank");
    }

    @Test
    public void testReceptionHandlerParameters_EmptyReceptionHandlerClassName() {
        final PluginHandlerParameters pHParameters = commonTestData.getPluginHandlerParameters(false);
        final ReceptionHandlerParameters rHParameters =
                new ReceptionHandlerParameters(CommonTestData.RECEPTION_HANDLER_TYPE, "",
                        CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        final ValidationResult validationResult = rHParameters.validate();
        assertEquals(CommonTestData.RECEPTION_HANDLER_TYPE, rHParameters.getReceptionHandlerType());
        assertEquals("", rHParameters.getReceptionHandlerClassName());
        assertEquals(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS,
                rHParameters.getReceptionHandlerConfigurationName());
        assertEquals(pHParameters, rHParameters.getPluginHandlerParameters());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"receptionHandlerClassName\" value \"\" INVALID, is blank");
    }

    @Test
    public void testReceptionHandlerParameters_EmptyPluginHandler() {
        final PluginHandlerParameters pHParameters = commonTestData.getPluginHandlerParameters(true);
        final ReceptionHandlerParameters rHParameters = new ReceptionHandlerParameters(
                CommonTestData.RECEPTION_HANDLER_TYPE, CommonTestData.RECEPTION_HANDLER_CLASS_NAME,
                CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        ValidationResult result = rHParameters.validate();
        assertFalse(result.isValid());
        assertThat(result.getResult()).contains("\"policyForwarders\"", "minimum");
    }

    @Test
    public void testReceptionHandlerParameters_InvalidReceptionHandlerClass() {
        final PluginHandlerParameters pHParameters = commonTestData.getPluginHandlerParameters(false);

        final ReceptionHandlerParameters rHParameters = new ReceptionHandlerParameters(
                CommonTestData.RECEPTION_HANDLER_TYPE, CommonTestData.RECEPTION_HANDLER_CLASS_NAME + "Invalid",
                CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
        final ValidationResult validationResult = rHParameters.validate();
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

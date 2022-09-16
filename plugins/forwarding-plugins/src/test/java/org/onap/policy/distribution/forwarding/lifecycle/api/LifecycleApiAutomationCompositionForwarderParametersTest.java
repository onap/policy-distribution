/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Nordix Foundation.
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

package org.onap.policy.distribution.forwarding.lifecycle.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.distribution.forwarding.testclasses.CommonTestData;

/**
 * Class to perform unit test of {@link LifecycleApiAutomationCompositionForwarderParameters}.
 *
 * @author Sirisha Manchikanti (sirisha.manchikanti@est.tech)
 */
public class LifecycleApiAutomationCompositionForwarderParametersTest {

    private static final String AUTOMATION_COMPOSITION_RUNTIME_HOST_NAME = "0.0.0.0";
    private static final int AUTOMATION_COMPOSITION_RUNTIME_PORT = 6969;
    private static final String AUTOMATION_COMPOSITION_RUNTIME_USER = "policyadmin";
    private static final String AUTOMATION_COMPOSITION_RUNTIME_PASSWORD = "zb!XztG34";


    @Test
    public void testValidParameters() {
        final LifecycleApiAutomationCompositionForwarderParameters configurationParameters =
                CommonTestData.getPolicyForwarderParameters(
                        "src/test/resources/parameters/LifecycleApiAutomationCompositionForwarderParameters.json",
                        LifecycleApiAutomationCompositionForwarderParameters.class);

        assertEquals(LifecycleApiAutomationCompositionForwarderParameters.class.getSimpleName(),
                configurationParameters.getName());

        assertEquals(AUTOMATION_COMPOSITION_RUNTIME_HOST_NAME,
                configurationParameters.getAutomationCompositionRuntimeParameters().getHostname());
        assertEquals(AUTOMATION_COMPOSITION_RUNTIME_PORT,
                configurationParameters.getAutomationCompositionRuntimeParameters().getPort());
        assertFalse(configurationParameters.getAutomationCompositionRuntimeParameters().isUseHttps());
        assertEquals(AUTOMATION_COMPOSITION_RUNTIME_USER,
                configurationParameters.getAutomationCompositionRuntimeParameters().getUserName());
        assertEquals(AUTOMATION_COMPOSITION_RUNTIME_PASSWORD,
                configurationParameters.getAutomationCompositionRuntimeParameters().getPassword());

        assertThat(configurationParameters.validate().getResult()).isNull();
        assertEquals(ValidationStatus.CLEAN, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidParameters() {
        final LifecycleApiForwarderParameters configurationParameters =
                CommonTestData.getPolicyForwarderParameters(
                        "src/test/resources/parameters/LifecycleApiPolicyForwarderParametersInvalid.json",
                        LifecycleApiForwarderParameters.class);

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }

    @Test
    public void testEmptyParameters() {
        final LifecycleApiForwarderParameters configurationParameters =
                CommonTestData.getPolicyForwarderParameters("src/test/resources/parameters/EmptyParameters.json",
                        LifecycleApiForwarderParameters.class);

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }
}

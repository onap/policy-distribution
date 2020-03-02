/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.distribution.forwarding.testclasses.CommonTestData;

/**
 * Class to perform unit test of {@link LifecycleApiForwarderParameters}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@est.tech)
 */
public class LifecycleApiForwarderParametersTest {

    private static final String POLICY_API_HOST_NAME = "0.0.0.0";
    private static final int POLICY_API_PORT = 6969;
    private static final String POLICY_API_USER = "healthcheck";
    private static final String POLICY_API_PASSWORD = "zb!XztG34";
    private static final String POLICY_PAP_HOST_NAME = "0.0.0.0";
    private static final int POLICY_PAP_PORT = 6969;
    private static final String POLICY_PAP_USER = "healthcheck";
    private static final String POLICY_PAP_PASSWORD = "zb!XztG34";


    @Test
    public void testValidParameters() {
        final LifecycleApiForwarderParameters configurationParameters =
                CommonTestData.getPolicyForwarderParameters(
                        "src/test/resources/parameters/LifecycleApiPolicyForwarderParameters.json",
                        LifecycleApiForwarderParameters.class);

        assertEquals(LifecycleApiForwarderParameters.class.getSimpleName(),
                configurationParameters.getName());
        assertFalse(configurationParameters.isHttps());
        assertTrue(configurationParameters.isDeployPolicies());
        assertEquals(POLICY_API_HOST_NAME, configurationParameters.getApiParameters().getHostName());
        assertEquals(POLICY_API_PORT, configurationParameters.getApiParameters().getPort());
        assertEquals(POLICY_API_USER, configurationParameters.getApiParameters().getUserName());
        assertEquals(POLICY_API_PASSWORD, configurationParameters.getApiParameters().getPassword());
        assertEquals(POLICY_PAP_HOST_NAME, configurationParameters.getPapParameters().getHostName());
        assertEquals(POLICY_PAP_PORT, configurationParameters.getPapParameters().getPort());
        assertEquals(POLICY_PAP_USER, configurationParameters.getPapParameters().getUserName());
        assertEquals(POLICY_PAP_PASSWORD, configurationParameters.getPapParameters().getPassword());

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

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
import org.onap.policy.distribution.forwarding.xacml.pdp.testclasses.CommonTestData;

/**
 * Class to perform unit test of {@link LifecycleApiPolicyForwarderParameterGroup}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@est.tech)
 */
public class LifecycleApiPolicyForwarderParameterGroupTest {

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
        final LifecycleApiPolicyForwarderParameterGroup configurationParameters =
                CommonTestData.getPolicyForwarderParameters(
                        "src/test/resources/parameters/LifecycleApiPolicyForwarderParameters.json",
                        LifecycleApiPolicyForwarderParameterGroup.class);

        assertEquals(LifecycleApiPolicyForwarderParameterGroup.class.getSimpleName(),
                configurationParameters.getName());
        assertFalse(configurationParameters.isHttps());
        assertTrue(configurationParameters.isDeployPolicies());
        assertEquals(POLICY_API_HOST_NAME, configurationParameters.getPolicyApiHostName());
        assertEquals(POLICY_API_PORT, configurationParameters.getPolicyApiPort());
        assertEquals(POLICY_API_USER, configurationParameters.getPolicyApiUserName());
        assertEquals(POLICY_API_PASSWORD, configurationParameters.getPolicyApiPassword());
        assertEquals(POLICY_PAP_HOST_NAME, configurationParameters.getPolicyPapHostName());
        assertEquals(POLICY_PAP_PORT, configurationParameters.getPolicyPapPort());
        assertEquals(POLICY_PAP_USER, configurationParameters.getPolicyPapUserName());
        assertEquals(POLICY_PAP_PASSWORD, configurationParameters.getPolicyPapPassword());

        assertEquals(ValidationStatus.CLEAN, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidParameters() {
        final LifecycleApiPolicyForwarderParameterGroup configurationParameters =
                CommonTestData.getPolicyForwarderParameters(
                        "src/test/resources/parameters/LifecycleApiPolicyForwarderParametersInvalid.json",
                        LifecycleApiPolicyForwarderParameterGroup.class);

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }

    @Test
    public void testEmptyParameters() {
        final LifecycleApiPolicyForwarderParameterGroup configurationParameters =
                CommonTestData.getPolicyForwarderParameters("src/test/resources/parameters/EmptyParameters.json",
                        LifecycleApiPolicyForwarderParameterGroup.class);

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }
}

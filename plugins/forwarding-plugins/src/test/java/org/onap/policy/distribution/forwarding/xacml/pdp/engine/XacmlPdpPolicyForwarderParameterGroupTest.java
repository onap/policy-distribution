/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.distribution.forwarding.xacml.pdp.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.distribution.forwarding.xacml.pdp.XacmlPdpPolicyForwarderParameterGroup;
import org.onap.policy.distribution.forwarding.xacml.pdp.testclasses.CommonTestData;

public class XacmlPdpPolicyForwarderParameterGroupTest {

    private static final String CLIENT_AUTH = "myClientAuth";
    private static final String PASSWORD = "myPassword";
    private static final String USER = "myUser";
    private static final int PORT = 1234;
    private static final String HOST_NAME = "10.10.10.10";

    @Test
    public void testValidParameters() {
        final XacmlPdpPolicyForwarderParameterGroup configurationParameters = CommonTestData
                .getPolicyForwarderParameters("src/test/resources/parameters/XacmlPdpPolicyForwarderParameters.json",
                        XacmlPdpPolicyForwarderParameterGroup.class);

        assertTrue(configurationParameters.isUseHttps());
        assertEquals(HOST_NAME, configurationParameters.getHostname());
        assertEquals(PORT, configurationParameters.getPort());
        assertEquals(USER, configurationParameters.getUserName());
        assertEquals(PASSWORD, configurationParameters.getPassword());
        assertEquals(CLIENT_AUTH, configurationParameters.getClientAuth());
        assertFalse(configurationParameters.isManaged());
    }

    @Test
    public void testInvalidParameters() {
        final XacmlPdpPolicyForwarderParameterGroup configurationParameters =
                CommonTestData.getPolicyForwarderParameters(
                        "src/test/resources/parameters/XacmlPdpPolicyForwarderParametersInvalid.json",
                        XacmlPdpPolicyForwarderParameterGroup.class);

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }

    @Test
    public void testEmptyParameters() {
        final XacmlPdpPolicyForwarderParameterGroup configurationParameters =
                CommonTestData.getPolicyForwarderParameters("src/test/resources/parameters/EmptyParameters.json",
                        XacmlPdpPolicyForwarderParameterGroup.class);

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }
}

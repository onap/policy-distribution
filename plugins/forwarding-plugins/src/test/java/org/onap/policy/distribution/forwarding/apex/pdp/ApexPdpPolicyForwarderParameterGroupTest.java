/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.distribution.forwarding.apex.pdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Class to perform unit test of {@link ApexPdpPolicyForwarderParameterGroup}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class ApexPdpPolicyForwarderParameterGroupTest {

    @Test
    public void testBuilderAndGetters() {
        final ApexPdpPolicyForwarderParameterBuilder builder = new ApexPdpPolicyForwarderParameterBuilder();
        builder.setHostname("10.10.10.10").setPort(1234).setIgnoreConflicts(false).setForceUpdate(true);
        final ApexPdpPolicyForwarderParameterGroup configurationParameters =
                new ApexPdpPolicyForwarderParameterGroup(builder);
        configurationParameters.setName("myConfiguration");

        assertEquals("myConfiguration", configurationParameters.getName());
        assertTrue(configurationParameters.isForceUpdate());
        assertEquals("10.10.10.10", configurationParameters.getHostname());
        assertEquals(1234, configurationParameters.getPort());
        assertFalse(configurationParameters.isIgnoreConflicts());
        assertEquals(ValidationStatus.CLEAN, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidHostName() {
        final ApexPdpPolicyForwarderParameterBuilder builder = new ApexPdpPolicyForwarderParameterBuilder();
        builder.setHostname("").setPort(1234).setIgnoreConflicts(false).setForceUpdate(true);
        final ApexPdpPolicyForwarderParameterGroup configurationParameters =
                new ApexPdpPolicyForwarderParameterGroup(builder);
        configurationParameters.setName("myConfiguration");

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidPort() {
        final ApexPdpPolicyForwarderParameterBuilder builder = new ApexPdpPolicyForwarderParameterBuilder();
        builder.setHostname("10.10.10.10").setPort(-1).setIgnoreConflicts(false).setForceUpdate(true);
        final ApexPdpPolicyForwarderParameterGroup configurationParameters =
                new ApexPdpPolicyForwarderParameterGroup(builder);
        configurationParameters.setName("myConfiguration");

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }
}

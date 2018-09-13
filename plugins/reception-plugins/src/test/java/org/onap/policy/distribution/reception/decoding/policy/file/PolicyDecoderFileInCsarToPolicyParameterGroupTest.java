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

package org.onap.policy.distribution.reception.decoding.policy.file;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.distribution.reception.decoding.policy.file.PolicyDecoderFileInCsarToPolicyParameterGroup;

/**
 * Class to perform unit test of {@link PolicyDecoderFileInCsarToPolicyParameterGroup}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class PolicyDecoderFileInCsarToPolicyParameterGroupTest {

    @Test
    public void testConstructorAndGetters() {
        final PolicyDecoderFileInCsarToPolicyParameterGroup configurationParameters =
                new PolicyDecoderFileInCsarToPolicyParameterGroup("SamplePolicy", "APEX");
        configurationParameters.setName("myConfiguration");

        assertEquals("myConfiguration", configurationParameters.getName());
        assertEquals("SamplePolicy", configurationParameters.getPolicyFileName());
        assertEquals("APEX", configurationParameters.getPolicyType());
        assertEquals(ValidationStatus.CLEAN, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidPolicyFileName() {
        final PolicyDecoderFileInCsarToPolicyParameterGroup configurationParameters =
                new PolicyDecoderFileInCsarToPolicyParameterGroup("", "APEX");
        configurationParameters.setName("myConfiguration");

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidPolicyType() {
        final PolicyDecoderFileInCsarToPolicyParameterGroup configurationParameters =
                new PolicyDecoderFileInCsarToPolicyParameterGroup("SamplePolicy", "");
        configurationParameters.setName("myConfiguration");

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }
}

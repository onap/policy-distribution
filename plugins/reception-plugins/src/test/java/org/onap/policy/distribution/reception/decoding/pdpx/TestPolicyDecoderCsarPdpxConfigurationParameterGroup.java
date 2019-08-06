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

package org.onap.policy.distribution.reception.decoding.pdpx;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Class to perform unit test of {@link PolicyDecoderCsarPdpxConfigurationParameterGroup}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestPolicyDecoderCsarPdpxConfigurationParameterGroup {

    @Test
    public void testValidParameters() {
        final PolicyDecoderCsarPdpxConfigurationParameterGroup parameterGroup = CommonTestData
                .getPolicyDecoderParameters("src/test/resources/parameters/PdpxPolicyDecoderParameters.json",
                        PolicyDecoderCsarPdpxConfigurationParameterGroup.class);

        assertEquals(PolicyDecoderCsarPdpxConfigurationParameterGroup.class.getSimpleName(), parameterGroup.getName());
        assertEquals("onapName", parameterGroup.getOnapName());
        assertEquals("OOF", parameterGroup.getPolicyNamePrefix());
        assertEquals("5", parameterGroup.getPriority());
        assertEquals("2", parameterGroup.getRiskLevel());
        assertEquals("Test", parameterGroup.getRiskType());
        assertEquals("1.0", parameterGroup.getVersion());
        assertEquals(ValidationStatus.CLEAN, parameterGroup.validate().getStatus());
    }

    @Test
    public void testInvalidParameters() {
        final PolicyDecoderCsarPdpxConfigurationParameterGroup parameterGroup = CommonTestData
                .getPolicyDecoderParameters("src/test/resources/parameters/PdpxPolicyDecoderParametersInvalid.json",
                        PolicyDecoderCsarPdpxConfigurationParameterGroup.class);

        assertEquals(ValidationStatus.INVALID, parameterGroup.validate().getStatus());
    }

    @Test
    public void testEmptyParameters() {
        final PolicyDecoderCsarPdpxConfigurationParameterGroup configurationParameters =
                CommonTestData.getPolicyDecoderParameters("src/test/resources/parameters/EmptyParameters.json",
                        PolicyDecoderCsarPdpxConfigurationParameterGroup.class);

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }
}

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

package org.onap.policy.distribution.reception.decoding.policy.file;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.distribution.reception.handling.sdc.CommonTestData;

/**
 * Class to perform unit test of {@link ControlLoopDecoderFileInCsarParameterGroup}.
 *
 * @author Sirisha Manchikanti (sirisha.manchikanti@est.tech)
 */
public class ControlLoopDecoderFileInCsarParameterGroupTest {

    @Test
    public void testValidParameters() {
        final ControlLoopDecoderFileInCsarParameterGroup configurationParameters = CommonTestData
                .getPolicyDecoderParameters("src/test/resources/parameters/FileInCsarControlLoopDecoderParameters.json",
                    ControlLoopDecoderFileInCsarParameterGroup.class);

        assertEquals(ControlLoopDecoderFileInCsarParameterGroup.class.getSimpleName(),
                configurationParameters.getName());
        assertEquals("controlloop", configurationParameters.getControlLoopType());
        assertEquals(ValidationStatus.CLEAN, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidParameters() {
        final ControlLoopDecoderFileInCsarParameterGroup configurationParameters =
                CommonTestData.getPolicyDecoderParameters(
                        "src/test/resources/parameters/FileInCsarControlLoopDecoderParametersInvalid.json",
                        ControlLoopDecoderFileInCsarParameterGroup.class);

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }

    @Test
    public void testEmptyParameters() {
        final ControlLoopDecoderFileInCsarParameterGroup configurationParameters =
                CommonTestData.getPolicyDecoderParameters("src/test/resources/parameters/EmptyParameters.json",
                    ControlLoopDecoderFileInCsarParameterGroup.class);

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }
}

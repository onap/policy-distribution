/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Intel Corp. All rights reserved.
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

package org.onap.policy.distribution.forwarding.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Class to perform unit test of {@link FilePolicyForwarderParameterGroup}.
 */
public class FilePolicyForwarderParameterGroupTest {

    @Test
    public void testBuilderAndGetters() {
        final FilePolicyForwarderParameterBuilder builder = new FilePolicyForwarderParameterBuilder();
        builder.setPath("/tmp").setVerbose(true);
        final FilePolicyForwarderParameterGroup configurationParameters =
                new FilePolicyForwarderParameterGroup(builder);
        configurationParameters.setName("myConfiguration");

        assertEquals("myConfiguration", configurationParameters.getName());
        assertTrue(configurationParameters.isVerbose());
        assertEquals("/tmp", configurationParameters.getPath());
        assertEquals(ValidationStatus.CLEAN, configurationParameters.validate().getStatus());
    }

    @Test
    public void testInvalidPath() {
        final FilePolicyForwarderParameterBuilder builder = new FilePolicyForwarderParameterBuilder();
        builder.setPath("").setVerbose(false);
        final FilePolicyForwarderParameterGroup configurationParameters =
                new FilePolicyForwarderParameterGroup(builder);
        configurationParameters.setName("myConfiguration");

        assertEquals(ValidationStatus.INVALID, configurationParameters.validate().getStatus());
    }
}

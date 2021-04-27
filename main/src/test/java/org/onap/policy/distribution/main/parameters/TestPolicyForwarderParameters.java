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
import static org.onap.policy.distribution.main.parameters.CommonTestData.FORWARDER_CLASS_NAME;
import static org.onap.policy.distribution.main.parameters.CommonTestData.FORWARDER_CONFIGURATION_PARAMETERS;
import static org.onap.policy.distribution.main.parameters.CommonTestData.FORWARDER_TYPE;

import org.junit.Test;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;

/**
 * Class to perform unit test of PolicyForwarderParameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestPolicyForwarderParameters {

    @Test
    public void testPolicyForwarderParameters() {
        final PolicyForwarderParameters pFParameters =
                new PolicyForwarderParameters(FORWARDER_TYPE, FORWARDER_CLASS_NAME, FORWARDER_CONFIGURATION_PARAMETERS);
        final ValidationResult validationResult = pFParameters.validate();
        assertEquals(FORWARDER_TYPE, pFParameters.getForwarderType());
        assertEquals(FORWARDER_CLASS_NAME, pFParameters.getForwarderClassName());
        assertTrue(validationResult.isValid());
    }

    @Test
    public void testPolicyForwarderParameters_InvalidForwarderType() {
        final PolicyForwarderParameters pFParameters =
                new PolicyForwarderParameters("", FORWARDER_CLASS_NAME, FORWARDER_CONFIGURATION_PARAMETERS);
        final ValidationResult validationResult = pFParameters.validate();
        assertEquals("", pFParameters.getForwarderType());
        assertEquals(FORWARDER_CLASS_NAME, pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"forwarderType\" value \"\" INVALID, is blank");
    }

    @Test
    public void testPolicyForwarderParameters_InvalidForwarderClassName() {
        final PolicyForwarderParameters pFParameters =
                new PolicyForwarderParameters(FORWARDER_TYPE, "", FORWARDER_CONFIGURATION_PARAMETERS);
        final ValidationResult validationResult = pFParameters.validate();
        assertEquals(CommonTestData.FORWARDER_TYPE, pFParameters.getForwarderType());
        assertEquals("", pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"forwarderClassName\" value \"\" INVALID, is blank");
    }

    @Test
    public void testPolicyForwarderParameters_InvalidForwarderTypeAndClassName() {
        final PolicyForwarderParameters pFParameters =
                new PolicyForwarderParameters("", "", FORWARDER_CONFIGURATION_PARAMETERS);
        final ValidationResult validationResult = pFParameters.validate();
        assertEquals("", pFParameters.getForwarderType());
        assertEquals("", pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"forwarderType\" value \"\" INVALID, is blank",
                        "\"forwarderClassName\" value \"\" INVALID, is blank");
    }

    @Test
    public void testPolicyForwarderParameters_NullForwarderType() {
        final PolicyForwarderParameters pFParameters =
                new PolicyForwarderParameters(null, FORWARDER_CLASS_NAME, FORWARDER_CONFIGURATION_PARAMETERS);
        final ValidationResult validationResult = pFParameters.validate();
        assertEquals(null, pFParameters.getForwarderType());
        assertEquals(FORWARDER_CLASS_NAME, pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"forwarderType\" value \"null\" INVALID, is null");
    }

    @Test
    public void testPolicyForwarderParameters_NullForwarderClassName() {
        final PolicyForwarderParameters pFParameters =
                new PolicyForwarderParameters(FORWARDER_TYPE, null, FORWARDER_CONFIGURATION_PARAMETERS);
        final ValidationResult validationResult = pFParameters.validate();
        assertEquals(FORWARDER_TYPE, pFParameters.getForwarderType());
        assertEquals(null, pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"forwarderClassName\" value \"null\" INVALID, is null");
    }

    @Test
    public void testPolicyForwarderParameters_InvalidForwarderClass() {
        final PolicyForwarderParameters pFParameters = new PolicyForwarderParameters(FORWARDER_TYPE,
                FORWARDER_CLASS_NAME + "Invalid", FORWARDER_CONFIGURATION_PARAMETERS);
        final ValidationResult validationResult = pFParameters.validate();
        assertEquals(FORWARDER_TYPE, pFParameters.getForwarderType());
        assertEquals(FORWARDER_CLASS_NAME + "Invalid", pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("class is not in the classpath");
    }
}

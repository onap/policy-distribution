/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2025 OpenInfra Foundation Europe.
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.onap.policy.distribution.main.parameters.CommonTestData.FORWARDER_CLASS_NAME;
import static org.onap.policy.distribution.main.parameters.CommonTestData.FORWARDER_CONFIGURATION_PARAMETERS;
import static org.onap.policy.distribution.main.parameters.CommonTestData.FORWARDER_TYPE;

import org.junit.jupiter.api.Test;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;

/**
 * Class to perform unit test of PolicyForwarderParameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
class TestPolicyForwarderParameters {

    @Test
    void testPolicyForwarderParameters() {
        final var pFParameters =
                new PolicyForwarderParameters(FORWARDER_TYPE, FORWARDER_CLASS_NAME, FORWARDER_CONFIGURATION_PARAMETERS);
        final var validationResult = pFParameters.validate();
        assertEquals(FORWARDER_TYPE, pFParameters.getForwarderType());
        assertEquals(FORWARDER_CLASS_NAME, pFParameters.getForwarderClassName());
        assertTrue(validationResult.isValid());
    }

    @Test
    void testPolicyForwarderParameters_InvalidForwarderType() {
        final var pFParameters =
                new PolicyForwarderParameters("", FORWARDER_CLASS_NAME, FORWARDER_CONFIGURATION_PARAMETERS);
        final var validationResult = pFParameters.validate();
        assertEquals("", pFParameters.getForwarderType());
        assertEquals(FORWARDER_CLASS_NAME, pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"forwarderType\" value \"\" INVALID, is blank");
    }

    @Test
    void testPolicyForwarderParameters_InvalidForwarderClassName() {
        final var pFParameters =
                new PolicyForwarderParameters(FORWARDER_TYPE, "", FORWARDER_CONFIGURATION_PARAMETERS);
        final var validationResult = pFParameters.validate();
        assertEquals(CommonTestData.FORWARDER_TYPE, pFParameters.getForwarderType());
        assertEquals("", pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"forwarderClassName\" value \"\" INVALID, is blank");
    }

    @Test
    void testPolicyForwarderParameters_InvalidForwarderTypeAndClassName() {
        final var pFParameters =
                new PolicyForwarderParameters("", "", FORWARDER_CONFIGURATION_PARAMETERS);
        final var validationResult = pFParameters.validate();
        assertEquals("", pFParameters.getForwarderType());
        assertEquals("", pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"forwarderType\" value \"\" INVALID, is blank",
                        "\"forwarderClassName\" value \"\" INVALID, is blank");
    }

    @Test
    void testPolicyForwarderParameters_NullForwarderType() {
        final var pFParameters =
                new PolicyForwarderParameters(null, FORWARDER_CLASS_NAME, FORWARDER_CONFIGURATION_PARAMETERS);
        final var validationResult = pFParameters.validate();
        assertThat(pFParameters.getForwarderType()).isNull();
        assertEquals(FORWARDER_CLASS_NAME, pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"forwarderType\" value \"null\" INVALID, is null");
    }

    @Test
    void testPolicyForwarderParameters_NullForwarderClassName() {
        final var pFParameters =
                new PolicyForwarderParameters(FORWARDER_TYPE, null, FORWARDER_CONFIGURATION_PARAMETERS);
        final var validationResult = pFParameters.validate();
        assertEquals(FORWARDER_TYPE, pFParameters.getForwarderType());
        assertThat(pFParameters.getForwarderClassName()).isNull();
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"forwarderClassName\" value \"null\" INVALID, is null");
    }

    @Test
    void testPolicyForwarderParameters_InvalidForwarderClass() {
        final var pFParameters = new PolicyForwarderParameters(FORWARDER_TYPE,
                FORWARDER_CLASS_NAME + "Invalid", FORWARDER_CONFIGURATION_PARAMETERS);
        final var validationResult = pFParameters.validate();
        assertEquals(FORWARDER_TYPE, pFParameters.getForwarderType());
        assertEquals(FORWARDER_CLASS_NAME + "Invalid", pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("class is not in the classpath");
    }
}

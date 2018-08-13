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

package org.onap.policy.distribution.main.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.common.parameters.GroupValidationResult;
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
                new PolicyForwarderParameters(CommonTestData.FORWARDER_TYPE, CommonTestData.FORWARDER_CLASS_NAME);
        final GroupValidationResult validationResult = pFParameters.validate();
        assertEquals(CommonTestData.FORWARDER_TYPE, pFParameters.getForwarderType());
        assertEquals(CommonTestData.FORWARDER_CLASS_NAME, pFParameters.getForwarderClassName());
        assertTrue(validationResult.isValid());
    }

    @Test
    public void testPolicyForwarderParameters_InvalidForwarderType() {
        final PolicyForwarderParameters pFParameters =
                new PolicyForwarderParameters("", CommonTestData.FORWARDER_CLASS_NAME);
        final GroupValidationResult validationResult = pFParameters.validate();
        assertEquals("", pFParameters.getForwarderType());
        assertEquals(CommonTestData.FORWARDER_CLASS_NAME, pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult().contains(
                "field \"forwarderType\" type \"java.lang.String\" value \"\" INVALID, must be a non-blank string"));
    }

    @Test
    public void testPolicyForwarderParameters_InvalidForwarderClassName() {
        final PolicyForwarderParameters pFParameters = new PolicyForwarderParameters(CommonTestData.FORWARDER_TYPE, "");
        final GroupValidationResult validationResult = pFParameters.validate();
        assertEquals(CommonTestData.FORWARDER_TYPE, pFParameters.getForwarderType());
        assertEquals("", pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult()
                .contains("field \"forwarderClassName\" type \"java.lang.String\" value \"\" INVALID, "
                        + "must be a non-blank string containing full class name of the forwarder"));
    }

    @Test
    public void testPolicyForwarderParameters_InvalidForwarderTypeAndClassName() {
        final PolicyForwarderParameters pFParameters = new PolicyForwarderParameters("", "");
        final GroupValidationResult validationResult = pFParameters.validate();
        assertEquals("", pFParameters.getForwarderType());
        assertEquals("", pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult().contains(
                "field \"forwarderType\" type \"java.lang.String\" value \"\" INVALID, must be a non-blank string"));
        assertTrue(validationResult.getResult()
                .contains("field \"forwarderClassName\" type \"java.lang.String\" value \"\" INVALID, "
                        + "must be a non-blank string containing full class name of the forwarder"));
    }

    @Test
    public void testPolicyForwarderParameters_NullForwarderType() {
        final PolicyForwarderParameters pFParameters =
                new PolicyForwarderParameters(null, CommonTestData.FORWARDER_CLASS_NAME);
        final GroupValidationResult validationResult = pFParameters.validate();
        assertEquals(null, pFParameters.getForwarderType());
        assertEquals(CommonTestData.FORWARDER_CLASS_NAME, pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult()
                .contains("field \"forwarderType\" type \"java.lang.String\" value \"null\" INVALID, "
                        + "must be a non-blank string"));
    }

    @Test
    public void testPolicyForwarderParameters_NullForwarderClassName() {
        final PolicyForwarderParameters pFParameters =
                new PolicyForwarderParameters(CommonTestData.FORWARDER_TYPE, null);
        final GroupValidationResult validationResult = pFParameters.validate();
        assertEquals(CommonTestData.FORWARDER_TYPE, pFParameters.getForwarderType());
        assertEquals(null, pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult()
                .contains("field \"forwarderClassName\" type \"java.lang.String\" value \"null\" INVALID, "
                        + "must be a non-blank string containing full class name of the forwarder"));
    }

    @Test
    public void testPolicyForwarderParameters_InvalidForwarderClass() {
        final PolicyForwarderParameters pFParameters = new PolicyForwarderParameters(CommonTestData.FORWARDER_TYPE,
                CommonTestData.FORWARDER_CLASS_NAME + "Invalid");
        final GroupValidationResult validationResult = pFParameters.validate();
        assertEquals(CommonTestData.FORWARDER_TYPE, pFParameters.getForwarderType());
        assertEquals(CommonTestData.FORWARDER_CLASS_NAME + "Invalid", pFParameters.getForwarderClassName());
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult().contains("policy forwarder class not found in classpath"));
    }
}

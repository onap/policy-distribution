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
import org.onap.policy.distribution.reception.parameters.PolicyDecoderParameters;

/**
 * Class to perform unit test of PolicyDecoderParameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestPolicyDecoderParameters {

    @Test
    public void testPolicyDecoderParameters() {
        final PolicyDecoderParameters pDParameters =
                new PolicyDecoderParameters(CommonTestData.DECODER_TYPE, CommonTestData.DECODER_CLASS_NAME);
        final GroupValidationResult validationResult = pDParameters.validate();
        assertEquals(CommonTestData.DECODER_TYPE, pDParameters.getDecoderType());
        assertEquals(CommonTestData.DECODER_CLASS_NAME, pDParameters.getDecoderClassName());
        assertTrue(validationResult.isValid());
    }

    @Test
    public void testPolicyDecoderParameters_InvalidDecoderType() {
        final PolicyDecoderParameters pDParameters = new PolicyDecoderParameters("", CommonTestData.DECODER_CLASS_NAME);
        final GroupValidationResult validationResult = pDParameters.validate();
        assertEquals("", pDParameters.getDecoderType());
        assertEquals(CommonTestData.DECODER_CLASS_NAME, pDParameters.getDecoderClassName());
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult().contains(
                "field \"decoderType\" type \"java.lang.String\" value \"\" INVALID, must be a non-blank string"));
    }

    @Test
    public void testPolicyDecoderParameters_InvalidDecoderClassName() {
        final PolicyDecoderParameters pDParameters = new PolicyDecoderParameters(CommonTestData.DECODER_TYPE, "");
        final GroupValidationResult validationResult = pDParameters.validate();
        assertEquals(CommonTestData.DECODER_TYPE, pDParameters.getDecoderType());
        assertEquals("", pDParameters.getDecoderClassName());
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult()
                .contains("field \"decoderClassName\" type \"java.lang.String\" value \"\" INVALID, "
                        + "must be a non-blank string containing full class name of the decoder"));
    }

    @Test
    public void testPolicyDecoderParameters_InvalidDecoderTypeAndClassName() {
        final PolicyDecoderParameters pDParameters = new PolicyDecoderParameters("", "");
        final GroupValidationResult validationResult = pDParameters.validate();
        assertEquals("", pDParameters.getDecoderType());
        assertEquals("", pDParameters.getDecoderClassName());
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult().contains(
                "field \"decoderType\" type \"java.lang.String\" value \"\" INVALID, must be a non-blank string"));
        assertTrue(validationResult.getResult()
                .contains("field \"decoderClassName\" type \"java.lang.String\" value \"\" INVALID, "
                        + "must be a non-blank string containing full class name of the decoder"));
    }

    @Test
    public void testPolicyDecoderParameters_NullDecoderType() {
        final PolicyDecoderParameters pDParameters = 
                new PolicyDecoderParameters(null, CommonTestData.DECODER_CLASS_NAME);
        final GroupValidationResult validationResult = pDParameters.validate();
        assertEquals(null, pDParameters.getDecoderType());
        assertEquals(CommonTestData.DECODER_CLASS_NAME, pDParameters.getDecoderClassName());
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult().contains(
                "field \"decoderType\" type \"java.lang.String\" value \"null\" INVALID, must be a non-blank string"));
    }

    @Test
    public void testPolicyDecoderParameters_NullDecoderClassName() {
        final PolicyDecoderParameters pDParameters = new PolicyDecoderParameters(CommonTestData.DECODER_TYPE, null);
        final GroupValidationResult validationResult = pDParameters.validate();
        assertEquals(CommonTestData.DECODER_TYPE, pDParameters.getDecoderType());
        assertEquals(null, pDParameters.getDecoderClassName());
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult()
                .contains("field \"decoderClassName\" type \"java.lang.String\" value \"null\" INVALID, "
                        + "must be a non-blank string containing full class name of the decoder"));
    }

    @Test
    public void testPolicyDecoderParameters_InvalidDecoderClass() {
        final PolicyDecoderParameters pDParameters =
                new PolicyDecoderParameters(CommonTestData.DECODER_TYPE, CommonTestData.DECODER_CLASS_NAME + "Invalid");
        final GroupValidationResult validationResult = pDParameters.validate();
        assertEquals(CommonTestData.DECODER_TYPE, pDParameters.getDecoderType());
        assertEquals(CommonTestData.DECODER_CLASS_NAME + "Invalid", pDParameters.getDecoderClassName());
        assertFalse(validationResult.isValid());
        assertTrue(validationResult.getResult().contains("policy decoder class not found in classpath"));
    }
}

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.onap.policy.distribution.main.parameters.CommonTestData.DECODER_CLASS_NAME;
import static org.onap.policy.distribution.main.parameters.CommonTestData.DECODER_CONFIGURATION_PARAMETERS;
import static org.onap.policy.distribution.main.parameters.CommonTestData.DECODER_TYPE;

import org.junit.jupiter.api.Test;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderParameters;

/**
 * Class to perform unit test of PolicyDecoderParameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
class TestPolicyDecoderParameters {

    @Test
    void testPolicyDecoderParameters() {
        final var pDParameters =
                new PolicyDecoderParameters(DECODER_TYPE, DECODER_CLASS_NAME, DECODER_CONFIGURATION_PARAMETERS);
        final var validationResult = pDParameters.validate();
        assertEquals(DECODER_TYPE, pDParameters.getDecoderType());
        assertEquals(DECODER_CLASS_NAME, pDParameters.getDecoderClassName());
        assertEquals(DECODER_CONFIGURATION_PARAMETERS, pDParameters.getDecoderConfigurationName());
        assertTrue(validationResult.isValid());
    }

    @Test
    void testPolicyDecoderParameters_InvalidDecoderType() {
        final var pDParameters =
                new PolicyDecoderParameters("", DECODER_CLASS_NAME, DECODER_CONFIGURATION_PARAMETERS);
        final var validationResult = pDParameters.validate();
        assertEquals("", pDParameters.getDecoderType());
        assertEquals(DECODER_CLASS_NAME, pDParameters.getDecoderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"decoderType\" value \"\" INVALID, is blank");
    }

    @Test
    void testPolicyDecoderParameters_InvalidDecoderClassName() {
        final var pDParameters =
                new PolicyDecoderParameters(DECODER_TYPE, "", DECODER_CONFIGURATION_PARAMETERS);
        final var validationResult = pDParameters.validate();
        assertEquals(DECODER_TYPE, pDParameters.getDecoderType());
        assertEquals("", pDParameters.getDecoderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"decoderClassName\" value \"\" INVALID, is blank");
    }

    @Test
    void testPolicyDecoderParameters_InvalidDecoderTypeAndClassName() {
        final var pDParameters =
                new PolicyDecoderParameters("", "", DECODER_CONFIGURATION_PARAMETERS);
        final var validationResult = pDParameters.validate();
        assertEquals("", pDParameters.getDecoderType());
        assertEquals("", pDParameters.getDecoderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"decoderType\" value \"\" INVALID, is blank",
                        "\"decoderClassName\" value \"\" INVALID, is blank");
    }

    @Test
    void testPolicyDecoderParameters_NullDecoderType() {
        final var pDParameters =
                new PolicyDecoderParameters(null, DECODER_CLASS_NAME, DECODER_CONFIGURATION_PARAMETERS);
        final var validationResult = pDParameters.validate();
        assertThat(pDParameters.getDecoderType()).isNull();
        assertEquals(DECODER_CLASS_NAME, pDParameters.getDecoderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"decoderType\" value \"null\" INVALID, is null");
    }

    @Test
    void testPolicyDecoderParameters_NullDecoderClassName() {
        final var pDParameters =
                new PolicyDecoderParameters(DECODER_TYPE, null, DECODER_CONFIGURATION_PARAMETERS);
        final var validationResult = pDParameters.validate();
        assertEquals(DECODER_TYPE, pDParameters.getDecoderType());
        assertThat(pDParameters.getDecoderClassName()).isNull();
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"decoderClassName\" value \"null\" INVALID, is null");
    }

    @Test
    void testPolicyDecoderParameters_InvalidDecoderClass() {
        final var pDParameters = new PolicyDecoderParameters(DECODER_TYPE,
                DECODER_CLASS_NAME + "Invalid", DECODER_CONFIGURATION_PARAMETERS);
        final var validationResult = pDParameters.validate();
        assertEquals(DECODER_TYPE, pDParameters.getDecoderType());
        assertEquals(DECODER_CLASS_NAME + "Invalid", pDParameters.getDecoderClassName());
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"decoderClassName\"", "classpath");
    }
}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
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

package org.onap.policy.distribution.reception.parameters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.onap.policy.distribution.reception.handling.DummyDecoder;

/**
 * Class for unit testing PolicyDecoderParameters class.
 *
 * @author Adheli Tavares (adheli.tavares@est.tech)
 *
 */
class TestPolicyDecoderParameters {

    static final String DECODER_CLASS_NAME = DummyDecoder.class.getName();
    static final String DECODER_CONFIG = "decoderConfigName";
    static final String DECODER_TYPE = "DummyDecoder";

    @Test
    void testValidate_DecoderTypeEmptyNull() {
        var sutParams = new PolicyDecoderParameters(null, DECODER_CLASS_NAME, DECODER_CONFIG);

        assertThat(sutParams.validate().getResult()).contains("\"decoderType\" value \"null\" INVALID, is null");

        sutParams.setName("");

        assertThat(sutParams.validate().getResult()).contains("\"decoderType\" value \"\" INVALID, is blank")
                        .doesNotContain("not found in classpath");
    }

    @Test
    void testValidate_ClassNameEmptyNull() {
        var nullClassName = new PolicyDecoderParameters(DECODER_TYPE, null, DECODER_CONFIG);

        assertThat(nullClassName.validate().getResult())
                        .contains("\"decoderClassName\" value \"null\" INVALID, is null");

        var emptyClassName = new PolicyDecoderParameters(DECODER_TYPE, "", DECODER_CONFIG);

        assertThat(emptyClassName.validate().getResult()).contains("\"decoderClassName\" value \"\" INVALID, is blank");
    }
}

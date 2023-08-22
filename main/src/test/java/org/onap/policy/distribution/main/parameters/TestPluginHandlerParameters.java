/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
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

import org.junit.jupiter.api.Test;
import org.onap.policy.distribution.reception.parameters.PluginHandlerParameters;

/**
 * Class to perform unit test of PluginHandlerParameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
class TestPluginHandlerParameters {
    CommonTestData commonTestData = new CommonTestData();

    @Test
    void testPluginHandlerParameters() {
        final var policyDecoders = commonTestData.getPolicyDecoders(false);
        final var policyForwarders = commonTestData.getPolicyForwarders(false);
        final var pHParameters = new PluginHandlerParameters(policyDecoders, policyForwarders);
        final var validationResult = pHParameters.validate();
        assertEquals(policyDecoders.get(CommonTestData.DUMMY_DECODER_KEY),
                        pHParameters.getPolicyDecoders().get(CommonTestData.DUMMY_DECODER_KEY));
        assertEquals(policyForwarders.get(CommonTestData.DUMMY_ENGINE_FORWARDER_KEY),
                        pHParameters.getPolicyForwarders().get(CommonTestData.DUMMY_ENGINE_FORWARDER_KEY));
        assertTrue(validationResult.isValid());
    }

    @Test
    void testPluginHandlerParameters_NullPolicyDecoders() {
        final var policyForwarders = commonTestData.getPolicyForwarders(false);
        final var pHParameters = new PluginHandlerParameters(null, policyForwarders);
        assertThat(pHParameters.validate().getResult()).contains("\"policyDecoders\" value \"null\" INVALID, is null");
    }

    @Test
    void testPluginHandlerParameters_NullPolicyForwarders() {
        final var policyDecoders = commonTestData.getPolicyDecoders(false);
        final var pHParameters = new PluginHandlerParameters(policyDecoders, null);
        assertThat(pHParameters.validate().getResult())
                        .contains("\"policyForwarders\" value \"null\" INVALID, is null");
    }

    @Test
    void testPluginHandlerParameters_EmptyPolicyDecoders() {
        final var policyDecoders = commonTestData.getPolicyDecoders(true);
        final var policyForwarders = commonTestData.getPolicyForwarders(false);
        final var pHParameters = new PluginHandlerParameters(policyDecoders, policyForwarders);
        var result = pHParameters.validate();
        assertFalse(result.isValid());
        assertThat(pHParameters.validate().getResult()).contains("\"policyDecoders\"", "minimum");
    }

    @Test
    void testPluginHandlerParameters_EmptyPolicyForwarders() {
        final var policyForwarders = commonTestData.getPolicyForwarders(true);
        final var policyDecoders = commonTestData.getPolicyDecoders(false);
        final var pHParameters = new PluginHandlerParameters(policyDecoders, policyForwarders);
        var result = pHParameters.validate();
        assertFalse(result.isValid());
        assertThat(pHParameters.validate().getResult()).contains("\"policyForwarders\"", "minimum");
    }
}

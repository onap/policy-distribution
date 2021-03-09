/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
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

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;

/**
 * Class for unit testing PluginHandlerParameters class.
 *
 * @author Adheli Tavares (adheli.tavares@est.tech)
 *
 */
public class TestPluginHandlerParameters {

    @Test
    public void testValidate_PolicyDecodersEmpty() {
        PluginHandlerParameters emptyDecoder = new PluginHandlerParameters(new HashMap<>(), getPolicyForwarders());

        GroupValidationResult result = emptyDecoder.validate();

        assertThat(result.getResult())
                .contains("parameter group map \"policyDecoders\" INVALID, must have at least one policy decoder");
        assertThat(result.getResult()).doesNotContain(
                "parameter group map \"policyForwarders\" INVALID, must have at least one policy forwarder");
    }

    @Test
    public void testValidate_PolicyForwardersNullEmpty() {
        PluginHandlerParameters emptyDecoder = new PluginHandlerParameters(getPolicyDecoders(), new HashMap<>());

        GroupValidationResult result = emptyDecoder.validate();

        assertThat(result.getResult())
                .contains("parameter group map \"policyForwarders\" INVALID, must have at least one policy forwarder");
        assertThat(result.getResult()).doesNotContain(
                "parameter group map \"policyDecoders\" INVALID, must have at least one policy decoder");
    }

    private Map<String, PolicyDecoderParameters> getPolicyDecoders() {
        final Map<String, PolicyDecoderParameters> policyDecoders = new HashMap<>();
        final PolicyDecoderParameters pDParameters =
                new PolicyDecoderParameters("DummyDecoder", "DummyDecoder", "dummyDecoderConfiguration");
        policyDecoders.put("DummyDecoder", pDParameters);

        return policyDecoders;
    }

    private Map<String, PolicyForwarderParameters> getPolicyForwarders() {
        final Map<String, PolicyForwarderParameters> policyForwarders = new HashMap<>();
        final PolicyForwarderParameters pFParameters =
                new PolicyForwarderParameters("DummyForwarder", "DummyForwarder", "dummyForwarderConfiguration");
        policyForwarders.put("DummyForwarder", pFParameters);

        return policyForwarders;
    }

}

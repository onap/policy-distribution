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
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;

/**
 * Class for unit testing ReceptionHandlerParameters class.
 *
 * @author Adheli Tavares (adheli.tavares@est.tech)
 *
 */
public class TestReceptionHandlerParameters {

    @Test
    public void testValidate_ClassNotFound() {
        final String className = "org.onap.policy.distribution.reception.testclasses.NotExistent";

        ReceptionHandlerParameters sutParams = getReceptionHandlerParameters(className);
        sutParams.setName(className);

        assertThat(sutParams.validate().getResult()).contains("reception handler class not found in classpath");
    }

    @Test
    public void testValidate_ReceptionHandlerTypeNullEmpty() {
        final String className = "org.onap.policy.distribution.reception.handling.DummyReceptionHandler";
        final PluginHandlerParameters pHParameters =
                new PluginHandlerParameters(getPolicyDecoders(), getPolicyForwarders());

        ReceptionHandlerParameters nullType = new ReceptionHandlerParameters(null, className, className, pHParameters);

        assertThatNoException().isThrownBy(() -> nullType.validate());
        assertThat(nullType.validate().getResult()).contains("field \"receptionHandlerType\" type \"java.lang.String\""
                + " value \"null\" INVALID, must be a non-blank string");

        ReceptionHandlerParameters emptyType = new ReceptionHandlerParameters("", className, className, pHParameters);

        assertThatNoException().isThrownBy(() -> emptyType.validate());
        assertThat(emptyType.validate().getResult()).contains("field \"receptionHandlerType\" type \"java.lang.String\""
                + " value \"\" INVALID, must be a non-blank string");
        assertThat(emptyType.validate().getResult()).doesNotContain("reception handler class not found in classpath");
    }

    @Test
    public void testValidate_ReceptionHandlerClassNameNullEmpty() {
        final PluginHandlerParameters pHParameters =
                new PluginHandlerParameters(getPolicyDecoders(), getPolicyForwarders());

        ReceptionHandlerParameters nullType = new ReceptionHandlerParameters("DummyReceptionHandler", null,
                "dummyReceptionHandlerConfiguration", pHParameters);

        assertThatNoException().isThrownBy(() -> nullType.validate());
        assertThat(nullType.validate().getResult())
                .contains("field \"receptionHandlerClassName\" type \"java.lang.String\" value "
                        + "\"null\" INVALID, must be a non-blank string containing full class name "
                        + "of the reception handler");

        ReceptionHandlerParameters emptyType = new ReceptionHandlerParameters("DummyReceptionHandler", "",
                "dummyReceptionHandlerConfiguration", pHParameters);

        assertThatNoException().isThrownBy(() -> emptyType.validate());
        assertThat(emptyType.validate().getResult())
                .contains("field \"receptionHandlerClassName\" type \"java.lang.String\" value "
                        + "\"\" INVALID, must be a non-blank string containing full class name of "
                        + "the reception handler");
    }

    @Test
    public void testValidate_PluginHandlerParametersNull() {
        final String className = "org.onap.policy.distribution.reception.testclasses.DummyReceptionHandler";

        ReceptionHandlerParameters sutParams = new ReceptionHandlerParameters("DummyReceptionHandler", className,
                "dummyReceptionHandlerConfiguration", null);

        assertThatNoException().isThrownBy(() -> sutParams.validate());
        assertThat(sutParams.validate().getResult())
                .contains("parameter group \"UNDEFINED\" INVALID, must have a plugin handler");
    }

    private ReceptionHandlerParameters getReceptionHandlerParameters(String className) {
        final Map<String, PolicyDecoderParameters> policyDecoders = getPolicyDecoders();
        final Map<String, PolicyForwarderParameters> policyForwarders = getPolicyForwarders();
        final PluginHandlerParameters pHParameters = new PluginHandlerParameters(policyDecoders, policyForwarders);
        final ReceptionHandlerParameters rhParameters = new ReceptionHandlerParameters("DummyReceptionHandler",
                className, "dummyReceptionHandlerConfiguration", pHParameters);
        return rhParameters;
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

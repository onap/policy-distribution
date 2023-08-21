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

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;
import org.onap.policy.distribution.reception.handling.DummyDecoder;

/**
 * Class for unit testing ReceptionHandlerParameters class.
 *
 * @author Adheli Tavares (adheli.tavares@est.tech)
 *
 */
class TestReceptionHandlerParameters {

    @Test
    void testValidate_ClassNotFound() {
        final var className = "org.onap.policy.distribution.reception.testclasses.NotExistent";

        var sutParams = getReceptionHandlerParameters(className);
        sutParams.setName(className);

        assertThat(sutParams.validate().getResult()).contains("class is not in the classpath");
    }

    @Test
    void testValidate_ReceptionHandlerTypeNullEmpty() {
        final var className = DummyDecoder.class.getName();
        final var pHParameters =
                new PluginHandlerParameters(getPolicyDecoders(), getPolicyForwarders());

        var nullType = new ReceptionHandlerParameters(null, className, className, pHParameters);

        assertThat(nullType.validate().getResult())
                        .contains("\"receptionHandlerType\" value \"null\" INVALID, is null");

        var emptyType = new ReceptionHandlerParameters("", className, className, pHParameters);

        assertThat(emptyType.validate().getResult()).contains("\"receptionHandlerType\" value \"\" INVALID, is blank")
                        .doesNotContain("classpath");
    }

    @Test
    void testValidate_ReceptionHandlerClassNameNullEmpty() {
        final PluginHandlerParameters pHParameters =
                new PluginHandlerParameters(getPolicyDecoders(), getPolicyForwarders());

        var nullType = new ReceptionHandlerParameters("DummyReceptionHandler", null,
                "dummyReceptionHandlerConfiguration", pHParameters);

        assertThat(nullType.validate().getResult())
                .contains("\"receptionHandlerClassName\" value \"null\" INVALID, is null");

        var emptyType = new ReceptionHandlerParameters("DummyReceptionHandler", "",
                "dummyReceptionHandlerConfiguration", pHParameters);

        assertThat(emptyType.validate().getResult())
                .contains("\"receptionHandlerClassName\" value \"\" INVALID, is blank");
    }

    @Test
    void testValidate_PluginHandlerParametersNull() {
        final var className = "org.onap.policy.distribution.reception.testclasses.DummyReceptionHandler";

        var sutParams = new ReceptionHandlerParameters("DummyReceptionHandler", className,
                "dummyReceptionHandlerConfiguration", null);

        assertThat(sutParams.validate().getResult())
                .contains("\"pluginHandlerParameters\" value \"null\" INVALID, is null");
    }

    private ReceptionHandlerParameters getReceptionHandlerParameters(String className) {
        final var policyDecoders = getPolicyDecoders();
        final var policyForwarders = getPolicyForwarders();
        final var pHParameters = new PluginHandlerParameters(policyDecoders, policyForwarders);
        final var rhParameters = new ReceptionHandlerParameters("DummyReceptionHandler",
                className, "dummyReceptionHandlerConfiguration", pHParameters);
        return rhParameters;
    }

    private Map<String, PolicyDecoderParameters> getPolicyDecoders() {
        final Map<String, PolicyDecoderParameters> policyDecoders = new HashMap<>();

        final var pDParameters =
                new PolicyDecoderParameters("DummyDecoder", DummyDecoder.class.getName(), "dummyDecoderConfiguration");
        policyDecoders.put("DummyDecoder", pDParameters);

        return policyDecoders;
    }

    private Map<String, PolicyForwarderParameters> getPolicyForwarders() {
        final Map<String, PolicyForwarderParameters> policyForwarders = new HashMap<>();

        final var pFParameters = new PolicyForwarderParameters("DummyForwarder",
                        DummyDecoder.class.getName(), "dummyForwarderConfiguration");
        policyForwarders.put("DummyForwarder", pFParameters);

        return policyForwarders;
    }

}

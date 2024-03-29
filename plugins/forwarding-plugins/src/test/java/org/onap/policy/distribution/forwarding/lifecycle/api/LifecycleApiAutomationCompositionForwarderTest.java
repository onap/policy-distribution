/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022-2023 Nordix Foundation.
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

package org.onap.policy.distribution.forwarding.lifecycle.api;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.forwarding.testclasses.CommonTestData;
import org.onap.policy.distribution.forwarding.testclasses.LifecycleApiAutomationCompositionSimulatorMain;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;

/**
 * Class to perform unit test of {@link LifecycleApiAutomationCompositionForwarder}.
 *
 * @author Sirisha Manchikanti (sirisha.manchikanti@est.tech)
 */
class LifecycleApiAutomationCompositionForwarderTest {

    private static final String AUTOMATION_COMPOSITION =
            "src/test/resources/parameters/sample_automation_composition.json";
    private final StandardCoder standardCoder = new StandardCoder();
    private static final LifecycleApiAutomationCompositionSimulatorMain simulator =
            new LifecycleApiAutomationCompositionSimulatorMain();

    /**
     * Set up.
     *
     * @throws CoderException if any error occurs
     * @throws PolicyForwardingException if any error occurs
     * @throws InterruptedException if any error occurs
     */
    @BeforeAll
    static void setUp() throws PolicyForwardingException, CoderException, InterruptedException {
        final var parameterGroup = CommonTestData.getPolicyForwarderParameters(
                "src/test/resources/parameters/LifecycleApiAutomationCompositionForwarderParameters.json",
                LifecycleApiAutomationCompositionForwarderParameters.class);
        ParameterService.register(parameterGroup);
        simulator.startLifecycleApiSimulator();
        if (!NetworkUtil.isTcpPortOpen("localhost", 6969, 50, 200L)) {
            throw new IllegalStateException("cannot connect to port 6969");
        }
    }

    /**
     * Tear down.
     */
    @AfterAll
    static void tearDown() {
        ParameterService.deregister(LifecycleApiAutomationCompositionForwarderParameters.class.getSimpleName());
        simulator.stopLifecycycleApiSimulator();
    }

    @Test
    void testForwardAutomationCompositionUsingSimulator() throws Exception {
        assertThatCode(() -> {
            final var toscaServiceTemplate = standardCoder.decode(
                ResourceUtils.getResourceAsString(AUTOMATION_COMPOSITION), ToscaServiceTemplate.class);

            final var forwarder =
                    new LifecycleApiAutomationCompositionForwarder();
            forwarder.configure(LifecycleApiAutomationCompositionForwarderParameters.class.getSimpleName());

            final Collection<ToscaEntity> automationCompositionList = new ArrayList<>();
            automationCompositionList.add(toscaServiceTemplate);

            forwarder.forward(automationCompositionList);

        }).doesNotThrowAnyException();
    }

    @Test
    void testForwardAutomationCompositionFailureUsingSimulator() throws Exception {

        final var toscaEntity = new ToscaEntity();
        toscaEntity.setName("FailureCase");

        final var forwarder = new LifecycleApiAutomationCompositionForwarder();
        forwarder.configure(LifecycleApiAutomationCompositionForwarderParameters.class.getSimpleName());

        final Collection<ToscaEntity> automationCompositionList = new ArrayList<>();
        automationCompositionList.add(toscaEntity);

        assertThatThrownBy(() -> forwarder.forward(automationCompositionList))
                .isInstanceOf(PolicyForwardingException.class)
                .hasMessageContaining("Failed forwarding the following entities:");
    }
}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 AT&T Intellectual Property. All rights reserved.
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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.forwarding.testclasses.CommonTestData;
import org.onap.policy.distribution.forwarding.testclasses.LifecycleApiSimulatorMain;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;

/**
 * Class to perform unit test of {@link LifecycleApiPolicyForwarder}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@est.tech)
 */
public class LifecycleApiPolicyForwarderTest {

    private static final String POLICY = "src/test/resources/parameters/sample_policy.json";
    private static final String POLICY_ERROR = "src/test/resources/parameters/sample_policy_failure.json";
    private static final String POLICY_TYPE = "src/test/resources/parameters/sample_policy_type.json";
    private StandardCoder standardCoder = new StandardCoder();
    private static LifecycleApiSimulatorMain simulator = new LifecycleApiSimulatorMain();

    /**
     * Set up.
     *
     * @throws CoderException if any error occurs
     * @throws PolicyForwardingException if any error occurs
     * @throws InterruptedException if any error occurs
     */
    @BeforeClass
    public static void setUp() throws PolicyForwardingException, CoderException, InterruptedException {
        final ParameterGroup parameterGroup = CommonTestData.getPolicyForwarderParameters(
                "src/test/resources/parameters/LifecycleApiPolicyForwarderParameters.json",
                LifecycleApiForwarderParameters.class);
        ParameterService.register(parameterGroup);
        simulator.startLifecycycleApiSimulator();
        if (!NetworkUtil.isTcpPortOpen("0.0.0.0", 6969, 50, 200L)) {
            throw new IllegalStateException("cannot connect to port 6969");
        }
    }

    /**
     * Tear down.
     */
    @AfterClass
    public static void tearDown() {
        ParameterService.deregister(LifecycleApiForwarderParameters.class.getSimpleName());
        simulator.stopLifecycycleApiSimulator();
    }

    @Test
    public void testForwardPolicyUsingSimulator() throws Exception {
        final ToscaServiceTemplate toscaServiceTemplate1 =
                standardCoder.decode(ResourceUtils.getResourceAsString(POLICY_TYPE), ToscaServiceTemplate.class);
        final ToscaServiceTemplate toscaServiceTemplate2 =
                standardCoder.decode(ResourceUtils.getResourceAsString(POLICY), ToscaServiceTemplate.class);

        final LifecycleApiPolicyForwarder forwarder = new LifecycleApiPolicyForwarder();
        forwarder.configure(LifecycleApiForwarderParameters.class.getSimpleName());

        final Collection<ToscaEntity> policies = new ArrayList<>();
        policies.add(toscaServiceTemplate1);
        policies.add(toscaServiceTemplate2);

        assertThatCode(() -> forwarder.forward(policies)).doesNotThrowAnyException();
    }

    @Test
    public void testForwardPolicyFailureUsingSimulator() throws Exception {

        final ToscaServiceTemplate toscaServiceTemplate1 =
                standardCoder.decode(ResourceUtils.getResourceAsString(POLICY_TYPE), ToscaServiceTemplate.class);
        final ToscaServiceTemplate toscaServiceTemplate2 =
                standardCoder.decode(ResourceUtils.getResourceAsString(POLICY), ToscaServiceTemplate.class);
        final ToscaServiceTemplate toscaServiceTemplate3 =
                standardCoder.decode(ResourceUtils.getResourceAsString(POLICY_ERROR), ToscaServiceTemplate.class);
        final ToscaEntity unsupportedPolicy = new UnsupportedPolicy();

        final LifecycleApiPolicyForwarder forwarder = new LifecycleApiPolicyForwarder();
        forwarder.configure(LifecycleApiForwarderParameters.class.getSimpleName());

        final Collection<ToscaEntity> policies = new ArrayList<>();
        policies.add(toscaServiceTemplate1);
        policies.add(toscaServiceTemplate2);
        policies.add(toscaServiceTemplate3);
        policies.add(unsupportedPolicy);

        assertThatThrownBy(() -> forwarder.forward(policies)).isInstanceOf(PolicyForwardingException.class)
                .hasMessageContaining("Failed forwarding the following entities:");
    }

    class UnsupportedPolicy extends ToscaEntity {

        @Override
        public String getName() {
            return "unsupported";
        }
    }
}

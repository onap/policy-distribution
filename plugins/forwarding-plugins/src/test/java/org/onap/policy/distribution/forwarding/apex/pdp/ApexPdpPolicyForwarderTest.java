/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.distribution.forwarding.apex.pdp;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.policy.apex.core.deployment.EngineServiceFacade;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.forwarding.xacml.pdp.testclasses.CommonTestData;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;

/**
 * Class to perform unit test of {@link ApexPdpPolicyForwarder}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ApexPdpPolicyForwarderTest {

    private static final boolean IGNORE_CONFLICTS = false;
    private static final boolean FORCE_UPDATE = true;
    private static final String GROUP_NAME = "apexPdpConfiguration";

    @Mock
    EngineServiceFacade engineServiceFacade;

    /**
     * Set up.
     */
    @BeforeClass
    public static void setUp() {
        final ParameterGroup parameterGroup = CommonTestData.getPolicyForwarderParameters(
                "src/test/resources/parameters/ApexPdpPolicyForwarderParameters.json",
                ApexPdpPolicyForwarderParameterGroup.class);

        parameterGroup.setName(GROUP_NAME);
        ParameterService.register(parameterGroup);
    }

    /**
     * Tear down.
     */
    @AfterClass
    public static void tearDown() {
        ParameterService.deregister(GROUP_NAME);
    }

    @Test
    public void testForwardPolicy() throws ApexException, FileNotFoundException, IOException, PolicyForwardingException,
            NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

        final Collection<ToscaEntity> policies = new ArrayList<>();
        final ApexPdpPolicyForwarder forwarder = new ApexPdpPolicyForwarder();
        forwarder.configure(GROUP_NAME);

        final Field forwarderField = forwarder.getClass().getDeclaredField("engineServiceFacade");
        forwarderField.setAccessible(true);
        forwarderField.set(forwarder, engineServiceFacade);

        createPolicy(policies, "policy", "APEX", "Sample Policy of apex");

        try {
            forwarder.forward(policies);
            verify(engineServiceFacade, times(1)).init();
            verify(engineServiceFacade, times(1)).deployModel(eq("policy"), anyObject(), eq(IGNORE_CONFLICTS),
                    eq(FORCE_UPDATE));
        } catch (final Exception exp) {
            fail("Test must not throw an exception");
        }
    }

    @Test
    public void testForwardPolicyError()
            throws ApexException, FileNotFoundException, IOException, PolicyForwardingException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {

        final Collection<ToscaEntity> policies = new ArrayList<>();
        final ApexPdpPolicyForwarder forwarder = new ApexPdpPolicyForwarder();
        forwarder.configure(GROUP_NAME);

        Mockito.doThrow(new ApexException("Failed")).when(engineServiceFacade).deployModel(eq("policy1"), anyObject(),
                eq(IGNORE_CONFLICTS), eq(FORCE_UPDATE));

        final Field decodersField = forwarder.getClass().getDeclaredField("engineServiceFacade");
        decodersField.setAccessible(true);
        decodersField.set(forwarder, engineServiceFacade);

        createPolicy(policies, "policy1", "APEX", "Sample Policy of apex");

        try {
            forwarder.forward(policies);
            fail("Test must throw an exception");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().contains("Error sending policy to apex-pdp engine"));
        }

    }

    @Test
    public void testForwardMoreThanOnePolicy()
            throws ApexException, FileNotFoundException, IOException, PolicyForwardingException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {

        final Collection<ToscaEntity> policies = new ArrayList<>();
        final ApexPdpPolicyForwarder forwarder = new ApexPdpPolicyForwarder();
        forwarder.configure(GROUP_NAME);

        final Field forwarderField = forwarder.getClass().getDeclaredField("engineServiceFacade");
        forwarderField.setAccessible(true);
        forwarderField.set(forwarder, engineServiceFacade);

        createPolicy(policies, "policy1", "APEX", "Sample Policy of apex");
        createPolicy(policies, "policy2", "APEX", "Sample Policy of apex");

        try {
            forwarder.forward(policies);
            fail("Test must throw an exception");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().contains("More than one apex policy cannot be forwarded to an apex engine"));
        }
    }

    @Test
    public void testForwardUnsupportedPolicy()
            throws ApexException, FileNotFoundException, IOException, PolicyForwardingException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {

        final Collection<ToscaEntity> policies = new ArrayList<>();
        final ApexPdpPolicyForwarder forwarder = new ApexPdpPolicyForwarder();
        forwarder.configure(GROUP_NAME);

        final Field forwarderField = forwarder.getClass().getDeclaredField("engineServiceFacade");
        forwarderField.setAccessible(true);
        forwarderField.set(forwarder, engineServiceFacade);

        final ToscaEntity policy = new UnsupportedPolicy();
        policies.add(policy);

        try {
            forwarder.forward(policies);
            fail("Test must throw an exception");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().contains("Ignoring the policy as it is not of type ToscaPolicy"));
        }
    }

    class UnsupportedPolicy extends ToscaEntity {

        @Override
        public String getName() {
            return "unsupported";
        }
    }

    private void createPolicy(final Collection<ToscaEntity> policies, final String name, final String type,
            final String description) {
        final ToscaPolicy policy = new ToscaPolicy();
        policy.setName(name);
        policy.setType(type);
        policy.setDescription(description);
        policies.add(policy);
    }
}

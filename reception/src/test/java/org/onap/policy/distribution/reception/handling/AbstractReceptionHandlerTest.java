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

package org.onap.policy.distribution.reception.handling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.model.Policy;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;

public class AbstractReceptionHandlerTest {

    @Test
    public void testInputReceived() throws PolicyDecodingException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        AbstractReceptionHandler handler = new DummyReceptionHandler();

        Policy generatedPolicy1 = new DummyPolicy1();
        Policy generatedPolicy2 = new DummyPolicy2();

        PolicyDecoder<PolicyInput, Policy> policyDecoder1 =
                new DummyDecoder(true, Collections.singletonList(generatedPolicy1));
        PolicyDecoder<PolicyInput, Policy> policyDecoder2 =
                new DummyDecoder(true, Collections.singletonList(generatedPolicy2));

        Collection<PolicyDecoder<PolicyInput, Policy>> policyDecoders = new ArrayList<>();
        policyDecoders.add(policyDecoder1);
        policyDecoders.add(policyDecoder2);

        DummyPolicyForwarder policyForwarder1 = new DummyPolicyForwarder();
        DummyPolicyForwarder policyForwarder2 = new DummyPolicyForwarder();

        Collection<PolicyForwarder> policyForwarders = new ArrayList<>();
        policyForwarders.add(policyForwarder1);
        policyForwarders.add(policyForwarder2);

        setUpPlugins(handler, policyDecoders, policyForwarders);

        handler.inputReceived(new DummyPolicyInput());

        assertEquals(2, policyForwarder1.getNumberOfPoliciesReceived());
        assertTrue(policyForwarder1.receivedPolicy(generatedPolicy1));
        assertTrue(policyForwarder1.receivedPolicy(generatedPolicy2));
        assertEquals(2, policyForwarder2.getNumberOfPoliciesReceived());
        assertTrue(policyForwarder2.receivedPolicy(generatedPolicy1));
        assertTrue(policyForwarder2.receivedPolicy(generatedPolicy2));
    }

    @Test(expected = PolicyDecodingException.class)
    public void testInputReceivedNoSupportingDecoder() throws PolicyDecodingException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        AbstractReceptionHandler handler = new DummyReceptionHandler();

        PolicyDecoder<PolicyInput, Policy> policyDecoder = new DummyDecoder(false, Collections.emptyList());
        DummyPolicyForwarder policyForwarder = new DummyPolicyForwarder();
        setUpPlugins(handler, Collections.singleton(policyDecoder), Collections.singleton(policyForwarder));

        handler.inputReceived(new DummyPolicyInput());
    }

    @Test(expected = PolicyDecodingException.class)
    public void testInputReceivedNoDecoder() throws PolicyDecodingException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        AbstractReceptionHandler handler = new DummyReceptionHandler();

        DummyPolicyForwarder policyForwarder = new DummyPolicyForwarder();
        setUpPlugins(handler, Collections.emptySet(), Collections.singleton(policyForwarder));

        handler.inputReceived(new DummyPolicyInput());
    }

    class DummyReceptionHandler extends AbstractReceptionHandler {
        @Override
        protected void initializeReception(String parameterGroupName) {}

        @Override
        public void destroy() {}
    }

    class DummyPolicyInput implements PolicyInput {
    }
    class DummyPolicy1 implements Policy {
    }
    class DummyPolicy2 implements Policy {
    }

    public class DummyDecoder implements PolicyDecoder<PolicyInput, Policy> {

        private boolean canHandleValue;
        private Collection<Policy> policesToReturn;

        public DummyDecoder(boolean canHandleValue, Collection<Policy> policesToReturn) {
            this.canHandleValue = canHandleValue;
            this.policesToReturn = policesToReturn;
        }

        @Override
        public boolean canHandle(PolicyInput policyInput) {
            return canHandleValue;
        }

        @Override
        public Collection<Policy> decode(PolicyInput input) throws PolicyDecodingException {
            return policesToReturn;
        }
    }

    public class DummyPolicyForwarder implements PolicyForwarder {
        private int numberOfPoliciesReceived = 0;
        private Collection<Policy> policiesReceived = new ArrayList<>();

        @Override
        public void forward(Collection<Policy> policies) throws PolicyForwardingException {
            numberOfPoliciesReceived += policies.size();
            policiesReceived.addAll(policies);
        }

        public int getNumberOfPoliciesReceived() {
            return numberOfPoliciesReceived;
        }

        public boolean receivedPolicy(Policy policy) {
            return policiesReceived.contains(policy);
        }
    }

    /**
     * Only needed until code is added for instantiating plugins from paramater file
     */
    private void setUpPlugins(AbstractReceptionHandler receptionHandler,
            Collection<PolicyDecoder<PolicyInput, Policy>> decoders, Collection<PolicyForwarder> forwarders)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        PluginHandler pluginHandler = new PluginHandler("");

        Field decodersField = pluginHandler.getClass().getDeclaredField("policyDecoders");
        decodersField.setAccessible(true);
        decodersField.set(pluginHandler, decoders);

        Field forwardersField = pluginHandler.getClass().getDeclaredField("policyForwarders");
        forwardersField.setAccessible(true);
        forwardersField.set(pluginHandler, forwarders);

        Field pluginHandlerField = AbstractReceptionHandler.class.getDeclaredField("pluginHandler");
        pluginHandlerField.setAccessible(true);
        pluginHandlerField.set(receptionHandler, pluginHandler);
    }

}

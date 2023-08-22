/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2022 Nordix Foundation.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PluginInitializationException;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.parameters.PluginHandlerParameters;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderParameters;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;

/**
 * Class to perform unit test of AbstractReceptionHandler.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
class AbstractReceptionHandlerTest {

    private static final String DISTRIBUTION_GROUP = "DummyDistributionGroup";
    private static final String DECODER_TYPE = "DummyDecoder";
    private static final String DECODER_CLASS_NAME = "org.onap.policy.distribution.reception.handling.DummyDecoder";
    private static final String DECODER_KEY = "DummyDecoderKey";
    private static final String FORWARDER_KEY = "DummyForwarderKey";
    private static final String FORWARDER_TYPE = "DummyForwarder";
    private static final String FORWARDER_CLASS_NAME =
            "org.onap.policy.distribution.reception.handling.DummyPolicyForwarder";
    private static final String FORWARDER_CONFIGURATION_PARAMETERS = "DummyConfiguration";
    private static final String DECODER_CONFIGURATION_PARAMETERS = "DummyDecoderConfiguration";

    @Test
    void testInputReceived() throws PolicyDecodingException, NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, PluginInitializationException {
        final var handler = new DummyReceptionHandler();

        final var generatedPolicy1 = new DummyPolicy1();
        final var generatedPolicy2 = new DummyPolicy2();

        final var policyDecoder1 =
                new DummyDecoder(true, Collections.singletonList(generatedPolicy1));
        final var policyDecoder2 =
                new DummyDecoder(true, Collections.singletonList(generatedPolicy2));

        final Collection<PolicyDecoder<PolicyInput, ToscaEntity>> policyDecoders = new ArrayList<>();
        policyDecoders.add(policyDecoder1);
        policyDecoders.add(policyDecoder2);

        final var policyForwarder1 = new DummyPolicyForwarder();
        final var policyForwarder2 = new DummyPolicyForwarder();

        final Collection<PolicyForwarder> policyForwarders = new ArrayList<>();
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

    @Test
    void testInputReceivedNoSupportingDecoder() throws NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException, PluginInitializationException {
        final var handler = new DummyReceptionHandler();

        final var policyDecoder = new DummyDecoder(false, Collections.emptyList());
        final var policyForwarder = new DummyPolicyForwarder();
        setUpPlugins(handler, Collections.singleton(policyDecoder), Collections.singleton(policyForwarder));
        var policyInput = new DummyPolicyInput();
        assertThrows(PolicyDecodingException.class, () -> handler.inputReceived(policyInput));
    }

    @Test
    void testInputReceivedNoDecoder() throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, PluginInitializationException {
        final var handler = new DummyReceptionHandler();

        final var policyForwarder = new DummyPolicyForwarder();
        setUpPlugins(handler, Collections.emptySet(), Collections.singleton(policyForwarder));

        var policyInput = new DummyPolicyInput();
        assertThrows(PolicyDecodingException.class, () -> handler.inputReceived(policyInput));
    }

    static class DummyPolicyInput implements PolicyInput {
    }

    static class DummyPolicy1 extends ToscaEntity {

        @Override
        public String getName() {
            return null;
        }
    }

    static class DummyPolicy2 extends ToscaEntity {

        @Override
        public String getName() {
            return null;
        }
    }

    private void setUpPlugins(final AbstractReceptionHandler receptionHandler,
            final Collection<PolicyDecoder<PolicyInput, ToscaEntity>> decoders,
            final Collection<PolicyForwarder> forwarders) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, PluginInitializationException {
        final var pluginParameters = getPluginHandlerParameters();
        pluginParameters.setName(DISTRIBUTION_GROUP);
        ParameterService.register(pluginParameters);
        final var pluginHandler = new PluginHandler(pluginParameters.getName());

        final var decodersField = pluginHandler.getClass().getDeclaredField("policyDecoders");
        decodersField.setAccessible(true);
        decodersField.set(pluginHandler, decoders);

        final var forwardersField = pluginHandler.getClass().getDeclaredField("policyForwarders");
        forwardersField.setAccessible(true);
        forwardersField.set(pluginHandler, forwarders);

        final var pluginHandlerField = AbstractReceptionHandler.class.getDeclaredField("pluginHandler");
        pluginHandlerField.setAccessible(true);
        pluginHandlerField.set(receptionHandler, pluginHandler);
        ParameterService.deregister(pluginParameters.getName());
    }

    private Map<String, PolicyDecoderParameters> getPolicyDecoders() {
        final Map<String, PolicyDecoderParameters> policyDecoders = new HashMap<>();
        final var pDParameters =
                new PolicyDecoderParameters(DECODER_TYPE, DECODER_CLASS_NAME, DECODER_CONFIGURATION_PARAMETERS);
        policyDecoders.put(DECODER_KEY, pDParameters);
        return policyDecoders;
    }

    private Map<String, PolicyForwarderParameters> getPolicyForwarders() {
        final Map<String, PolicyForwarderParameters> policyForwarders = new HashMap<>();
        final var pFParameters =
                new PolicyForwarderParameters(FORWARDER_TYPE, FORWARDER_CLASS_NAME, FORWARDER_CONFIGURATION_PARAMETERS);
        policyForwarders.put(FORWARDER_KEY, pFParameters);
        return policyForwarders;
    }

    private PluginHandlerParameters getPluginHandlerParameters() {
        final var policyDecoders = getPolicyDecoders();
        final var policyForwarders = getPolicyForwarders();
        return new PluginHandlerParameters(policyDecoders, policyForwarders);
    }

}

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

import java.util.ArrayList;
import java.util.Collection;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.model.Policy;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/***
 * Base implementation of {@link ReceptionHandler}. All reception handlers should extend this base
 * class by implementing the {@link #initializeReception(String)} method to perform the
 * specific initialization required to receive inputs and by invoking
 * {@link #inputReceived(PolicyInput)} when the reception handler receives input
 */
public abstract class AbstractReceptionHandler implements ReceptionHandler {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(AbstractReceptionHandler.class);

    private PluginHandler pluginHandler;

    @Override
    public void initialize(String parameterGroupName) {
        pluginHandler = new PluginHandler(parameterGroupName);
        initializeReception(parameterGroupName);
    }

    /**
     * Sub classes must implement this method to perform the specific initialization required to
     * receive inputs, for example setting up subscriptions
     * 
     * @param parameterGroupName the parameter group name
     */
    protected abstract void initializeReception(String parameterGroupName);

    /**
     * Handle input that has been received. The given input shall be decoded using the
     * {@link PolicyDecoder}s configured for this reception handler and forwarded using the
     * {@link PolicyForwarder}s configured for this reception handler.
     * 
     * @param policyInput the input that has been received
     * @throws PolicyDecodingException if an error occurs in decoding a policy from the received
     *         input
     */
    protected void inputReceived(PolicyInput policyInput) throws PolicyDecodingException {

        Collection<Policy> policies = new ArrayList<>();
        for (PolicyDecoder<PolicyInput, Policy> policyDecoder : getRelevantPolicyDecoders(policyInput)) {
            policies.addAll(policyDecoder.decode(policyInput));
        }

        for (PolicyForwarder policyForwarder : pluginHandler.getPolicyForwarders()) {
            try {
                policyForwarder.forward(policies);
            } catch (PolicyForwardingException policyForwardingException) {
                LOGGER.error("Error when forwarding policies to " + policyForwarder, policyForwardingException);
            }
        }
    }

    private Collection<PolicyDecoder<PolicyInput, Policy>> getRelevantPolicyDecoders(PolicyInput policyInput)
            throws PolicyDecodingException {
        Collection<PolicyDecoder<PolicyInput, Policy>> relevantPolicyDecoders = new ArrayList<>();
        for (PolicyDecoder<PolicyInput, Policy> policyDecoder : pluginHandler.getPolicyDecoders()) {
            if (policyDecoder.canHandle(policyInput)) {
                relevantPolicyDecoders.add(policyDecoder);
            }
        }
        if (relevantPolicyDecoders.isEmpty()) {
            throw new PolicyDecodingException("No decoder available matching requirements");
        }
        return relevantPolicyDecoders;
    }

}

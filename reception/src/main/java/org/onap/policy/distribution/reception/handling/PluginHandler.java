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
import java.util.Map;

import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;
import org.onap.policy.distribution.model.Policy;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.parameters.PluginHandlerParameters;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderParameters;

/**
 * Handles the plugins to policy distribution.
 */
public class PluginHandler {

    private Collection<PolicyDecoder<PolicyInput, Policy>> policyDecoders;
    private Collection<PolicyForwarder> policyForwarders;

    /**
     * Create an instance to instantiate plugins based on the given parameter group.
     *
     * @param parameterGroupName the name of the parameter group
     * @throws PolicyDecodingException
     * @throws PolicyForwardingException
     */
    public PluginHandler(final String parameterGroupName) throws PolicyDecodingException, PolicyForwardingException {
        final PluginHandlerParameters params = (PluginHandlerParameters) ParameterService.get(parameterGroupName);
        initializePolicyDecoders(params.getPolicyDecoders());
        initializePolicyForwarders(params.getPolicyForwarders());
    }

    /**
     * Get the policy decoders.
     *
     * @return the policy decoders
     */
    public Collection<PolicyDecoder<PolicyInput, Policy>> getPolicyDecoders() {
        return policyDecoders;
    }

    /**
     * Get the policy forwarders.
     *
     * @return the policy forwarders
     */
    public Collection<PolicyForwarder> getPolicyForwarders() {
        return policyForwarders;
    }

    /**
     * Initialize policy decoders.
     *
     * @param policyDecoderParameters
     * @throws PolicyDecodingException
     */
    @SuppressWarnings("unchecked")
    private void initializePolicyDecoders(final Map<String, PolicyDecoderParameters> policyDecoderParameters)
            throws PolicyDecodingException {
        policyDecoders = new ArrayList<PolicyDecoder<PolicyInput, Policy>>();
        for (final PolicyDecoderParameters pDParameters : policyDecoderParameters.values()) {
            try {
                final Class<PolicyDecoder<PolicyInput, Policy>> policyDecoderClass =
                        (Class<PolicyDecoder<PolicyInput, Policy>>) Class.forName(pDParameters.getDecoderClassName());
                final PolicyDecoder<PolicyInput, Policy> decoder = policyDecoderClass.newInstance();
                policyDecoders.add(decoder);
            } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException exp) {
                throw new PolicyDecodingException(exp.getMessage());
            }
        }
    }

    /**
     * Initialize policy forwarders
     *
     * @param policyForwarderParameters
     * @throws PolicyForwardingException
     */
    @SuppressWarnings("unchecked")
    private void initializePolicyForwarders(final Map<String, PolicyForwarderParameters> policyForwarderParameters)
            throws PolicyForwardingException {
        policyForwarders = new ArrayList<PolicyForwarder>();
        for (final PolicyForwarderParameters pFParameters : policyForwarderParameters.values()) {
            try {
                final Class<PolicyForwarder> policyForwarderClass =
                        (Class<PolicyForwarder>) Class.forName(pFParameters.getForwarderClassName());
                policyForwarders.add(policyForwarderClass.newInstance());
            } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException exp) {
                throw new PolicyForwardingException(exp.getMessage(), exp.getCause());
            }
        }
    }

}

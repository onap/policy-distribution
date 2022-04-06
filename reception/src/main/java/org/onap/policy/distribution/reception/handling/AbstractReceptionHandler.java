/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019, 2022 Nordix Foundation.
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

package org.onap.policy.distribution.reception.handling;

import java.util.ArrayList;
import java.util.Collection;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PluginInitializationException;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base implementation of {@link ReceptionHandler}. All reception handlers should extend this base class by implementing
 * the {@link #initializeReception(String)} method to perform the specific initialization required to receive inputs and
 * by invoking {@link #inputReceived(PolicyInput)} when the reception handler receives input.
 */
public abstract class AbstractReceptionHandler implements ReceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReceptionHandler.class);

    private PluginHandler pluginHandler;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void initialize(final String parameterGroupName) throws PluginInitializationException {
        final var receptionHandlerParameters = (ReceptionHandlerParameters) ParameterService.get(parameterGroupName);
        pluginHandler = new PluginHandler(receptionHandlerParameters.getPluginHandlerParameters().getName());
        initializeReception(receptionHandlerParameters.getReceptionHandlerConfigurationName());
    }

    /**
     * Sub classes must implement this method to perform the specific initialization required to receive inputs, for
     * example setting up subscriptions.
     *
     * @param parameterGroupName the parameter group name
     * @throws PluginInitializationException if initialization of reception handler fails
     */
    protected abstract void initializeReception(String parameterGroupName) throws PluginInitializationException;

    /**
     * Handle input that has been received. The given input shall be decoded using the {@link PolicyDecoder}s configured
     * for this reception handler and forwarded using the {@link PolicyForwarder}s configured for this reception
     * handler.
     *
     * @param policyInput the input that has been received
     * @throws PolicyDecodingException if an error occurs when no decoders are available
     */
    protected void inputReceived(final PolicyInput policyInput) throws PolicyDecodingException {

        final Collection<ToscaEntity> policies = new ArrayList<>();

        try {
            for (final PolicyDecoder<PolicyInput, ToscaEntity> policyDecoder : getRelevantPolicyDecoders(policyInput)) {
                LOGGER.debug("Policy decoder: {}", policyDecoder.getClass());
                policies.addAll(policyDecoder.decode(policyInput));
            }
        } catch (PolicyDecodingException decodingException) {
            if (decodingException.getMessage().contains("No decoder")) {
                throw decodingException;
            } else {
                LOGGER.error("Couldn't decode the policy", decodingException);
            }
        }

        for (final PolicyForwarder policyForwarder : pluginHandler.getPolicyForwarders()) {
            try {
                LOGGER.debug("Trying to forward policy to {}", policyForwarder.getClass());
                policyForwarder.forward(policies);
            } catch (final PolicyForwardingException policyForwardingException) {
                LOGGER.error("Error when forwarding policies to {}", policyForwarder, policyForwardingException);
            }
        }
    }

    private Collection<PolicyDecoder<PolicyInput, ToscaEntity>> getRelevantPolicyDecoders(final PolicyInput policyInput)
            throws PolicyDecodingException {
        final Collection<PolicyDecoder<PolicyInput, ToscaEntity>> relevantPolicyDecoders = new ArrayList<>();
        for (final PolicyDecoder<PolicyInput, ToscaEntity> policyDecoder : pluginHandler.getPolicyDecoders()) {
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

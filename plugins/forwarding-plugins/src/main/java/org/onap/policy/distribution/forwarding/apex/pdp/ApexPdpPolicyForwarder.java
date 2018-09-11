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

package org.onap.policy.distribution.forwarding.apex.pdp;

import java.util.Collection;

import org.onap.policy.apex.core.deployment.EngineServiceFacade;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.forwarding.xacml.pdp.XacmlPdpPolicyForwarder;
import org.onap.policy.distribution.model.ApexPdpPolicy;
import org.onap.policy.distribution.model.Policy;

/**
 * This class provides an implementation of {@link PolicyForwarder} interface for forwarding the given policies to
 * apex-pdp.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class ApexPdpPolicyForwarder implements PolicyForwarder {

    private static final Logger LOGGER = FlexLogger.getLogger(XacmlPdpPolicyForwarder.class);

    private ApexPdpPolicyForwarderParameterGroup apexForwarderParameters;
    private EngineServiceFacade engineServiceFacade;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void configure(final String parameterGroupName) {
        apexForwarderParameters = ParameterService.get(parameterGroupName);
        engineServiceFacade =
                new EngineServiceFacade(apexForwarderParameters.getHostname(), apexForwarderParameters.getPort());

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void forward(final Collection<Policy> policies) throws PolicyForwardingException {
        for (final Policy policy : policies) {
            if (policy.getClass().isAssignableFrom(ApexPdpPolicy.class)) {
                forwardPolicy((ApexPdpPolicy) policy);
            } else {
                LOGGER.debug("Ignoring the policy as it is not an apex-pdp policy");
            }
        }
    }

    /**
     * Method to forward a given policy to apex-pdp.
     *
     * @param apexPolicy the apex policy
     * @throws PolicyForwardingException if any exception occurs while forwarding policy
     */
    private void forwardPolicy(final ApexPdpPolicy apexPolicy) throws PolicyForwardingException {
        try {
            engineServiceFacade.init();
            engineServiceFacade.deployModel(apexPolicy.getPolicyName(), apexPolicy.getPolicyInputStream(),
                    apexForwarderParameters.isIgnoreConflicts(), apexForwarderParameters.isForceUpdate());

            LOGGER.debug("Sucessfully forwarded the policy to apex-pdp egine at "
                    + apexForwarderParameters.getHostname() + ":" + apexForwarderParameters.getPort());

        } catch (final ApexException exp) {
            final String message = "Error sending policy to apex-pdp engine at" + apexForwarderParameters.getHostname()
                    + ":" + apexForwarderParameters.getPort();
            LOGGER.error(message, exp);
            throw new PolicyForwardingException(message, exp);
        }
    }
}


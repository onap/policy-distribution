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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.onap.policy.apex.core.deployment.EngineServiceFacade;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.forwarding.xacml.pdp.XacmlPdpPolicyForwarder;
import org.onap.policy.distribution.model.Policy;
import org.onap.policy.distribution.model.PolicyAsString;

/**
 * This class provides an implementation of {@link PolicyForwarder} interface for forwarding the given policies to
 * apex-pdp.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class ApexPdpPolicyForwarder implements PolicyForwarder {

    private static final Logger LOGGER = FlexLogger.getLogger(XacmlPdpPolicyForwarder.class);
    private static final String POLICY_TYPE = "APEX";

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
        if (policies.size() > 1) {
            final String message = "More than one apex policy cannot be forwarded to an apex engine";
            LOGGER.debug(message);
            throw new PolicyForwardingException(message);

        } else {
            final Policy policy = (Policy) policies.toArray()[0];
            if (policy.getClass().isAssignableFrom(PolicyAsString.class)
                    && policy.getPolicyType().equalsIgnoreCase(POLICY_TYPE)) {
                forwardPolicy((PolicyAsString) policy);
            } else {
                final String message = "Ignoring the policy as it is not an apex-pdp policy";
                LOGGER.debug(message);
                throw new PolicyForwardingException(message);
            }
        }
    }

    /**
     * Method to forward a given policy to apex-pdp.
     *
     * @param apexPolicy the apex policy
     * @throws PolicyForwardingException if any exception occurs while forwarding policy
     */
    private void forwardPolicy(final PolicyAsString apexPolicy) throws PolicyForwardingException {
        try {
            engineServiceFacade.init();
            final InputStream input = IOUtils.toInputStream(apexPolicy.getPolicy(), "UTF-8");
            engineServiceFacade.deployModel(apexPolicy.getPolicyName(), input,
                    apexForwarderParameters.isIgnoreConflicts(), apexForwarderParameters.isForceUpdate());

            LOGGER.debug("Sucessfully forwarded the policy to apex-pdp egine at "
                    + apexForwarderParameters.getHostname() + ":" + apexForwarderParameters.getPort());

        } catch (final ApexException | IOException exp) {
            final String message = "Error sending policy to apex-pdp engine at" + apexForwarderParameters.getHostname()
                    + ":" + apexForwarderParameters.getPort();
            LOGGER.error(message, exp);
            throw new PolicyForwardingException(message, exp);
        }
    }
}


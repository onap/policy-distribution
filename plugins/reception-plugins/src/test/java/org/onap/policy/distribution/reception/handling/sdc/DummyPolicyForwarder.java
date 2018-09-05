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

package org.onap.policy.distribution.reception.handling.sdc;

import java.util.ArrayList;
import java.util.Collection;

import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.model.Policy;

/**
 * Class to create a dummy forwarder for test cases.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class DummyPolicyForwarder implements PolicyForwarder {
    private int numberOfPoliciesReceived = 0;
    private Collection<Policy> policiesReceived = new ArrayList<>();

    /**
     * {@inheritDoc}.
     */
    @Override
    public void forward(final Collection<Policy> policies) throws PolicyForwardingException {
        numberOfPoliciesReceived += policies.size();
        policiesReceived.addAll(policies);
    }

    /**
     * Returns the number of policies received by this forwarder.
     *
     * @return the integer value
     */
    public int getNumberOfPoliciesReceived() {
        return numberOfPoliciesReceived;
    }

    /**
     * Checks if the forwarder has received a policy with given policy type.
     *
     * @param policyType the policy type
     * @return the boolean result
     */
    public boolean receivedPolicyWithGivenType(final String policyType) {
        for (final Policy policy : policiesReceived) {
            if (policy.getPolicyType().equals(policyType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void configure(final String parameterGroupName) {}
}

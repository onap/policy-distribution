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

package org.onap.policy.distribution.forwarding.xacml.pdp.adapters;

import org.onap.policy.api.PolicyParameters;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;

/**
 * Adapts {@link ToscaPolicy} objects to objects compatible with the XACML PDP API.
 */
public class XacmlPdpOptimizationPolicyAdapter extends AbstractXacmlPdpPolicyAdapter<ToscaPolicy> {

    /**
     * Create an instance to adapt the given {@link ToscaPolicy}.
     *
     * @param optimizationPolicy the {@link ToscaPolicy} to be adapted
     */
    public XacmlPdpOptimizationPolicyAdapter(final ToscaPolicy optimizationPolicy) {
        super(optimizationPolicy);
    }

    @Override
    public PolicyParameters getAsPolicyParameters() {
        final PolicyParameters policyParameters = new PolicyParameters();
        policyParameters.setPolicyName(getPolicy().getName());
        return policyParameters;
    }

}

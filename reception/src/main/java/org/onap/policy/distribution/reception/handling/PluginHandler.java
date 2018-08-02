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

import java.util.Collection;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.model.Policy;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;

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
     */
    public PluginHandler(String parameterGroupName) {
        // Read configuration using common/common-parameters and instantiate decoders and forwarders
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



}

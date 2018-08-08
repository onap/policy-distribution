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

package org.onap.policy.distribution.main.parameters;

import java.util.Map;
import java.util.Map.Entry;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Class to hold all the plugin handler parameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class PluginHandlerParameters implements ParameterGroup {

    private Map<String, PolicyDecoderParameters> policyDecoders;
    private Map<String, PolicyForwarderParameters> policyForwarders;

    /**
     * Return the policyDecoders of this PluginHandlerParameters instance.
     *
     * @return the policyDecoders
     */
    public Map<String, PolicyDecoderParameters> getPolicyDecoders() {
        return policyDecoders;
    }

    /**
     * Return the policyForwarders of this PluginHandlerParameters instance.
     *
     * @return the policyForwarders
     */
    public Map<String, PolicyForwarderParameters> getPolicyForwarders() {
        return policyForwarders;
    }

    @Override
    public String getName() {
        return null;
    }

    /**
     * Validate the plugin handler parameters.
     *
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        if (policyDecoders == null || policyDecoders.size() == 0) {
            validationResult.setResult("policyDecoders", ValidationStatus.INVALID,
                    "must have atleast one policy decoder");
        } else {
            for (final Entry<String, PolicyDecoderParameters> nestedGroupEntry : policyDecoders.entrySet()) {
                validationResult.setResult("policyDecoders", nestedGroupEntry.getKey(),
                        nestedGroupEntry.getValue().validate());
            }
        }
        if (policyForwarders == null || policyForwarders.size() == 0) {
            validationResult.setResult("policyForwarders", ValidationStatus.INVALID,
                    "must have atleast one policy forwarder");
        } else {
            for (final Entry<String, PolicyForwarderParameters> nestedGroupEntry : policyForwarders.entrySet()) {
                validationResult.setResult("policyForwarders", nestedGroupEntry.getKey(),
                        nestedGroupEntry.getValue().validate());
            }
        }
        return validationResult;
    }
}

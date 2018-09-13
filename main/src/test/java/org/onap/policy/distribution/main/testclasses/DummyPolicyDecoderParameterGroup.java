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

package org.onap.policy.distribution.main.testclasses;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderConfigurationParameterGroup;

/**
 * Dummy policy decoder parameter group.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class DummyPolicyDecoderParameterGroup extends PolicyDecoderConfigurationParameterGroup {

    private String policyName;
    private String policyType;

    /**
     * Constructor for instantiating the {@link DummyPolicyDecoderParameterGroup} class.
     *
     * @param policyName the policy name
     * @param policyType the policy type
     */
    public DummyPolicyDecoderParameterGroup(final String policyName, final String policyType) {
        super();
        this.policyName = policyName;
        this.policyType = policyType;
    }


    public String getPolicyName() {
        return policyName;
    }


    public String getPolicyType() {
        return policyType;
    }

    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        return validationResult;
    }

}

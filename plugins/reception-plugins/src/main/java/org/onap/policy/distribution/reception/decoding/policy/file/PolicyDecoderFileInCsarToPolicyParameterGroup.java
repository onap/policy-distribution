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

package org.onap.policy.distribution.reception.decoding.policy.file;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderConfigurationParameterGroup;

/**
 * Holds the parameters for the{@link PolicyDecoderFileInCsarToPolicy}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class PolicyDecoderFileInCsarToPolicyParameterGroup extends PolicyDecoderConfigurationParameterGroup {

    private String policyFileName;
    private String policyTypeFileName;

    /**
     * Constructor for instantiating {@link PolicyDecoderFileInCsarToPolicyParameterGroup} class.
     *
     * @param policyFileName the policy file name
     * @param policyTypeFileName the policy type file name
     */
    public PolicyDecoderFileInCsarToPolicyParameterGroup(final String policyFileName, final String policyTypeFileName) {
        this.policyFileName = policyFileName;
        this.policyTypeFileName = policyTypeFileName;
    }

    public String getPolicyFileName() {
        return policyFileName;
    }

    public String getPolicyTypeFileName() {
        return policyTypeFileName;
    }

    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        if (!ParameterValidationUtils.validateStringParameter(policyFileName)) {
            validationResult.setResult("policyFileName", ValidationStatus.INVALID,
                    "must be a non-blank string containing the policy file name");
        }
        return validationResult;
    }
}

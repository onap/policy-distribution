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

import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;

public class DistributionParameterGroup implements ParameterGroup {
    private String name;

    /**
     * Create the distribution parameter group.
     * 
     * @param name the parameter group name
     */
    public DistributionParameterGroup(final String name) {
        this.name = name;
    }

    /**
     * Return the name of this parameter group instance
     * 
     * @return name the parameter group name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Validate the parameter group
     * 
     * @return the result of the validation
     */
    @Override
    public GroupValidationResult validate() {
        GroupValidationResult validationResult = new GroupValidationResult(this);

        if (name == null || name.trim().length() == 0) {
            validationResult.setResult("name", ValidationStatus.INVALID, "must be a non-blank string");
        }
        
        return validationResult;
    }
}

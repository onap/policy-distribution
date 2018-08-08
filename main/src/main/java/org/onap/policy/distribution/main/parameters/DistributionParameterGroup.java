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
 * Class to hold all parameters needed for Distribution component.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class DistributionParameterGroup implements ParameterGroup {
    private String name;
    private Map<String, ReceptionHandlerParameters> receptionHandlerParameters;

    /**
     * Create the distribution parameter group.
     *
     * @param name the parameter group name
     */
    public DistributionParameterGroup(final String name,
            final Map<String, ReceptionHandlerParameters> receptionHandlerParameters) {
        this.name = name;
        this.receptionHandlerParameters = receptionHandlerParameters;
    }

    /**
     * Return the name of this parameter group instance.
     *
     * @return name the parameter group name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Return the receptionHandlerParameters of this parameter group instance.
     *
     * @return the receptionHandlerParameters
     */
    public Map<String, ReceptionHandlerParameters> getReceptionHandlerParameters() {
        return receptionHandlerParameters;
    }

    /**
     * Validate the parameter group.
     *
     * @return the result of the validation
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        if (name == null || name.trim().length() == 0) {
            validationResult.setResult("name", ValidationStatus.INVALID, "must be a non-blank string");
        } else {
            validateReceptionHandlers(validationResult);
        }
        return validationResult;
    }

    /**
     * Validate the reception handlers.
     *
     */
    private void validateReceptionHandlers(final GroupValidationResult validationResult) {
        if (receptionHandlerParameters == null || receptionHandlerParameters.size() == 0) {
            validationResult.setResult("receptionHandlerParameters", ValidationStatus.INVALID,
                    "must have at least one reception handler");
        } else {
            for (final Entry<String, ReceptionHandlerParameters> nestedGroupEntry : receptionHandlerParameters
                    .entrySet()) {
                validationResult.setResult("receptionHandlerParameters", nestedGroupEntry.getKey(),
                        nestedGroupEntry.getValue().validate());
            }
        }
    }
}

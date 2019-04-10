/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Intel Corp. All rights reserved.
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

package org.onap.policy.distribution.forwarding.file;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.onap.policy.distribution.main.parameters.PolicyForwarderConfigurationParameterGroup;

/**
 * Holds the parameters for the{@link FilePolicyForwarder}.
 */
public class FilePolicyForwarderParameterGroup extends PolicyForwarderConfigurationParameterGroup {
    public static final String POLICY_FORWARDER_PLUGIN_CLASS = FilePolicyForwarder.class.getCanonicalName();

    private String path;
    private boolean verbose;

    /**
     * Constructor for instantiating {@link FilePolicyForwarderParameterGroup} class.
     *
     * @param builder the apex forwarder parameter builder
     */
    public FilePolicyForwarderParameterGroup(final FilePolicyForwarderParameterBuilder builder) {
        this.path = builder.getPath();
        this.verbose = builder.isVerbose();
    }

    public String getPath() {
        return path;
    }

    public boolean isVerbose() {
        return verbose;
    }

    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        if (!ParameterValidationUtils.validateStringParameter(path)) {
            validationResult.setResult("path", ValidationStatus.INVALID,
                    "must be a non-blank string containing path directory");
        }
        return validationResult;
    }
}

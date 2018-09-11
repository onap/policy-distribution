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

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.onap.policy.distribution.main.parameters.PolicyForwarderConfigurationParameterGroup;

/**
 * Holds the parameters for the{@link ApexPdpPolicyForwarder}.
 */
public class ApexPdpPolicyForwarderParameterGroup extends PolicyForwarderConfigurationParameterGroup {
    public static final String POLICY_FORWARDER_PLUGIN_CLASS = ApexPdpPolicyForwarder.class.getCanonicalName();

    private String hostname;
    private int port;
    private boolean ignoreConflicts;
    private boolean forceUpdate;

    /**
     * Constructor for instantiating {@link ApexPdpPolicyForwarderParameterGroup} class.
     *
     * @param builder the apex forwarder parameter builder
     */
    public ApexPdpPolicyForwarderParameterGroup(final ApexPdpPolicyForwarderParameterBuilder builder) {
        this.hostname = builder.getHostname();
        this.port = builder.getPort();
        this.ignoreConflicts = builder.isIgnoreConflicts();
        this.forceUpdate = builder.isForceUpdate();
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public boolean isIgnoreConflicts() {
        return ignoreConflicts;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        if (!ParameterValidationUtils.validateStringParameter(hostname)) {
            validationResult.setResult("hostname", ValidationStatus.INVALID,
                    "must be a non-blank string containing hostname/ipaddress");
        }
        if (!ParameterValidationUtils.validateIntParameter(port)) {
            validationResult.setResult("port", ValidationStatus.INVALID, "must be a positive integer containing port");
        }
        return validationResult;
    }
}

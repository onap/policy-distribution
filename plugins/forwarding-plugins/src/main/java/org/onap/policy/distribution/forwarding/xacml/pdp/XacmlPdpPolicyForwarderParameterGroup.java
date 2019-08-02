/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 AT&T Intellectual Property.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.distribution.forwarding.xacml.pdp;

import lombok.Getter;

import org.onap.policy.common.parameters.annotations.Min;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.distribution.main.parameters.PolicyForwarderConfigurationParameterGroup;

/**
 * Holds the parameters for the{@link XacmlPdpPolicyForwarder}.
 */
@Getter
@NotNull
@NotBlank
public class XacmlPdpPolicyForwarderParameterGroup extends PolicyForwarderConfigurationParameterGroup {
    public static final String POLICY_FORWARDER_PLUGIN_CLASS = XacmlPdpPolicyForwarder.class.getName();

    private boolean useHttps;
    private String hostname;
    @Min(value = 1)
    private int port;
    private String userName;
    private String password;
    private String clientAuth;
    private boolean isManaged;
    private String pdpGroup;

    public XacmlPdpPolicyForwarderParameterGroup() {
        super(XacmlPdpPolicyForwarderParameterGroup.class.getSimpleName());
    }
}

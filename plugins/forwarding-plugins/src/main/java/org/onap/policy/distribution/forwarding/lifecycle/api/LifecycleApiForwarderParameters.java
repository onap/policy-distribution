/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.distribution.forwarding.lifecycle.api;

import lombok.Getter;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.common.parameters.annotations.Valid;
import org.onap.policy.distribution.main.parameters.PolicyForwarderConfigurationParameterGroup;

/**
 * Holds the parameters for the {@link LifecycleApiPolicyForwarder}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@est.tech)
 */
@Getter
@NotNull
@NotBlank
public class LifecycleApiForwarderParameters extends PolicyForwarderConfigurationParameterGroup {
    public static final String POLICY_FORWARDER_PLUGIN_CLASS = LifecycleApiPolicyForwarder.class.getName();

    private @Valid LifecycleApiParameters apiParameters;
    private @Valid LifecycleApiParameters papParameters;
    private boolean isHttps;
    private boolean allowSelfSignedCerts;
    private boolean deployPolicies = true;

    public LifecycleApiForwarderParameters() {
        super(LifecycleApiForwarderParameters.class.getSimpleName());
    }
}

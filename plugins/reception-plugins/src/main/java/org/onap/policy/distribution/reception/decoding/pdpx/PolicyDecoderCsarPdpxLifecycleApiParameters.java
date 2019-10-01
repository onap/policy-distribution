/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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

package org.onap.policy.distribution.reception.decoding.pdpx;

import lombok.Getter;

import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderConfigurationParameterGroup;

/**
 * This class handles the parameters needed for {@link PolicyDecoderCsarPdpxLifecycleApi}.
 */
@Getter
@NotNull
@NotBlank
public class PolicyDecoderCsarPdpxLifecycleApiParameters extends PolicyDecoderConfigurationParameterGroup {

    private String policyNamePrefix;
    private String onapName;
    private String version;
    private String priority;
    private String riskType;
    private String riskLevel;

    public PolicyDecoderCsarPdpxLifecycleApiParameters() {
        super(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());
    }
}


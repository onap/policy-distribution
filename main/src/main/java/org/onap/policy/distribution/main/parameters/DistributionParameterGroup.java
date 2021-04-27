/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019, 2021 AT&T Intellectual Property. All rights reserved.
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

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.common.endpoints.parameters.RestServerParameters;
import org.onap.policy.common.parameters.ParameterGroupImpl;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.common.parameters.annotations.Size;
import org.onap.policy.common.parameters.annotations.Valid;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

/**
 * Class to hold all parameters needed for Distribution component.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
@Getter
@Setter
@NotNull
@NotBlank
public class DistributionParameterGroup extends ParameterGroupImpl {
    // @formatter:off
    @Valid
    private RestServerParameters restServerParameters;

    @Size(min = 1)
    private Map<String, @NotNull @Valid ReceptionHandlerParameters> receptionHandlerParameters;

    private Map<String, @NotNull @Valid ReceptionHandlerConfigurationParameterGroup>
                receptionHandlerConfigurationParameters = new LinkedHashMap<>();

    private Map<String, @NotNull @Valid PolicyForwarderConfigurationParameterGroup>
                policyForwarderConfigurationParameters = new LinkedHashMap<>();

    private Map<String, @NotNull @Valid PolicyDecoderConfigurationParameterGroup>
                policyDecoderConfigurationParameters = new LinkedHashMap<>();
    // @formatter:on

    /**
     * Create the distribution parameter group.
     *
     * @param name the parameter group name
     */
    public DistributionParameterGroup(final String name, final RestServerParameters restServerParameters,
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters,
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurationParameters,
        final Map<String, PolicyForwarderConfigurationParameterGroup> policyForwarderConfigurationParameters,
        final Map<String, PolicyDecoderConfigurationParameterGroup> policyDecoderConfigurationParameters) {
        super(name);
        this.restServerParameters = restServerParameters;
        this.receptionHandlerParameters = receptionHandlerParameters;
        this.receptionHandlerConfigurationParameters = receptionHandlerConfigurationParameters;
        this.policyForwarderConfigurationParameters = policyForwarderConfigurationParameters;
        this.policyDecoderConfigurationParameters = policyDecoderConfigurationParameters;
    }
}

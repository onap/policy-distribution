/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.distribution.reception.parameters;

import java.util.Map;
import lombok.Setter;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.BeanValidator;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.common.parameters.annotations.Size;
import org.onap.policy.common.parameters.annotations.Valid;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;

/**
 * Class to hold all the plugin handler parameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
@NotNull
public class PluginHandlerParameters implements ParameterGroup {

    private static final String PLUGIN_HANDLER = "_PluginHandler";

    @Setter
    private String name;

    @Size(min = 1)
    private Map<String, @NotNull @Valid PolicyDecoderParameters> policyDecoders;

    @Size(min = 1)
    private Map<String, @NotNull @Valid PolicyForwarderParameters> policyForwarders;

    /**
     * Constructor for instantiating PluginHandlerParameters.
     *
     * @param policyDecoders the map of policy decoders
     * @param policyForwarders the map of policy forwarders
     */
    public PluginHandlerParameters(final Map<String, PolicyDecoderParameters> policyDecoders,
            final Map<String, PolicyForwarderParameters> policyForwarders) {
        this.policyDecoders = policyDecoders;
        this.policyForwarders = policyForwarders;
    }

    /**
     * Return the policyDecoders of this PluginHandlerParameters instance.
     *
     * @return the policyDecoders
     */
    public Map<String, PolicyDecoderParameters> getPolicyDecoders() {
        return policyDecoders;
    }

    /**
     * Return the policyForwarders of this PluginHandlerParameters instance.
     *
     * @return the policyForwarders
     */
    public Map<String, PolicyForwarderParameters> getPolicyForwarders() {
        return policyForwarders;
    }

    @Override
    public String getName() {
        return name + PLUGIN_HANDLER;
    }

    /**
     * Validate the plugin handler parameters.
     *
     */
    @Override
    public BeanValidationResult validate() {
        return new BeanValidator().validateTop(getClass().getSimpleName(), this);
    }
}

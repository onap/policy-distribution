/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019, 2022 Nordix Foundation.
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

import lombok.Getter;
import lombok.Setter;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.BeanValidator;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.annotations.ClassName;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.common.parameters.annotations.Valid;

/**
 * Class to hold all the reception handler parameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
@NotNull
@NotBlank
@Getter
public class ReceptionHandlerParameters implements ParameterGroup {
    private @Setter String name;
    private final String receptionHandlerType;
    private final @ClassName String receptionHandlerClassName;
    private final String receptionHandlerConfigurationName;
    private final @Valid PluginHandlerParameters pluginHandlerParameters;

    /**
     * Constructor for instantiating ReceptionHandlerParameters.
     *
     * @param receptionHandlerType the reception handler type
     * @param receptionHandlerClassName the reception handler class name
     * @param receptionHandlerConfigurationName the name of the configuration for the reception handler
     * @param pluginHandlerParameters the plugin handler parameters
     */
    public ReceptionHandlerParameters(final String receptionHandlerType, final String receptionHandlerClassName,
            final String receptionHandlerConfigurationName, final PluginHandlerParameters pluginHandlerParameters) {
        this.receptionHandlerType = receptionHandlerType;
        this.receptionHandlerClassName = receptionHandlerClassName;
        this.receptionHandlerConfigurationName = receptionHandlerConfigurationName;
        this.pluginHandlerParameters = pluginHandlerParameters;
    }

    @Override
    public String getName() {
        return name + "_" + receptionHandlerType;
    }

    /**
     * Validate the reception handler parameters.
     *
     */
    @Override
    public BeanValidationResult validate() {
        return new BeanValidator().validateTop(getClass().getSimpleName(), this);
    }
}

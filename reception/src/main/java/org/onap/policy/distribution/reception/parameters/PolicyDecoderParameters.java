/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.BeanValidator;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.annotations.ClassName;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;

/**
 * Class to hold all the policy decoder parameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
@Getter
@NotBlank
@AllArgsConstructor
public class PolicyDecoderParameters implements ParameterGroup {
    private @NotNull String decoderType;
    private @NotNull @ClassName String decoderClassName;
    private String decoderConfigurationName;

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getName() {
        return getDecoderType();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setName(final String name) {
        this.decoderType = name;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public BeanValidationResult validate() {
        return new BeanValidator().validateTop(getClass().getSimpleName(), this);
    }
}

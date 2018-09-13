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

package org.onap.policy.distribution.reception.parameters;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Class to hold all the policy decoder parameters.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class PolicyDecoderParameters implements ParameterGroup {

    private static final Logger LOGGER = FlexLogger.getLogger(PolicyDecoderParameters.class);

    private String decoderType;
    private String decoderClassName;
    private String decoderConfigurationName;

    /**
     * Constructor for instantiating PolicyDecoderParameters.
     *
     * @param decoderType the policy decoder type
     * @param decoderClassName the policy decoder class name
     * @param decoderConfigurationName the policy decoder configuration name
     */
    public PolicyDecoderParameters(final String decoderType, final String decoderClassName,
            final String decoderConfigurationName) {
        this.decoderType = decoderType;
        this.decoderClassName = decoderClassName;
        this.decoderConfigurationName = decoderConfigurationName;
    }

    /**
     * Return the decoderType of this PolicyDecoderParameters instance.
     *
     * @return the decoderType
     */
    public String getDecoderType() {
        return decoderType;
    }

    /**
     * Return the decoderClassName of this PolicyDecoderParameters instance.
     *
     * @return the decoderClassName
     */
    public String getDecoderClassName() {
        return decoderClassName;
    }

    /**
     * Return the name of the decoder configuration of this {@link PolicyDecoderParameters} instance.
     *
     * @return the the name of the decoder configuration
     */
    public String getDecoderConfigurationName() {
        return decoderConfigurationName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return decoderType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(final String name) {
        this.decoderType = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        if (decoderType == null || decoderType.trim().length() == 0) {
            validationResult.setResult("decoderType", ValidationStatus.INVALID, "must be a non-blank string");
        }
        if (decoderClassName == null || decoderClassName.trim().length() == 0) {
            validationResult.setResult("decoderClassName", ValidationStatus.INVALID,
                    "must be a non-blank string containing full class name of the decoder");
        } else {
            validatePolicyDecoderClass(validationResult);
        }
        return validationResult;
    }

    private void validatePolicyDecoderClass(final GroupValidationResult validationResult) {
        try {
            Class.forName(decoderClassName);
        } catch (final ClassNotFoundException exp) {
            LOGGER.trace("policy decoder class not found in classpath", exp);
            validationResult.setResult("decoderClassName", ValidationStatus.INVALID,
                    "policy decoder class not found in classpath");
        }
    }
}

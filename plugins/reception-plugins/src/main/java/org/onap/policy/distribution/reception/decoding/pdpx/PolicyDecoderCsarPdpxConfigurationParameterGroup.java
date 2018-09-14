/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.onap.policy.distribution.reception.handling.sdc.SdcReceptionHandlerConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderConfigurationParameterGroup;

/**
 * This class handles reading, parsing and validating of the paramaters for the
 * {@link PolicyDecoderCsarPdpx}
 */
public class PolicyDecoderCsarPdpxConfigurationParameterGroup extends PolicyDecoderConfigurationParameterGroup {

    private String policyNamePrefix;
    private String onapName;
    private String version;
    private String priority;
    private String riskType;
    private String riskLevel;

    /**
     * The constructor for instantiating {@link SdcReceptionHandlerConfigurationParameterGroup}
     * class.
     *
     * @param builder the SDC configuration builder
     */
    public PolicyDecoderCsarPdpxConfigurationParameterGroup(
            final PolicyDecoderCsarPdpxConfigurationParameterBuilder builder) {
        policyNamePrefix = builder.getPolicyNamePrefix();
        onapName = builder.getOnapName();
        version = builder.getVersion();
        priority = builder.getPriority();
        riskType = builder.getRiskType();
        riskLevel = builder.getRiskLevel();
    }

    public String getPolicyNamePrefix() {
        return policyNamePrefix;
    }

    public String getOnapName() {
        return onapName;
    }

    public String getVersion() {
        return version;
    }

    public String getPriority() {
        return priority;
    }

    public String getRiskType() {
        return riskType;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        validateStringElement(validationResult, policyNamePrefix, "policyNamePrefix");
        validateStringElement(validationResult, onapName, "onapName");
        validateStringElement(validationResult, version, "version");
        validateStringElement(validationResult, priority, "priority");
        validateStringElement(validationResult, riskType, "riskType");
        validateStringElement(validationResult, riskLevel, "riskLevel");
        return validationResult;
    }

    /**
     * Validate the string element.
     *
     * @param validationResult the result object
     * @param element the element to validate
     * @param elementName the element name for error message
     */
    private void validateStringElement(final GroupValidationResult validationResult, final String element,
            final String elementName) {
        if (!ParameterValidationUtils.validateStringParameter(element)) {
            validationResult.setResult(elementName, ValidationStatus.INVALID,
                    elementName + " must be a non-blank string");
        }
    }
}


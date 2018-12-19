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

package org.onap.policy.distribution.main.parameters;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

/**
 * Class to hold all parameters needed for Distribution component.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class DistributionParameterGroup implements ParameterGroup {
    // @formatter:off
    private String name;
    private RestServerParameters restServerParameters;
    private Map<String, ReceptionHandlerParameters> receptionHandlerParameters;
    private Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurationParameters
        = new LinkedHashMap<>();
    private Map<String, PolicyForwarderConfigurationParameterGroup> policyForwarderConfigurationParameters
        = new LinkedHashMap<>();
    private Map<String, PolicyDecoderConfigurationParameterGroup> policyDecoderConfigurationParameters
        = new LinkedHashMap<>();
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
        this.name = name;
        this.restServerParameters = restServerParameters;
        this.receptionHandlerParameters = receptionHandlerParameters;
        this.receptionHandlerConfigurationParameters = receptionHandlerConfigurationParameters;
        this.policyForwarderConfigurationParameters = policyForwarderConfigurationParameters;
        this.policyDecoderConfigurationParameters = policyDecoderConfigurationParameters;
    }

    /**
     * Return the name of this parameter group instance.
     *
     * @return name the parameter group name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the name of this parameter group instance.
     *
     * @param name the parameter group name
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Return the receptionHandlerParameters of this parameter group instance.
     *
     * @return the receptionHandlerParameters
     */
    public Map<String, ReceptionHandlerParameters> getReceptionHandlerParameters() {
        return receptionHandlerParameters;
    }

    /**
     * Return the restServerParameters of this parameter group instance.
     *
     * @return the restServerParameters
     */
    public RestServerParameters getRestServerParameters() {
        return restServerParameters;
    }

    /**
     * Gets the reception handler configuration parameter map.
     *
     * @return the reception handler configuration parameter map
     */
    public Map<String, ReceptionHandlerConfigurationParameterGroup> getReceptionHandlerConfigurationParameters() {
        return receptionHandlerConfigurationParameters;
    }

    /**
     * Sets the reception handler configuration parameter map.
     *
     * @param receptionHandlerConfigurationParameters the reception handler configuration parameters
     */
    public void setReceptionHandlerConfigurationParameters(
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurationParameters) {
        this.receptionHandlerConfigurationParameters = receptionHandlerConfigurationParameters;
    }

    /**
     * Gets the policy forwarder configuration parameter map.
     *
     * @return the policy forwarder configuration parameter map
     */
    public Map<String, PolicyForwarderConfigurationParameterGroup> getPolicyForwarderConfigurationParameters() {
        return policyForwarderConfigurationParameters;
    }

    /**
     * Sets the policy forwarder configuration parameter map.
     *
     * @param policyForwarderConfigurationParameters the policy forwarder configuration parameters
     */
    public void setPolicyForwarderConfigurationParameters(
        final Map<String, PolicyForwarderConfigurationParameterGroup> policyForwarderConfigurationParameters) {
        this.policyForwarderConfigurationParameters = policyForwarderConfigurationParameters;
    }

    /**
     * Returns the policy decoder configuration parameter map.
     *
     * @return the policyDecoderConfigurationParameters
     */
    public Map<String, PolicyDecoderConfigurationParameterGroup> getPolicyDecoderConfigurationParameters() {
        return policyDecoderConfigurationParameters;
    }

    /**
     * Set the policy decoder configuration parameter map.
     *
     * @param policyDecoderConfigurationParameters the policyDecoderConfigurationParameters to set
     */
    public void setPolicyDecoderConfigurationParameters(
        final Map<String, PolicyDecoderConfigurationParameterGroup> policyDecoderConfigurationParameters) {
        this.policyDecoderConfigurationParameters = policyDecoderConfigurationParameters;
    }

    /**
     * Validate the parameter group.
     *
     * @return the result of the validation
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        if (!ParameterValidationUtils.validateStringParameter(name)) {
            validationResult.setResult("name", ValidationStatus.INVALID, "must be a non-blank string");
        }
        if (restServerParameters == null) {
            validationResult.setResult("restServerParameters", ValidationStatus.INVALID,
                "must have restServerParameters to configure distribution rest server");
        } else {
            validationResult.setResult("restServerParameters", restServerParameters.validate());
        }
        validateReceptionHandlers(validationResult);
        validateForwarderConfigurations(validationResult);
        validateDecoderConfigurations(validationResult);
        return validationResult;
    }

    private void validateReceptionHandlers(final GroupValidationResult validationResult) {
        if (receptionHandlerParameters == null || receptionHandlerParameters.size() == 0) {
            validationResult.setResult("receptionHandlerParameters", ValidationStatus.INVALID,
                "must have at least one reception handler");
        } else {
            for (final Entry<String, ReceptionHandlerParameters> nestedGroupEntry : receptionHandlerParameters
                .entrySet()) {
                validationResult.setResult("receptionHandlerParameters", nestedGroupEntry.getKey(),
                    nestedGroupEntry.getValue().validate());
            }
        }
    }

    private void validateForwarderConfigurations(final GroupValidationResult validationResult) {
        // @formatter:off
        for (final Entry<String, PolicyForwarderConfigurationParameterGroup> configurationParameters :
            policyForwarderConfigurationParameters.entrySet()) {
            validationResult.setResult("policyForwarderConfigurationParameters", configurationParameters.getKey(),
                configurationParameters.getValue().validate());
        }
        // @formatter:on
    }

    private void validateDecoderConfigurations(final GroupValidationResult validationResult) {
        // @formatter:off
        for (final Entry<String, PolicyDecoderConfigurationParameterGroup> configurationParameters :
            policyDecoderConfigurationParameters.entrySet()) {
            validationResult.setResult("policyDecoderConfigurationParameters", configurationParameters.getKey(),
                configurationParameters.getValue().validate());
        }
        // @formatter:on
    }
}

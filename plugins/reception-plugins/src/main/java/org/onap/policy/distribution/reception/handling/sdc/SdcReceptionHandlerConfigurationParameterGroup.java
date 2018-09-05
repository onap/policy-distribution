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

package org.onap.policy.distribution.reception.handling.sdc;

import java.util.List;
import java.util.UUID;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;

/**
 * This class handles reading, parsing and validating of the Policy SDC Service Distribution
 * parameters from Json format, which strictly adheres to the interface:IConfiguration, defined by
 * SDC SDK.
 */
public class SdcReceptionHandlerConfigurationParameterGroup extends ReceptionHandlerConfigurationParameterGroup {

    // Interface of IConfiguration item
    private String asdcAddress;
    private List<String> messageBusAddress;
    private String user;
    private String password;
    private int pollingInterval;
    private int pollingTimeout;
    private String consumerId;
    private List<String> artifactTypes;
    private String consumerGroup;
    private String environmentName;
    private String keystorePath;
    private String keystorePassword;
    private boolean activeserverTlsAuth;
    private boolean isFilterinEmptyResources;
    private Boolean isUseHttpsWithDmaap;

    /**
     * Inner static class is to used as a Builder.
     *
     */
    public static class PssdConfigurationBuilder {
        private String asdcAddress;
        private List<String> messageBusAddress;
        private String user;
        private String password;
        private int pollingInterval;
        private int pollingTimeout;
        private String consumerId;
        private List<String> artifactTypes;
        private String consumerGroup;
        private String environmentName;
        private String keystorePath;
        private String keystorePassword;
        private boolean activeserverTlsAuth;
        private boolean isFilterinEmptyResources;
        private Boolean isUseHttpsWithDmaap;

        public PssdConfigurationBuilder setAsdcAddress(final String val) {
            asdcAddress = val;
            return this;
        }

        public PssdConfigurationBuilder setMessageBusAddress(final List<String> val) {
            messageBusAddress = val;
            return this;
        }

        public PssdConfigurationBuilder setUser(final String val) {
            user = val;
            return this;
        }

        public PssdConfigurationBuilder setPassword(final String val) {
            password = val;
            return this;
        }

        public PssdConfigurationBuilder setPollingInterval(final int val) {
            pollingInterval = val;
            return this;
        }

        public PssdConfigurationBuilder setPollingTimeout(final int val) {
            pollingTimeout = val;
            return this;
        }

        public PssdConfigurationBuilder setConsumerId(final String val) {
            consumerId = val;
            return this;
        }

        public PssdConfigurationBuilder setArtifactTypes(final List<String> val) {
            artifactTypes = val;
            return this;
        }

        public PssdConfigurationBuilder setConsumerGroup(final String val) {
            consumerGroup = val;
            return this;
        }

        public PssdConfigurationBuilder setEnvironmentName(final String val) {
            environmentName = val;
            return this;
        }

        public PssdConfigurationBuilder setKeystorePath(final String val) {
            keystorePath = val;
            return this;
        }

        public PssdConfigurationBuilder setKeystorePassword(final String val) {
            keystorePassword = val;
            return this;
        }

        public PssdConfigurationBuilder setActiveserverTlsAuth(final boolean val) {
            activeserverTlsAuth = val;
            return this;
        }

        public PssdConfigurationBuilder setIsFilterinEmptyResources(final boolean val) {
            isFilterinEmptyResources = val;
            return this;
        }

        public PssdConfigurationBuilder setIsUseHttpsWithDmaap(final Boolean val) {
            isUseHttpsWithDmaap = val;
            return this;
        }

        /**
         * Creates a new PssdConfigurationParametersGroup instance.
         */
        public SdcReceptionHandlerConfigurationParameterGroup build() {
            return new SdcReceptionHandlerConfigurationParameterGroup(this);
        }
    }

    /**
     * The constructor for instantiating PssdConfigurationParametersGroup. It is kept private so
     * that it could only be called by PssdConfigurationBuilder.
     *
     * @param builder stores all the values used by PssdConfigurationParametersGroup
     */
    private SdcReceptionHandlerConfigurationParameterGroup(final PssdConfigurationBuilder builder) {
        asdcAddress = builder.asdcAddress;
        messageBusAddress = builder.messageBusAddress;
        user = builder.user;
        password = builder.password;
        pollingInterval = builder.pollingInterval;
        pollingTimeout = builder.pollingTimeout;
        consumerId = builder.consumerId;
        artifactTypes = builder.artifactTypes;
        consumerGroup = builder.consumerGroup;
        environmentName = builder.environmentName;
        keystorePath = builder.keystorePath;
        keystorePassword = builder.keystorePassword;
        activeserverTlsAuth = builder.activeserverTlsAuth;
        isFilterinEmptyResources = builder.isFilterinEmptyResources;
        isUseHttpsWithDmaap = builder.isUseHttpsWithDmaap;

    }

    public String getAsdcAddress() {
        return asdcAddress;
    }

    public List<String> getMsgBusAddress() {
        return messageBusAddress;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }

    public int getPollingTimeout() {
        return pollingTimeout;
    }

    public String getConsumerID() {
        return consumerId;
    }

    public List<String> getArtifactTypes() {
        return artifactTypes;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public String getKeyStorePassword() {
        return keystorePassword;
    }

    public String getKeyStorePath() {
        return keystorePath;
    }

    public boolean activateServerTLSAuth() {
        return activeserverTlsAuth;
    }

    public boolean isFilterInEmptyResources() {
        return isFilterinEmptyResources;
    }

    public Boolean isUseHttpsWithDmaap() {
        return isUseHttpsWithDmaap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "name =" + getName() + ",TestParameters:[asdcAddress = " + asdcAddress + ", messageBusAddress = "
                + messageBusAddress + ", user = " + user + "]";
    }

    /**
     * Set the name of this group.
     *
     * @param name the name to set.
     */
    @Override
    public void setName(final String name) {
        super.setName(name + "_" + UUID.randomUUID().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        validateStringElement(validationResult, asdcAddress, "asdcAddress");
        validateStringElement(validationResult, user, "user");
        validateStringElement(validationResult, consumerId, "consumerId");
        validateStringElement(validationResult, consumerGroup, "consumerGroup");
        validateStringElement(validationResult, keystorePath, "keystorePath");
        validateStringElement(validationResult, keystorePassword, "keystorePassword");
        validateIntElement(validationResult, pollingInterval, "pollingInterval");
        validateIntElement(validationResult, pollingTimeout, "pollingTimeout");
        validateStringListElement(validationResult, messageBusAddress, "messageBusAddress");
        validateStringListElement(validationResult, artifactTypes, "artifactTypes");
        return validationResult;
    }

    /**
     * Validate the integer Element.
     *
     * @param validationResult the result object
     * @param element the element to validate
     * @param elementName the element name for error message
     */
    private void validateIntElement(final GroupValidationResult validationResult, final int element,
            final String elementName) {
        if (!ParameterValidationUtils.validateIntParameter(element)) {
            validationResult.setResult(elementName, ValidationStatus.INVALID,
                    elementName + " must be a positive integer");
        }
    }

    /**
     * Validate the String List Element.
     *
     * @param validationResult the result object
     * @param element the element to validate
     * @param elementName the element name for error message
     */
    private void validateStringListElement(final GroupValidationResult validationResult, final List<String> element,
            final String elementName) {
        if (element == null) {
            validationResult.setResult(elementName, ValidationStatus.INVALID,
                    elementName + " must be a list of non-blank string");
        } else {
            for (final String temp : element) {
                if (!ParameterValidationUtils.validateStringParameter(temp)) {
                    validationResult.setResult(elementName, ValidationStatus.INVALID,
                            "the string of " + elementName + "must be a non-blank string");
                }
            }
        }
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
        if (!ParameterValidationUtils.validateStringParameter(asdcAddress)) {
            validationResult.setResult(elementName, ValidationStatus.INVALID,
                    elementName + " must be a non-blank string");
        }
    }
}


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

package org.onap.policy.distribution.reception.parameters;

import java.util.List;
import java.util.UUID;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * This class handles reading, parsing and validating of the Policy SDC Service Distribution parameters from Json
 * format, which strictly adheres to the interface:IConfiguration, defined by SDC SDK.
 */
public class PSSDConfigurationParametersGroup implements ParameterGroup {

    // Policy SDC Service Distribution specified field.
    private String name;

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
     *Inner static class is to used as a Builder
     *
     */
    public static class PSSDConfigurationBuilder {
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

        public PSSDConfigurationBuilder setAsdcAddress(String val) { 
            asdcAddress = val;      
            return this; 
        }

        public PSSDConfigurationBuilder setMessageBusAddress(List<String> val) { 
            messageBusAddress = val;           
            return this; 
        }

        public PSSDConfigurationBuilder setUser(String val) { 
            user = val;  
            return this; 
        }

        public PSSDConfigurationBuilder setPassword(String val) { 
            password = val;        
            return this; 
        }

        public PSSDConfigurationBuilder setPollingInterval(int val) { 
            pollingInterval = val;        
            return this; 
        }

        public PSSDConfigurationBuilder setPollingTimeout(int val) { 
            pollingTimeout = val;        
            return this; 
        }

        public PSSDConfigurationBuilder setConsumerId(String val) { 
            consumerId = val;        
            return this; 
        }

        public PSSDConfigurationBuilder setArtifactTypes(List<String>  val) { 
            artifactTypes = val;        
            return this; 
        }

        public PSSDConfigurationBuilder setConsumerGroup(String val) { 
            consumerGroup = val;        
            return this; 
        }

        public PSSDConfigurationBuilder setEnvironmentName(String val) { 
            environmentName = val;        
            return this; 
        }

        public PSSDConfigurationBuilder setKeystorePath(String val) { 
            keystorePath = val;        
            return this; 
        }

        public PSSDConfigurationBuilder setKeystorePassword(String val) { 
            keystorePassword = val;        
            return this; 
        }

        public PSSDConfigurationBuilder setActiveserverTlsAuth(boolean val) { 
            activeserverTlsAuth = val;        
            return this; 
        }

        public PSSDConfigurationBuilder setIsFilterinEmptyResources(boolean val) { 
            isFilterinEmptyResources = val;        
            return this; 
        }

        public PSSDConfigurationBuilder setIsUseHttpsWithDmaap(Boolean val) { 
            isUseHttpsWithDmaap = val;        
            return this; 
        }

        /**
         * it is to create a new PSSDConfigurationParametersGroup instance.
         */
        public PSSDConfigurationParametersGroup build() {
            return new PSSDConfigurationParametersGroup(this);
        }
    }

    /**
     * The constructor for instantiating PSSDConfigurationParametersGroup it is a private
     * so that it could ONLY be instantiated by PSSDConfigurationBuilder
     *
     * @param builder stores all the values used by PSSDConfigurationParametersGroup
     */
    private PSSDConfigurationParametersGroup(PSSDConfigurationBuilder builder) {
        asdcAddress  = builder.asdcAddress;
        messageBusAddress     = builder.messageBusAddress;
        user     = builder.user;
        password          = builder.password;
        pollingInterval       = builder.pollingInterval;
        pollingTimeout = builder.pollingTimeout;
        consumerId = builder.consumerId;
        artifactTypes = builder.artifactTypes;
        consumerGroup = builder.consumerGroup;
        environmentName = builder.environmentName;
        keystorePath = builder.keystorePath;
        keystorePassword = builder.keystorePassword;
        activeserverTlsAuth = builder.activeserverTlsAuth;
        isFilterinEmptyResources = builder.isFilterinEmptyResources;
        isUseHttpsWithDmaap  = builder.isUseHttpsWithDmaap;
        
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
     * Return a string representation of the object
     *
     * @return textually represents this object
     */
    @Override
    public String toString() {
        return "name =" + name + ",TestParameters:[asdcAddress = " + asdcAddress + ", messageBusAddress = "
                + messageBusAddress + ", user = " + user + "]";
    }

    /**
     * Return the name of this parameter group instance
     *
     * @return name the parameter group name
     */
    @Override
    public String getName() {
        return name ;
    }

    /** 
     * Validate all the int Elements 
     *
     */
    private void validateIntElement(final GroupValidationResult validationResult) {
        if (pollingInterval <= 0) {
            validationResult.setResult("pollingInterval", ValidationStatus.INVALID,
                    "pollingInterval must be a positive integer");
        }

        if (pollingTimeout <= 0) {
            validationResult.setResult("pollingTimeout", ValidationStatus.INVALID,
                    "pollingTimeout must be a positive integer");
        }
    }

    /** 
     * Validate all the String List Elements 
     *
     */
    private void validateStringListElement(final GroupValidationResult validationResult) {
        if (messageBusAddress == null) {
            validationResult.setResult("messageBusAddress", ValidationStatus.INVALID,
                    "messageBusAddress must be a list of non-blank string");
        } else {
            for (final String temp : messageBusAddress) {
                if (temp.trim().length() == 0) {
                    validationResult.setResult("messageBusAddress", ValidationStatus.INVALID,
                            "the string of messageBusAddress must be a non-blank string");
                }
            }
        }

        if (artifactTypes == null) {
            validationResult.setResult("artifactTypes", ValidationStatus.INVALID,
                    "artifactTypes must be a list of non-blank string");
        } else {
            for (final String temp : artifactTypes) {
                if (temp.trim().length() == 0) {
                    validationResult.setResult("artifactTypes", ValidationStatus.INVALID,
                            "the string of artifactTypes must be a non-blank string");
                }
            }
        }
    }

    /**
     * Validate the parameter group
     * @return the result of the validation
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        
        if (asdcAddress == null || asdcAddress.trim().length() == 0) {
            validationResult.setResult("asdcAddress", ValidationStatus.INVALID,
                    "asdcAddress must be a non-blank string");
        }

        if (user == null || user.trim().length() == 0) {
            validationResult.setResult("user", ValidationStatus.INVALID, "user must be a non-blank string");
        }

        if (consumerId == null || consumerId.trim().length() == 0) {
            validationResult.setResult("consumerId", ValidationStatus.INVALID, "consumerId must be a non-blank string");
        }

        if (consumerGroup == null || consumerGroup.trim().length() == 0) {
            validationResult.setResult("consumerGroup", ValidationStatus.INVALID,
                    "consumerGroup must be a non-blank string");
        }

        if (keystorePath == null || keystorePath.trim().length() == 0) {
            validationResult.setResult("keystorePath", ValidationStatus.INVALID,
                    "keystorePath must be a non-blank string");
        }

        if (keystorePassword == null || keystorePassword.trim().length() == 0) {
            validationResult.setResult("keystorePassword", ValidationStatus.INVALID,
                    "keystorePassword must be a non-blank string");
        }

        validateIntElement(validationResult);
        validateStringListElement(validationResult);
        return validationResult;
    }

    /**
     * Set the name of this group.
     *
     * @param name the name to set.
     */
    public void setName(final String name) {
        this.name = name + "_" + UUID.randomUUID().toString();
    }
}


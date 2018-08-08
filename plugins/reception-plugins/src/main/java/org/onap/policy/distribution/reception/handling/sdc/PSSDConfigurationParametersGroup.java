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
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterConstants;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ValidationStatus;

/**
* This class handles reading, parsing and validating of the Policy SDC Service Distribution parameters from Json 
* format, which strictly adheres to the interface:IConfiguration, defined by SDC SDK.
*/
public class  PSSDConfigurationParametersGroup implements ParameterGroup {
    //Policy SDC Service Distribution specified field.
    private String name;
    
    //Interface of IConfiguration item
    private String asdcAddress;
    private List<String> messageBusAddress;
    private String user;
    private String password;
    private String pollingInterval;
    private String pollingTimeout;
    private String consumerId;
    private List<String> artifactTypes;
    private String consumerGroup;
    private String environmentName;
    private String keystorePath;
    private String keystorePassword;
    private String activeserverTlsAuth;
    private String isFilterinEmptyResources;
    private String isUseHttpsWithDmaap;

    public String getAsdcAddress(){
        return asdcAddress;
    }

    public List<String> getMsgBusAddress(){
        return messageBusAddress;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getPollingInterval() {
        return pollingInterval;
    }
    
    public String getPollingTimeout() {
        return pollingTimeout;
    }

    public String getConsumerID() {
        return consumerId;
    }

    public List<String> getArtifactTypes(){
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

    public String activateServerTLSAuth() {
        return activeserverTlsAuth;
    }

    public String isFilterInEmptyResources() {
        return isFilterinEmptyResources;
    }

    public String isUseHttpsWithDmaap() {
        return isUseHttpsWithDmaap;
    }

    @Override
    public String toString() {
        return "name =" + name +",TestParameters:[asdcAddress = " + asdcAddress + ", messageBusAddress = " + messageBusAddress + ", user = "
                + user + "]";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public GroupValidationResult validate() {
        GroupValidationResult validationResult = new GroupValidationResult(this);

        if (name == null || name.trim().length() == 0) {
            validationResult.setResult("name", ValidationStatus.INVALID, "name must be a non-blank string");
       }
        return validationResult;
    }


}


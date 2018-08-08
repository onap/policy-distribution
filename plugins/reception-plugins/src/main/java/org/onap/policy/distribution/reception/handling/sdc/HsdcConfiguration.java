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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.onap.sdc.api.consumer.IConfiguration;

/**
 * Properties for the handling Sdc
 *
 */
public class HsdcConfiguration implements IConfiguration {

    // Configuration file structure

    // Configuration file properties
    private static String configFile=null;
    private static ConfigurationParametersL00 configParameters=null;

    private String certLocation = ".";


    public static void setConfigFile(String configFile) {
        HsdcConfiguration.configFile = configFile;
    }

    public static String getConfigFile() {
        return HsdcConfiguration.configFile;
    }


    /**
     * Original constructor
     *
     * @param configParameters properties needed to be configured for the model loader
     * @param certLocation location of the certificate
     */
    public HsdcConfiguration(ConfigurationParametersL00 configParameters, String certLocation) {
        this.configParameters = configParameters;
        this.certLocation = certLocation;

    }


    @Override
    public String getAsdcAddress() {
        return configParameters.getAsdcAddress();
    }

    @Override
    public List<String> getMsgBusAddress() {
        return configParameters.getMsgBusAddress();
    }

    @Override
    public String getUser() {
        return configParameters.getUser();
    }

    @Override
    public String getPassword() {
        return configParameters.getPassword();
    }

    @Override
    public int getPollingInterval() {
        return Integer.parseInt(configParameters.getPollingInterval());
    }

    @Override
    public int getPollingTimeout() {
        return Integer.parseInt(configParameters.getPollingTimeout());
    }

    @Override
    public List<String> getRelevantArtifactTypes() {
        return configParameters.getArtifactTypes();
    }

    @Override
    public String getConsumerGroup() {
        return configParameters.getConsumerGroup();
    }

    @Override
    public String getEnvironmentName() {
        return configParameters.getEnvironmentName();
    }

    @Override
    public String getConsumerID() {
        return configParameters.getConsumerID();
    }

    @Override
    public String getKeyStorePassword() {
        return configParameters.getKeyStorePassword();
    }

    @Override
    public String getKeyStorePath() {
        return certLocation + configParameters.getKeyStorePath();
    }

    @Override
    public boolean activateServerTLSAuth() {
        String value = configParameters.activateServerTLSAuth();
        return value == null ? false : Boolean.parseBoolean(value);
    }

    @Override
    public boolean isFilterInEmptyResources() {
        String isFilter = configParameters.isFilterInEmptyResources();
        return isFilter == null ? false : Boolean.valueOf(isFilter);
    }

    @Override
    public Boolean isUseHttpsWithDmaap() {
        String useHTTPS = configParameters.isUseHttpsWithDmaap();
        return useHTTPS == null ? false : Boolean.valueOf(useHTTPS);
    }


}

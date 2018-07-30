/*-
 * ﻿============LICENSE_START=======================================================
 * org.onap.policy
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 European Software Marketing Ltd.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.policy.distribution.handlingSdc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.eclipse.jetty.util.security.Password;
import org.onap.sdc.api.consumer.IConfiguration;

/**
 * Properties for the handling Sdc
 *
 */
public class ASDCConfiguration implements IConfiguration {

    // Configuration file structure
    public static final String PREFIX_MODEL_LOADER_CONFIG = "hs";
    public static final String PREFIX_DISTRIBUTION_CLIENT = PREFIX_MODEL_LOADER_CONFIG + ".distribution.";

    private static final String SUFFIX_KEYSTORE_FILE = "KEYSTORE_FILE";
    private static final String SUFFIX_KEYSTORE_PASS = "KEYSTORE_PASSWORD";

    // Configuration file properties
    protected static final String PROP_HS_DISTRIBUTION_ACTIVE_SERVER_TLS_AUTH =
            PREFIX_DISTRIBUTION_CLIENT + "ACTIVE_SERVER_TLS_AUTH";
    protected static final String PROP_HS_DISTRIBUTION_ASDC_CONNECTION_DISABLED =
            PREFIX_DISTRIBUTION_CLIENT + "ASDC_CONNECTION_DISABLE";
    protected static final String PROP_HS_DISTRIBUTION_ASDC_ADDRESS = PREFIX_DISTRIBUTION_CLIENT + "ASDC_ADDRESS";
    protected static final String PROP_HS_DISTRIBUTION_CONSUMER_GROUP = PREFIX_DISTRIBUTION_CLIENT + "CONSUMER_GROUP";
    protected static final String PROP_HS_DISTRIBUTION_CONSUMER_ID = PREFIX_DISTRIBUTION_CLIENT + "CONSUMER_ID";
    protected static final String PROP_HS_DISTRIBUTION_ENVIRONMENT_NAME =
            PREFIX_DISTRIBUTION_CLIENT + "ENVIRONMENT_NAME";
    protected static final String PROP_HS_DISTRIBUTION_KEYSTORE_PASSWORD =
            PREFIX_DISTRIBUTION_CLIENT + SUFFIX_KEYSTORE_PASS;
    protected static final String PROP_HS_DISTRIBUTION_KEYSTORE_FILE =
            PREFIX_DISTRIBUTION_CLIENT + SUFFIX_KEYSTORE_FILE;
    protected static final String PROP_HS_DISTRIBUTION_PASSWORD = PREFIX_DISTRIBUTION_CLIENT + "PASSWORD";
    protected static final String PROP_HS_DISTRIBUTION_POLLING_INTERVAL =
            PREFIX_DISTRIBUTION_CLIENT + "POLLING_INTERVAL";
    protected static final String PROP_HS_DISTRIBUTION_POLLING_TIMEOUT = PREFIX_DISTRIBUTION_CLIENT + "POLLING_TIMEOUT";
    protected static final String PROP_HS_DISTRIBUTION_USER = PREFIX_DISTRIBUTION_CLIENT + "USER";
    protected static final String PROP_HS_DISTRIBUTION_ARTIFACT_TYPES = PREFIX_DISTRIBUTION_CLIENT + "ARTIFACT_TYPES";
    protected static final String PROP_HS_DISTRIBUTION_MSG_BUS_ADDRESSES =
            PREFIX_DISTRIBUTION_CLIENT + "MSG_BUS_ADDRESSES";
    protected static final String PROP_HS_DISTRIBUTION_HTTPS_WITH_DMAAP =
            PREFIX_DISTRIBUTION_CLIENT + "USE_HTTPS_WITH_DMAAP";

    private static String configHome;

    private Properties configProperties = null;

    private String certLocation = ".";

    private List<String> artifactTypes = null;

    private List<String> msgBusAddrs = null;


    protected static final String FILESEP =
            (System.getProperty("file.separator") == null) ? "/" : System.getProperty("file.separator");

    public static void setConfigHome(String configHome) {
        ASDCConfiguration.configHome = configHome;
    }

    public static String propertiesFile() {
        return configHome + FILESEP + "model-loader.properties";
    }

    public ASDCConfiguration(Properties configProperties) {
        this(configProperties, ASDCConfiguration.configHome + FILESEP + "auth" + FILESEP);
    }

    /**
     * Original constructor
     *
     * @param configProperties properties needed to be configured for the model loader
     * @param certLocation location of the certificate
     */
    public ASDCConfiguration(Properties configProperties, String certLocation) {
        this.configProperties = configProperties;
        this.certLocation = certLocation;

        // Get list of artifacts
        artifactTypes = new ArrayList<String>();
        if (configProperties.getProperty(PROP_HS_DISTRIBUTION_ARTIFACT_TYPES) != null) {
            String[] artTypeList = configProperties.getProperty(PROP_HS_DISTRIBUTION_ARTIFACT_TYPES).split(",");
            for (String artType : artTypeList) {
                artifactTypes.add(artType);
            }
        }

        // Get list of message bus addresses
        msgBusAddrs = new ArrayList<String>();
        if (configProperties.getProperty(PROP_HS_DISTRIBUTION_MSG_BUS_ADDRESSES) != null) {
            String[] msgBusList = configProperties.getProperty(PROP_HS_DISTRIBUTION_MSG_BUS_ADDRESSES).split(",");
            for (String addr : msgBusList) {
                msgBusAddrs.add(addr);
            }
        }
    }


    @Override
    public boolean activateServerTLSAuth() {
        String value = configProperties.getProperty(PROP_HS_DISTRIBUTION_ACTIVE_SERVER_TLS_AUTH);
        return value == null ? false : Boolean.parseBoolean(value);
    }

    @Override
    public String getAsdcAddress() {
        return configProperties.getProperty(PROP_HS_DISTRIBUTION_ASDC_ADDRESS);
    }

    @Override
    public String getConsumerGroup() {
        return configProperties.getProperty(PROP_HS_DISTRIBUTION_CONSUMER_GROUP);
    }

    @Override
    public String getConsumerID() {
        return configProperties.getProperty(PROP_HS_DISTRIBUTION_CONSUMER_ID);
    }

    @Override
    public String getEnvironmentName() {
        return configProperties.getProperty(PROP_HS_DISTRIBUTION_ENVIRONMENT_NAME);
    }

    @Override
    public String getKeyStorePassword() {
        return Password.deobfuscate(configProperties.getProperty(PROP_HS_DISTRIBUTION_KEYSTORE_PASSWORD));
    }

    @Override
    public String getKeyStorePath() {
        return certLocation + configProperties.getProperty(PROP_HS_DISTRIBUTION_KEYSTORE_FILE);
    }

    @Override
    public String getPassword() {
        return Password.deobfuscate(configProperties.getProperty(PROP_HS_DISTRIBUTION_PASSWORD));
    }

    @Override
    public int getPollingInterval() {
        return Integer.parseInt(configProperties.getProperty(PROP_HS_DISTRIBUTION_POLLING_INTERVAL));
    }

    @Override
    public int getPollingTimeout() {
        return Integer.parseInt(configProperties.getProperty(PROP_HS_DISTRIBUTION_POLLING_TIMEOUT));
    }

    @Override
    public List<String> getRelevantArtifactTypes() {
        return artifactTypes;
    }

    @Override
    public String getUser() {
        return configProperties.getProperty(PROP_HS_DISTRIBUTION_USER);
    }

    @Override
    public boolean isFilterInEmptyResources() {
        return false;
    }

    @Override
    public Boolean isUseHttpsWithDmaap() {
        String useHTTPS = configProperties.getProperty(PROP_HS_DISTRIBUTION_HTTPS_WITH_DMAAP);
        return useHTTPS == null ? false : Boolean.valueOf(useHTTPS);
    }

    @Override
    public List<String> getMsgBusAddress() {
        return msgBusAddrs;
    }


    /**
     * @return a boolean value indicating whether model loader is connected to ASDC.
     */
    public boolean getASDCConnectionDisabled() {
        String propValue = configProperties.getProperty(PROP_HS_DISTRIBUTION_ASDC_CONNECTION_DISABLED);
        return propValue != null && "true".equalsIgnoreCase(propValue);

    }

}

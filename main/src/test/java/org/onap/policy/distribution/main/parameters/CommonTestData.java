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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;
import org.onap.policy.distribution.main.testclasses.DummyPolicyForwarderParameterGroup;
import org.onap.policy.distribution.main.testclasses.DummyPolicyForwarderParameterGroup.DummyPolicyForwarderParameterGroupBuilder;
import org.onap.policy.distribution.reception.parameters.PluginHandlerParameters;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderParameters;
import org.onap.policy.distribution.reception.parameters.PssdConfigurationParametersGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

/**
 * Class to hold/create all parameters for test cases.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class CommonTestData {

    private static final String REST_SERVER_PASSWORD = "zb!XztG34";
    private static final String REST_SERVER_USER = "healthcheck";
    private static final int REST_SERVER_PORT = 6969;
    private static final String REST_SERVER_HOST = "0.0.0.0";
    public static final String DISTRIBUTION_GROUP_NAME = "SDCDistributionGroup";
    public static final String DECODER_TYPE = "DummyDecoder";
    public static final String DECODER_CLASS_NAME = "org.onap.policy.distribution.main.testclasses.DummyDecoder";
    public static final String FORWARDER_TYPE = "DummyForwarder";
    public static final String FORWARDER_CLASS_NAME =
            "org.onap.policy.distribution.main.testclasses.DummyPolicyForwarder";
    public static final String FORWARDER_CONFIGURATION_PARAMETERS = "dummyConfiguration";
    public static final String FORWARDER_HOST = "192.168.99.100";
    public static final String RECEPTION_HANDLER_TYPE = "DummyReceptionHandler";
    public static final String RECEPTION_HANDLER_CLASS_NAME =
            "org.onap.policy.distribution.main.testclasses.DummyReceptionHandler";
    public static final String DUMMY_RECEPTION_HANDLER_KEY = "DummyReceptionHandler";
    public static final String DUMMY_ENGINE_FORWARDER_KEY = "DummyForwarder";
    public static final String DUMMY_DECODER_KEY = "DummyDecoder";

    /**
     * Returns an instance of ReceptionHandlerParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the restServerParameters object
     */
    public RestServerParameters getRestServerParameters(final boolean isEmpty) {
        final RestServerParameters restServerParameters;
        if (!isEmpty) {
            restServerParameters = new RestServerParameters(REST_SERVER_HOST, REST_SERVER_PORT, REST_SERVER_USER,
                    REST_SERVER_PASSWORD);
        } else {
            restServerParameters = new RestServerParameters(null, 0, null, null);
        }
        return restServerParameters;
    }

    /**
     * Returns an instance of ReceptionHandlerParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the receptionHandlerParameters object
     */
    public Map<String, ReceptionHandlerParameters> getReceptionHandlerParameters(final boolean isEmpty) {
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                new HashMap<String, ReceptionHandlerParameters>();
        if (!isEmpty) {
            final Map<String, PolicyDecoderParameters> policyDecoders = getPolicyDecoders(isEmpty);
            final Map<String, PolicyForwarderParameters> policyForwarders = getPolicyForwarders(isEmpty);
            final PssdConfigurationParametersGroup pssdConfiguration = getPssdConfigurationParametersGroup(isEmpty);;
            final PluginHandlerParameters pHParameters = new PluginHandlerParameters(policyDecoders, policyForwarders);
            final ReceptionHandlerParameters rhParameters = new ReceptionHandlerParameters(RECEPTION_HANDLER_TYPE,
                    RECEPTION_HANDLER_CLASS_NAME, pssdConfiguration, pHParameters);
            receptionHandlerParameters.put(DUMMY_RECEPTION_HANDLER_KEY, rhParameters);
        }
        return receptionHandlerParameters;
    }

    /**
     * Returns an instance of PssdConfigurationParametersGroup for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the PssdConfigurationParametersGroup object
     */
    public PssdConfigurationParametersGroup getPssdConfigurationParametersGroup(final boolean isEmpty) {
        final PssdConfigurationParametersGroup pssdConfiguration;
        if (!isEmpty) {
            final List<String> messageBusAddress = new ArrayList<>();
            messageBusAddress.add("localhost");
            final List<String> artifactTypes = new ArrayList<>();
            artifactTypes.add("TOSCA_CSAR");
            pssdConfiguration = new PssdConfigurationParametersGroup.PssdConfigurationBuilder()
                    .setAsdcAddress("localhost").setMessageBusAddress(messageBusAddress).setUser("policy")
                    .setPassword("policy").setPollingInterval(20).setPollingTimeout(30).setConsumerId("policy-id")
                    .setArtifactTypes(artifactTypes).setConsumerGroup("policy-group").setEnvironmentName("TEST")
                    .setKeystorePath("").setKeystorePassword("").setActiveserverTlsAuth(false)
                    .setIsFilterinEmptyResources(true).setIsUseHttpsWithDmaap(false).build();
        } else {
            pssdConfiguration = new PssdConfigurationParametersGroup.PssdConfigurationBuilder().build();
        }
        return pssdConfiguration;
    }

    /**
     * Returns an instance of PluginHandlerParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the pluginHandlerParameters object
     */
    public PluginHandlerParameters getPluginHandlerParameters(final boolean isEmpty) {
        final Map<String, PolicyDecoderParameters> policyDecoders = getPolicyDecoders(isEmpty);
        final Map<String, PolicyForwarderParameters> policyForwarders = getPolicyForwarders(isEmpty);
        final PluginHandlerParameters pluginHandlerParameters =
                new PluginHandlerParameters(policyDecoders, policyForwarders);
        return pluginHandlerParameters;
    }

    /**
     * Returns an instance of PolicyForwarderParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the policyForwarders object
     */
    public Map<String, PolicyForwarderParameters> getPolicyForwarders(final boolean isEmpty) {
        final Map<String, PolicyForwarderParameters> policyForwarders =
                new HashMap<String, PolicyForwarderParameters>();
        if (!isEmpty) {
            final PolicyForwarderParameters pFParameters = new PolicyForwarderParameters(FORWARDER_TYPE,
                    FORWARDER_CLASS_NAME, FORWARDER_CONFIGURATION_PARAMETERS);
            policyForwarders.put(DUMMY_ENGINE_FORWARDER_KEY, pFParameters);
        }
        return policyForwarders;
    }

    /**
     * Returns an instance of PolicyDecoderParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the policyDecoders object
     */
    public Map<String, PolicyDecoderParameters> getPolicyDecoders(final boolean isEmpty) {
        final Map<String, PolicyDecoderParameters> policyDecoders = new HashMap<String, PolicyDecoderParameters>();
        if (!isEmpty) {
            final PolicyDecoderParameters pDParameters = new PolicyDecoderParameters(DECODER_TYPE, DECODER_CLASS_NAME);
            policyDecoders.put(DUMMY_DECODER_KEY, pDParameters);
        }
        return policyDecoders;
    }

    /**
     * Returns PolicyForwarderConfigurationParameterGroups for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the PolicyForwarderConfigurationParameterGroups
     */
    public Map<String, PolicyForwarderConfigurationParameterGroup> getPolicyForwarderConfigurationParameters(
            final boolean isEmpty) {
        final Map<String, PolicyForwarderConfigurationParameterGroup> policyForwarderConfigurationParameters =
                new HashMap<String, PolicyForwarderConfigurationParameterGroup>();
        if (!isEmpty) {
            DummyPolicyForwarderParameterGroup dummyPolicyForwarderParameterGroup =
                    new DummyPolicyForwarderParameterGroupBuilder().setUseHttps(true).setHostname(FORWARDER_HOST)
                            .setPort(1234).setUserName("myUser").setPassword("myPassword").setIsManaged(true).build();
            policyForwarderConfigurationParameters.put(FORWARDER_CONFIGURATION_PARAMETERS,
                    dummyPolicyForwarderParameterGroup);
        }
        return policyForwarderConfigurationParameters;
    }
}

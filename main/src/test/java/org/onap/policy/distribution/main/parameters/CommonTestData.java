/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.onap.policy.common.endpoints.parameters.RestServerParameters;
import org.onap.policy.common.utils.coder.Coder;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;
import org.onap.policy.distribution.main.testclasses.DummyPolicyDecoderParameterGroup;
import org.onap.policy.distribution.main.testclasses.DummyPolicyForwarderParameterGroup;
import org.onap.policy.distribution.main.testclasses.DummyReceptionHandlerParameterGroup;
import org.onap.policy.distribution.reception.parameters.PluginHandlerParameters;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderParameters;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

/**
 * Class to hold/create all parameters for test cases.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class CommonTestData {

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
    public static final String RECEPTION_CONFIGURATION_PARAMETERS = "dummyReceptionHandlerConfiguration";
    public static final String MY_STRING_PARAMETER_VALUE = "aStringValue";
    public static final Boolean MY_BOOLEAN_PARAMETER_VALUE = true;
    public static final int MY_INTEGER_PARAMETER_VALUE = 1234;

    public static final String DUMMY_RECEPTION_HANDLER_KEY = "DummyReceptionHandler";
    public static final String DUMMY_ENGINE_FORWARDER_KEY = "DummyForwarder";
    public static final String DUMMY_DECODER_KEY = "DummyDecoder";

    public static final String POLICY_TYPE = "DUMMY";
    public static final String POLICY_NAME = "SamplePolicy";
    public static final String DECODER_CONFIGURATION_PARAMETERS = "dummyDecoderConfiguration";

    public static final String CONFIG_FILE = "src/test/resources/parameters/TestConfigParams.json";

    private Coder coder = new StandardCoder();

    /**
     * Makes a parameter configuration file by substituting an available port number within a
     * source file.
     *
     * @param sourceName original configuration file containing 6969
     * @return the port that was substituted into the config file
     * @throws IOException if the config file cannot be created
     */
    public static int makeConfigFile(String sourceName) throws IOException {
        int port = NetworkUtil.allocPort();

        String json = Files.readString(new File("src/test/resources/" + sourceName).toPath());
        json = json.replace("6969", String.valueOf(port));

        File file = new File(CONFIG_FILE);
        file.deleteOnExit();

        try (FileOutputStream output = new FileOutputStream(file)) {
            output.write(json.getBytes(StandardCharsets.UTF_8));
        }

        return port;
    }

    /**
     * Waits for the server to connect to a port.
     * @param port server's port
     * @throws InterruptedException if interrupted
     */
    public static void awaitServer(int port) throws InterruptedException {
        if (!NetworkUtil.isTcpPortOpen("localhost", port, 50, 200L)) {
            throw new IllegalStateException("cannot connect to port " + port);
        }
    }

    /**
     * Returns an instance of ReceptionHandlerParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the restServerParameters object
     */
    public RestServerParameters getRestServerParameters(final boolean isEmpty) {
        final String fileName = "src/test/resources/parameters/"
                + (isEmpty ? "RestServerParametersEmpty" : "RestServerParameters") + ".json";
        try {
            return coder.decode(new File(fileName), RestServerParameters.class);
        } catch (final CoderException exp) {
            throw new RuntimeException("cannot read/decode " + fileName, exp);
        }
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
            final PluginHandlerParameters pHParameters = new PluginHandlerParameters(policyDecoders, policyForwarders);
            final ReceptionHandlerParameters rhParameters = new ReceptionHandlerParameters(RECEPTION_HANDLER_TYPE,
                    RECEPTION_HANDLER_CLASS_NAME, RECEPTION_CONFIGURATION_PARAMETERS, pHParameters);
            receptionHandlerParameters.put(DUMMY_RECEPTION_HANDLER_KEY, rhParameters);
        }
        return receptionHandlerParameters;
    }

    /**
     * Returns ReceptionHandlerConfigurationParameterGroups for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the ReceptionHandlerConfigurationParameterGroups
     */
    public Map<String, ReceptionHandlerConfigurationParameterGroup> getReceptionHandlerConfigurationParameters(
            final boolean isEmpty) {
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurationParameters =
                new HashMap<String, ReceptionHandlerConfigurationParameterGroup>();
        if (!isEmpty) {
            final DummyReceptionHandlerParameterGroup dummyReceptionHandlerParameterGroup =
                    new DummyReceptionHandlerParameterGroup();
            dummyReceptionHandlerParameterGroup.setMyStringParameter(MY_STRING_PARAMETER_VALUE);
            dummyReceptionHandlerParameterGroup.setMyIntegerParameter(MY_INTEGER_PARAMETER_VALUE);
            dummyReceptionHandlerParameterGroup.setMyBooleanParameter(MY_BOOLEAN_PARAMETER_VALUE);
            receptionHandlerConfigurationParameters.put(RECEPTION_CONFIGURATION_PARAMETERS,
                    dummyReceptionHandlerParameterGroup);
        }
        return receptionHandlerConfigurationParameters;
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
            final PolicyDecoderParameters pDParameters =
                    new PolicyDecoderParameters(DECODER_TYPE, DECODER_CLASS_NAME, DECODER_CONFIGURATION_PARAMETERS);
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
            final DummyPolicyForwarderParameterGroup parameters = new DummyPolicyForwarderParameterGroup();
            parameters.setHostname(FORWARDER_HOST);
            parameters.setManaged(true);
            parameters.setUserName("myUser");
            parameters.setPassword("myPassword");
            parameters.setPort(1234);
            parameters.setUseHttps(true);
            policyForwarderConfigurationParameters.put(FORWARDER_CONFIGURATION_PARAMETERS, parameters);
        }
        return policyForwarderConfigurationParameters;
    }

    /**
     * Returns PolicyDecoderConfigurationParameterGroups for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the PolicyDecoderConfigurationParameterGroups
     */
    public Map<String, PolicyDecoderConfigurationParameterGroup> getPolicyDecoderConfigurationParameters(
            final boolean isEmpty) {
        final Map<String, PolicyDecoderConfigurationParameterGroup> policyDecoderConfigurationParameters =
                new HashMap<String, PolicyDecoderConfigurationParameterGroup>();
        if (!isEmpty) {
            final DummyPolicyDecoderParameterGroup dummyPolicyForwarderParameterGroup =
                    new DummyPolicyDecoderParameterGroup();
            dummyPolicyForwarderParameterGroup.setPolicyName(POLICY_NAME);
            dummyPolicyForwarderParameterGroup.setPolicyType(POLICY_TYPE);
            policyDecoderConfigurationParameters.put(DECODER_CONFIGURATION_PARAMETERS,
                    dummyPolicyForwarderParameterGroup);
        }
        return policyDecoderConfigurationParameters;
    }
}

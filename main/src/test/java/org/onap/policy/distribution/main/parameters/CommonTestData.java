/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.onap.policy.common.endpoints.parameters.RestServerParameters;
import org.onap.policy.common.utils.coder.Coder;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.distribution.forwarding.parameters.PolicyForwarderParameters;
import org.onap.policy.distribution.main.testclasses.DummyPolicyDecoderParameterGroup;
import org.onap.policy.distribution.main.testclasses.DummyPolicyForwarderParameterGroup;
import org.onap.policy.distribution.main.testclasses.DummyPolicyForwarderParameterGroup.DummyPolicyForwarderParameterGroupBuilder;
import org.onap.policy.distribution.main.testclasses.DummyReceptionHandlerParameterGroup;
import org.onap.policy.distribution.main.testclasses.DummyReceptionHandlerParameterGroup.DummyReceptionHandlerParameterGroupBuilder;
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

    private Coder coder = new StandardCoder();

    /**
     * Returns an instance of ReceptionHandlerParameters for test cases.
     *
     * @param isEmpty boolean value to represent that object created should be empty or not
     * @return the restServerParameters object
     */
    public RestServerParameters getRestServerParameters(final boolean isEmpty) {
        String fileName = "src/test/resources/parameters/"
                        + (isEmpty ? "RestServerParametersEmpty" : "RestServerParameters") + ".json";
        try {
            String text = new String(Files.readAllBytes(new File(fileName).toPath()), StandardCharsets.UTF_8);
            return coder.decode(text, RestServerParameters.class);
        } catch (CoderException | IOException e) {
            throw new RuntimeException("cannot read/decode " + fileName, e);
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
            final List<String> messageBusAddress = new ArrayList<>();
            messageBusAddress.add("localhost");
            final List<String> artifactTypes = new ArrayList<>();
            artifactTypes.add("TOSCA_CSAR");
            final DummyReceptionHandlerParameterGroup dummyReceptionHandlerParameterGroup =
                    new DummyReceptionHandlerParameterGroupBuilder().setMyStringParameter(MY_STRING_PARAMETER_VALUE)
                            .setMyIntegerParameter(MY_INTEGER_PARAMETER_VALUE)
                            .setMyBooleanParameter(MY_BOOLEAN_PARAMETER_VALUE).build();
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
            final DummyPolicyForwarderParameterGroup dummyPolicyForwarderParameterGroup =
                    new DummyPolicyForwarderParameterGroupBuilder().setUseHttps(true).setHostname(FORWARDER_HOST)
                            .setPort(1234).setUserName("myUser").setPassword("myPassword").setIsManaged(true).build();
            policyForwarderConfigurationParameters.put(FORWARDER_CONFIGURATION_PARAMETERS,
                    dummyPolicyForwarderParameterGroup);
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
                    new DummyPolicyDecoderParameterGroup(POLICY_NAME, POLICY_TYPE);
            policyDecoderConfigurationParameters.put(DECODER_CONFIGURATION_PARAMETERS,
                    dummyPolicyForwarderParameterGroup);
        }
        return policyDecoderConfigurationParameters;
    }
}

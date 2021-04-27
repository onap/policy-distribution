/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 AT&T Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;
import org.onap.policy.common.utils.cmd.CommandLineException;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.startstop.DistributionCommandLineArguments;
import org.onap.policy.distribution.main.testclasses.DummyPolicyDecoderParameterGroup;
import org.onap.policy.distribution.main.testclasses.DummyPolicyForwarderParameterGroup;

/**
 * Class to perform unit test of DistributionParameterHandler.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestDistributionParameterHandler {
    @Test
    public void testParameterHandlerNoParameterFile() throws PolicyDistributionException, CommandLineException {
        verifyFailure("NoParameterFile.json", PolicyDistributionException.class, "FileNotFoundException");
    }

    @Test
    public void testParameterHandlerEmptyParameters() throws PolicyDistributionException, CommandLineException {
        verifyFailure("EmptyParameters.json", PolicyDistributionException.class,
                "no parameters found in \"parameters/EmptyParameters.json\"");
    }

    @Test
    public void testParameterHandlerBadParameters() throws PolicyDistributionException, CommandLineException {
        verifyFailure("BadParameters.json", PolicyDistributionException.class,
                "error reading parameters from \"parameters/BadParameters.json\"\n"
                        + "(JsonSyntaxException):java.lang.IllegalStateException: "
                        + "Expected a string but was BEGIN_ARRAY at line 2 column 15 path $.name");
    }

    @Test
    public void testParameterHandlerInvalidParameters() throws PolicyDistributionException, CommandLineException {
        verifyFailure("InvalidParameters.json", PolicyDistributionException.class,
                "error reading parameters from \"parameters/InvalidParameters.json\"\n"
                        + "(JsonSyntaxException):java.lang.IllegalStateException: "
                        + "Expected a string but was BEGIN_ARRAY at line 2 column 15 path $.name");
    }

    @Test
    public void testParameterHandlerNoParameters() throws PolicyDistributionException, CommandLineException {
        verifyFailure("NoParameters.json", PolicyDistributionException.class,
                "\"receptionHandlerParameters\" value \"null\" INVALID, is null");
    }

    @Test
    public void testParameterHandlerMinumumParameters() throws PolicyDistributionException, CommandLineException {
        final String[] minArgumentString = {"-c", "parameters/MinimumParameters.json"};

        final DistributionCommandLineArguments minArguments = new DistributionCommandLineArguments();
        minArguments.parse(minArgumentString);

        final DistributionParameterGroup parGroup = new DistributionParameterHandler().getParameters(minArguments);
        assertEquals(CommonTestData.DISTRIBUTION_GROUP_NAME, parGroup.getName());
    }

    @Test
    public void testDistributionParameterGroup() throws PolicyDistributionException, CommandLineException {
        final String[] distributionConfigParameters = {"-c", "parameters/DistributionConfigParameters.json"};

        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        final DistributionParameterGroup parGroup = new DistributionParameterHandler().getParameters(arguments);
        assertTrue(arguments.checkSetConfigurationFilePath());
        assertEquals(CommonTestData.DISTRIBUTION_GROUP_NAME, parGroup.getName());
        assertEquals(CommonTestData.RECEPTION_HANDLER_TYPE, parGroup.getReceptionHandlerParameters()
                .get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getReceptionHandlerType());
        assertEquals(CommonTestData.DECODER_TYPE,
                parGroup.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getPluginHandlerParameters().getPolicyDecoders().get(CommonTestData.DUMMY_DECODER_KEY)
                        .getDecoderType());
        assertEquals(CommonTestData.FORWARDER_TYPE,
                parGroup.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getPluginHandlerParameters().getPolicyForwarders()
                        .get(CommonTestData.DUMMY_ENGINE_FORWARDER_KEY).getForwarderType());
        assertEquals(CommonTestData.FORWARDER_HOST,
                ((DummyPolicyForwarderParameterGroup) parGroup.getPolicyForwarderConfigurationParameters()
                        .get(CommonTestData.FORWARDER_CONFIGURATION_PARAMETERS)).getHostname());
        assertEquals(CommonTestData.POLICY_TYPE,
                ((DummyPolicyDecoderParameterGroup) parGroup.getPolicyDecoderConfigurationParameters()
                        .get(CommonTestData.DECODER_CONFIGURATION_PARAMETERS)).getPolicyType());
        assertEquals(CommonTestData.POLICY_NAME,
                ((DummyPolicyDecoderParameterGroup) parGroup.getPolicyDecoderConfigurationParameters()
                        .get(CommonTestData.DECODER_CONFIGURATION_PARAMETERS)).getPolicyName());
    }

    @Test
    public void testDistributionParameterGroup_InvalidForwarderConfigurationClassName()
            throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_InvalidForwarderConfigurationClassName.json",
                PolicyDistributionException.class, "parameter \"parameterClassName\" value \"\" invalid in JSON file");
    }

    @Test
    public void testDistributionParameterGroup_UnknownForwarderConfigurationClassName()
            throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_UnknownForwarderConfigurationClassName.json",
                PolicyDistributionException.class,
                "parameter \"parameterClassName\" value \"org.onap.policy.Unknown\", could not find class");
    }

    @Test
    public void testDistributionParameterGroup_InvalidName() throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_InvalidName.json", PolicyDistributionException.class,
                "\"name\" value \" \" INVALID, is blank");
    }

    @Test
    public void testDistributionParameterGroup_NoReceptionHandler()
            throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_NoReceptionHandler.json", PolicyDistributionException.class,
                "\"receptionHandlerParameters\" value \"null\" INVALID, is null");
    }

    @Test
    public void testDistributionParameterGroup_EmptyReceptionHandler()
            throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_EmptyReceptionHandler.json", PolicyDistributionException.class,
                "\"receptionHandlerParameters\" value \"{}\" INVALID, minimum number of elements: 1");
    }

    @Test
    public void testDistributionParameterGroup_NoPolicyDecoder()
            throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_NoPolicyDecoder.json", PolicyDistributionException.class,
                "\"policyDecoders\" value \"null\" INVALID, is null");
    }

    @Test
    public void testDistributionParameterGroup_NoPolicyForwarder()
            throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_NoPolicyForwarder.json", PolicyDistributionException.class,
                "\"policyForwarderConfigurationParameters\" value \"null\" INVALID, is null");
    }

    @Test
    public void testDistributionParameterGroup_EmptyPolicyDecoder()
            throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_EmptyPolicyDecoder.json", PolicyDistributionException.class,
                "\"policyDecoders\" value \"{}\" INVALID, minimum number of elements: 1");
    }

    @Test
    public void testDistributionParameterGroup_EmptyPolicyForwarder()
            throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_EmptyPolicyForwarder.json", PolicyDistributionException.class,
                "\"policyForwarders\" value \"{}\" INVALID, minimum number of elements: 1");
    }

    @Test
    public void testDistributionParameterGroup_InvalidReceptionHandlerParameters()
            throws PolicyDistributionException, IOException, CommandLineException {

        String resultString = Files
                .readString(
                        Paths.get("src/test/resources/expectedValidationResults/InvalidReceptionHandlerParameters.txt"))
                .trim().replaceAll("\\r\\n", "\\\n");

        verifyFailure("DistributionConfigParameters_InvalidReceptionHandlerParameters.json",
                PolicyDistributionException.class, resultString);
    }

    @Test
    public void testDistributionParameterGroup_InvalidDecoderAndForwarderParameters()
            throws PolicyDistributionException, IOException, CommandLineException {

        String resultString = new String(Files
                .readString(Paths
                        .get("src/test/resources/expectedValidationResults/InvalidDecoderAndForwarderParameters.txt"))
                .trim().replaceAll("\\r\\n", "\\\n"));

        verifyFailure("DistributionConfigParameters_InvalidDecoderAndForwarderParameters.json",
                PolicyDistributionException.class, resultString);
    }

    @Test
    public void testDistributionParameterGroup_InvalidRestServerParameters()
            throws PolicyDistributionException, IOException, CommandLineException {

        String resultString = new String(Files
                .readString(Paths.get("src/test/resources/expectedValidationResults/InvalidRestServerParameters.txt"))
                .trim().replaceAll("\\r\\n", "\\\n"));

        verifyFailure("DistributionConfigParameters_InvalidRestServerParameters.json",
                PolicyDistributionException.class, resultString);
    }

    @Test
    public void testDistributionVersion() throws PolicyDistributionException, CommandLineException {
        final String[] distributionConfigParameters = {"-v"};
        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        final String version = arguments.parse(distributionConfigParameters);
        assertTrue(version.startsWith("ONAP Policy Framework Distribution Service"));
    }

    @Test
    public void testDistributionHelp() throws PolicyDistributionException, CommandLineException {
        final String[] distributionConfigParameters = {"-h"};
        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        final String help = arguments.parse(distributionConfigParameters);
        assertTrue(help.startsWith("usage:"));
    }

    @Test
    public void testDistributionInvalidOption() throws PolicyDistributionException {
        final String[] distributionConfigParameters = {"-d"};
        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        assertThatThrownBy(() -> arguments.parse(distributionConfigParameters))
                .hasMessageContaining("invalid command line arguments specified");
    }

    @Test
    public void testDistributionParameterGroup_InvalidReceptionHandlerClass()
            throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_InvalidReceptionHandlerClass.json",
                PolicyDistributionException.class, "could not find class");
    }

    @Test
    public void testDistributionParameterGroup_EmptyReceptionHandlerClass()
            throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_EmptyReceptionHandlerClass.json", PolicyDistributionException.class,
                "invalid in JSON file");
    }

    @Test
    public void testDistributionParameterGroup_InvalidDecoderConfigurationClassName()
            throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_InvalidDecoderConfigurationClassName.json",
                PolicyDistributionException.class, "parameter \"parameterClassName\" value \"\" invalid in JSON file");
    }

    @Test
    public void testDistributionParameterGroup_UnknownDecoderConfigurationClassName()
            throws PolicyDistributionException, CommandLineException {
        verifyFailure("DistributionConfigParameters_UnknownDecoderConfigurationClassName.json",
                PolicyDistributionException.class,
                "parameter \"parameterClassName\" value \"org.onap.policy.Unknown\", could not find class");
    }

    private <T> void verifyFailure(String fileName, Class<T> clazz, String expectedMessage)
            throws PolicyDistributionException, CommandLineException {
        final String[] distributionConfigParameters = {"-c", "parameters/" + fileName};

        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        DistributionParameterHandler paramHandler = new DistributionParameterHandler();

        assertThatThrownBy(() -> paramHandler.getParameters(arguments)).isInstanceOf(clazz)
                .hasMessageContaining(expectedMessage);
    }
}

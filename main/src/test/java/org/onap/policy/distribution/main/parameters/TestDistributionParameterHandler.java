/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021, 2025 OpenInfra Foundation Europe.
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
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
class TestDistributionParameterHandler {
    @Test
    void testParameterHandlerNoParameterFile() throws CommandLineException {
        verifyFailure("NoParameterFile.json", "FileNotFoundException");
    }

    @Test
    void testParameterHandlerEmptyParameters() throws CommandLineException {
        verifyFailure("EmptyParameters.json",
            "no parameters found in \"parameters/EmptyParameters.json\"");
    }

    @Test
    void testParameterHandlerBadParameters() throws CommandLineException {
        verifyFailure("BadParameters.json",
            "error reading parameters from \"parameters/BadParameters.json\"\n"
                        + "(JsonSyntaxException):java.lang.IllegalStateException: "
                        + "Expected a string but was BEGIN_ARRAY at line 2 column 15 path $.name");
    }

    @Test
    void testParameterHandlerInvalidParameters() throws CommandLineException {
        verifyFailure("InvalidParameters.json",
            "error reading parameters from \"parameters/InvalidParameters.json\"\n"
                        + "(JsonSyntaxException):java.lang.IllegalStateException: "
                        + "Expected a string but was BEGIN_ARRAY at line 2 column 15 path $.name");
    }

    @Test
    void testParameterHandlerNoParameters() throws CommandLineException {
        verifyFailure("NoParameters.json",
            "\"receptionHandlerParameters\" value \"null\" INVALID, is null");
    }

    @Test
    void testParameterHandlerMinumumParameters() throws PolicyDistributionException, CommandLineException {
        final String[] minArgumentString = {"-c", "parameters/MinimumParameters.json"};

        final var minArguments = new DistributionCommandLineArguments();
        minArguments.parse(minArgumentString);

        final var parGroup = new DistributionParameterHandler().getParameters(minArguments);
        assertEquals(CommonTestData.DISTRIBUTION_GROUP_NAME, parGroup.getName());
    }

    @Test
    void testDistributionParameterGroup() throws PolicyDistributionException, CommandLineException {
        final String[] distributionConfigParameters = {"-c", "parameters/DistributionConfigParameters.json"};

        final var arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        final var parGroup = new DistributionParameterHandler().getParameters(arguments);
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
    void testDistributionParameterGroup_InvalidForwarderConfigurationClassName()
            throws CommandLineException {
        verifyFailure("DistributionConfigParameters_InvalidForwarderConfigurationClassName.json",
            "parameter \"parameterClassName\" value \"\" invalid in JSON file");
    }

    @Test
    void testDistributionParameterGroup_UnknownForwarderConfigurationClassName()
            throws CommandLineException {
        verifyFailure("DistributionConfigParameters_UnknownForwarderConfigurationClassName.json",
            "parameter \"parameterClassName\" value \"org.onap.policy.Unknown\", could not find class");
    }

    @Test
    void testDistributionParameterGroup_InvalidName() throws CommandLineException {
        verifyFailure("DistributionConfigParameters_InvalidName.json",
            "\"name\" value \" \" INVALID, is blank");
    }

    @Test
    void testDistributionParameterGroup_NoReceptionHandler()
            throws CommandLineException {
        verifyFailure("DistributionConfigParameters_NoReceptionHandler.json",
            "\"receptionHandlerParameters\" value \"null\" INVALID, is null");
    }

    @Test
    void testDistributionParameterGroup_EmptyReceptionHandler()
            throws CommandLineException {
        verifyFailure("DistributionConfigParameters_EmptyReceptionHandler.json",
            "\"receptionHandlerParameters\" value \"{}\" INVALID, minimum number of elements: 1");
    }

    @Test
    void testDistributionParameterGroup_NoPolicyDecoder()
            throws CommandLineException {
        verifyFailure("DistributionConfigParameters_NoPolicyDecoder.json",
            "\"policyDecoders\" value \"null\" INVALID, is null");
    }

    @Test
    void testDistributionParameterGroup_NoPolicyForwarder()
            throws CommandLineException {
        verifyFailure("DistributionConfigParameters_NoPolicyForwarder.json",
            "\"policyForwarderConfigurationParameters\" value \"null\" INVALID, is null");
    }

    @Test
    void testDistributionParameterGroup_EmptyPolicyDecoder()
            throws CommandLineException {
        verifyFailure("DistributionConfigParameters_EmptyPolicyDecoder.json",
            "\"policyDecoders\" value \"{}\" INVALID, minimum number of elements: 1");
    }

    @Test
    void testDistributionParameterGroup_EmptyPolicyForwarder()
            throws CommandLineException {
        verifyFailure("DistributionConfigParameters_EmptyPolicyForwarder.json",
            "\"policyForwarders\" value \"{}\" INVALID, minimum number of elements: 1");
    }

    @Test
    void testDistributionParameterGroup_InvalidReceptionHandlerParameters()
            throws IOException, CommandLineException {

        var resultString = Files
                .readString(
                        Paths.get("src/test/resources/expectedValidationResults/InvalidReceptionHandlerParameters.txt"))
                .trim().replaceAll("\\r\\n", "\n");

        verifyFailure("DistributionConfigParameters_InvalidReceptionHandlerParameters.json",
            resultString);
    }

    @Test
    void testDistributionParameterGroup_InvalidDecoderAndForwarderParameters()
            throws IOException, CommandLineException {

        var resultString = Files
                .readString(Paths
                        .get("src/test/resources/expectedValidationResults/InvalidDecoderAndForwarderParameters.txt"))
                .trim().replaceAll("\\r\\n", "\n");

        verifyFailure("DistributionConfigParameters_InvalidDecoderAndForwarderParameters.json",
            resultString);
    }

    @Test
    void testDistributionParameterGroup_InvalidRestServerParameters()
            throws IOException, CommandLineException {

        var resultString = Files
                .readString(Paths.get("src/test/resources/expectedValidationResults/InvalidRestServerParameters.txt"))
                .trim().replaceAll("\\r\\n", "\n");

        verifyFailure("DistributionConfigParameters_InvalidRestServerParameters.json",
            resultString);
    }

    @Test
    void testDistributionVersion() throws CommandLineException {
        final String[] distributionConfigParameters = {"-v"};
        final var arguments = new DistributionCommandLineArguments();
        final var version = arguments.parse(distributionConfigParameters);
        assertTrue(version.startsWith("ONAP Policy Framework Distribution Service"));
    }

    @Test
    void testDistributionHelp() throws CommandLineException {
        final String[] distributionConfigParameters = {"-h"};
        final var arguments = new DistributionCommandLineArguments();
        final var help = arguments.parse(distributionConfigParameters);
        assertTrue(help.startsWith("usage:"));
    }

    @Test
    void testDistributionInvalidOption() {
        final String[] distributionConfigParameters = {"-d"};
        final var arguments = new DistributionCommandLineArguments();
        assertThatThrownBy(() -> arguments.parse(distributionConfigParameters))
                .hasMessageContaining("invalid command line arguments specified");
    }

    @Test
    void testDistributionParameterGroup_InvalidReceptionHandlerClass()
            throws CommandLineException {
        verifyFailure("DistributionConfigParameters_InvalidReceptionHandlerClass.json",
            "could not find class");
    }

    @Test
    void testDistributionParameterGroup_EmptyReceptionHandlerClass()
            throws CommandLineException {
        verifyFailure("DistributionConfigParameters_EmptyReceptionHandlerClass.json",
            "invalid in JSON file");
    }

    @Test
    void testDistributionParameterGroup_InvalidDecoderConfigurationClassName()
            throws CommandLineException {
        verifyFailure("DistributionConfigParameters_InvalidDecoderConfigurationClassName.json",
            "parameter \"parameterClassName\" value \"\" invalid in JSON file");
    }

    @Test
    void testDistributionParameterGroup_UnknownDecoderConfigurationClassName()
            throws CommandLineException {
        verifyFailure("DistributionConfigParameters_UnknownDecoderConfigurationClassName.json",
            "parameter \"parameterClassName\" value \"org.onap.policy.Unknown\", could not find class");
    }

    private <T> void verifyFailure(String fileName, String expectedMessage)
            throws CommandLineException {
        final String[] distributionConfigParameters = {"-c", "parameters/" + fileName};

        final var arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        var paramHandler = new DistributionParameterHandler();

        assertThatThrownBy(() -> paramHandler.getParameters(arguments)).isInstanceOf(PolicyDistributionException.class)
                .hasMessageContaining(expectedMessage);
    }
}

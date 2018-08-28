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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.startstop.DistributionCommandLineArguments;

/**
 * Class to perform unit test of DistributionParameterHandler.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestDistributionParameterHandler {
    @Test
    public void testParameterHandlerNoParameterFile() throws PolicyDistributionException {
        final String[] noArgumentString =
        { "-c", "parameters/NoParameterFile.json" };

        final DistributionCommandLineArguments noArguments = new DistributionCommandLineArguments();
        noArguments.parse(noArgumentString);

        try {
            new DistributionParameterHandler().getParameters(noArguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("FileNotFoundException"));
        }
    }

    @Test
    public void testParameterHandlerEmptyParameters() throws PolicyDistributionException {
        final String[] emptyArgumentString =
        { "-c", "parameters/EmptyParameters.json" };

        final DistributionCommandLineArguments emptyArguments = new DistributionCommandLineArguments();
        emptyArguments.parse(emptyArgumentString);

        try {
            new DistributionParameterHandler().getParameters(emptyArguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("no parameters found in \"parameters/EmptyParameters.json\"", e.getMessage());
        }
    }

    @Test
    public void testParameterHandlerBadParameters() throws PolicyDistributionException {
        final String[] badArgumentString =
        { "-c", "parameters/BadParameters.json" };

        final DistributionCommandLineArguments badArguments = new DistributionCommandLineArguments();
        badArguments.parse(badArgumentString);

        try {
            new DistributionParameterHandler().getParameters(badArguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("error reading parameters from \"parameters/BadParameters.json\"\n"
                    + "(JsonSyntaxException):java.lang.IllegalStateException: "
                    + "Expected a string but was BEGIN_ARRAY at line 2 column 15 path $.name", e.getMessage());
        }
    }

    @Test
    public void testParameterHandlerInvalidParameters() throws PolicyDistributionException {
        final String[] invalidArgumentString =
        { "-c", "parameters/InvalidParameters.json" };

        final DistributionCommandLineArguments invalidArguments = new DistributionCommandLineArguments();
        invalidArguments.parse(invalidArgumentString);

        try {
            new DistributionParameterHandler().getParameters(invalidArguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("error reading parameters from \"parameters/InvalidParameters.json\"\n"
                    + "(JsonSyntaxException):java.lang.IllegalStateException: "
                    + "Expected a string but was BEGIN_ARRAY at line 2 column 15 path $.name", e.getMessage());
        }
    }

    @Test
    public void testParameterHandlerNoParameters() throws PolicyDistributionException {
        final String[] noArgumentString =
        { "-c", "parameters/NoParameters.json" };

        final DistributionCommandLineArguments noArguments = new DistributionCommandLineArguments();
        noArguments.parse(noArgumentString);

        try {
            new DistributionParameterHandler().getParameters(noArguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("map parameter \"receptionHandlerParameters\" is null", e.getMessage());
        }
    }

    @Test
    public void testParameterHandlerMinumumParameters() throws PolicyDistributionException {
        final String[] minArgumentString =
        { "-c", "parameters/MinimumParameters.json" };

        final DistributionCommandLineArguments minArguments = new DistributionCommandLineArguments();
        minArguments.parse(minArgumentString);

        final DistributionParameterGroup parGroup = new DistributionParameterHandler().getParameters(minArguments);
        assertEquals(CommonTestData.DISTRIBUTION_GROUP_NAME, parGroup.getName());
    }

    @Test
    public void testDistributionParameterGroup() throws PolicyDistributionException {
        final String[] distributionConfigParameters =
        { "-c", "parameters/DistributionConfigParameters.json" };

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
    }

    @Test
    public void testDistributionParameterGroup_InvalidName() throws PolicyDistributionException {
        final String[] distributionConfigParameters =
        { "-c", "parameters/DistributionConfigParameters_InvalidName.json" };

        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        try {
            new DistributionParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains(
                    "field \"name\" type \"java.lang.String\" value \" \" INVALID, must be a non-blank string"));
        }
    }

    @Test
    public void testDistributionParameterGroup_NoReceptionHandler() throws PolicyDistributionException {
        final String[] distributionConfigParameters =
        { "-c", "parameters/DistributionConfigParameters_NoReceptionHandler.json" };

        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        try {
            new DistributionParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("map parameter \"receptionHandlerParameters\" is null"));
        }
    }

    @Test
    public void testDistributionParameterGroup_EmptyReceptionHandler() throws PolicyDistributionException {
        final String[] distributionConfigParameters =
        { "-c", "parameters/DistributionConfigParameters_EmptyReceptionHandler.json" };

        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        try {
            new DistributionParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("parameter not a regular parameter: receptionHandlerParameters"));
        }
    }

    @Test
    public void testDistributionParameterGroup_NoPolicyDecoder() throws PolicyDistributionException {
        final String[] distributionConfigParameters =
        { "-c", "parameters/DistributionConfigParameters_NoPolicyDecoder.json" };

        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        try {
            new DistributionParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("map parameter \"policyDecoders\" is null"));
        }
    }

    @Test
    public void testDistributionParameterGroup_NoPolicyForwarder() throws PolicyDistributionException {
        final String[] distributionConfigParameters =
        { "-c", "parameters/DistributionConfigParameters_NoPolicyForwarder.json" };

        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        try {
            new DistributionParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("map parameter \"policyForwarders\" is null"));
        }
    }

    @Test
    public void testDistributionParameterGroup_EmptyPolicyDecoder() throws PolicyDistributionException {
        final String[] distributionConfigParameters =
        { "-c", "parameters/DistributionConfigParameters_EmptyPolicyDecoder.json" };

        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        try {
            new DistributionParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("parameter not a regular parameter: policyDecoders"));
        }
    }

    @Test
    public void testDistributionParameterGroup_EmptyPolicyForwarder() throws PolicyDistributionException {
        final String[] distributionConfigParameters =
        { "-c", "parameters/DistributionConfigParameters_EmptyPolicyForwarder.json" };

        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        try {
            new DistributionParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("parameter not a regular parameter: policyForwarders"));
        }
    }

    @Test
    public void testDistributionParameterGroup_InvalidReceptionHandlerParameters()
            throws PolicyDistributionException, IOException {
        final String[] distributionConfigParameters =
        { "-c", "parameters/DistributionConfigParameters_InvalidReceptionHandlerParameters.json" };

        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        try {
            new DistributionParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            final String expectedResult = new String(Files.readAllBytes(
                    Paths.get("src/test/resources/expectedValidationResults/InvalidReceptionHandlerParameters.txt")))
                            .replaceAll("\\s+", "");
            assertEquals(expectedResult, e.getMessage().replaceAll("\\s+", ""));
        }
    }

    @Test
    public void testDistributionParameterGroup_InvalidDecoderAndForwarderParameters()
            throws PolicyDistributionException, IOException {
        final String[] distributionConfigParameters =
        { "-c", "parameters/DistributionConfigParameters_InvalidDecoderAndForwarderParameters.json" };

        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        try {
            new DistributionParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            final String expectedResult = new String(Files.readAllBytes(
                    Paths.get("src/test/resources/expectedValidationResults/InvalidDecoderAndForwarderParameters.txt")))
                            .replaceAll("\\s+", "");
            assertEquals(expectedResult, e.getMessage().replaceAll("\\s+", ""));
        }
    }

    @Test
    public void testDistributionParameterGroup_InvalidRestServerParameters()
            throws PolicyDistributionException, IOException {
        final String[] distributionConfigParameters =
        { "-c", "parameters/DistributionConfigParameters_InvalidRestServerParameters.json" };

        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        arguments.parse(distributionConfigParameters);

        try {
            new DistributionParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            final String expectedResult = new String(Files.readAllBytes(
                    Paths.get("src/test/resources/expectedValidationResults/InvalidRestServerParameters.txt")))
                            .replaceAll("\\s+", "");
            assertEquals(expectedResult, e.getMessage().replaceAll("\\s+", ""));
        }
    }

    @Test
    public void testDistributionVersion() throws PolicyDistributionException {
        final String[] distributionConfigParameters =
        { "-v" };
        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        final String version = arguments.parse(distributionConfigParameters);
        assertTrue(version.startsWith("ONAP Policy Framework Distribution Service"));
    }

    @Test
    public void testDistributionHelp() throws PolicyDistributionException {
        final String[] distributionConfigParameters =
        { "-h" };
        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        final String help = arguments.parse(distributionConfigParameters);
        assertTrue(help.startsWith("usage:"));
    }

    @Test
    public void testDistributionInvalidOption() throws PolicyDistributionException {
        final String[] distributionConfigParameters =
        { "-d" };
        final DistributionCommandLineArguments arguments = new DistributionCommandLineArguments();
        try {
            arguments.parse(distributionConfigParameters);
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("invalid command line arguments specified"));
        }
    }
}

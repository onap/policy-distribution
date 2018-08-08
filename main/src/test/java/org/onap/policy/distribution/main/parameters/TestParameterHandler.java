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

import org.junit.Test;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.startstop.DistributionCommandLineArguments;

public class TestParameterHandler {
    @Test
    public void testParameterHandlerNoParameterFile() throws PolicyDistributionException {
        final String[] noArgumentString = { "-c", "parameters/NoParameterFile.json" };

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
        final String[] emptyArgumentString = { "-c", "parameters/EmptyParameters.json" };

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
        final String[] badArgumentString = { "-c", "parameters/BadParameters.json" };

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
        final String[] invalidArgumentString = { "-c", "parameters/InvalidParameters.json" };

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
        final String[] noArgumentString = { "-c", "parameters/NoParameters.json" };

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
        final String[] minArgumentString = { "-c", "parameters/MinimumParameters.json" };

        final DistributionCommandLineArguments minArguments = new DistributionCommandLineArguments();
        minArguments.parse(minArgumentString);

        final DistributionParameterGroup parGroup = new DistributionParameterHandler().getParameters(minArguments);
        assertEquals("SDCDistributionGroup", parGroup.getName());
    }
}

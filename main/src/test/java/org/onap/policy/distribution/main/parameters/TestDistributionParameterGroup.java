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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;
import org.onap.policy.common.parameters.GroupValidationResult;

/**
 * Class to perform unit test of DistributionParameterGroup.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestDistributionParameterGroup {
    CommonTestData commonTestData = new CommonTestData();

    @Test
    public void testDistributionParameterGroup() {
        final String name = "SDCDistributionGroup";
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(false);

        final DistributionParameterGroup distributionParameters =
                new DistributionParameterGroup(name, receptionHandlerParameters);
        final GroupValidationResult validationResult = distributionParameters.validate();
        assertTrue(validationResult.isValid());
        assertEquals(name, distributionParameters.getName());
        assertEquals(receptionHandlerParameters.get("SDCReceptionHandler").getReceptionHandlerType(),
                distributionParameters.getReceptionHandlerParameters().get("SDCReceptionHandler")
                        .getReceptionHandlerType());
        assertEquals(receptionHandlerParameters.get("SDCReceptionHandler").getReceptionHandlerClassName(),
                distributionParameters.getReceptionHandlerParameters().get("SDCReceptionHandler")
                        .getReceptionHandlerClassName());
        assertEquals(receptionHandlerParameters.get("SDCReceptionHandler").getPluginHandlerParameters(),
                distributionParameters.getReceptionHandlerParameters().get("SDCReceptionHandler")
                        .getPluginHandlerParameters());
    }

    @Test
    public void testDistributionParameterGroup_NullName() {
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(false);

        final DistributionParameterGroup distributionParameters =
                new DistributionParameterGroup(null, receptionHandlerParameters);
        final GroupValidationResult validationResult = distributionParameters.validate();
        assertFalse(validationResult.isValid());
        assertEquals(null, distributionParameters.getName());
        assertEquals(receptionHandlerParameters.get("SDCReceptionHandler").getReceptionHandlerType(),
                distributionParameters.getReceptionHandlerParameters().get("SDCReceptionHandler")
                        .getReceptionHandlerType());
        assertEquals(receptionHandlerParameters.get("SDCReceptionHandler").getReceptionHandlerClassName(),
                distributionParameters.getReceptionHandlerParameters().get("SDCReceptionHandler")
                        .getReceptionHandlerClassName());
        assertEquals(receptionHandlerParameters.get("SDCReceptionHandler").getPluginHandlerParameters(),
                distributionParameters.getReceptionHandlerParameters().get("SDCReceptionHandler")
                        .getPluginHandlerParameters());
        assertTrue(validationResult.getResult().contains(
                "field \"name\" type \"java.lang.String\" value \"null\" INVALID, " + "must be a non-blank string"));
    }

    @Test
    public void testDistributionParameterGroup_EmptyName() {
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(false);

        final DistributionParameterGroup distributionParameters =
                new DistributionParameterGroup("", receptionHandlerParameters);
        final GroupValidationResult validationResult = distributionParameters.validate();
        assertFalse(validationResult.isValid());
        assertEquals("", distributionParameters.getName());
        assertEquals(receptionHandlerParameters.get("SDCReceptionHandler").getReceptionHandlerType(),
                distributionParameters.getReceptionHandlerParameters().get("SDCReceptionHandler")
                        .getReceptionHandlerType());
        assertEquals(receptionHandlerParameters.get("SDCReceptionHandler").getReceptionHandlerClassName(),
                distributionParameters.getReceptionHandlerParameters().get("SDCReceptionHandler")
                        .getReceptionHandlerClassName());
        assertEquals(receptionHandlerParameters.get("SDCReceptionHandler").getPluginHandlerParameters(),
                distributionParameters.getReceptionHandlerParameters().get("SDCReceptionHandler")
                        .getPluginHandlerParameters());
        assertTrue(validationResult.getResult().contains(
                "field \"name\" type \"java.lang.String\" value \"\" INVALID, " + "must be a non-blank string"));
    }

    @Test
    public void testDistributionParameterGroup_NullReceptionHandlerParameters() {
        final String name = "SDCDistributionGroup";
        try {
            final DistributionParameterGroup distributionParameters = new DistributionParameterGroup(name, null);
            distributionParameters.validate();
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("map parameter \"receptionHandlerParameters\" is null"));
        }

    }

    @Test
    public void testDistributionParameterGroup_EmptyReceptionHandlerParameters() {
        final String name = "SDCDistributionGroup";
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(true);
        try {
            final DistributionParameterGroup distributionParameters =
                    new DistributionParameterGroup(name, receptionHandlerParameters);
            distributionParameters.validate();
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("parameter not a regular parameter: receptionHandlerParameters"));
        }

    }
}

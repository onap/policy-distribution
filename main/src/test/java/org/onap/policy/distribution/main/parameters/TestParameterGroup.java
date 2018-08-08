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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;

import org.junit.Test;
import org.onap.policy.common.parameters.GroupValidationResult;

public class TestParameterGroup {

    @Test
    public void testDistributionParameterGroup() {
        DistributionParameterGroup testParameterGroup = null;
        try {
            final Gson gson = new GsonBuilder().create();
            testParameterGroup =
                    gson.fromJson(new FileReader("src/test/resources/parameters/DistributionConfigParameters.json"),
                            DistributionParameterGroup.class);
        } catch (final Exception e) {
            fail("test should not throw an exception here: " + e.getMessage());
        }
        final GroupValidationResult validationResult = testParameterGroup.validate();
        assertTrue(validationResult.isValid());
        assertEquals("SDCDistributionGroup", testParameterGroup.getName());
    }

    @Test
    public void testInvalidDistributionParameterGroup() {
        DistributionParameterGroup testParameterGroup = null;
        try {
            final Gson gson = new GsonBuilder().create();
            testParameterGroup = gson.fromJson(
                    new FileReader("src/test/resources/parameters/DistributionConfigParametersInvalid.json"),
                    DistributionParameterGroup.class);
        } catch (final Exception e) {
            fail("test should not throw an exception here: " + e.getMessage());
        }
        final GroupValidationResult validationResult = testParameterGroup.validate();
        assertFalse(validationResult.isValid());
    }
}

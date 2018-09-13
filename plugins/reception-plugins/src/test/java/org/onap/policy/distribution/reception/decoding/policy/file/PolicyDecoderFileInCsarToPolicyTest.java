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

package org.onap.policy.distribution.reception.decoding.policy.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.model.PolicyAsString;

/**
 * Class to perform unit test of {@link PolicyDecoderFileInCsarToPolicy}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class PolicyDecoderFileInCsarToPolicyTest {

    private static final String POLICY_FILE_NAME = "SamplePolicyModelJAVASCRIPT";
    private static final String POLICY_TYPE = "APEX";
    private static final String GROUP_NAME = "apexPdpDecoderConfiguration";

    /**
     * Set up.
     */
    @BeforeClass
    public static void setUp() {
        final PolicyDecoderFileInCsarToPolicyParameterGroup configurationParameters =
                new PolicyDecoderFileInCsarToPolicyParameterGroup(POLICY_FILE_NAME, POLICY_TYPE);
        configurationParameters.setName(GROUP_NAME);
        ParameterService.register(configurationParameters);
    }

    /**
     * Tear down.
     */
    @AfterClass
    public static void tearDown() {
        ParameterService.deregister(GROUP_NAME);
    }

    @Test
    public void testDecodePolicy() {

        final PolicyDecoderFileInCsarToPolicy decoder = new PolicyDecoderFileInCsarToPolicy();
        decoder.configure(GROUP_NAME);

        final File file = new File("src/test/resources/sampleTestService.csar");
        final Csar csar = new Csar(file.getAbsolutePath());

        try {
            decoder.canHandle(csar);
            final Collection<PolicyAsString> policyHolders = decoder.decode(csar);
            for (final PolicyAsString policy : policyHolders) {
                assertEquals(POLICY_FILE_NAME, policy.getPolicyName());
                assertEquals(POLICY_TYPE, policy.getPolicyType());
            }
        } catch (final Exception exp) {
            fail("Test must not throw an exception");
        }
    }

    @Test
    public void testDecodePolicyError() throws IOException {

        final PolicyDecoderFileInCsarToPolicy decoder = new PolicyDecoderFileInCsarToPolicy();
        decoder.configure(GROUP_NAME);

        final File file = new File("unknown.csar");
        final Csar csar = new Csar(file.getAbsolutePath());

        try {
            decoder.canHandle(csar);
            decoder.decode(csar);
            fail("Test must throw an exception");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().contains("Failed decoding the policy"));
        }
    }
}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2022 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.handling.sdc.CommonTestData;

/**
 * Class to perform unit test of {@link PolicyDecoderFileInCsarToPolicy}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
class PolicyDecoderFileInCsarToPolicyTest {

    /**
     * Set up.
     */
    @BeforeAll
    static void setUp() {
        final PolicyDecoderFileInCsarToPolicyParameterGroup configurationParameters = CommonTestData
                .getPolicyDecoderParameters("src/test/resources/parameters/FileInCsarPolicyDecoderParameters.json",
                        PolicyDecoderFileInCsarToPolicyParameterGroup.class);
        configurationParameters.setName(PolicyDecoderFileInCsarToPolicyParameterGroup.class.getSimpleName());
        ParameterService.register(configurationParameters);
    }

    /**
     * Tear down.
     */
    @AfterAll
    static void tearDown() {
        ParameterService.deregister(PolicyDecoderFileInCsarToPolicyParameterGroup.class.getSimpleName());
    }

    @Test
    void testDecodePolicy() throws PolicyDecodingException {

        final var decoder = new PolicyDecoderFileInCsarToPolicy();
        decoder.configure(PolicyDecoderFileInCsarToPolicyParameterGroup.class.getSimpleName());

        final var file = new File("src/test/resources/service-Sampleservice.csar");
        final var csar = new Csar(file.getAbsolutePath());

        assertTrue(decoder.canHandle(csar));
        final var policyHolders = decoder.decode(csar);
        assertEquals(2, policyHolders.size());
    }

    @Test
    void testDecodeYamlPolicy() throws PolicyDecodingException {

        final var decoder = new PolicyDecoderFileInCsarToPolicy();
        decoder.configure(PolicyDecoderFileInCsarToPolicyParameterGroup.class.getSimpleName());

        final var file = new File("src/test/resources/service-Sampleservice-yaml.csar");
        final var csar = new Csar(file.getAbsolutePath());

        assertTrue(decoder.canHandle(csar));
        final var policyHolders = decoder.decode(csar);
        assertEquals(2, policyHolders.size());
    }

    @Test
    void testDecodePolicyZipError() {

        final var decoder = new PolicyDecoderFileInCsarToPolicy();
        decoder.configure(PolicyDecoderFileInCsarToPolicyParameterGroup.class.getSimpleName());

        final var file = new File("unknown.csar");
        final var csar = new Csar(file.getAbsolutePath());

        assertTrue(decoder.canHandle(csar));
        assertThatThrownBy(() -> decoder.decode(csar)).isInstanceOf(PolicyDecodingException.class)
        .hasMessageContaining("Couldn't read the zipFile");
    }


    @Test
    void testDecodePolicyCoderError() {

        final var decoder = new PolicyDecoderFileInCsarToPolicy();
        decoder.configure(PolicyDecoderFileInCsarToPolicyParameterGroup.class.getSimpleName());

        final var file = new File("src/test/resources/service-Sampleservice-test.csar");
        final var csar = new Csar(file.getAbsolutePath());

        assertTrue(decoder.canHandle(csar));
        assertThatThrownBy(() -> decoder.decode(csar)).isInstanceOf(PolicyDecodingException.class)
        .hasMessageContaining("Failed decoding the policy");
    }
}

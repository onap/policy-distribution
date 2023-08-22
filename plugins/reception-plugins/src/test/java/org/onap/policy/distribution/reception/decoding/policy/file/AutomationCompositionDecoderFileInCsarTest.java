/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Nordix Foundation.
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
 * Class to perform unit test of {@link AutomationCompositionDecoderFileInCsar}.
 *
 * @author Sirisha Manchikanti (sirisha.manchikanti@est.tech)
 */
class AutomationCompositionDecoderFileInCsarTest {

    /**
     * Set up.
     */
    @BeforeAll
    static void setUp() {
        final var configurationParameters = CommonTestData
                .getPolicyDecoderParameters(
                    "src/test/resources/parameters/FileInCsarAutomationCompositionDecoderParameters.json",
                    AutomationCompositionDecoderFileInCsarParameterGroup.class);
        configurationParameters.setName(AutomationCompositionDecoderFileInCsarParameterGroup.class.getSimpleName());
        ParameterService.register(configurationParameters);
    }

    /**
     * Tear down.
     */
    @AfterAll
    static void tearDown() {
        ParameterService.deregister(AutomationCompositionDecoderFileInCsarParameterGroup.class.getSimpleName());
    }

    @Test
    void testDecodeAutomationComposition() throws PolicyDecodingException {

        final var decoder = new AutomationCompositionDecoderFileInCsar();
        decoder.configure(AutomationCompositionDecoderFileInCsarParameterGroup.class.getSimpleName());

        final var file = new File("src/test/resources/service-Sampleservice-acm.csar");
        final var csar = new Csar(file.getAbsolutePath());

        assertTrue(decoder.canHandle(csar));
        final var automationCompositionHolders = decoder.decode(csar);
        assertEquals(1, automationCompositionHolders.size());
    }

    @Test
    void testDecodeAutomationCompositionZipError() {

        final var decoder = new AutomationCompositionDecoderFileInCsar();
        decoder.configure(AutomationCompositionDecoderFileInCsarParameterGroup.class.getSimpleName());

        final var file = new File("unknown.csar");
        final var csar = new Csar(file.getAbsolutePath());

        assertTrue(decoder.canHandle(csar));
        assertThatThrownBy(() -> decoder.decode(csar)).isInstanceOf(PolicyDecodingException.class)
        .hasMessageContaining("Couldn't read the zipFile");
    }
}

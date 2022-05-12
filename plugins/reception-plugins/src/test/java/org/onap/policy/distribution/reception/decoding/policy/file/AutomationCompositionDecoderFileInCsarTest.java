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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.handling.sdc.CommonTestData;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;

/**
 * Class to perform unit test of {@link AutomationCompositionDecoderFileInCsar}.
 *
 * @author Sirisha Manchikanti (sirisha.manchikanti@est.tech)
 */
@RunWith(MockitoJUnitRunner.class)
public class AutomationCompositionDecoderFileInCsarTest {

    /**
     * Set up.
     */
    @BeforeClass
    public static void setUp() {
        final AutomationCompositionDecoderFileInCsarParameterGroup configurationParameters = CommonTestData
                .getPolicyDecoderParameters(
                    "src/test/resources/parameters/FileInCsarAutomationCompositionDecoderParameters.json",
                    AutomationCompositionDecoderFileInCsarParameterGroup.class);
        configurationParameters.setName(AutomationCompositionDecoderFileInCsarParameterGroup.class.getSimpleName());
        ParameterService.register(configurationParameters);
    }

    /**
     * Tear down.
     */
    @AfterClass
    public static void tearDown() {
        ParameterService.deregister(AutomationCompositionDecoderFileInCsarParameterGroup.class.getSimpleName());
    }

    @Test
    public void testDecodeAutomationComposition() throws PolicyDecodingException {

        final AutomationCompositionDecoderFileInCsar decoder = new AutomationCompositionDecoderFileInCsar();
        decoder.configure(AutomationCompositionDecoderFileInCsarParameterGroup.class.getSimpleName());

        final File file = new File("src/test/resources/service-Sampleservice-acm.csar");
        final Csar csar = new Csar(file.getAbsolutePath());

        assertTrue(decoder.canHandle(csar));
        final Collection<ToscaEntity> automationCompositionHolders = decoder.decode(csar);
        assertEquals(1, automationCompositionHolders.size());
    }

    @Test
    public void testDecodeAutomationCompositionZipError() {

        final AutomationCompositionDecoderFileInCsar decoder = new AutomationCompositionDecoderFileInCsar();
        decoder.configure(AutomationCompositionDecoderFileInCsarParameterGroup.class.getSimpleName());

        final File file = new File("unknown.csar");
        final Csar csar = new Csar(file.getAbsolutePath());

        assertTrue(decoder.canHandle(csar));
        assertThatThrownBy(() -> decoder.decode(csar)).isInstanceOf(PolicyDecodingException.class)
        .hasMessageContaining("Couldn't read the zipFile");
    }
}

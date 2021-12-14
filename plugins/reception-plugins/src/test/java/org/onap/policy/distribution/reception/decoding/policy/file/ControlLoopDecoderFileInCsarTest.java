/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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
 * Class to perform unit test of {@link ControlLoopDecoderFileInCsar}.
 *
 * @author Sirisha Manchikanti (sirisha.manchikanti@est.tech)
 */
@RunWith(MockitoJUnitRunner.class)
public class ControlLoopDecoderFileInCsarTest {

    /**
     * Set up.
     */
    @BeforeClass
    public static void setUp() {
        final ControlLoopDecoderFileInCsarParameterGroup configurationParameters = CommonTestData
                .getPolicyDecoderParameters("src/test/resources/parameters/FileInCsarControlLoopDecoderParameters.json",
                    ControlLoopDecoderFileInCsarParameterGroup.class);
        configurationParameters.setName(ControlLoopDecoderFileInCsarParameterGroup.class.getSimpleName());
        ParameterService.register(configurationParameters);
    }

    /**
     * Tear down.
     */
    @AfterClass
    public static void tearDown() {
        ParameterService.deregister(ControlLoopDecoderFileInCsarParameterGroup.class.getSimpleName());
    }

    @Test
    public void testDecodeControlLoop() throws PolicyDecodingException {

        final ControlLoopDecoderFileInCsar decoder = new ControlLoopDecoderFileInCsar();
        decoder.configure(ControlLoopDecoderFileInCsarParameterGroup.class.getSimpleName());

        final File file = new File("src/test/resources/service-Sampleservice-controlloop.csar");
        final Csar csar = new Csar(file.getAbsolutePath());

        assertTrue(decoder.canHandle(csar));
        final Collection<ToscaEntity> controlLoopHolders = decoder.decode(csar);
        assertEquals(1, controlLoopHolders.size());
    }

    @Test
    public void testDecodePolicyZipError() {

        final ControlLoopDecoderFileInCsar decoder = new ControlLoopDecoderFileInCsar();
        decoder.configure(ControlLoopDecoderFileInCsarParameterGroup.class.getSimpleName());

        final File file = new File("unknown.csar");
        final Csar csar = new Csar(file.getAbsolutePath());

        assertTrue(decoder.canHandle(csar));
        assertThatThrownBy(() -> decoder.decode(csar)).isInstanceOf(PolicyDecodingException.class)
        .hasMessageContaining("Failed decoding the controlloop");
    }
}

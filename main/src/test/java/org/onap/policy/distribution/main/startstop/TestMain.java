/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021 AT&T Inc.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

package org.onap.policy.distribution.main.startstop;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.resources.MessageConstants;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.PolicyDistributionRuntimeException;
import org.onap.policy.distribution.main.parameters.CommonTestData;

/**
 * Class to perform unit test of Main.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestMain {

    @Before
    public void setUp() {
        ParameterService.clear();
    }

    @Test
    public void testMain() throws PolicyDistributionException, IOException {
        CommonTestData.makeConfigFile("parameters/DistributionConfigParameters.json");
        final String[] distributionConfigParameters = { "-c", CommonTestData.CONFIG_FILE };
        final Main main = new Main(distributionConfigParameters);
        assertTrue(main.getDistributionParameterGroup().isValid());
        assertEquals(CommonTestData.DISTRIBUTION_GROUP_NAME, main.getDistributionParameterGroup().getName());
        main.shutdown();
    }

    @Test
    public void testMain_NoArguments() {
        final String[] distributionConfigParameters = {};
        assertThatThrownBy(() -> new Main(distributionConfigParameters))
            .isInstanceOf(PolicyDistributionRuntimeException.class)
            .hasMessage(String.format(MessageConstants.START_FAILURE_MSG, MessageConstants.POLICY_DISTRIBUTION));
    }

    @Test
    public void testMain_InvalidArguments() {
        final String[] distributionConfigParameters = {"parameters/DistributionConfigParameters.json"};
        assertThatThrownBy(() -> new Main(distributionConfigParameters))
            .isInstanceOf(PolicyDistributionRuntimeException.class)
            .hasMessage(String.format(MessageConstants.START_FAILURE_MSG, MessageConstants.POLICY_DISTRIBUTION));
    }

    @Test
    public void testMain_Help() {
        assertThatCode(() -> {
            final String[] distributionConfigParameters =
            { "-h" };
            Main.main(distributionConfigParameters);
        }).doesNotThrowAnyException();
    }

    @Test
    public void testMain_InvalidParameters() {
        final String[] distributionConfigParameters =
            {"-c", "parameters/DistributionConfigParameters_InvalidName.json"};
        assertThatThrownBy(() -> new Main(distributionConfigParameters))
            .isInstanceOf(PolicyDistributionRuntimeException.class)
            .hasMessage(String.format(MessageConstants.START_FAILURE_MSG, MessageConstants.POLICY_DISTRIBUTION));
    }
}

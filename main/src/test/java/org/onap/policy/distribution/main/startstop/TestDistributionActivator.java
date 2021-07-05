/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021 AT&T Intellectual Property. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.parameters.CommonTestData;
import org.onap.policy.distribution.main.parameters.DistributionParameterGroup;
import org.onap.policy.distribution.main.parameters.DistributionParameterHandler;
import org.onap.policy.distribution.main.testclasses.DummyPolicyForwarderParameterGroup;

/**
 * Class to perform unit test of DistributionActivator.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestDistributionActivator {

    @Before
    public void setUp() {
        ParameterService.clear();
    }

    @Test
    public void testDistributionActivator() throws PolicyDistributionException, IOException {
        CommonTestData.makeConfigFile("parameters/DistributionConfigParameters.json");
        final String[] distributionConfigParameters = { "-c", CommonTestData.CONFIG_FILE };

        final DistributionCommandLineArguments arguments =
                new DistributionCommandLineArguments(distributionConfigParameters);

        final DistributionParameterGroup parGroup = new DistributionParameterHandler().getParameters(arguments);

        final DistributionActivator activator = new DistributionActivator(parGroup);
        activator.initialize();
        assertTrue(activator.getDistributionParameterGroup().isValid());
        assertTrue(DistributionActivator.isAlive());
        assertEquals(CommonTestData.DISTRIBUTION_GROUP_NAME, activator.getDistributionParameterGroup().getName());
        assertEquals(CommonTestData.RECEPTION_HANDLER_TYPE,
                activator.getDistributionParameterGroup().getReceptionHandlerParameters()
                        .get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getReceptionHandlerType());
        assertEquals(CommonTestData.DECODER_TYPE,
                activator.getDistributionParameterGroup().getReceptionHandlerParameters()
                        .get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getPluginHandlerParameters()
                        .getPolicyDecoders().get(CommonTestData.DUMMY_DECODER_KEY).getDecoderType());
        assertEquals(CommonTestData.FORWARDER_TYPE,
                activator.getDistributionParameterGroup().getReceptionHandlerParameters()
                        .get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getPluginHandlerParameters()
                        .getPolicyForwarders().get(CommonTestData.DUMMY_ENGINE_FORWARDER_KEY).getForwarderType());
        assertEquals(DummyPolicyForwarderParameterGroup.class,
                activator.getDistributionParameterGroup().getPolicyForwarderConfigurationParameters()
                        .get(CommonTestData.FORWARDER_CONFIGURATION_PARAMETERS).getClass());
        activator.terminate();
        assertFalse(DistributionActivator.isAlive());
    }
}

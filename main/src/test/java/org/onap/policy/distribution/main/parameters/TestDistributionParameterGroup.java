/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2020-2021 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import org.junit.Test;
import org.onap.policy.common.endpoints.parameters.RestServerParameters;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.distribution.main.testclasses.DummyPolicyForwarderParameterGroup;
import org.onap.policy.distribution.main.testclasses.DummyReceptionHandlerParameterGroup;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

/**
 * Class to perform unit test of DistributionParameterGroup.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class TestDistributionParameterGroup {
    CommonTestData commonTestData = new CommonTestData();

    @Test
    public void testDistributionParameterGroup() {
        final RestServerParameters restServerParameters = commonTestData.getRestServerParameters(false);
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(false);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, PolicyForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getPolicyForwarderConfigurationParameters(false);
        final Map<String, PolicyDecoderConfigurationParameterGroup> decoderConfigurations =
                commonTestData.getPolicyDecoderConfigurationParameters(false);

        final DistributionParameterGroup distributionParameters = new DistributionParameterGroup(
                CommonTestData.DISTRIBUTION_GROUP_NAME, restServerParameters, receptionHandlerParameters,
                receptionHandlerConfigurations, forwarderConfigurations, decoderConfigurations);
        final ValidationResult validationResult = distributionParameters.validate();
        assertTrue(validationResult.isValid());
        assertEquals(restServerParameters.getHost(), distributionParameters.getRestServerParameters().getHost());
        assertEquals(restServerParameters.getPort(), distributionParameters.getRestServerParameters().getPort());
        assertEquals(restServerParameters.getUserName(),
                distributionParameters.getRestServerParameters().getUserName());
        assertEquals(restServerParameters.getPassword(),
                distributionParameters.getRestServerParameters().getPassword());
        assertEquals(CommonTestData.DISTRIBUTION_GROUP_NAME, distributionParameters.getName());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getReceptionHandlerType(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerType());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerClassName(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerClassName());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getPluginHandlerParameters(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getPluginHandlerParameters());
        assertEquals(CommonTestData.MY_STRING_PARAMETER_VALUE,
                ((DummyReceptionHandlerParameterGroup) distributionParameters
                        .getReceptionHandlerConfigurationParameters()
                        .get(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS)).getMyStringParameter());
        assertEquals(CommonTestData.MY_INTEGER_PARAMETER_VALUE,
                ((DummyReceptionHandlerParameterGroup) distributionParameters
                        .getReceptionHandlerConfigurationParameters()
                        .get(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS)).getMyIntegerParameter());
        assertEquals(CommonTestData.MY_BOOLEAN_PARAMETER_VALUE,
                ((DummyReceptionHandlerParameterGroup) distributionParameters
                        .getReceptionHandlerConfigurationParameters()
                        .get(CommonTestData.RECEPTION_CONFIGURATION_PARAMETERS)).isMyBooleanParameter());
        assertEquals(CommonTestData.FORWARDER_HOST,
                ((DummyPolicyForwarderParameterGroup) distributionParameters.getPolicyForwarderConfigurationParameters()
                        .get(CommonTestData.FORWARDER_CONFIGURATION_PARAMETERS)).getHostname());
    }

    @Test
    public void testDistributionParameterGroup_NullName() {
        final RestServerParameters restServerParameters = commonTestData.getRestServerParameters(false);
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(false);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, PolicyForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getPolicyForwarderConfigurationParameters(false);
        final Map<String, PolicyDecoderConfigurationParameterGroup> decoderConfigurations =
                commonTestData.getPolicyDecoderConfigurationParameters(false);

        final DistributionParameterGroup distributionParameters =
                new DistributionParameterGroup(null, restServerParameters, receptionHandlerParameters,
                        receptionHandlerConfigurations, forwarderConfigurations, decoderConfigurations);
        final ValidationResult validationResult = distributionParameters.validate();
        assertFalse(validationResult.isValid());
        assertEquals(null, distributionParameters.getName());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getReceptionHandlerType(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerType());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerClassName(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerClassName());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getPluginHandlerParameters(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getPluginHandlerParameters());
        assertThat(validationResult.getResult()).contains("\"name\" value \"null\" INVALID, is null");
    }

    @Test
    public void testDistributionParameterGroup_EmptyName() {
        final RestServerParameters restServerParameters = commonTestData.getRestServerParameters(false);
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(false);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, PolicyForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getPolicyForwarderConfigurationParameters(false);
        final Map<String, PolicyDecoderConfigurationParameterGroup> decoderConfigurations =
                commonTestData.getPolicyDecoderConfigurationParameters(false);

        final DistributionParameterGroup distributionParameters =
                new DistributionParameterGroup("", restServerParameters, receptionHandlerParameters,
                        receptionHandlerConfigurations, forwarderConfigurations, decoderConfigurations);
        final ValidationResult validationResult = distributionParameters.validate();
        assertFalse(validationResult.isValid());
        assertEquals("", distributionParameters.getName());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getReceptionHandlerType(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerType());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerClassName(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getReceptionHandlerClassName());
        assertEquals(
                receptionHandlerParameters.get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY).getPluginHandlerParameters(),
                distributionParameters.getReceptionHandlerParameters().get(CommonTestData.DUMMY_RECEPTION_HANDLER_KEY)
                        .getPluginHandlerParameters());
        assertThat(validationResult.getResult()).contains("\"name\" value \"\" INVALID, is blank");
    }

    @Test
    public void testDistributionParameterGroup_NullReceptionHandlerParameters() {
        final RestServerParameters restServerParameters = commonTestData.getRestServerParameters(false);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, PolicyForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getPolicyForwarderConfigurationParameters(false);
        final Map<String, PolicyDecoderConfigurationParameterGroup> decoderConfigurations =
                commonTestData.getPolicyDecoderConfigurationParameters(false);
        final DistributionParameterGroup distributionParameters =
                new DistributionParameterGroup(CommonTestData.DISTRIBUTION_GROUP_NAME, restServerParameters, null,
                                        receptionHandlerConfigurations, forwarderConfigurations, decoderConfigurations);
        assertThat(distributionParameters.validate().getResult())
                        .contains("\"receptionHandlerParameters\" value \"null\" INVALID, is null");
    }

    @Test
    public void testDistributionParameterGroup_EmptyReceptionHandlerParameters() {
        final RestServerParameters restServerParameters = commonTestData.getRestServerParameters(false);
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(true);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, PolicyForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getPolicyForwarderConfigurationParameters(false);
        final Map<String, PolicyDecoderConfigurationParameterGroup> decoderConfigurations =
                commonTestData.getPolicyDecoderConfigurationParameters(false);
        final DistributionParameterGroup distributionParameters = new DistributionParameterGroup(
                CommonTestData.DISTRIBUTION_GROUP_NAME, restServerParameters, receptionHandlerParameters,
                receptionHandlerConfigurations, forwarderConfigurations, decoderConfigurations);
        distributionParameters.validate();
        final ValidationResult result = distributionParameters.validate();
        assertFalse(result.isValid());
        assertThat(result.getResult()).contains("minimum");
    }

    @Test
    public void testDistributionParameterGroup_EmptyRestServerParameters() {
        final RestServerParameters restServerParameters = commonTestData.getRestServerParameters(true);
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(false);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, PolicyForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getPolicyForwarderConfigurationParameters(false);
        final Map<String, PolicyDecoderConfigurationParameterGroup> decoderConfigurations =
                commonTestData.getPolicyDecoderConfigurationParameters(false);

        final DistributionParameterGroup distributionParameters = new DistributionParameterGroup(
                CommonTestData.DISTRIBUTION_GROUP_NAME, restServerParameters, receptionHandlerParameters,
                receptionHandlerConfigurations, forwarderConfigurations, decoderConfigurations);
        final ValidationResult validationResult = distributionParameters.validate();
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"RestServerParameters\" INVALID");
    }

    @Test
    public void testDistributionParameterGroup_NullRestServerParameters() {
        final RestServerParameters restServerParameters = null;
        final Map<String, ReceptionHandlerParameters> receptionHandlerParameters =
                commonTestData.getReceptionHandlerParameters(false);
        final Map<String, ReceptionHandlerConfigurationParameterGroup> receptionHandlerConfigurations =
                commonTestData.getReceptionHandlerConfigurationParameters(false);
        final Map<String, PolicyForwarderConfigurationParameterGroup> forwarderConfigurations =
                commonTestData.getPolicyForwarderConfigurationParameters(false);
        final Map<String, PolicyDecoderConfigurationParameterGroup> decoderConfigurations =
                commonTestData.getPolicyDecoderConfigurationParameters(false);

        final DistributionParameterGroup distributionParameters = new DistributionParameterGroup(
                CommonTestData.DISTRIBUTION_GROUP_NAME, restServerParameters, receptionHandlerParameters,
                receptionHandlerConfigurations, forwarderConfigurations, decoderConfigurations);
        final ValidationResult validationResult = distributionParameters.validate();
        assertFalse(validationResult.isValid());
        assertThat(validationResult.getResult()).contains("\"restServerParameters\" value \"null\" INVALID, is null");
    }
}

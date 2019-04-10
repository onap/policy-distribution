/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Intel Corp. All rights reserved.
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

package org.onap.policy.distribution.forwarding.file;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.model.OptimizationPolicy;
import org.onap.policy.distribution.model.Policy;

/**
 * Class to perform unit test of {@link FilePolicyForwarder}.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class FilePolicyForwarderTest {
    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    private static final boolean VERBOSE = true;
    private static final String GROUP_NAME = "fileConfiguration";

    /**
     * Set up.
     */
    @BeforeClass
    public static void setUp() {
        final FilePolicyForwarderParameterBuilder builder = new FilePolicyForwarderParameterBuilder();
        builder.setPath(tempFolder.getRoot().getAbsolutePath().toString()).setVerbose(VERBOSE);
        final ParameterGroup parameterGroup = new FilePolicyForwarderParameterGroup(builder);
        parameterGroup.setName(GROUP_NAME);
        ParameterService.register(parameterGroup);
    }

    /**
     * Tear down.
     */
    @AfterClass
    public static void tearDown() {
        ParameterService.deregister(GROUP_NAME);
    }

    @Test
    public void testForwardPolicy() {
        final Collection<Policy> policies = new ArrayList<>();
        final OptimizationPolicy policy = new OptimizationPolicy();

        policy.setPolicyName("test");
        policy.setPolicyDescription("test");
        policy.setOnapName("");
        policy.setConfigBody("");
        policy.setConfigBodyType("");
        policy.setTimetolive(new Date());
        policy.setGuard("");
        policy.setRiskLevel("");
        policy.setRiskType("");
        policies.add(policy);

        final FilePolicyForwarder forwarder = new FilePolicyForwarder();
        forwarder.configure(GROUP_NAME);

        try {
            forwarder.forward(policies);
            Path path = Paths.get(tempFolder.getRoot().getAbsolutePath().toString(), policy.getPolicyName());
            assertTrue(Files.exists(path));
        } catch (final Exception exp) {
            fail("Test must not throw an exception");
        }
    }

    @Test
    public void testForwardPolicyError() {
        final Collection<Policy> policies = new ArrayList<>();
        OptimizationPolicy policy = new OptimizationPolicy();
        policy.setPolicyName("test");
        policy.setPolicyDescription("test");
        policy.setOnapName("");
        policy.setConfigBody("");
        policy.setConfigBodyType("");
        policy.setTimetolive(new Date());
        policy.setGuard("");
        policy.setRiskLevel("");
        policy.setRiskType("");

        OptimizationPolicy spy = Mockito.spy(policy);
        Mockito.when(spy.getRiskType()).thenThrow(IOException.class);
        policies.add(spy);

        final FilePolicyForwarder forwarder = new FilePolicyForwarder();
        forwarder.configure(GROUP_NAME);

        try {
            forwarder.forward(policies);
            fail("Test must throw an exception");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().contains("Error sending policy"));
        }
    }

    @Test
    public void testForwardUnsupportedPolicy() {
        final Collection<Policy> policies = new ArrayList<>();
        final FilePolicyForwarder forwarder = new FilePolicyForwarder();
        forwarder.configure(GROUP_NAME);

        final Policy policy = new UnsupportedPolicy();
        policies.add(policy);

        try {
            forwarder.forward(policies);
            fail("Test must throw an exception");
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().contains("Cannot forward policy"));
        }
    }

    class UnsupportedPolicy implements Policy {

        @Override
        public String getPolicyName() {
            return "unsupported";
        }

        @Override
        public String getPolicyType() {
            return "unsupported";
        }
    }
}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Intel Corp. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 AT&T Intellectual Property.
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

package org.onap.policy.distribution.forwarding.file;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;


/**
 * Class to perform unit test of {@link FilePolicyForwarder}.
 *
 */
class FilePolicyForwarderTest {

    private static Path tempFolder;

    private static final boolean VERBOSE = true;
    private static final String GROUP_NAME = "fileConfiguration";

    /**
     * Set up.
     */
    @BeforeAll
    static void setUp(@TempDir Path tempDir) {
        tempFolder = tempDir;
        final FilePolicyForwarderParameterGroup configurationParameters = new FilePolicyForwarderParameterGroup();
        configurationParameters.setPath(tempFolder.toFile().getAbsolutePath());
        configurationParameters.setVerbose(VERBOSE);
        configurationParameters.setName(GROUP_NAME);
        ParameterService.register(configurationParameters);
    }

    /**
     * Tear down.
     */
    @AfterAll
    static void tearDown() {
        ParameterService.deregister(GROUP_NAME);
    }

    @Test
    void testForwardPolicy() throws PolicyForwardingException {
        final Collection<ToscaEntity> policies = new ArrayList<>();
        final var policy = createPolicy(policies);

        final var forwarder = new FilePolicyForwarder();
        forwarder.configure(GROUP_NAME);

        forwarder.forward(policies);
        final var path = Paths.get(tempFolder.toFile().getAbsolutePath(), policy.getName());
        assertTrue(Files.exists(path));
    }

    @Test
    void testForwardPolicyError() {
        final Collection<ToscaEntity> policies = new ArrayList<>();
        final var policy = createPolicy(policies);

        final var spy = Mockito.spy(policy);
        Mockito.when(spy.toString()).thenAnswer(invocation -> {
            throw new IOException();
        });
        policies.add(spy);

        final var forwarder = new FilePolicyForwarder();
        forwarder.configure(GROUP_NAME);

        assertThatThrownBy(() -> forwarder.forward(policies)).isInstanceOf(PolicyForwardingException.class)
        .hasMessageContaining("Error sending policy");
    }

    @Test
    void testForwardUnsupportedPolicy() {
        final Collection<ToscaEntity> policies = new ArrayList<>();
        final var forwarder = new FilePolicyForwarder();
        forwarder.configure(GROUP_NAME);

        final var policy = new UnsupportedPolicy();
        policies.add(policy);

        assertThatThrownBy(() -> forwarder.forward(policies)).isInstanceOf(PolicyForwardingException.class)
        .hasMessageContaining("Cannot forward policy");
    }

    static class UnsupportedPolicy extends ToscaEntity {

        @Override
        public String getName() {
            return "unsupported";
        }
    }

    private ToscaPolicy createPolicy(final Collection<ToscaEntity> policies) {
        final ToscaPolicy policy = new ToscaPolicy();
        policy.setName("test");
        policy.setDescription("test");
        policies.add(policy);
        return policy;
    }
}

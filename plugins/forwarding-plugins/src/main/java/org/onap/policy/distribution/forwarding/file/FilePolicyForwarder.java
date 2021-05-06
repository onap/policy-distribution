/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Intel Crop. All rights reserved.
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

package org.onap.policy.distribution.forwarding.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Collection;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides an implementation of {@link PolicyForwarder} interface for forwarding the given policies to a
 * file directory.
 */
public class FilePolicyForwarder implements PolicyForwarder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilePolicyForwarder.class);
    private FilePolicyForwarderParameterGroup fileForwarderParameters;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void configure(final String parameterGroupName) {
        fileForwarderParameters = ParameterService.get(parameterGroupName);
        try {
            final var path = Paths.get(fileForwarderParameters.getPath());
            if (!path.toFile().exists()) {
                Files.createDirectories(path);
            }
        } catch (final InvalidPathException | IOException e) {
            LOGGER.error("Configuring FilePolicyForwarder failed!", e);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void forward(final Collection<ToscaEntity> policies) throws PolicyForwardingException {
        for (final ToscaEntity policy : policies) {
            if (policy instanceof ToscaPolicy) {
                forwardPolicy((ToscaPolicy) policy);
            } else {
                final String message = "Cannot forward policy " + policy + ". Unsupported policy type "
                        + policy.getClass().getSimpleName();
                LOGGER.error(message);
                throw new PolicyForwardingException(message);
            }
        }
    }

    /**
     * Method to forward a given policy to be logged into a file.
     *
     * @param pol the policy
     * @throws PolicyForwardingException if any exception occurs while forwarding policy
     */
    private void forwardPolicy(final ToscaPolicy pol) throws PolicyForwardingException {
        final String name = pol.getName();
        final var path = Paths.get(fileForwarderParameters.getPath(), name);
        try (var writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("policyName: " + name);
            if (fileForwarderParameters.isVerbose()) {
                writer.newLine();
                writer.write("policy: " + pol.toString());
            }
            LOGGER.debug("Sucessfully forwarded the policy to store into file {}.", path);
        } catch (final InvalidPathException | IOException exp) {
            final String message = "Error sending policy to file under path:" + fileForwarderParameters.getPath();
            throw new PolicyForwardingException(message, exp);
        }
    }
}


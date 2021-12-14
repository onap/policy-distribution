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

package org.onap.policy.distribution.forwarding.lifecycle.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.onap.policy.common.endpoints.http.client.HttpClient;
import org.onap.policy.common.endpoints.http.client.HttpClientConfigException;
import org.onap.policy.common.endpoints.http.client.HttpClientFactoryInstance;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides an implementation of {@link PolicyForwarder} interface for forwarding the
 * controlloop design template to the life cycle api's of controlloop components.
 *
 * @author Sirisha Manchikanti (sirisha.manchikanti@est.tech)
 */
public class LifecycleApiControlLoopForwarder implements PolicyForwarder {

    private static final String COMMISSION_CONTROLLOOP_URI = "/onap/controlloop/v2/commission";
    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleApiControlLoopForwarder.class);

    private LifecycleApiControlLoopForwarderParameters forwarderParameters;
    private HttpClient controlLoopClient;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void configure(final String parameterGroupName) throws HttpClientConfigException {
        forwarderParameters = ParameterService.get(parameterGroupName);

        controlLoopClient = HttpClientFactoryInstance.getClientFactory().build(
                forwarderParameters.getControlLoopRuntimeParameters());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void forward(final Collection<ToscaEntity> entities) throws PolicyForwardingException {
        final List<ToscaEntity> failedEntities = new ArrayList<>();
        for (final ToscaEntity entity : entities) {
            forwardSingleEntity(failedEntities, entity);
        }
        if (!failedEntities.isEmpty()) {
            throw new PolicyForwardingException(
                    "Failed forwarding the following entities: " + Arrays.toString(failedEntities.toArray()));
        }
    }

    private void forwardSingleEntity(final List<ToscaEntity> failedEntities, final ToscaEntity entity) {
        try {
            if (entity instanceof ToscaServiceTemplate) {
                final var toscaServiceTemplate = (ToscaServiceTemplate) entity;
                if (null != toscaServiceTemplate.getToscaTopologyTemplate()
                        && null != toscaServiceTemplate.getNodeTypes()
                        && null != toscaServiceTemplate.getDataTypes()) {
                    commissionControlLoop(toscaServiceTemplate);
                }
            } else {
                throw new PolicyForwardingException("The entity is not of type ToscaServiceTemplate - " + entity);
            }
        } catch (final Exception exp) {
            LOGGER.error(exp.getMessage(), exp);
            failedEntities.add(entity);
        }
    }

    private Response commissionControlLoop(final ToscaServiceTemplate toscaServiceTemplate)
            throws PolicyForwardingException {
        return invokeHttpClient(Entity.entity(toscaServiceTemplate, MediaType.APPLICATION_JSON),
                COMMISSION_CONTROLLOOP_URI);
    }

    private Response invokeHttpClient(final Entity<?> entity, final String path)
            throws PolicyForwardingException {
        var response = controlLoopClient.post(path, entity, Map.of(HttpHeaders.ACCEPT,
                        MediaType.APPLICATION_JSON, HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        if (response.getStatus() / 100 != 2) {
            LOGGER.error(
                    "Invocation of path {} failed for entity {}. Response status: {}, Response status info: {}",
                    path, entity, response.getStatus(), response.getStatusInfo());
            throw new PolicyForwardingException("Failed creating the entity - " + entity);
        }
        return response;
    }
}


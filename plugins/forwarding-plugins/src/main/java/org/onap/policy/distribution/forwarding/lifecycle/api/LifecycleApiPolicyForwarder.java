/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada.
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
import org.onap.policy.common.endpoints.event.comm.bus.internal.BusTopicParams;
import org.onap.policy.common.endpoints.http.client.HttpClient;
import org.onap.policy.common.endpoints.http.client.HttpClientConfigException;
import org.onap.policy.common.endpoints.http.client.HttpClientFactoryInstance;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.models.pap.concepts.PdpDeployPolicies;
import org.onap.policy.models.tosca.authorative.concepts.ToscaConceptIdentifierOptVersion;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides an implementation of {@link PolicyForwarder} interface for forwarding the given policies & policy
 * types to the life cycle api's of policy framework.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@est.tech)
 */
public class LifecycleApiPolicyForwarder implements PolicyForwarder {

    private static final String DEPLOY_POLICY_URI = "/policy/pap/v1/pdps/policies";
    private static final String CREATE_POLICY_TYPE_URI = "/policy/api/v1/policytypes/";
    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleApiPolicyForwarder.class);
    private LifecycleApiForwarderParameters forwarderParameters;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void configure(final String parameterGroupName) {
        forwarderParameters = ParameterService.get(parameterGroupName);
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
        Response policyCreated = null;
        try {
            if (entity instanceof ToscaServiceTemplate) {
                final var toscaServiceTemplate = (ToscaServiceTemplate) entity;
                if (null != toscaServiceTemplate.getPolicyTypes() && !toscaServiceTemplate.getPolicyTypes().isEmpty()) {
                    createPolicyType(toscaServiceTemplate);
                }
                if (null != toscaServiceTemplate.getToscaTopologyTemplate()
                        && null != toscaServiceTemplate.getToscaTopologyTemplate().getPolicies()
                        && !toscaServiceTemplate.getToscaTopologyTemplate().getPolicies().isEmpty()
                        && !toscaServiceTemplate.getToscaTopologyTemplate().getPolicies().get(0).entrySet().isEmpty()) {
                    policyCreated = createPolicy(toscaServiceTemplate);
                }
                if (forwarderParameters.isDeployPolicies() && policyCreated != null) {
                    deployPolicy(policyCreated.readEntity(ToscaServiceTemplate.class));
                }
            } else {
                throw new PolicyForwardingException("The entity is not of type ToscaServiceTemplate - " + entity);
            }
        } catch (final Exception exp) {
            LOGGER.error(exp.getMessage(), exp);
            failedEntities.add(entity);
        }
    }

    private Response createPolicyType(final ToscaServiceTemplate toscaServiceTemplate)
            throws PolicyForwardingException {
        return invokeHttpClient(Entity.entity(toscaServiceTemplate, MediaType.APPLICATION_JSON), CREATE_POLICY_TYPE_URI,
                true);
    }

    private Response createPolicy(final ToscaServiceTemplate toscaServiceTemplate) throws PolicyForwardingException {
        final ToscaPolicy policy = toscaServiceTemplate.getToscaTopologyTemplate().getPolicies().get(0).entrySet()
                .iterator().next().getValue();
        return invokeHttpClient(Entity.entity(toscaServiceTemplate, MediaType.APPLICATION_JSON),
                CREATE_POLICY_TYPE_URI + policy.getType() + "/versions/" + policy.getTypeVersion() + "/policies", true);
    }

    private Response deployPolicy(final ToscaServiceTemplate toscaServiceTemplate) throws PolicyForwardingException {
        final var pdpPolicies = new PdpDeployPolicies();
        final List<ToscaConceptIdentifierOptVersion> policyIdentifierList = new ArrayList<>();
        for (final Map<String, ToscaPolicy> policyMap : toscaServiceTemplate.getToscaTopologyTemplate().getPolicies()) {
            final String policyId = policyMap.entrySet().iterator().next().getValue().getMetadata().get("policy-id");
            final String policyVersion =
                    policyMap.entrySet().iterator().next().getValue().getMetadata().get("policy-version");
            final var toscaPolicyIdentifier =
                    new ToscaConceptIdentifierOptVersion(policyId, policyVersion);
            policyIdentifierList.add(toscaPolicyIdentifier);
        }
        pdpPolicies.setPolicies(policyIdentifierList);
        return invokeHttpClient(Entity.entity(pdpPolicies, MediaType.APPLICATION_JSON), DEPLOY_POLICY_URI, false);
    }

    private Response invokeHttpClient(final Entity<?> entity, final String path, final boolean wantApi)
            throws PolicyForwardingException {
        Response response = null;
        try {
            response = getHttpClient(wantApi).post(path, entity, Map.of(HttpHeaders.ACCEPT,
                    MediaType.APPLICATION_JSON, HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
            if (response.getStatus() / 100 != 2) {
                LOGGER.error(
                        "Invocation of path {} failed for entity {}. Response status: {}, Response status info: {}",
                        path, entity, response.getStatus(), response.getStatusInfo());
                throw new PolicyForwardingException("Failed creating the entity - " + entity);
            }
        } catch (final HttpClientConfigException exception) {
            throw new PolicyForwardingException("Invocation of path " + path + " failed for entity " + entity,
                    exception);
        }
        return response;
    }

    private HttpClient getHttpClient(final boolean wantApi) throws HttpClientConfigException {
        final boolean https = forwarderParameters.isHttps();
        final LifecycleApiParameters parameters =
                (wantApi ? forwarderParameters.getApiParameters() : forwarderParameters.getPapParameters());
        final BusTopicParams params = BusTopicParams.builder().clientName("Policy Distribution").useHttps(https)
                .hostname(parameters.getHostName()).port(parameters.getPort()).userName(parameters.getUserName())
                .password(parameters.getPassword()).allowSelfSignedCerts(forwarderParameters.isAllowSelfSignedCerts())
                .build();
        return HttpClientFactoryInstance.getClientFactory().build(params);
    }
}


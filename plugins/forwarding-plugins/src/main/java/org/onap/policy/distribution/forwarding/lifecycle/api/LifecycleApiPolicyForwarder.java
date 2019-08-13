/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.onap.policy.common.endpoints.event.comm.bus.internal.BusTopicParams;
import org.onap.policy.common.endpoints.http.client.HttpClient;
import org.onap.policy.common.endpoints.http.client.HttpClientConfigException;
import org.onap.policy.common.endpoints.http.client.HttpClientFactoryInstance;
import org.onap.policy.common.gson.GsonMessageBodyHandler;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.models.pap.concepts.PdpDeployPolicies;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyIdentifierOptVersion;
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
    private LifecycleApiPolicyForwarderParameterGroup forwarderParameters;

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
    public void forward(final Collection<ToscaEntity> policies) throws PolicyForwardingException {
        final List<ToscaEntity> failedPolicies = new ArrayList<>();
        for (final ToscaEntity policy : policies) {
            forwardSingleEntity(failedPolicies, policy);
        }
        if (!failedPolicies.isEmpty()) {
            throw new PolicyForwardingException(
                    "Failed forwarding the folowing policies: " + Arrays.toString(failedPolicies.toArray()));
        }
    }

    private void forwardSingleEntity(final List<ToscaEntity> failedPolicies, final ToscaEntity policy) {
        Response policyCreated = null;
        try {
            if (policy instanceof ToscaServiceTemplate) {
                final ToscaServiceTemplate toscaServiceTemplate = (ToscaServiceTemplate) policy;
                if (null != toscaServiceTemplate.getPolicyTypes() && !toscaServiceTemplate.getPolicyTypes().isEmpty()) {
                    createPolicyType(toscaServiceTemplate);
                }
                if (null != toscaServiceTemplate.getToscaTopologyTemplate()
                        && null != toscaServiceTemplate.getToscaTopologyTemplate().getPolicies()
                        && !toscaServiceTemplate.getToscaTopologyTemplate().getPolicies().isEmpty()) {
                    policyCreated = createPolicy(toscaServiceTemplate);
                }
                if (forwarderParameters.isDeployPolicies() && policyCreated != null) {
                    deployPolicy(policyCreated.readEntity(ToscaServiceTemplate.class));
                }
            }
        } catch (final Exception exp) {
            failedPolicies.add(policy);
        }
    }

    private Response createPolicyType(final ToscaServiceTemplate toscaServiceTemplate)
            throws PolicyForwardingException {
        return invokeHttpClient(Entity.entity(toscaServiceTemplate, MediaType.APPLICATION_JSON), CREATE_POLICY_TYPE_URI,
                "API");
    }

    private Response createPolicy(final ToscaServiceTemplate toscaServiceTemplate) throws PolicyForwardingException {
        final ToscaPolicy policy = toscaServiceTemplate.getToscaTopologyTemplate().getPolicies().get(0).entrySet()
                .iterator().next().getValue();
        return invokeHttpClient(Entity.entity(toscaServiceTemplate, MediaType.APPLICATION_JSON),
                CREATE_POLICY_TYPE_URI + policy.getType() + "/versions/" + policy.getTypeVersion() + "/policies",
                "API");
    }

    private Response deployPolicy(final ToscaServiceTemplate toscaServiceTemplate) throws PolicyForwardingException {
        final String policyId = toscaServiceTemplate.getToscaTopologyTemplate().getPolicies().get(0).entrySet()
                .iterator().next().getValue().getMetadata().get("policy-id");
        final String policyVersion = toscaServiceTemplate.getToscaTopologyTemplate().getPolicies().get(0).entrySet()
                .iterator().next().getValue().getMetadata().get("policy-version");
        final ToscaPolicyIdentifierOptVersion toscaPolicyIdentifier =
                new ToscaPolicyIdentifierOptVersion(policyId, policyVersion);
        final PdpDeployPolicies pdpPolicies = new PdpDeployPolicies();
        pdpPolicies.setPolicies(Arrays.asList(toscaPolicyIdentifier));
        return invokeHttpClient(Entity.entity(pdpPolicies, MediaType.APPLICATION_JSON), DEPLOY_POLICY_URI, "PAP");
    }

    private Response invokeHttpClient(final Entity<?> entity, final String path, final String componentName)
            throws PolicyForwardingException {
        Response response = null;
        try {
            response = getHttpClient(componentName).post(path, entity, ImmutableMap.of(HttpHeaders.ACCEPT,
                    MediaType.APPLICATION_JSON, HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
            if (response.getStatus() != Status.OK.getStatusCode()) {
                LOGGER.error(
                        "Invocation of path {} failed for entity {}. Response status: {}, Response status info: {}",
                        path, entity, response.getStatus(), response.getStatusInfo());
                throw new PolicyForwardingException("Failed creating the entity - " + entity);
            }
        } catch (final HttpClientConfigException exception) {
            LOGGER.error(
                    "Invocation of path " + path + " failed for entity " + entity + " due to error opening Http client",
                    exception);
            throw new PolicyForwardingException("Failed creating the entity - " + entity, exception);
        }
        return response;
    }

    private HttpClient getHttpClient(final String component) throws HttpClientConfigException {
        String hostname = null;
        int port = 0;
        String userName = null;
        String password = null;
        final boolean https = forwarderParameters.isHttps();
        if ("API".equals(component)) {
            hostname = forwarderParameters.getPolicyApiHostName();
            port = forwarderParameters.getPolicyApiPort();
            userName = forwarderParameters.getPolicyApiUserName();
            password = forwarderParameters.getPolicyApiPassword();
        } else {
            hostname = forwarderParameters.getPolicyPapHostName();
            port = forwarderParameters.getPolicyPapPort();
            userName = forwarderParameters.getPolicyPapUserName();
            password = forwarderParameters.getPolicyPapPassword();
        }
        final BusTopicParams params = BusTopicParams.builder().clientName("Policy Distribution").useHttps(https)
                .hostname(hostname).port(port).userName(userName).password(password)
                .serializationProvider(GsonMessageBodyHandler.class.getName()).build();
        return HttpClientFactoryInstance.getClientFactory().build(params);
    }
}


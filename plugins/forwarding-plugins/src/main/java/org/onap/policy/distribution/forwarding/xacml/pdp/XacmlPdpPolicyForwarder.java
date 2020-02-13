/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Copyright (C) 2019 Intel Corp. All rights reserved.
 *  Modifications Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.distribution.forwarding.xacml.pdp;

import java.util.Collection;
import java.util.Collections;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PushPolicyParameters;
import org.onap.policy.common.endpoints.event.comm.bus.internal.BusTopicParams;
import org.onap.policy.common.endpoints.http.client.HttpClient;
import org.onap.policy.common.endpoints.http.client.HttpClientConfigException;
import org.onap.policy.common.endpoints.http.client.HttpClientFactory;
import org.onap.policy.common.endpoints.http.client.HttpClientFactoryInstance;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.xacml.pdp.adapters.XacmlPdpOptimizationPolicyAdapter;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * Forwards policies to the XACML PDP.
 */
public class XacmlPdpPolicyForwarder implements PolicyForwarder {

    private static final Logger LOGGER = LoggerFactory.getLogger(XacmlPdpPolicyForwarder.class);
    private static final String BASE_PATH = "pdp/api/";

    private XacmlPdpPolicyForwarderParameterGroup configurationParameters = null;


    @Override
    public void forward(final Collection<ToscaEntity> policies) {
        for (final ToscaEntity policy : policies) {
            forward(policy);
        }
    }

    private void forward(final ToscaEntity policy) {
        final XacmlPdpPolicyAdapter<?> policyAdapter = getXacmlPdpPolicyAdapter(policy);

        if (policyAdapter == null) {
            LOGGER.error("Cannot forward policy {}. Unsupported policy type {}", policy,
                    policy.getClass().getSimpleName());
            return;
        }

        final boolean policyCreated = createPolicy(policyAdapter);
        if (policyCreated) {
            pushPolicy(policyAdapter);
        }
    }

    private XacmlPdpPolicyAdapter<?> getXacmlPdpPolicyAdapter(final ToscaEntity policy) {
        if (policy instanceof ToscaPolicy) {
            return new XacmlPdpOptimizationPolicyAdapter((ToscaPolicy) policy);
        }
        return null;
    }

    private boolean createPolicy(final XacmlPdpPolicyAdapter<?> policyAdapter) {
        final PolicyParameters policyParameters = policyAdapter.getAsPolicyParameters();
        final Entity<PolicyParameters> entity = Entity.entity(policyParameters, MediaType.APPLICATION_JSON);

        return invokeHttpClient(entity, "createPolicy", policyAdapter.getPolicy().getName());
    }

    private boolean pushPolicy(final XacmlPdpPolicyAdapter<?> policyAdapter) {
        final PushPolicyParameters pushPolicyParameters =
                policyAdapter.getAsPushPolicyParameters(configurationParameters.getPdpGroup());
        final Entity<PushPolicyParameters> entity = Entity.entity(pushPolicyParameters, MediaType.APPLICATION_JSON);

        return invokeHttpClient(entity, "pushPolicy", policyAdapter.getPolicy().getName());
    }

    private boolean invokeHttpClient(final Entity<?> entity, final String method, final String policyName) {

        try {
            final Response response = getHttpClient().put(method, entity,
                    Collections.singletonMap("ClientAuth", getValue(configurationParameters.getClientAuth())));

            if (response.getStatus() != HttpStatus.OK.value()) {
                LOGGER.error(
                        "Invocation of method {} failed for policy {}. Response status: {}, Response status info: {}",
                        method, policyName, response.getStatus(), response.getStatusInfo());
                return false;
            }
        } catch (final HttpClientConfigException exception) {
            LOGGER.error("Invocation of method " + method + " failed for policy " + policyName
                    + " due to error opening Http client", exception);
            return false;
        }
        return true;
    }

    private HttpClient getHttpClient() throws HttpClientConfigException {
        final boolean useHttps = configurationParameters.isUseHttps();
        final String hostname = configurationParameters.getHostname();
        final int port = configurationParameters.getPort();
        final String userName = getValue(configurationParameters.getUserName());
        final String password = getValue(configurationParameters.getPassword());
        final boolean managed = configurationParameters.isManaged();
        final BusTopicParams params =
                BusTopicParams.builder().clientName("SDC Dist").useHttps(useHttps).hostname(hostname).port(port)
                        .userName(userName).password(password).basePath(BASE_PATH).managed(managed).build();
        return getHttpClientFactory().build(params);
    }

    private String getValue(final String value) {
        if (value != null && value.matches("[$][{].*[}]$")) {
            return System.getenv(value.substring(2, value.length() - 1));
        }
        return value;
    }

    @Override
    public void configure(final String parameterGroupName) {
        configurationParameters = ParameterService.get(parameterGroupName);
    }

    // these may be overridden by junit tests

    protected HttpClientFactory getHttpClientFactory() {
        return HttpClientFactoryInstance.getClientFactory();
    }
}

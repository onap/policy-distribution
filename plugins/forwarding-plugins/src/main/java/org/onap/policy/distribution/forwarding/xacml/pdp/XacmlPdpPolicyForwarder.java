/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Collections;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PushPolicyParameters;
import org.onap.policy.common.endpoints.event.comm.bus.internal.BusTopicParams;
import org.onap.policy.common.endpoints.http.client.HttpClient;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.PolicyForwarder;
import org.onap.policy.distribution.forwarding.xacml.pdp.adapters.XacmlPdpOptimizationPolicyAdapter;
import org.onap.policy.distribution.model.OptimizationPolicy;
import org.onap.policy.distribution.model.Policy;
import org.springframework.http.HttpStatus;

/**
 * Forwards policies to the XACML PDP.
 */
public class XacmlPdpPolicyForwarder implements PolicyForwarder {

    private static final Logger LOGGER = FlexLogger.getLogger(XacmlPdpPolicyForwarder.class);
    private static final String BASE_PATH = "pdp/api/";

    private XacmlPdpPolicyForwarderParameterGroup configurationParameters = null;


    @Override
    public void forward(final Collection<Policy> policies) {
        for (Policy policy : policies) {
            forward(policy);
        }
    }

    private void forward(Policy policy) {
        XacmlPdpPolicyAdapter<?> policyAdapter = getXacmlPdpPolicyAdapter(policy);

        if (policyAdapter == null) {
            LOGGER.error("Cannot forward policy " + policy + ". Unsupported policy type "
                    + policy.getClass().getSimpleName());
            return;
        }

        boolean policyCreated = createPolicy(policyAdapter);
        if (policyCreated) {
            pushPolicy(policyAdapter);
        }
    }

    private XacmlPdpPolicyAdapter<?> getXacmlPdpPolicyAdapter(Policy policy) {
        if (policy instanceof OptimizationPolicy) {
            return new XacmlPdpOptimizationPolicyAdapter((OptimizationPolicy) policy);
        }
        return null;
    }

    private boolean createPolicy(XacmlPdpPolicyAdapter<?> policyAdapter) {
        PolicyParameters policyParameters = policyAdapter.getAsPolicyParameters();
        Entity<PolicyParameters> entity = Entity.entity(policyParameters, MediaType.APPLICATION_JSON);

        return invokeHttpClient(entity, "createPolicy", policyAdapter.getPolicy().getPolicyName());
    }

    private boolean pushPolicy(XacmlPdpPolicyAdapter<?> policyAdapter) {
        PushPolicyParameters pushPolicyParameters =
                policyAdapter.getAsPushPolicyParameters(configurationParameters.getPdpGroup());
        Entity<PushPolicyParameters> entity = Entity.entity(pushPolicyParameters, MediaType.APPLICATION_JSON);

        return invokeHttpClient(entity, "pushPolicy", policyAdapter.getPolicy().getPolicyName());
    }

    private boolean invokeHttpClient(final Entity<?> entity, final String method, final String policyName) {

        try {
            Response response = getHttpClient().put(method, entity,
                    Collections.singletonMap("ClientAuth", configurationParameters.getClientAuth()));

            if (response.getStatus() != HttpStatus.OK.value()) {
                LOGGER.error(
                        "Invocation of method " + method + " failed for policy " + policyName + ". Response status: "
                                + response.getStatus() + ", Response status info: " + response.getStatusInfo());
                return false;
            }
        } catch (KeyManagementException | NoSuchAlgorithmException exception) {
            LOGGER.error("Invocation of method " + method + " failed for policy " + policyName
                    + " due to error opening Http client", exception);
            return false;
        }
        return true;
    }

    private HttpClient getHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
        boolean useHttps = configurationParameters.isUseHttps();
        String hostname = configurationParameters.getHostname();
        int port = configurationParameters.getPort();
        String userName = configurationParameters.getUserName();
        String password = configurationParameters.getPassword();
        boolean managed = configurationParameters.isManaged();
        BusTopicParams params = BusTopicParams.builder().clientName("SDC Dist").useHttps(useHttps).hostname(hostname)
                .port(port).userName(userName).password(password).basePath(BASE_PATH).managed(managed).build();
        return HttpClient.factory.build(params);
    }

    @Override
    public void configure(String parameterGroupName) {
        configurationParameters = ParameterService.get(parameterGroupName);
    }

}

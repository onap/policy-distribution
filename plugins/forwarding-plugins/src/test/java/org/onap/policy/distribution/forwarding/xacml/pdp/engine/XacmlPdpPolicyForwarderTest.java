/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.distribution.forwarding.xacml.pdp.engine;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PushPolicyParameters;
import org.onap.policy.common.endpoints.event.comm.bus.internal.BusTopicParams;
import org.onap.policy.common.endpoints.http.client.HttpClient;
import org.onap.policy.common.endpoints.http.client.HttpClientConfigException;
import org.onap.policy.common.endpoints.http.client.HttpClientFactory;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.xacml.pdp.XacmlPdpPolicyForwarder;
import org.onap.policy.distribution.forwarding.xacml.pdp.XacmlPdpPolicyForwarderParameterGroup.XacmlPdpPolicyForwarderParameterGroupBuilder;
import org.onap.policy.distribution.model.OptimizationPolicy;
import org.onap.policy.distribution.model.Policy;

public class XacmlPdpPolicyForwarderTest {

    private static final BusTopicParams BUS_TOPIC_PARAMS = BusTopicParams.builder().useHttps(false).hostname("myHost")
            .port(1234).userName("myUser").password("myPassword").managed(true).build();
    private static final String CLIENT_AUTH = "ClientAuth";
    private static final String CLIENT_AUTH_VALUE = "myClientAuth";
    private static final String PDP_GROUP_VALUE = "myPdpGroup";
    private HashMap<String, Object> headers = new HashMap<>();
    private BusTopicParamsMatcher matcher = new BusTopicParamsMatcher(BUS_TOPIC_PARAMS);

    /**
     * Set up.
     */
    @BeforeClass
    public static void setUp() {
        ParameterGroup parameterGroup = new XacmlPdpPolicyForwarderParameterGroupBuilder()
                .setUseHttps(BUS_TOPIC_PARAMS.isUseHttps()).setHostname(BUS_TOPIC_PARAMS.getHostname())
                .setPort(BUS_TOPIC_PARAMS.getPort()).setUserName(BUS_TOPIC_PARAMS.getUserName())
                .setPassword(BUS_TOPIC_PARAMS.getPassword()).setClientAuth(CLIENT_AUTH_VALUE)
                .setIsManaged(BUS_TOPIC_PARAMS.isManaged()).setPdpGroup(PDP_GROUP_VALUE).build();
        parameterGroup.setName("xacmlPdpConfiguration");
        ParameterService.register(parameterGroup);
    }

    @Test
    public void testForwardPolicy() throws Exception {

        HttpClient httpClientMock = mock(HttpClient.class);
        headers.put(CLIENT_AUTH, CLIENT_AUTH_VALUE);
        when(httpClientMock.put(eq("createPolicy"), anyObject(), eq(headers))).thenReturn(Response.ok().build());
        when(httpClientMock.put(eq("pushPolicy"), anyObject(), eq(headers))).thenReturn(Response.ok().build());

        HttpClientFactory httpClientFactoryMock = mock(HttpClientFactory.class);
        when(httpClientFactoryMock.build(argThat(matcher))).thenReturn(httpClientMock);

        XacmlPdpPolicyForwarder forwarder = new MyXacmlPdpPolicyForwarder(httpClientFactoryMock);
        forwarder.configure("xacmlPdpConfiguration");

        Collection<Policy> policies = new ArrayList<>();

        OptimizationPolicy policy1 = new OptimizationPolicy();
        policy1.setPolicyName("policy1");
        policies.add(policy1);

        Policy policy2 = new UnsupportedPolicy();
        policies.add(policy2);

        OptimizationPolicy policy3 = new OptimizationPolicy();
        policy3.setPolicyName("policy3");
        policies.add(policy3);

        forwarder.forward(policies);

        verify(httpClientMock).put(eq("createPolicy"), argThat(new PolicyParametersEntityMatcher(policy1)),
                eq(headers));
        verify(httpClientMock).put(eq("createPolicy"), argThat(new PolicyParametersEntityMatcher(policy3)),
                eq(headers));
        verify(httpClientMock).put(eq("pushPolicy"), argThat(new PushPolicyParametersEntityMatcher(policy1)),
                eq(headers));
        verify(httpClientMock).put(eq("pushPolicy"), argThat(new PushPolicyParametersEntityMatcher(policy3)),
                eq(headers));
    }

    @Test
    public void testForwardPolicy_CreateFailsPushNotInvoked() throws Exception {

        HttpClient httpClientMock = mock(HttpClient.class);
        headers.put(CLIENT_AUTH, CLIENT_AUTH_VALUE);
        when(httpClientMock.put(eq("createPolicy"), anyObject(), eq(headers))).thenReturn(Response.status(400).build());
        when(httpClientMock.put(eq("pushPolicy"), anyObject(), eq(headers))).thenReturn(Response.ok().build());

        HttpClientFactory httpClientFactoryMock = mock(HttpClientFactory.class);
        when(httpClientFactoryMock.build(argThat(matcher))).thenReturn(httpClientMock);

        XacmlPdpPolicyForwarder forwarder = new MyXacmlPdpPolicyForwarder(httpClientFactoryMock);
        forwarder.configure("xacmlPdpConfiguration");

        Collection<Policy> policies = new ArrayList<>();
        OptimizationPolicy policy = new OptimizationPolicy();
        policy.setPolicyName("policy");
        policies.add(policy);
        forwarder.forward(policies);

        verify(httpClientMock).put(eq("createPolicy"), argThat(new PolicyParametersEntityMatcher(policy)), eq(headers));
        verify(httpClientMock, times(0)).put(eq("pushPolicy"), anyObject(), anyObject());
    }

    @Test
    public void testForwardPolicy_PushFails() throws Exception {

        HttpClient httpClientMock = mock(HttpClient.class);
        headers.put(CLIENT_AUTH, CLIENT_AUTH_VALUE);
        when(httpClientMock.put(eq("createPolicy"), anyObject(), eq(headers))).thenReturn(Response.ok().build());
        when(httpClientMock.put(eq("pushPolicy"), anyObject(), eq(headers))).thenReturn(Response.status(400).build());

        HttpClientFactory httpClientFactoryMock = mock(HttpClientFactory.class);
        when(httpClientFactoryMock.build(argThat(matcher))).thenReturn(httpClientMock);

        XacmlPdpPolicyForwarder forwarder = new MyXacmlPdpPolicyForwarder(httpClientFactoryMock);
        forwarder.configure("xacmlPdpConfiguration");

        Collection<Policy> policies = new ArrayList<>();
        OptimizationPolicy policy = new OptimizationPolicy();
        policy.setPolicyName("policy");
        policies.add(policy);
        forwarder.forward(policies);

        verify(httpClientMock).put(eq("createPolicy"), argThat(new PolicyParametersEntityMatcher(policy)), eq(headers));
        verify(httpClientMock).put(eq("pushPolicy"), argThat(new PushPolicyParametersEntityMatcher(policy)),
                eq(headers));
    }

    @Test
    public void testForwardPolicy_HttpClientInitFailureForPolicyCreate() throws Exception {

        HttpClient httpClientMock = mock(HttpClient.class);
        headers.put(CLIENT_AUTH, CLIENT_AUTH_VALUE);
        when(httpClientMock.put(eq("createPolicy"), anyObject(), eq(headers))).thenReturn(Response.ok().build());
        when(httpClientMock.put(eq("pushPolicy"), anyObject(), eq(headers))).thenReturn(Response.status(400).build());

        HttpClientFactory httpClientFactoryMock = mock(HttpClientFactory.class);
        when(httpClientFactoryMock.build(argThat(matcher))).thenThrow(new HttpClientConfigException());

        XacmlPdpPolicyForwarder forwarder = new MyXacmlPdpPolicyForwarder(httpClientFactoryMock);
        forwarder.configure("xacmlPdpConfiguration");

        Collection<Policy> policies = new ArrayList<>();
        OptimizationPolicy policy = new OptimizationPolicy();
        policy.setPolicyName("policy");
        policies.add(policy);
        forwarder.forward(policies);

        verify(httpClientMock, times(0)).put(eq("createPolicy"), anyObject(), anyObject());
        verify(httpClientMock, times(0)).put(eq("pushPolicy"), anyObject(), anyObject());
    }

    @Test
    public void testForwardPolicy_HttpClientInitFailureForPolicyPush() throws Exception {

        HttpClient httpClientMock = mock(HttpClient.class);
        headers.put(CLIENT_AUTH, CLIENT_AUTH_VALUE);
        when(httpClientMock.put(eq("createPolicy"), anyObject(), eq(headers))).thenReturn(Response.ok().build());
        when(httpClientMock.put(eq("pushPolicy"), anyObject(), eq(headers))).thenReturn(Response.status(400).build());

        HttpClientFactory httpClientFactoryMock = mock(HttpClientFactory.class);
        when(httpClientFactoryMock.build(argThat(matcher))).thenReturn(httpClientMock)
                .thenThrow(new HttpClientConfigException());

        XacmlPdpPolicyForwarder forwarder = new MyXacmlPdpPolicyForwarder(httpClientFactoryMock);
        forwarder.configure("xacmlPdpConfiguration");

        Collection<Policy> policies = new ArrayList<>();
        OptimizationPolicy policy = new OptimizationPolicy();
        policy.setPolicyName("policy");
        policies.add(policy);
        forwarder.forward(policies);

        verify(httpClientMock).put(eq("createPolicy"), argThat(new PolicyParametersEntityMatcher(policy)), eq(headers));
        verify(httpClientMock, times(0)).put(eq("pushPolicy"), anyObject(), anyObject());
    }

    class BusTopicParamsMatcher extends BaseMatcher<BusTopicParams> {

        private BusTopicParams busTopicParams;

        BusTopicParamsMatcher(final BusTopicParams busTopicParams) {
            this.busTopicParams = busTopicParams;
        }

        @Override
        public boolean matches(Object arg0) {
            if (arg0 instanceof BusTopicParams) {
                BusTopicParams toCompareTo = (BusTopicParams) arg0;
                return toCompareTo.isUseHttps() == busTopicParams.isUseHttps()
                        && toCompareTo.getHostname().equals(busTopicParams.getHostname())
                        && toCompareTo.getPort() == busTopicParams.getPort()
                        && toCompareTo.getUserName().equals(busTopicParams.getUserName())
                        && toCompareTo.getPassword().equals(busTopicParams.getPassword())
                        && toCompareTo.isManaged() == busTopicParams.isManaged();
            }
            return false;
        }

        @Override
        public void describeTo(Description arg0) {}
    }

    class PolicyParametersEntityMatcher extends BaseMatcher<Entity<PolicyParameters>> {

        private OptimizationPolicy policy;

        PolicyParametersEntityMatcher(final OptimizationPolicy policy) {
            this.policy = policy;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean matches(Object arg0) {
            if (arg0 instanceof Entity) {
                PolicyParameters toCompareTo = ((Entity<PolicyParameters>) arg0).getEntity();
                return toCompareTo.getPolicyName().equals(policy.getPolicyName())
                        && toCompareTo.getPolicyConfigType().toString().equals(policy.getPolicyConfigType());
            }
            return false;
        }

        @Override
        public void describeTo(Description arg0) {}
    }

    class PushPolicyParametersEntityMatcher extends BaseMatcher<Entity<PushPolicyParameters>> {

        private Policy policy;

        PushPolicyParametersEntityMatcher(final Policy policy) {
            this.policy = policy;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean matches(Object arg0) {
            if (arg0 instanceof Entity) {
                PushPolicyParameters toCompareTo = ((Entity<PushPolicyParameters>) arg0).getEntity();
                return toCompareTo.getPolicyName().equals(policy.getPolicyName())
                        && toCompareTo.getPolicyType().equals(policy.getPolicyType())
                        && toCompareTo.getPdpGroup().equals(PDP_GROUP_VALUE);
            }
            return false;
        }

        @Override
        public void describeTo(Description arg0) {}
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

    private class MyXacmlPdpPolicyForwarder extends XacmlPdpPolicyForwarder {
        private HttpClientFactory factory;

        public MyXacmlPdpPolicyForwarder(HttpClientFactory httpClientFactory) {
            this.factory = httpClientFactory;
        }

        @Override
        protected HttpClientFactory getHttpClientFactory() {
            return this.factory;
        }
    }
}

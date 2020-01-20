/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.onap.policy.api.PolicyParameters;
import org.onap.policy.api.PushPolicyParameters;
import org.onap.policy.common.endpoints.event.comm.bus.internal.BusTopicParams;
import org.onap.policy.common.endpoints.http.client.HttpClient;
import org.onap.policy.common.endpoints.http.client.HttpClientConfigException;
import org.onap.policy.common.endpoints.http.client.HttpClientFactory;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.forwarding.xacml.pdp.XacmlPdpPolicyForwarder;
import org.onap.policy.distribution.forwarding.xacml.pdp.XacmlPdpPolicyForwarderParameterGroup;
import org.onap.policy.distribution.forwarding.xacml.pdp.testclasses.CommonTestData;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;

public class XacmlPdpPolicyForwarderTest {

    private static final BusTopicParams BUS_TOPIC_PARAMS = BusTopicParams.builder().useHttps(true)
            .hostname("10.10.10.10").port(1234).userName("myUser").password("myPassword").managed(false).build();
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
        final ParameterGroup parameterGroup = CommonTestData.getPolicyForwarderParameters(
                "src/test/resources/parameters/XacmlPdpPolicyForwarderParameters.json",
                XacmlPdpPolicyForwarderParameterGroup.class);
        parameterGroup.setName("xacmlPdpConfiguration");
        ParameterService.register(parameterGroup);
    }

    @Test
    public void testForwardPolicy() throws Exception {

        final HttpClient httpClientMock = mock(HttpClient.class);
        headers.put(CLIENT_AUTH, CLIENT_AUTH_VALUE);
        when(httpClientMock.put(eq("createPolicy"), any(), eq(headers))).thenReturn(Response.ok().build());
        when(httpClientMock.put(eq("pushPolicy"), any(), eq(headers))).thenReturn(Response.ok().build());

        final HttpClientFactory httpClientFactoryMock = mock(HttpClientFactory.class);
        when(httpClientFactoryMock.build(argThat(matcher))).thenReturn(httpClientMock);

        final XacmlPdpPolicyForwarder forwarder = new MyXacmlPdpPolicyForwarder(httpClientFactoryMock);
        forwarder.configure("xacmlPdpConfiguration");

        final Collection<ToscaEntity> policies = new ArrayList<>();

        final ToscaPolicy policy1 = createPolicy(policies, "policy1", "optimization");

        final ToscaEntity policy2 = new UnsupportedPolicy();
        policies.add(policy2);

        final ToscaPolicy policy3 = createPolicy(policies, "policy3", "optimization");

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

        final HttpClient httpClientMock = mock(HttpClient.class);
        headers.put(CLIENT_AUTH, CLIENT_AUTH_VALUE);
        when(httpClientMock.put(eq("createPolicy"), any(), eq(headers))).thenReturn(Response.status(400).build());
        when(httpClientMock.put(eq("pushPolicy"), any(), eq(headers))).thenReturn(Response.ok().build());

        final HttpClientFactory httpClientFactoryMock = mock(HttpClientFactory.class);
        when(httpClientFactoryMock.build(argThat(matcher))).thenReturn(httpClientMock);

        final XacmlPdpPolicyForwarder forwarder = new MyXacmlPdpPolicyForwarder(httpClientFactoryMock);
        forwarder.configure("xacmlPdpConfiguration");

        final Collection<ToscaEntity> policies = new ArrayList<>();
        final ToscaPolicy policy = createPolicy(policies, "policy", "optimization");
        forwarder.forward(policies);

        verify(httpClientMock).put(eq("createPolicy"), argThat(new PolicyParametersEntityMatcher(policy)), eq(headers));
        verify(httpClientMock, times(0)).put(eq("pushPolicy"), any(), any());
    }

    @Test
    public void testForwardPolicy_PushFails() throws Exception {

        final HttpClient httpClientMock = mock(HttpClient.class);
        headers.put(CLIENT_AUTH, CLIENT_AUTH_VALUE);
        when(httpClientMock.put(eq("createPolicy"), any(), eq(headers))).thenReturn(Response.ok().build());
        when(httpClientMock.put(eq("pushPolicy"), any(), eq(headers))).thenReturn(Response.status(400).build());

        final HttpClientFactory httpClientFactoryMock = mock(HttpClientFactory.class);
        when(httpClientFactoryMock.build(argThat(matcher))).thenReturn(httpClientMock);

        final XacmlPdpPolicyForwarder forwarder = new MyXacmlPdpPolicyForwarder(httpClientFactoryMock);
        forwarder.configure("xacmlPdpConfiguration");

        final Collection<ToscaEntity> policies = new ArrayList<>();
        final ToscaPolicy policy = createPolicy(policies, "policy", "optimization");
        forwarder.forward(policies);

        verify(httpClientMock).put(eq("createPolicy"), argThat(new PolicyParametersEntityMatcher(policy)), eq(headers));
        verify(httpClientMock).put(eq("pushPolicy"), argThat(new PushPolicyParametersEntityMatcher(policy)),
                eq(headers));
    }

    @Test
    public void testForwardPolicy_HttpClientInitFailureForPolicyCreate() throws Exception {

        final HttpClient httpClientMock = mock(HttpClient.class);
        headers.put(CLIENT_AUTH, CLIENT_AUTH_VALUE);
        when(httpClientMock.put(eq("createPolicy"), any(), eq(headers))).thenReturn(Response.ok().build());
        when(httpClientMock.put(eq("pushPolicy"), any(), eq(headers))).thenReturn(Response.status(400).build());

        final HttpClientFactory httpClientFactoryMock = mock(HttpClientFactory.class);
        when(httpClientFactoryMock.build(argThat(matcher))).thenThrow(new HttpClientConfigException());

        final XacmlPdpPolicyForwarder forwarder = new MyXacmlPdpPolicyForwarder(httpClientFactoryMock);
        forwarder.configure("xacmlPdpConfiguration");

        final Collection<ToscaEntity> policies = new ArrayList<>();
        final ToscaPolicy policy = createPolicy(policies, "policy", "optimization");
        forwarder.forward(policies);

        assertSame(policy, policies.iterator().next());

        verify(httpClientMock, times(0)).put(eq("createPolicy"), any(), any());
        verify(httpClientMock, times(0)).put(eq("pushPolicy"), any(), any());
    }

    @Test
    public void testForwardPolicy_HttpClientInitFailureForPolicyPush() throws Exception {

        final HttpClient httpClientMock = mock(HttpClient.class);
        headers.put(CLIENT_AUTH, CLIENT_AUTH_VALUE);
        when(httpClientMock.put(eq("createPolicy"), any(), eq(headers))).thenReturn(Response.ok().build());
        when(httpClientMock.put(eq("pushPolicy"), any(), eq(headers))).thenReturn(Response.status(400).build());

        final HttpClientFactory httpClientFactoryMock = mock(HttpClientFactory.class);
        when(httpClientFactoryMock.build(argThat(matcher))).thenReturn(httpClientMock)
                .thenThrow(new HttpClientConfigException());

        final XacmlPdpPolicyForwarder forwarder = new MyXacmlPdpPolicyForwarder(httpClientFactoryMock);
        forwarder.configure("xacmlPdpConfiguration");

        final Collection<ToscaEntity> policies = new ArrayList<>();
        final ToscaPolicy policy = createPolicy(policies, "policy", "optimization");
        forwarder.forward(policies);

        verify(httpClientMock).put(eq("createPolicy"), argThat(new PolicyParametersEntityMatcher(policy)), eq(headers));
        verify(httpClientMock, times(0)).put(eq("pushPolicy"), any(), any());
    }

    class BusTopicParamsMatcher implements ArgumentMatcher<BusTopicParams> {

        private BusTopicParams busTopicParams;

        BusTopicParamsMatcher(final BusTopicParams busTopicParams) {
            this.busTopicParams = busTopicParams;
        }

        @Override
        public boolean matches(final BusTopicParams arg0) {
            if (arg0 instanceof BusTopicParams) {
                final BusTopicParams toCompareTo = (BusTopicParams) arg0;
                return toCompareTo.isUseHttps() == busTopicParams.isUseHttps()
                        && toCompareTo.getHostname().equals(busTopicParams.getHostname())
                        && toCompareTo.getPort() == busTopicParams.getPort()
                        && toCompareTo.getUserName().equals(busTopicParams.getUserName())
                        && toCompareTo.getPassword().equals(busTopicParams.getPassword())
                        && toCompareTo.isManaged() == busTopicParams.isManaged();
            }
            return false;
        }
    }

    class PolicyParametersEntityMatcher implements ArgumentMatcher<Entity<PolicyParameters>> {

        private ToscaPolicy policy;

        PolicyParametersEntityMatcher(final ToscaPolicy policy) {
            this.policy = policy;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean matches(final Entity<PolicyParameters> arg0) {
            if (arg0 instanceof Entity) {
                final PolicyParameters toCompareTo = ((Entity<PolicyParameters>) arg0).getEntity();
                return toCompareTo.getPolicyName().equals(policy.getName());
            }
            return false;
        }
    }

    class PushPolicyParametersEntityMatcher implements ArgumentMatcher<Entity<PushPolicyParameters>> {

        private ToscaPolicy policy;

        PushPolicyParametersEntityMatcher(final ToscaPolicy policy) {
            this.policy = policy;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean matches(final Entity<PushPolicyParameters> arg0) {
            if (arg0 instanceof Entity) {
                final PushPolicyParameters toCompareTo = ((Entity<PushPolicyParameters>) arg0).getEntity();
                return toCompareTo.getPolicyName().equals(policy.getName())
                        && toCompareTo.getPolicyType().equals(policy.getType())
                        && toCompareTo.getPdpGroup().equals(PDP_GROUP_VALUE);
            }
            return false;
        }
    }

    class UnsupportedPolicy extends ToscaEntity {

        @Override
        public String getName() {
            return "unsupported";
        }
    }

    private class MyXacmlPdpPolicyForwarder extends XacmlPdpPolicyForwarder {
        private HttpClientFactory factory;

        public MyXacmlPdpPolicyForwarder(final HttpClientFactory httpClientFactory) {
            this.factory = httpClientFactory;
        }

        @Override
        protected HttpClientFactory getHttpClientFactory() {
            return this.factory;
        }
    }

    private ToscaPolicy createPolicy(final Collection<ToscaEntity> policies, final String name, final String type) {
        final ToscaPolicy policy1 = new ToscaPolicy();
        policy1.setName(name);
        policy1.setType(type);
        policies.add(policy1);
        return policy1;
    }
}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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

package org.onap.policy.distribution.reception.decoding.pdpx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;

/**
 * Class to perform unit test of {@link PolicyDecoderCsarPdpx}.
 *
 */
public class TestPolicyDecoderCsarPdpx {

    private static final String CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION = "csarToOptimizationPolicyConfiguration";

    /**
     * Set up for test cases.
     */
    @BeforeClass
    public static void setUp() {
        final ParameterGroup parameterGroup = new PolicyDecoderCsarPdpxConfigurationParameterBuilder()
                .setOnapName("onapName").setPolicyNamePrefix("OOF").setPriority("5").setRiskLevel("2")
                .setRiskType("Test").setVersion("1.0").build();
        parameterGroup.setName(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);
        ParameterService.register(parameterGroup);
    }

    /**
     * Tear down.
     */
    @AfterClass
    public static void tearDown() {
        ParameterService.deregister(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);
    }

    @Test
    public void testHpaPolicy2Vnf() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/service-TestNs8-csar.csar");
        final PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        policyDecoderCsarPdpx.configure(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);

        final Collection<ToscaPolicy> ret = policyDecoderCsarPdpx.decode(csar);

        assertEquals(2, ret.size());
        final ToscaPolicy policy = (ToscaPolicy) ret.toArray()[0];
        Map<String, Object> prop = policy.getProperties();

        assertEquals("onapName", (String)prop.get(ToscaPolicy.TOSCA_POLICY_ONAP_NAME));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_NAME)).startsWith("OOF."));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_PRIORITY)).contains("5"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_RISK_LEVEL)).contains("2"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_RISK_TYPE)).contains("Test"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_VERSION)).contains("1.0"));

        String content = (String)prop.get(ToscaPolicy.TOSCA_POLICY_CONTENT);
        assertTrue(content.contains("\"policyType\":\"hpa\""));
        assertTrue(content.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(content.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(content.contains("\"type\":\"flavor_directives\""));
        assertTrue(content.contains("\"hpa-feature\":\"basicCapabilities\""));
        assertTrue(content.contains("\"mandatory\":\"True\""));
        assertTrue(content.contains("\"architecture\":\"generic\""));
        assertTrue(content.contains("\"hpa-version\":\"v1\""));
        assertTrue(content.contains("\"hpa-attribute-key\":\"virtualMemSize\""));
        assertTrue(content.contains("\"operator\":\"=\""));
        assertTrue(content.contains("\"unit\":\"MB\""));
    }

    @Test
    public void testHpaPolicySriov() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/hpaPolicySriov.csar");
        final PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        policyDecoderCsarPdpx.configure(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);

        final Collection<ToscaPolicy> policies = policyDecoderCsarPdpx.decode(csar);
        final ToscaPolicy policy = (ToscaPolicy) policies.toArray()[0];

        Map<String, Object> prop = policy.getProperties();

        assertEquals("onapName", (String)prop.get(ToscaPolicy.TOSCA_POLICY_ONAP_NAME));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_NAME)).startsWith("OOF."));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_PRIORITY)).contains("5"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_RISK_LEVEL)).contains("2"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_RISK_TYPE)).contains("Test"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_VERSION)).contains("1.0"));

        String content = (String)prop.get(ToscaPolicy.TOSCA_POLICY_CONTENT);
        assertTrue(content.contains("\"policyType\":\"hpa\""));
        assertTrue(content.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(content.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(content.contains("\"type\":\"flavor_directives\""));
        assertTrue(content.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(content.contains("\"attribute_value\":\"\""));
        assertTrue(content.contains("\"hpa-feature\":\"sriovNICNetwork\""));
        assertTrue(content.contains("\"mandatory\":\"True\""));
        assertTrue(content.contains("\"architecture\":\"generic\""));
        assertTrue(content.contains("\"hpa-version\":\"v1\""));
        assertTrue(content.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(content.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(content.contains("\"operator\":\"=\""));
        assertTrue(content.contains("\"unit\":\"\""));
        assertTrue(content.contains("\"hpa-attribute-key\":\"pciDeviceId\""));
        assertTrue(content.contains("\"hpa-attribute-value\":\"5678\""));
        assertTrue(content.contains("\"operator\":\"=\""));
        assertTrue(content.contains("\"unit\":\"\""));
        assertTrue(content.contains("\"hpa-attribute-key\":\"pciNumDevices\""));
        assertTrue(content.contains("\"hpa-attribute-value\":\"1\""));
        assertTrue(content.contains("\"operator\":\"=\""));
        assertTrue(content.contains("\"unit\":\"\""));
    }

    @Test
    public void testHpaPolicyPciePassthrough() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/hpaPolicyPciePassthrough.csar");
        final PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        policyDecoderCsarPdpx.configure(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);

        final Collection<ToscaPolicy> policies = policyDecoderCsarPdpx.decode(csar);
        assertEquals(2, policies.size());
        final ToscaPolicy policy = (ToscaPolicy) policies.toArray()[0];

        Map<String, Object> prop = policy.getProperties();

        assertEquals("onapName", (String)prop.get(ToscaPolicy.TOSCA_POLICY_ONAP_NAME));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_NAME)).startsWith("OOF."));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_PRIORITY)).contains("5"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_RISK_LEVEL)).contains("2"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_RISK_TYPE)).contains("Test"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_VERSION)).contains("1.0"));

        String content = (String)prop.get(ToscaPolicy.TOSCA_POLICY_CONTENT);
        assertTrue(content.contains("\"policyType\":\"hpa\""));
        assertTrue(content.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(content.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(content.contains("\"type\":\"flavor_directives\""));
        assertTrue(content.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(content.contains("\"attribute_value\":\"\""));
        assertTrue(content.contains("\"hpa-feature\":\"pciePassthrough\""));
        assertTrue(content.contains("\"mandatory\":\"True\""));
        assertTrue(content.contains("\"architecture\":\"generic\""));
        assertTrue(content.contains("\"hpa-version\":\"v1\""));
        assertTrue(content.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(content.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(content.contains("\"operator\":\"=\""));
        assertTrue(content.contains("\"unit\":\"\""));
        assertTrue(content.contains("\"hpa-attribute-key\":\"pciDeviceId\""));
        assertTrue(content.contains("\"hpa-attribute-value\":\"5678\""));
        assertTrue(content.contains("\"operator\":\"=\""));
        assertTrue(content.contains("\"unit\":\"\""));
        assertTrue(content.contains("\"hpa-attribute-key\":\"pciNumDevices\""));
        assertTrue(content.contains("\"hpa-attribute-value\":\"1\""));
        assertTrue(content.contains("\"operator\":\"=\""));
        assertTrue(content.contains("\"unit\":\"\""));


    }

    @Test
    public void testHpaPolicyHugePage() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/hpaPolicyHugePage.csar");
        final PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        policyDecoderCsarPdpx.configure(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);

        final Collection<ToscaPolicy> policies = policyDecoderCsarPdpx.decode(csar);
        assertEquals(2, policies.size());
        final ToscaPolicy policy = (ToscaPolicy) policies.toArray()[0];

        Map<String, Object> prop = policy.getProperties();

        assertEquals("onapName", (String)prop.get(ToscaPolicy.TOSCA_POLICY_ONAP_NAME));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_NAME)).startsWith("OOF."));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_PRIORITY)).contains("5"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_RISK_LEVEL)).contains("2"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_RISK_TYPE)).contains("Test"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_VERSION)).contains("1.0"));

        String content = (String)prop.get(ToscaPolicy.TOSCA_POLICY_CONTENT);
        assertTrue(content.contains("\"policyType\":\"hpa\""));
        assertTrue(content.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(content.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(content.contains("\"type\":\"flavor_directives\""));
        assertTrue(content.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(content.contains("\"attribute_value\":\"\""));
        assertTrue(content.contains("\"hpa-feature\":\"hugePages\""));
        assertTrue(content.contains("\"mandatory\":\"true\""));
        assertTrue(content.contains("\"architecture\":\"generic\""));
        assertTrue(content.contains("\"hpa-version\":\"v1\""));
        assertTrue(content.contains("\"hpa-attribute-key\":\"memoryPageSize\""));
        assertTrue(content.contains("\"hpa-attribute-value\":\"2\""));
        assertTrue(content.contains("\"operator\":\"=\""));
        assertTrue(content.contains("\"unit\":\"MB\""));
    }

    @Test
    public void testS3p0PciVendorId() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/s3p_0_pciVendorId.csar");
        final PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        policyDecoderCsarPdpx.configure(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);

        final Collection<ToscaPolicy> policies = policyDecoderCsarPdpx.decode(csar);
        assertEquals(1, policies.size());
        final ToscaPolicy policy = (ToscaPolicy) policies.toArray()[0];

        Map<String, Object> prop = policy.getProperties();

        assertEquals("onapName", (String)prop.get(ToscaPolicy.TOSCA_POLICY_ONAP_NAME));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_NAME)).startsWith("OOF."));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_PRIORITY)).contains("5"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_RISK_LEVEL)).contains("2"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_RISK_TYPE)).contains("Test"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_VERSION)).contains("1.0"));

        String content = (String)prop.get(ToscaPolicy.TOSCA_POLICY_CONTENT);
        assertTrue(content.contains("\"policyType\":\"hpa\""));
        assertTrue(content.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(content.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(content.contains("\"type\":\"flavor_directives\""));
        assertTrue(content.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(content.contains("\"attribute_value\":\"\""));
        assertTrue(content.contains("\"hpa-feature\":\"pciePassthrough\""));
        assertTrue(content.contains("\"mandatory\":\"True\""));
        assertTrue(content.contains("\"architecture\":\"generic\""));
        assertTrue(content.contains("\"hpa-version\":\"v1\""));
        assertTrue(content.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(content.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(content.contains("\"operator\":\"=\""));
        assertTrue(content.contains("\"unit\":\"\""));

    }

    @Test
    public void testserviceVcpeWithAll() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/service-VcpeWithAll-csar.csar");
        final PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        policyDecoderCsarPdpx.configure(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);

        final Collection<ToscaPolicy> policies = policyDecoderCsarPdpx.decode(csar);
        assertEquals(5, policies.size());
        final ToscaPolicy policy = (ToscaPolicy) policies.toArray()[0];

        Map<String, Object> prop = policy.getProperties();

        assertEquals("onapName", (String)prop.get(ToscaPolicy.TOSCA_POLICY_ONAP_NAME));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_NAME)).startsWith("OOF."));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_PRIORITY)).contains("5"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_RISK_LEVEL)).contains("2"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_RISK_TYPE)).contains("Test"));
        assertTrue(((String)prop.get(ToscaPolicy.TOSCA_POLICY_VERSION)).contains("1.0"));

        String content = (String)prop.get(ToscaPolicy.TOSCA_POLICY_CONTENT);
        assertTrue(content.contains("\"policyType\":\"hpa\""));
        assertTrue(content.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(content.contains("\"type\":\"flavor_directives\""));
        assertTrue(content.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(content.contains("\"attribute_value\":\"\""));
        assertTrue(content.contains("\"hpa-feature\":\"sriovNICNetwork\""));
        assertTrue(content.contains("\"mandatory\":\"True\""));
        assertTrue(content.contains("\"architecture\":\"generic\""));
        assertTrue(content.contains("\"hpa-version\":\"v1\""));
        assertTrue(content.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(content.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(content.contains("\"operator\":\"=\""));
        assertTrue(content.contains("\"unit\":\"\""));

    }
}

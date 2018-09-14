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
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.model.OptimizationPolicy;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;

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
        ParameterGroup parameterGroup = new PolicyDecoderCsarPdpxConfigurationParameterBuilder().setOnapName("onapName")
                .setPolicyNamePrefix("OOF").setPriority("5").setRiskLevel("2").setRiskType("Test").setVersion("1.0")
                .build();
        parameterGroup.setName(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);
        ParameterService.register(parameterGroup);
    }

    @Test
    public void testHpaPolicy2Vnf() throws IOException, PolicyDecodingException {
        Csar csar = new Csar("src/test/resources/service-TestNs8-csar.csar");
        PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        policyDecoderCsarPdpx.configure(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);

        Collection<OptimizationPolicy> ret = policyDecoderCsarPdpx.decode(csar);

        assertEquals(2, ret.size());
        OptimizationPolicy policy = (OptimizationPolicy) ret.toArray()[0];

        assertEquals("onapName", policy.getOnapName());
        assertTrue(policy.getPolicyName().startsWith("OOF."));
        assertTrue(policy.getConfigBody().contains("\"priority\":\"5\""));
        assertTrue(policy.getConfigBody().contains("\"riskLevel\":\"2\""));
        assertTrue(policy.getConfigBody().contains("\"riskType\":\"Test\""));
        assertTrue(policy.getConfigBody().contains("\"version\":\"1.0\""));
        assertTrue(policy.getConfigBody().contains("\"policyType\":\"Optimization\""));

        assertTrue(policy.getConfigBody().contains("\"id\":\"vdu_vnf_1\""));
        assertTrue(policy.getConfigBody().contains("\"type\":\"tosca.node.nfv.Vdu.Compute\""));
        assertTrue(policy.getConfigBody().contains("\"type\":\"flavor_directive\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-feature\":\"BasicCapabilities\""));
        assertTrue(policy.getConfigBody().contains("\"mandatory\":\"true\""));
        assertTrue(policy.getConfigBody().contains("\"architecture\":\"generic\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-version\":\"v1\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-key\":\"virtualMemSize\""));
        assertTrue(policy.getConfigBody().contains("\"operator\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"unit\":\"MB\""));
    }

    @Test
    public void testHpaPolicySriov() throws IOException, PolicyDecodingException {
        Csar csar = new Csar("src/test/resources/hpaPolicySriov.csar");
        PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        policyDecoderCsarPdpx.configure(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);

        Collection<OptimizationPolicy> policies = policyDecoderCsarPdpx.decode(csar);
        OptimizationPolicy policy = (OptimizationPolicy) policies.toArray()[0];

        assertEquals("onapName", policy.getOnapName());
        assertTrue(policy.getPolicyName().startsWith("OOF."));
        assertTrue(policy.getConfigBody().contains("\"priority\":\"5\""));
        assertTrue(policy.getConfigBody().contains("\"riskLevel\":\"2\""));
        assertTrue(policy.getConfigBody().contains("\"riskType\":\"Test\""));
        assertTrue(policy.getConfigBody().contains("\"version\":\"1.0\""));
        assertTrue(policy.getConfigBody().contains("\"policyType\":\"Optimization\""));

        assertTrue(policy.getConfigBody().contains("\"id\":\"vdu_vnf_1\""));
        assertTrue(policy.getConfigBody().contains("\"type\":\"tosca.node.nfv.Vdu.Compute\""));
        assertTrue(policy.getConfigBody().contains("\"type\":\"flavor_directive\""));
        assertTrue(policy.getConfigBody().contains("\"attribute_name\":\"flavorName\""));
        assertTrue(policy.getConfigBody().contains("\"attribute_value\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-feature\":\"SriovNICNetwork\""));
        assertTrue(policy.getConfigBody().contains("\"mandatory\":\"true\""));
        assertTrue(policy.getConfigBody().contains("\"architecture\":\"generic\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-version\":\"v1\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(policy.getConfigBody().contains("\"operator\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"unit\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-key\":\"pciDeviceId\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-value\":\"5678\""));
        assertTrue(policy.getConfigBody().contains("\"operator\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"unit\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-key\":\"pciNumDevices\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-value\":\"1\""));
        assertTrue(policy.getConfigBody().contains("\"operator\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"unit\":\"\""));
    }

    @Test
    public void testHpaPolicyPciePassthrough() throws IOException, PolicyDecodingException {
        Csar csar = new Csar("src/test/resources/hpaPolicyPciePassthrough.csar");
        PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        policyDecoderCsarPdpx.configure(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);

        Collection<OptimizationPolicy> policies = policyDecoderCsarPdpx.decode(csar);
        assertEquals(2, policies.size());
        OptimizationPolicy policy = (OptimizationPolicy) policies.toArray()[0];

        assertEquals("onapName", policy.getOnapName());
        assertTrue(policy.getPolicyName().startsWith("OOF."));
        assertTrue(policy.getConfigBody().contains("\"priority\":\"5\""));
        assertTrue(policy.getConfigBody().contains("\"riskLevel\":\"2\""));
        assertTrue(policy.getConfigBody().contains("\"riskType\":\"Test\""));
        assertTrue(policy.getConfigBody().contains("\"version\":\"1.0\""));
        assertTrue(policy.getConfigBody().contains("\"policyType\":\"Optimization\""));

        assertTrue(policy.getConfigBody().contains("\"id\":\"vdu_vnf_1\""));
        assertTrue(policy.getConfigBody().contains("\"type\":\"tosca.node.nfv.Vdu.Compute\""));
        assertTrue(policy.getConfigBody().contains("\"type\":\"flavor_directive\""));
        assertTrue(policy.getConfigBody().contains("\"attribute_name\":\"flavorName\""));
        assertTrue(policy.getConfigBody().contains("\"attribute_value\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-feature\":\"pciePassthrough\""));
        assertTrue(policy.getConfigBody().contains("\"mandatory\":\"true\""));
        assertTrue(policy.getConfigBody().contains("\"architecture\":\"generic\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-version\":\"v1\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(policy.getConfigBody().contains("\"operator\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"unit\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-key\":\"pciDeviceId\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-value\":\"5678\""));
        assertTrue(policy.getConfigBody().contains("\"operator\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"unit\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-key\":\"pciNumDevices\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-value\":\"1\""));
        assertTrue(policy.getConfigBody().contains("\"operator\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"unit\":\"\""));


    }

    @Test
    public void testHpaPolicyHugePage() throws IOException, PolicyDecodingException {
        Csar csar = new Csar("src/test/resources/hpaPolicyHugePage.csar");
        PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        policyDecoderCsarPdpx.configure(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);

        Collection<OptimizationPolicy> policies = policyDecoderCsarPdpx.decode(csar);
        assertEquals(2, policies.size());
        OptimizationPolicy policy = (OptimizationPolicy) policies.toArray()[0];

        assertEquals("onapName", policy.getOnapName());
        assertTrue(policy.getPolicyName().startsWith("OOF."));
        assertTrue(policy.getConfigBody().contains("\"priority\":\"5\""));
        assertTrue(policy.getConfigBody().contains("\"riskLevel\":\"2\""));
        assertTrue(policy.getConfigBody().contains("\"riskType\":\"Test\""));
        assertTrue(policy.getConfigBody().contains("\"version\":\"1.0\""));
        assertTrue(policy.getConfigBody().contains("\"policyType\":\"Optimization\""));

        assertTrue(policy.getConfigBody().contains("\"id\":\"vdu_vnf_1\""));
        assertTrue(policy.getConfigBody().contains("\"type\":\"tosca.node.nfv.Vdu.Compute\""));
        assertTrue(policy.getConfigBody().contains("\"type\":\"flavor_directive\""));
        assertTrue(policy.getConfigBody().contains("\"attribute_name\":\"flavorName\""));
        assertTrue(policy.getConfigBody().contains("\"attribute_value\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-feature\":\"hugePages\""));
        assertTrue(policy.getConfigBody().contains("\"mandatory\":\"false\""));
        assertTrue(policy.getConfigBody().contains("\"architecture\":\"generic\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-version\":\"v1\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-key\":\"memoryPageSize\""));
        assertTrue(policy.getConfigBody().contains("\"hpa-attribute-value\":\"2\""));
        assertTrue(policy.getConfigBody().contains("\"operator\":\"\""));
        assertTrue(policy.getConfigBody().contains("\"unit\":\"MB\""));
    }
}

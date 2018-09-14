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

        Collection<OptimizationPolicy> policies = policyDecoderCsarPdpx.decode(csar);
        assertEquals(2, policies.size());

        for (OptimizationPolicy policy : policies) {
            assertEquals("onapName", policy.getOnapName());
            assertTrue(policy.getPolicyName().startsWith("OOF."));
            assertTrue(policy.getConfigBody().contains("\"priority\":\"5\""));
            assertTrue(policy.getConfigBody().contains("\"riskLevel\":\"2\""));
            assertTrue(policy.getConfigBody().contains("\"riskType\":\"Test\""));
            assertTrue(policy.getConfigBody().contains("\"version\":\"1.0\""));
        }
    }

    @Test
    public void testHpaPolicyFeature() throws IOException, PolicyDecodingException {
        Csar csar = new Csar("src/test/resources/hpaPolicySRIOV.csar");

        PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        policyDecoderCsarPdpx.configure(CSAR_TO_OPTIMIZATION_POLICY_CONFIGURATION);

        Collection<OptimizationPolicy> policies = policyDecoderCsarPdpx.decode(csar);
        assertEquals(2, policies.size());
        for (OptimizationPolicy policy : policies) {
            assertEquals("onapName", policy.getOnapName());
            assertTrue(policy.getPolicyName().startsWith("OOF."));
            assertTrue(policy.getConfigBody().contains("\"priority\":\"5\""));
            assertTrue(policy.getConfigBody().contains("\"riskLevel\":\"2\""));
            assertTrue(policy.getConfigBody().contains("\"riskType\":\"Test\""));
            assertTrue(policy.getConfigBody().contains("\"version\":\"1.0\""));
        }
    }
}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2020 AT&T Intellectual Property. All rights reserved.
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
import java.lang.String;
import java.util.Collection;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.decoding.pdpx.PolicyDecoderCsarPdpxLifecycleApi;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;
import org.onap.policy.models.tosca.authorative.concepts.ToscaTopologyTemplate;

/**
 * Class to perform unit test of {@link PolicyDecoderCsarPdpxLifecycleApiLifecycleApi}.
 */
public class TestPolicyDecoderCsarPdpxLifecycleApi {

    private final StandardCoder encoder = new StandardCoder();

    /**
     * Set up for test cases.
     */
    @BeforeClass
    public static void setUp() {
        final ParameterGroup parameterGroup = CommonTestData.getPolicyDecoderParameters(
                "src/test/resources/parameters/PdpxPolicyDecoderParameters.json",
                PolicyDecoderCsarPdpxLifecycleApiParameters.class);
        parameterGroup.setName(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());
        ParameterService.register(parameterGroup);
    }

    /**
     * Tear down.
     */
    @AfterClass
    public static void tearDown() {
        ParameterService.deregister(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());
    }

    @Test
    public void testHpaPolicy2Vnf() throws IOException, PolicyDecodingException, CoderException {
        final Csar csar = new Csar("src/test/resources/service-TestNs8-csar.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);

        assertEquals(2, entities.size());
        final ToscaServiceTemplate entity = entities.iterator().next();
        ToscaTopologyTemplate topologyTemplate = entity.getToscaTopologyTemplate();
        Map<String, ToscaPolicy> map = topologyTemplate.getPolicies().get(0);
        ToscaPolicy policy = map.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_HPA_OOF);
        Map<String, Object> props = policy.getProperties();
        Object flavorFeatures =
                      props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_FLAVORFEATURES);
        String features = encoder.encode(flavorFeatures);
        assertTrue(features.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(features.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(features.contains("\"type\":\"flavor_directives\""));
        assertTrue(features.contains("\"hpa-feature\":\"basicCapabilities\""));
        assertTrue(features.contains("\"mandatory\":\"True\""));
        assertTrue(features.contains("\"architecture\":\"generic\""));
        assertTrue(features.contains("\"hpa-version\":\"v1\""));
        assertTrue(features.contains("\"hpa-attribute-key\":\"virtualMemSize\""));
        assertTrue(features.contains("\"operator\":\"=\""));
        assertTrue(features.contains("\"unit\":\"MB\""));
    }

    @Test
    public void testHpaPolicySriov() throws IOException, PolicyDecodingException, CoderException {
        final Csar csar = new Csar("src/test/resources/hpaPolicySriov.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        final ToscaServiceTemplate entity = entities.iterator().next();
        ToscaTopologyTemplate topologyTemplate = entity.getToscaTopologyTemplate();
        Map<String, ToscaPolicy> map = topologyTemplate.getPolicies().get(0);
        ToscaPolicy policy = map.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_HPA_OOF);
        Map<String, Object> props = policy.getProperties();
        Object flavorFeatures =
                      props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_FLAVORFEATURES);
        String features = encoder.encode(flavorFeatures);
        assertTrue(features.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(features.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(features.contains("\"type\":\"flavor_directives\""));
        assertTrue(features.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(features.contains("\"attribute_value\":\"\""));
        assertTrue(features.contains("\"hpa-feature\":\"sriovNICNetwork\""));
        assertTrue(features.contains("\"mandatory\":\"True\""));
        assertTrue(features.contains("\"architecture\":\"generic\""));
        assertTrue(features.contains("\"hpa-version\":\"v1\""));
        assertTrue(features.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(features.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(features.contains("\"operator\":\"=\""));
        assertTrue(features.contains("\"unit\":\"\""));
        assertTrue(features.contains("\"hpa-attribute-key\":\"pciDeviceId\""));
        assertTrue(features.contains("\"hpa-attribute-value\":\"5678\""));
        assertTrue(features.contains("\"operator\":\"=\""));
        assertTrue(features.contains("\"unit\":\"\""));
        assertTrue(features.contains("\"hpa-attribute-key\":\"pciNumDevices\""));
        assertTrue(features.contains("\"hpa-attribute-value\":\"1\""));
        assertTrue(features.contains("\"operator\":\"=\""));
        assertTrue(features.contains("\"unit\":\"\""));
    }

    @Test
    public void testHpaPolicyPciePassthrough() throws IOException, PolicyDecodingException, CoderException {
        final Csar csar = new Csar("src/test/resources/hpaPolicyPciePassthrough.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        assertEquals(2, entities.size());
        final ToscaServiceTemplate entity = entities.iterator().next();
        ToscaTopologyTemplate topologyTemplate = entity.getToscaTopologyTemplate();
        Map<String, ToscaPolicy> map = topologyTemplate.getPolicies().get(0);
        ToscaPolicy policy = map.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_HPA_OOF);
        Map<String, Object> props = policy.getProperties();
        Object flavorFeatures =
                      props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_FLAVORFEATURES);
        String features = encoder.encode(flavorFeatures);
        assertTrue(features.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(features.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(features.contains("\"type\":\"flavor_directives\""));
        assertTrue(features.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(features.contains("\"attribute_value\":\"\""));
        assertTrue(features.contains("\"hpa-feature\":\"pciePassthrough\""));
        assertTrue(features.contains("\"mandatory\":\"True\""));
        assertTrue(features.contains("\"architecture\":\"generic\""));
        assertTrue(features.contains("\"hpa-version\":\"v1\""));
        assertTrue(features.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(features.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(features.contains("\"operator\":\"=\""));
        assertTrue(features.contains("\"unit\":\"\""));
        assertTrue(features.contains("\"hpa-attribute-key\":\"pciDeviceId\""));
        assertTrue(features.contains("\"hpa-attribute-value\":\"5678\""));
        assertTrue(features.contains("\"operator\":\"=\""));
        assertTrue(features.contains("\"unit\":\"\""));
        assertTrue(features.contains("\"hpa-attribute-key\":\"pciNumDevices\""));
        assertTrue(features.contains("\"hpa-attribute-value\":\"1\""));
        assertTrue(features.contains("\"operator\":\"=\""));
        assertTrue(features.contains("\"unit\":\"\""));
    }

    @Test
    public void testHpaPolicyHugePage() throws IOException, PolicyDecodingException, CoderException {
        final Csar csar = new Csar("src/test/resources/hpaPolicyHugePage.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        assertEquals(2, entities.size());
        final ToscaServiceTemplate entity = entities.iterator().next();
        ToscaTopologyTemplate topologyTemplate = entity.getToscaTopologyTemplate();
        Map<String, ToscaPolicy> map = topologyTemplate.getPolicies().get(0);
        ToscaPolicy policy = map.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_HPA_OOF);
        Map<String, Object> props = policy.getProperties();
        Object flavorFeatures =
                      props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_FLAVORFEATURES);
        String features = encoder.encode(flavorFeatures);
        assertTrue(features.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(features.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(features.contains("\"type\":\"flavor_directives\""));
        assertTrue(features.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(features.contains("\"attribute_value\":\"\""));
        assertTrue(features.contains("\"hpa-feature\":\"hugePages\""));
        assertTrue(features.contains("\"mandatory\":\"true\""));
        assertTrue(features.contains("\"architecture\":\"generic\""));
        assertTrue(features.contains("\"hpa-version\":\"v1\""));
        assertTrue(features.contains("\"hpa-attribute-key\":\"memoryPageSize\""));
        assertTrue(features.contains("\"hpa-attribute-value\":\"2\""));
        assertTrue(features.contains("\"operator\":\"=\""));
        assertTrue(features.contains("\"unit\":\"MB\""));
    }

    @Test
    public void testS3p0PciVendorId() throws IOException, PolicyDecodingException, CoderException {
        final Csar csar = new Csar("src/test/resources/s3p_0_pciVendorId.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        assertEquals(1, entities.size());
        final ToscaServiceTemplate entity = entities.iterator().next();
        ToscaTopologyTemplate topologyTemplate = entity.getToscaTopologyTemplate();
        Map<String, ToscaPolicy> map = topologyTemplate.getPolicies().get(0);
        ToscaPolicy policy = map.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_HPA_OOF);
        Map<String, Object> props = policy.getProperties();
        Object flavorFeatures =
                      props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_FLAVORFEATURES);
        String features = encoder.encode(flavorFeatures);
        assertTrue(features.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(features.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(features.contains("\"type\":\"flavor_directives\""));
        assertTrue(features.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(features.contains("\"attribute_value\":\"\""));
        assertTrue(features.contains("\"hpa-feature\":\"pciePassthrough\""));
        assertTrue(features.contains("\"mandatory\":\"True\""));
        assertTrue(features.contains("\"architecture\":\"generic\""));
        assertTrue(features.contains("\"hpa-version\":\"v1\""));
        assertTrue(features.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(features.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(features.contains("\"operator\":\"=\""));
        assertTrue(features.contains("\"unit\":\"\""));
    }

    @Test
    public void testserviceVcpeWithAll() throws IOException, PolicyDecodingException, CoderException {
        final Csar csar = new Csar("src/test/resources/service-VcpeWithAll-csar.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        assertEquals(5, entities.size());
        final ToscaServiceTemplate entity = entities.iterator().next();
        ToscaTopologyTemplate topologyTemplate = entity.getToscaTopologyTemplate();
        Map<String, ToscaPolicy> map = topologyTemplate.getPolicies().get(0);
        ToscaPolicy policy = map.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_HPA_OOF);
        Map<String, Object> props = policy.getProperties();
        Object flavorFeatures =
                      props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_FLAVORFEATURES);
        String features = encoder.encode(flavorFeatures);
        assertTrue(features.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(features.contains("\"type\":\"flavor_directives\""));
        assertTrue(features.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(features.contains("\"attribute_value\":\"\""));
        assertTrue(features.contains("\"hpa-feature\":\"sriovNICNetwork\""));
        assertTrue(features.contains("\"mandatory\":\"True\""));
        assertTrue(features.contains("\"architecture\":\"generic\""));
        assertTrue(features.contains("\"hpa-version\":\"v1\""));
        assertTrue(features.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(features.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(features.contains("\"operator\":\"=\""));
        assertTrue(features.contains("\"unit\":\"\""));
    }
}

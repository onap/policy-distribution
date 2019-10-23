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

package org.onap.policy.distribution.reception.decoding.pdpx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.String;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.decoding.pdpx.FlavorFeature;
import org.onap.policy.distribution.reception.decoding.pdpx.PolicyDecoderCsarPdpxLifecycleApi;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyType;
import org.onap.policy.models.tosca.authorative.concepts.ToscaProperty;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;
import org.onap.policy.models.tosca.authorative.concepts.ToscaTopologyTemplate;

/**
 * Class to perform unit test of {@link PolicyDecoderCsarPdpxLifecycleApiLifecycleApi}.
 *
 */
public class TestPolicyDecoderCsarPdpxLifecycleApi {

    private final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
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
    public void testHpaPolicy2Vnf() throws IOException, PolicyDecodingException {
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
        String featrues = gson.toJson(flavorFeatures);
        assertTrue(featrues.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(featrues.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(featrues.contains("\"type\":\"flavor_directives\""));
        assertTrue(featrues.contains("\"hpa-feature\":\"basicCapabilities\""));
        assertTrue(featrues.contains("\"mandatory\":\"True\""));
        assertTrue(featrues.contains("\"architecture\":\"generic\""));
        assertTrue(featrues.contains("\"hpa-version\":\"v1\""));
        assertTrue(featrues.contains("\"hpa-attribute-key\":\"virtualMemSize\""));
        assertTrue(featrues.contains("\"operator\":\"=\""));
        assertTrue(featrues.contains("\"unit\":\"MB\""));
    }

    @Test
    public void testHpaPolicySriov() throws IOException, PolicyDecodingException {
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
        String featrues = gson.toJson(flavorFeatures);
        assertTrue(featrues.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(featrues.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(featrues.contains("\"type\":\"flavor_directives\""));
        assertTrue(featrues.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(featrues.contains("\"attribute_value\":\"\""));
        assertTrue(featrues.contains("\"hpa-feature\":\"sriovNICNetwork\""));
        assertTrue(featrues.contains("\"mandatory\":\"True\""));
        assertTrue(featrues.contains("\"architecture\":\"generic\""));
        assertTrue(featrues.contains("\"hpa-version\":\"v1\""));
        assertTrue(featrues.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(featrues.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(featrues.contains("\"operator\":\"=\""));
        assertTrue(featrues.contains("\"unit\":\"\""));
        assertTrue(featrues.contains("\"hpa-attribute-key\":\"pciDeviceId\""));
        assertTrue(featrues.contains("\"hpa-attribute-value\":\"5678\""));
        assertTrue(featrues.contains("\"operator\":\"=\""));
        assertTrue(featrues.contains("\"unit\":\"\""));
        assertTrue(featrues.contains("\"hpa-attribute-key\":\"pciNumDevices\""));
        assertTrue(featrues.contains("\"hpa-attribute-value\":\"1\""));
        assertTrue(featrues.contains("\"operator\":\"=\""));
        assertTrue(featrues.contains("\"unit\":\"\""));
    }

    @Test
    public void testHpaPolicyPciePassthrough() throws IOException, PolicyDecodingException {
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
        String featrues = gson.toJson(flavorFeatures);
        assertTrue(featrues.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(featrues.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(featrues.contains("\"type\":\"flavor_directives\""));
        assertTrue(featrues.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(featrues.contains("\"attribute_value\":\"\""));
        assertTrue(featrues.contains("\"hpa-feature\":\"pciePassthrough\""));
        assertTrue(featrues.contains("\"mandatory\":\"True\""));
        assertTrue(featrues.contains("\"architecture\":\"generic\""));
        assertTrue(featrues.contains("\"hpa-version\":\"v1\""));
        assertTrue(featrues.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(featrues.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(featrues.contains("\"operator\":\"=\""));
        assertTrue(featrues.contains("\"unit\":\"\""));
        assertTrue(featrues.contains("\"hpa-attribute-key\":\"pciDeviceId\""));
        assertTrue(featrues.contains("\"hpa-attribute-value\":\"5678\""));
        assertTrue(featrues.contains("\"operator\":\"=\""));
        assertTrue(featrues.contains("\"unit\":\"\""));
        assertTrue(featrues.contains("\"hpa-attribute-key\":\"pciNumDevices\""));
        assertTrue(featrues.contains("\"hpa-attribute-value\":\"1\""));
        assertTrue(featrues.contains("\"operator\":\"=\""));
        assertTrue(featrues.contains("\"unit\":\"\""));
    }

    @Test
    public void testHpaPolicyHugePage() throws IOException, PolicyDecodingException {
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
        String featrues = gson.toJson(flavorFeatures);
        assertTrue(featrues.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(featrues.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(featrues.contains("\"type\":\"flavor_directives\""));
        assertTrue(featrues.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(featrues.contains("\"attribute_value\":\"\""));
        assertTrue(featrues.contains("\"hpa-feature\":\"hugePages\""));
        assertTrue(featrues.contains("\"mandatory\":\"true\""));
        assertTrue(featrues.contains("\"architecture\":\"generic\""));
        assertTrue(featrues.contains("\"hpa-version\":\"v1\""));
        assertTrue(featrues.contains("\"hpa-attribute-key\":\"memoryPageSize\""));
        assertTrue(featrues.contains("\"hpa-attribute-value\":\"2\""));
        assertTrue(featrues.contains("\"operator\":\"=\""));
        assertTrue(featrues.contains("\"unit\":\"MB\""));
    }

    @Test
    public void testS3p0PciVendorId() throws IOException, PolicyDecodingException {
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
        String featrues = gson.toJson(flavorFeatures);
        assertTrue(featrues.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(featrues.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(featrues.contains("\"type\":\"flavor_directives\""));
        assertTrue(featrues.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(featrues.contains("\"attribute_value\":\"\""));
        assertTrue(featrues.contains("\"hpa-feature\":\"pciePassthrough\""));
        assertTrue(featrues.contains("\"mandatory\":\"True\""));
        assertTrue(featrues.contains("\"architecture\":\"generic\""));
        assertTrue(featrues.contains("\"hpa-version\":\"v1\""));
        assertTrue(featrues.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(featrues.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(featrues.contains("\"operator\":\"=\""));
        assertTrue(featrues.contains("\"unit\":\"\""));
    }

    @Test
    public void testserviceVcpeWithAll() throws IOException, PolicyDecodingException {
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
        String featrues = gson.toJson(flavorFeatures);
        assertTrue(featrues.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(featrues.contains("\"type\":\"flavor_directives\""));
        assertTrue(featrues.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(featrues.contains("\"attribute_value\":\"\""));
        assertTrue(featrues.contains("\"hpa-feature\":\"sriovNICNetwork\""));
        assertTrue(featrues.contains("\"mandatory\":\"True\""));
        assertTrue(featrues.contains("\"architecture\":\"generic\""));
        assertTrue(featrues.contains("\"hpa-version\":\"v1\""));
        assertTrue(featrues.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(featrues.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(featrues.contains("\"operator\":\"=\""));
        assertTrue(featrues.contains("\"unit\":\"\""));
    }
}

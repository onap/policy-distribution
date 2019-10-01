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

import java.io.IOException;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;

/**
 * Class to perform unit test of {@link PolicyDecoderCsarPdpxLifecycleApi}.
 *
 */
public class TestPolicyDecoderCsarPdpxLifecycleApi {

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

        // assertEquals("onapName", entity.getOnapName());
        // assertTrue(entity.getName().startsWith("OOF."));
        // assertTrue(entity.getConfigBody().contains("\"priority\":\"5\""));
        // assertTrue(entity.getConfigBody().contains("\"riskLevel\":\"2\""));
        // assertTrue(entity.getConfigBody().contains("\"riskType\":\"Test\""));
        // assertTrue(entity.getConfigBody().contains("\"version\":\"1.0\""));
        // assertTrue(entity.getConfigBody().contains("\"policyType\":\"hpa\""));
        //
        // assertTrue(entity.getConfigBody().contains("\"id\":\"VDU_vgw_0\""));
        // assertTrue(entity.getConfigBody().contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        // assertTrue(entity.getConfigBody().contains("\"type\":\"flavor_directives\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-feature\":\"basicCapabilities\""));
        // assertTrue(entity.getConfigBody().contains("\"mandatory\":\"True\""));
        // assertTrue(entity.getConfigBody().contains("\"architecture\":\"generic\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-version\":\"v1\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-key\":\"virtualMemSize\""));
        // assertTrue(entity.getConfigBody().contains("\"operator\":\"=\""));
        // assertTrue(entity.getConfigBody().contains("\"unit\":\"MB\""));
    }

    @Test
    public void testHpaPolicySriov() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/hpaPolicySriov.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        final ToscaServiceTemplate entity = entities.iterator().next();

        // assertEquals("onapName", entity.getOnapName());
        // assertTrue(entity.getName().startsWith("OOF."));
        // assertTrue(entity.getConfigBody().contains("\"priority\":\"5\""));
        // assertTrue(entity.getConfigBody().contains("\"riskLevel\":\"2\""));
        // assertTrue(entity.getConfigBody().contains("\"riskType\":\"Test\""));
        // assertTrue(entity.getConfigBody().contains("\"version\":\"1.0\""));
        // assertTrue(entity.getConfigBody().contains("\"policyType\":\"hpa\""));
        //
        // assertTrue(entity.getConfigBody().contains("\"id\":\"VDU_vgw_0\""));
        // assertTrue(entity.getConfigBody().contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        // assertTrue(entity.getConfigBody().contains("\"type\":\"flavor_directives\""));
        // assertTrue(entity.getConfigBody().contains("\"attribute_name\":\"flavorName\""));
        // assertTrue(entity.getConfigBody().contains("\"attribute_value\":\"\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-feature\":\"sriovNICNetwork\""));
        // assertTrue(entity.getConfigBody().contains("\"mandatory\":\"True\""));
        // assertTrue(entity.getConfigBody().contains("\"architecture\":\"generic\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-version\":\"v1\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-key\":\"pciVendorId\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-value\":\"1234\""));
        // assertTrue(entity.getConfigBody().contains("\"operator\":\"=\""));
        // assertTrue(entity.getConfigBody().contains("\"unit\":\"\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-key\":\"pciDeviceId\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-value\":\"5678\""));
        // assertTrue(entity.getConfigBody().contains("\"operator\":\"=\""));
        // assertTrue(entity.getConfigBody().contains("\"unit\":\"\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-key\":\"pciNumDevices\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-value\":\"1\""));
        // assertTrue(entity.getConfigBody().contains("\"operator\":\"=\""));
        // assertTrue(entity.getConfigBody().contains("\"unit\":\"\""));
    }

    @Test
    public void testHpaPolicyPciePassthrough() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/hpaPolicyPciePassthrough.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        assertEquals(2, entities.size());
        final ToscaServiceTemplate entity = entities.toArray(new ToscaServiceTemplate[0])[0];

        // assertEquals("onapName", entity.getOnapName());
        // assertTrue(entity.getName().startsWith("OOF."));
        // assertTrue(entity.getConfigBody().contains("\"priority\":\"5\""));
        // assertTrue(entity.getConfigBody().contains("\"riskLevel\":\"2\""));
        // assertTrue(entity.getConfigBody().contains("\"riskType\":\"Test\""));
        // assertTrue(entity.getConfigBody().contains("\"version\":\"1.0\""));
        // assertTrue(entity.getConfigBody().contains("\"policyType\":\"hpa\""));
        //
        // assertTrue(entity.getConfigBody().contains("\"id\":\"VDU_vgw_0\""));
        // assertTrue(entity.getConfigBody().contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        // assertTrue(entity.getConfigBody().contains("\"type\":\"flavor_directives\""));
        // assertTrue(entity.getConfigBody().contains("\"attribute_name\":\"flavorName\""));
        // assertTrue(entity.getConfigBody().contains("\"attribute_value\":\"\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-feature\":\"pciePassthrough\""));
        // assertTrue(entity.getConfigBody().contains("\"mandatory\":\"True\""));
        // assertTrue(entity.getConfigBody().contains("\"architecture\":\"generic\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-version\":\"v1\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-key\":\"pciVendorId\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-value\":\"1234\""));
        // assertTrue(entity.getConfigBody().contains("\"operator\":\"=\""));
        // assertTrue(entity.getConfigBody().contains("\"unit\":\"\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-key\":\"pciDeviceId\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-value\":\"5678\""));
        // assertTrue(entity.getConfigBody().contains("\"operator\":\"=\""));
        // assertTrue(entity.getConfigBody().contains("\"unit\":\"\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-key\":\"pciNumDevices\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-value\":\"1\""));
        // assertTrue(entity.getConfigBody().contains("\"operator\":\"=\""));
        // assertTrue(entity.getConfigBody().contains("\"unit\":\"\""));


    }

    @Test
    public void testHpaPolicyHugePage() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/hpaPolicyHugePage.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        assertEquals(2, entities.size());
        final ToscaServiceTemplate entity = entities.toArray(new ToscaServiceTemplate[0])[0];

        // assertEquals("onapName", entity.getOnapName());
        // assertTrue(entity.getName().startsWith("OOF."));
        // assertTrue(entity.getConfigBody().contains("\"priority\":\"5\""));
        // assertTrue(entity.getConfigBody().contains("\"riskLevel\":\"2\""));
        // assertTrue(entity.getConfigBody().contains("\"riskType\":\"Test\""));
        // assertTrue(entity.getConfigBody().contains("\"version\":\"1.0\""));
        // assertTrue(entity.getConfigBody().contains("\"policyType\":\"hpa\""));
        //
        // assertTrue(entity.getConfigBody().contains("\"id\":\"VDU_vgw_0\""));
        // assertTrue(entity.getConfigBody().contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        // assertTrue(entity.getConfigBody().contains("\"type\":\"flavor_directives\""));
        // assertTrue(entity.getConfigBody().contains("\"attribute_name\":\"flavorName\""));
        // assertTrue(entity.getConfigBody().contains("\"attribute_value\":\"\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-feature\":\"hugePages\""));
        // assertTrue(entity.getConfigBody().contains("\"mandatory\":\"true\""));
        // assertTrue(entity.getConfigBody().contains("\"architecture\":\"generic\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-version\":\"v1\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-key\":\"memoryPageSize\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-value\":\"2\""));
        // assertTrue(entity.getConfigBody().contains("\"operator\":\"=\""));
        // assertTrue(entity.getConfigBody().contains("\"unit\":\"MB\""));
    }

    @Test
    public void testS3p0PciVendorId() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/s3p_0_pciVendorId.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        assertEquals(1, entities.size());
        final ToscaServiceTemplate entity = entities.toArray(new ToscaServiceTemplate[0])[0];

        // assertEquals("onapName", entity.getOnapName());
        // assertTrue(entity.getName().startsWith("OOF."));
        // assertTrue(entity.getConfigBody().contains("\"priority\":\"5\""));
        // assertTrue(entity.getConfigBody().contains("\"riskLevel\":\"2\""));
        // assertTrue(entity.getConfigBody().contains("\"riskType\":\"Test\""));
        // assertTrue(entity.getConfigBody().contains("\"version\":\"1.0\""));
        // assertTrue(entity.getConfigBody().contains("\"policyType\":\"hpa\""));
        //
        // assertTrue(entity.getConfigBody().contains("\"id\":\"VDU_vgw_0\""));
        // assertTrue(entity.getConfigBody().contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        // assertTrue(entity.getConfigBody().contains("\"type\":\"flavor_directives\""));
        // assertTrue(entity.getConfigBody().contains("\"attribute_name\":\"flavorName\""));
        // assertTrue(entity.getConfigBody().contains("\"attribute_value\":\"\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-feature\":\"pciePassthrough\""));
        // assertTrue(entity.getConfigBody().contains("\"mandatory\":\"True\""));
        // assertTrue(entity.getConfigBody().contains("\"architecture\":\"generic\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-version\":\"v1\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-key\":\"pciVendorId\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-value\":\"1234\""));
        // assertTrue(entity.getConfigBody().contains("\"operator\":\"=\""));
        // assertTrue(entity.getConfigBody().contains("\"unit\":\"\""));

    }

    @Test
    public void testserviceVcpeWithAll() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/service-VcpeWithAll-csar.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        assertEquals(5, entities.size());
        final ToscaServiceTemplate entity = entities.toArray(new ToscaServiceTemplate[0])[0];

        // assertEquals("onapName", entity.getOnapName());
        // assertTrue(entity.getName().startsWith("OOF."));
        // assertTrue(entity.getConfigBody().contains("\"priority\":\"5\""));
        // assertTrue(entity.getConfigBody().contains("\"riskLevel\":\"2\""));
        // assertTrue(entity.getConfigBody().contains("\"riskType\":\"Test\""));
        // assertTrue(entity.getConfigBody().contains("\"version\":\"1.0\""));
        // assertTrue(entity.getConfigBody().contains("\"policyType\":\"hpa\""));
        //
        // assertTrue(entity.getConfigBody().contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        // assertTrue(entity.getConfigBody().contains("\"type\":\"flavor_directives\""));
        // assertTrue(entity.getConfigBody().contains("\"attribute_name\":\"flavorName\""));
        // assertTrue(entity.getConfigBody().contains("\"attribute_value\":\"\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-feature\":\"sriovNICNetwork\""));
        // assertTrue(entity.getConfigBody().contains("\"mandatory\":\"True\""));
        // assertTrue(entity.getConfigBody().contains("\"architecture\":\"generic\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-version\":\"v1\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-key\":\"pciVendorId\""));
        // assertTrue(entity.getConfigBody().contains("\"hpa-attribute-value\":\"1234\""));
        // assertTrue(entity.getConfigBody().contains("\"operator\":\"=\""));
        // assertTrue(entity.getConfigBody().contains("\"unit\":\"\""));

    }
}

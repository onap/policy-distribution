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
import org.onap.policy.distribution.reception.decoding.pdpx.PolicyDecoderCsarPdpxLifecycleApi;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyType;
import org.onap.policy.models.tosca.authorative.concepts.ToscaProperty;

/**
 * Class to perform unit test of {@link PolicyDecoderCsarPdpxLifecycleApiLifecycleApi}.
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
        Map<String, ToscaPolicyType> policyTypes = entity.getPolicyTypes();

        ToscaPolicyType type = policyTypes.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_NAME_PREFIX);
        Map<String, ToscaProperty> props = type.getProperties();

        ToscaProperty prefix = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_NAME_PREFIX);
        assertEquals("OOF", prefix.getDefaultValue());
        ToscaProperty onapName = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_ONAP_NAME);
        assertEquals("onapName", onapName.getDefaultValue());
        ToscaProperty priority = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_PRIORITY);
        assertTrue(priority.getDefaultValue().contains("5"));
        ToscaProperty riskLevel = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_RISK_LEVEL);
        assertTrue(riskLevel.getDefaultValue().contains("2"));
        ToscaProperty riskType = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_RISK_TYPE);
        assertTrue(riskType.getDefaultValue().contains("Test"));
        ToscaProperty version = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_VERSION);
        assertTrue(version.getDefaultValue().contains("1.0"));

        ToscaProperty content = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_CONTENT);
        String c = (String)content.getDefaultValue();
        assertTrue(c.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(c.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(c.contains("\"type\":\"flavor_directives\""));
        assertTrue(c.contains("\"hpa-feature\":\"basicCapabilities\""));
        assertTrue(c.contains("\"mandatory\":\"True\""));
        assertTrue(c.contains("\"architecture\":\"generic\""));
        assertTrue(c.contains("\"hpa-version\":\"v1\""));
        assertTrue(c.contains("\"hpa-attribute-key\":\"virtualMemSize\""));
        assertTrue(c.contains("\"operator\":\"=\""));
        assertTrue(c.contains("\"unit\":\"MB\""));
    }

    @Test
    public void testHpaPolicySriov() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/hpaPolicySriov.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        final ToscaServiceTemplate entity = entities.iterator().next();
        Map<String, ToscaPolicyType> policyTypes = entity.getPolicyTypes();

        ToscaPolicyType type = policyTypes.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_NAME_PREFIX);
        Map<String, ToscaProperty> props = type.getProperties();

        ToscaProperty prefix = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_NAME_PREFIX);
        assertEquals("OOF", prefix.getDefaultValue());
        ToscaProperty onapName = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_ONAP_NAME);
        assertEquals("onapName", onapName.getDefaultValue());
        ToscaProperty priority = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_PRIORITY);
        assertTrue(priority.getDefaultValue().contains("5"));
        ToscaProperty riskLevel = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_RISK_LEVEL);
        assertTrue(riskLevel.getDefaultValue().contains("2"));
        ToscaProperty riskType = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_RISK_TYPE);
        assertTrue(riskType.getDefaultValue().contains("Test"));
        ToscaProperty version = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_VERSION);
        assertTrue(version.getDefaultValue().contains("1.0"));

        ToscaProperty content = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_CONTENT);
        String c = (String)content.getDefaultValue();
        assertTrue(c.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(c.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(c.contains("\"type\":\"flavor_directives\""));
        assertTrue(c.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(c.contains("\"attribute_value\":\"\""));
        assertTrue(c.contains("\"hpa-feature\":\"sriovNICNetwork\""));
        assertTrue(c.contains("\"mandatory\":\"True\""));
        assertTrue(c.contains("\"architecture\":\"generic\""));
        assertTrue(c.contains("\"hpa-version\":\"v1\""));
        assertTrue(c.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(c.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(c.contains("\"operator\":\"=\""));
        assertTrue(c.contains("\"unit\":\"\""));
        assertTrue(c.contains("\"hpa-attribute-key\":\"pciDeviceId\""));
        assertTrue(c.contains("\"hpa-attribute-value\":\"5678\""));
        assertTrue(c.contains("\"operator\":\"=\""));
        assertTrue(c.contains("\"unit\":\"\""));
        assertTrue(c.contains("\"hpa-attribute-key\":\"pciNumDevices\""));
        assertTrue(c.contains("\"hpa-attribute-value\":\"1\""));
        assertTrue(c.contains("\"operator\":\"=\""));
        assertTrue(c.contains("\"unit\":\"\""));
    }

    @Test
    public void testHpaPolicyPciePassthrough() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/hpaPolicyPciePassthrough.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        assertEquals(2, entities.size());
        final ToscaServiceTemplate entity = entities.iterator().next();
        Map<String, ToscaPolicyType> policyTypes = entity.getPolicyTypes();

        ToscaPolicyType type = policyTypes.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_NAME_PREFIX);
        Map<String, ToscaProperty> props = type.getProperties();

        ToscaProperty prefix = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_NAME_PREFIX);
        assertEquals("OOF", prefix.getDefaultValue());
        ToscaProperty onapName = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_ONAP_NAME);
        assertEquals("onapName", onapName.getDefaultValue());
        ToscaProperty priority = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_PRIORITY);
        assertTrue(priority.getDefaultValue().contains("5"));
        ToscaProperty riskLevel = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_RISK_LEVEL);
        assertTrue(riskLevel.getDefaultValue().contains("2"));
        ToscaProperty riskType = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_RISK_TYPE);
        assertTrue(riskType.getDefaultValue().contains("Test"));
        ToscaProperty version = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_VERSION);
        assertTrue(version.getDefaultValue().contains("1.0"));

        ToscaProperty content = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_CONTENT);
        String c = (String)content.getDefaultValue();
        assertTrue(c.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(c.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(c.contains("\"type\":\"flavor_directives\""));
        assertTrue(c.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(c.contains("\"attribute_value\":\"\""));
        assertTrue(c.contains("\"hpa-feature\":\"pciePassthrough\""));
        assertTrue(c.contains("\"mandatory\":\"True\""));
        assertTrue(c.contains("\"architecture\":\"generic\""));
        assertTrue(c.contains("\"hpa-version\":\"v1\""));
        assertTrue(c.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(c.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(c.contains("\"operator\":\"=\""));
        assertTrue(c.contains("\"unit\":\"\""));
        assertTrue(c.contains("\"hpa-attribute-key\":\"pciDeviceId\""));
        assertTrue(c.contains("\"hpa-attribute-value\":\"5678\""));
        assertTrue(c.contains("\"operator\":\"=\""));
        assertTrue(c.contains("\"unit\":\"\""));
        assertTrue(c.contains("\"hpa-attribute-key\":\"pciNumDevices\""));
        assertTrue(c.contains("\"hpa-attribute-value\":\"1\""));
        assertTrue(c.contains("\"operator\":\"=\""));
        assertTrue(c.contains("\"unit\":\"\""));
    }

    @Test
    public void testHpaPolicyHugePage() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/hpaPolicyHugePage.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        assertEquals(2, entities.size());
        final ToscaServiceTemplate entity = entities.iterator().next();
        Map<String, ToscaPolicyType> policyTypes = entity.getPolicyTypes();

        ToscaPolicyType type = policyTypes.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_NAME_PREFIX);
        Map<String, ToscaProperty> props = type.getProperties();

        ToscaProperty prefix = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_NAME_PREFIX);
        assertEquals("OOF", prefix.getDefaultValue());
        ToscaProperty onapName = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_ONAP_NAME);
        assertEquals("onapName", onapName.getDefaultValue());
        ToscaProperty priority = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_PRIORITY);
        assertTrue(priority.getDefaultValue().contains("5"));
        ToscaProperty riskLevel = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_RISK_LEVEL);
        assertTrue(riskLevel.getDefaultValue().contains("2"));
        ToscaProperty riskType = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_RISK_TYPE);
        assertTrue(riskType.getDefaultValue().contains("Test"));
        ToscaProperty version = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_VERSION);
        assertTrue(version.getDefaultValue().contains("1.0"));

        ToscaProperty content = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_CONTENT);
        String c = (String)content.getDefaultValue();
        assertTrue(c.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(c.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(c.contains("\"type\":\"flavor_directives\""));
        assertTrue(c.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(c.contains("\"attribute_value\":\"\""));
        assertTrue(c.contains("\"hpa-feature\":\"hugePages\""));
        assertTrue(c.contains("\"mandatory\":\"true\""));
        assertTrue(c.contains("\"architecture\":\"generic\""));
        assertTrue(c.contains("\"hpa-version\":\"v1\""));
        assertTrue(c.contains("\"hpa-attribute-key\":\"memoryPageSize\""));
        assertTrue(c.contains("\"hpa-attribute-value\":\"2\""));
        assertTrue(c.contains("\"operator\":\"=\""));
        assertTrue(c.contains("\"unit\":\"MB\""));
    }

    @Test
    public void testS3p0PciVendorId() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/s3p_0_pciVendorId.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        assertEquals(1, entities.size());
        final ToscaServiceTemplate entity = entities.iterator().next();
        Map<String, ToscaPolicyType> policyTypes = entity.getPolicyTypes();

        ToscaPolicyType type = policyTypes.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_NAME_PREFIX);
        Map<String, ToscaProperty> props = type.getProperties();

        ToscaProperty prefix = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_NAME_PREFIX);
        assertEquals("OOF", prefix.getDefaultValue());
        ToscaProperty onapName = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_ONAP_NAME);
        assertEquals("onapName", onapName.getDefaultValue());
        ToscaProperty priority = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_PRIORITY);
        assertTrue(priority.getDefaultValue().contains("5"));
        ToscaProperty riskLevel = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_RISK_LEVEL);
        assertTrue(riskLevel.getDefaultValue().contains("2"));
        ToscaProperty riskType = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_RISK_TYPE);
        assertTrue(riskType.getDefaultValue().contains("Test"));
        ToscaProperty version = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_VERSION);
        assertTrue(version.getDefaultValue().contains("1.0"));

        ToscaProperty content = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_CONTENT);
        String c = (String)content.getDefaultValue();
        assertTrue(c.contains("\"id\":\"VDU_vgw_0\""));
        assertTrue(c.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(c.contains("\"type\":\"flavor_directives\""));
        assertTrue(c.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(c.contains("\"attribute_value\":\"\""));
        assertTrue(c.contains("\"hpa-feature\":\"pciePassthrough\""));
        assertTrue(c.contains("\"mandatory\":\"True\""));
        assertTrue(c.contains("\"architecture\":\"generic\""));
        assertTrue(c.contains("\"hpa-version\":\"v1\""));
        assertTrue(c.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(c.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(c.contains("\"operator\":\"=\""));
        assertTrue(c.contains("\"unit\":\"\""));
    }

    @Test
    public void testserviceVcpeWithAll() throws IOException, PolicyDecodingException {
        final Csar csar = new Csar("src/test/resources/service-VcpeWithAll-csar.csar");
        final PolicyDecoderCsarPdpxLifecycleApi policyDecoderCsarPdpx = new PolicyDecoderCsarPdpxLifecycleApi();
        policyDecoderCsarPdpx.configure(PolicyDecoderCsarPdpxLifecycleApiParameters.class.getSimpleName());

        final Collection<ToscaServiceTemplate> entities = policyDecoderCsarPdpx.decode(csar);
        assertEquals(5, entities.size());
        final ToscaServiceTemplate entity = entities.iterator().next();
        Map<String, ToscaPolicyType> policyTypes = entity.getPolicyTypes();

        ToscaPolicyType type = policyTypes.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_NAME_PREFIX);
        Map<String, ToscaProperty> props = type.getProperties();

        ToscaProperty prefix = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_NAME_PREFIX);
        assertEquals("OOF", prefix.getDefaultValue());
        ToscaProperty onapName = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_ONAP_NAME);
        assertEquals("onapName", onapName.getDefaultValue());
        ToscaProperty priority = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_PRIORITY);
        assertTrue(priority.getDefaultValue().contains("5"));
        ToscaProperty riskLevel = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_RISK_LEVEL);
        assertTrue(riskLevel.getDefaultValue().contains("2"));
        ToscaProperty riskType = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_RISK_TYPE);
        assertTrue(riskType.getDefaultValue().contains("Test"));
        ToscaProperty version = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_VERSION);
        assertTrue(version.getDefaultValue().contains("1.0"));

        ToscaProperty content = (ToscaProperty)props.get(PolicyDecoderCsarPdpxLifecycleApi.TOSCA_POLICY_CONTENT);
        String c = (String)content.getDefaultValue();
        assertTrue(c.contains("\"type\":\"tosca.nodes.nfv.Vdu.Compute\""));
        assertTrue(c.contains("\"type\":\"flavor_directives\""));
        assertTrue(c.contains("\"attribute_name\":\"flavorName\""));
        assertTrue(c.contains("\"attribute_value\":\"\""));
        assertTrue(c.contains("\"hpa-feature\":\"sriovNICNetwork\""));
        assertTrue(c.contains("\"mandatory\":\"True\""));
        assertTrue(c.contains("\"architecture\":\"generic\""));
        assertTrue(c.contains("\"hpa-version\":\"v1\""));
        assertTrue(c.contains("\"hpa-attribute-key\":\"pciVendorId\""));
        assertTrue(c.contains("\"hpa-attribute-value\":\"1234\""));
        assertTrue(c.contains("\"operator\":\"=\""));
        assertTrue(c.contains("\"unit\":\"\""));
    }
}

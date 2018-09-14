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
import static org.junit.Assert.fail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.onap.policy.distribution.model.Csar;

/**
 * Class to perform unit test of {@link PolicyDecoderCsarPdpx}.
 *
 */
public class TestPolicyDecoderCsarPdpx {

    @Test
    public void testHpaPolicy2Vnf() throws IOException {
        Csar csar = new Csar("src/test/resources/service-TestNs8-csar.csar");
        PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        try {
            Collection<PdpxPolicy> ret = policyDecoderCsarPdpx.decode(csar);
            assertEquals(2, ret.size());
            PdpxPolicy pdpxPolicy = (PdpxPolicy) ret.toArray()[0];
            assertEquals("Optimization", pdpxPolicy.getContent().getPolicyType());
            assertEquals(1, pdpxPolicy.getContent().getFlavorFeatures().size());

            FlavorFeature flavorFeature = pdpxPolicy.getContent().getFlavorFeatures().get(0);
            assertEquals("vdu_vnf_1", flavorFeature.getId());
            assertEquals("tosca.node.nfv.Vdu.Compute", flavorFeature.getType());
            assertEquals(1, flavorFeature.getDirectives().size());
            Directive directive = flavorFeature.getDirectives().get(0);
            assertEquals("flavor_directive", directive.getType());            
            assertEquals(1, directive.getAttributes().size());
            assertEquals("flavorName", directive.getAttributes().get(0).getAttributeName());
            assertEquals("", directive.getAttributes().get(0).getAttributeValue());
            assertEquals(2, flavorFeature.getFlavorProperties().size());
            FlavorProperty flavorProperty = flavorFeature.getFlavorProperties().get(0);
            assertEquals("BasicCapabilities", flavorProperty.getHpaFeature());
            assertEquals("true", flavorProperty.getMandatory());
            assertEquals("generic", flavorProperty.getArchitecture());
            assertEquals("v1", flavorProperty.getHpaVersion());
            assertEquals(0, flavorProperty.getDirectives().size());
            assertEquals(1, flavorProperty.getHpaFeatureAttributes().size());
            HpaFeatureAttribute hpaFeatreAttribute = flavorProperty.getHpaFeatureAttributes().get(0);
            assertEquals("virtualMemSize",hpaFeatreAttribute.getHpaAttributeKey());
            assertEquals("4096",hpaFeatreAttribute.getHpaAttributeValue());
            assertEquals("",hpaFeatreAttribute.getOperator());
            assertEquals("MB",hpaFeatreAttribute.getUnit());
            
        } catch (Exception e) {
            fail("test should not thrown an exception here: " + e.getMessage());
        }
    }

    @Test
    public void testHpaPolicySriov() throws IOException {
        Csar csar = new Csar("src/test/resources/hpaPolicySriov.csar");
        PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        try {
            Collection<PdpxPolicy> ret = policyDecoderCsarPdpx.decode(csar);
            assertEquals(2, ret.size());
            PdpxPolicy pdpxPolicy = (PdpxPolicy) ret.toArray()[0];
            assertEquals("Optimization", pdpxPolicy.getContent().getPolicyType());
            assertEquals(1, pdpxPolicy.getContent().getFlavorFeatures().size());

            FlavorFeature flavorFeature = pdpxPolicy.getContent().getFlavorFeatures().get(0);
            assertEquals("vdu_vnf_1", flavorFeature.getId());
            assertEquals("tosca.node.nfv.Vdu.Compute", flavorFeature.getType());
            assertEquals(1, flavorFeature.getDirectives().size());
            Directive directive = flavorFeature.getDirectives().get(0);
            assertEquals("flavor_directive", directive.getType());            
            assertEquals(1, directive.getAttributes().size());
            assertEquals("flavorName", directive.getAttributes().get(0).getAttributeName());
            assertEquals("", directive.getAttributes().get(0).getAttributeValue());
            assertEquals(4, flavorFeature.getFlavorProperties().size());
            FlavorProperty flavorProperty = flavorFeature.getFlavorProperties().get(3);
            assertEquals("SriovNICNetwork", flavorProperty.getHpaFeature());
            assertEquals("true", flavorProperty.getMandatory());
            assertEquals("generic", flavorProperty.getArchitecture());
            assertEquals("v1", flavorProperty.getHpaVersion());
            assertEquals(0, flavorProperty.getDirectives().size());
            assertEquals(3, flavorProperty.getHpaFeatureAttributes().size());

            HpaFeatureAttribute pciVendorId = flavorProperty.getHpaFeatureAttributes().get(0);
            assertEquals("pciVendorId",pciVendorId.getHpaAttributeKey());
            assertEquals("1234",pciVendorId.getHpaAttributeValue());
            assertEquals("",pciVendorId.getOperator());
            assertEquals("",pciVendorId.getUnit());
            HpaFeatureAttribute pciDeviceId = flavorProperty.getHpaFeatureAttributes().get(1);
            assertEquals("pciDeviceId",pciDeviceId.getHpaAttributeKey());
            assertEquals("5678",pciDeviceId.getHpaAttributeValue());
            assertEquals("",pciDeviceId.getOperator());
            assertEquals("",pciDeviceId.getUnit());
            HpaFeatureAttribute pciNumDevices = flavorProperty.getHpaFeatureAttributes().get(2);
            assertEquals("pciNumDevices",pciNumDevices.getHpaAttributeKey());
            assertEquals("1",pciNumDevices.getHpaAttributeValue());
            assertEquals("",pciNumDevices.getOperator());
            assertEquals("",pciNumDevices.getUnit());
        } catch (Exception e) {
            fail("test should not thrown an exception here: " + e.getMessage());
        }
    }

    @Test
    public void testHpaPolicyPciePassthrough() throws IOException {
        Csar csar = new Csar("src/test/resources/hpaPolicyPciePassthrough.csar");
        PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        try {
            Collection<PdpxPolicy> ret = policyDecoderCsarPdpx.decode(csar);
            assertEquals(2, ret.size());
            PdpxPolicy pdpxPolicy = (PdpxPolicy) ret.toArray()[0];
            assertEquals("Optimization", pdpxPolicy.getContent().getPolicyType());
            assertEquals(1, pdpxPolicy.getContent().getFlavorFeatures().size());

            FlavorFeature flavorFeature = pdpxPolicy.getContent().getFlavorFeatures().get(0);
            assertEquals("vdu_vnf_1", flavorFeature.getId());
            assertEquals("tosca.node.nfv.Vdu.Compute", flavorFeature.getType());
            assertEquals(1, flavorFeature.getDirectives().size());
            Directive directive = flavorFeature.getDirectives().get(0);
            assertEquals("flavor_directive", directive.getType());            
            assertEquals(1, directive.getAttributes().size());
            assertEquals("flavorName", directive.getAttributes().get(0).getAttributeName());
            assertEquals("", directive.getAttributes().get(0).getAttributeValue());
            assertEquals(4, flavorFeature.getFlavorProperties().size());
            FlavorProperty flavorProperty = flavorFeature.getFlavorProperties().get(3);
            assertEquals("pciePassthrough", flavorProperty.getHpaFeature());
            assertEquals("true", flavorProperty.getMandatory());
            assertEquals("generic", flavorProperty.getArchitecture());
            assertEquals("v1", flavorProperty.getHpaVersion());
            assertEquals(0, flavorProperty.getDirectives().size());
            assertEquals(3, flavorProperty.getHpaFeatureAttributes().size());

            HpaFeatureAttribute pciVendorId = flavorProperty.getHpaFeatureAttributes().get(0);
            assertEquals("pciVendorId",pciVendorId.getHpaAttributeKey());
            assertEquals("1234",pciVendorId.getHpaAttributeValue());
            assertEquals("",pciVendorId.getOperator());
            assertEquals("",pciVendorId.getUnit());
            HpaFeatureAttribute pciDeviceId = flavorProperty.getHpaFeatureAttributes().get(1);
            assertEquals("pciDeviceId",pciDeviceId.getHpaAttributeKey());
            assertEquals("5678",pciDeviceId.getHpaAttributeValue());
            assertEquals("",pciDeviceId.getOperator());
            assertEquals("",pciDeviceId.getUnit());
            HpaFeatureAttribute pciNumDevices = flavorProperty.getHpaFeatureAttributes().get(2);
            assertEquals("pciNumDevices",pciNumDevices.getHpaAttributeKey());
            assertEquals("1",pciNumDevices.getHpaAttributeValue());
            assertEquals("",pciNumDevices.getOperator());
            assertEquals("",pciNumDevices.getUnit());
        } catch (Exception e) {
            fail("test should not thrown an exception here: " + e.getMessage());
        }
    }

    @Test
    public void testHpaPolicyHugePage() throws IOException {
        Csar csar = new Csar("src/test/resources/hpaPolicyHugePage.csar");
        PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        try {
            Collection<PdpxPolicy> ret = policyDecoderCsarPdpx.decode(csar);
            assertEquals(2, ret.size());
            PdpxPolicy pdpxPolicy = (PdpxPolicy) ret.toArray()[0];
            assertEquals("Optimization", pdpxPolicy.getContent().getPolicyType());
            assertEquals(1, pdpxPolicy.getContent().getFlavorFeatures().size());

            FlavorFeature flavorFeature = pdpxPolicy.getContent().getFlavorFeatures().get(0);
            assertEquals("vdu_vnf_1", flavorFeature.getId());
            assertEquals("tosca.node.nfv.Vdu.Compute", flavorFeature.getType());
            assertEquals(1, flavorFeature.getDirectives().size());
            Directive directive = flavorFeature.getDirectives().get(0);
            assertEquals("flavor_directive", directive.getType());            
            assertEquals(1, directive.getAttributes().size());
            assertEquals("flavorName", directive.getAttributes().get(0).getAttributeName());
            assertEquals("", directive.getAttributes().get(0).getAttributeValue());
            assertEquals(3, flavorFeature.getFlavorProperties().size());
            FlavorProperty flavorProperty = flavorFeature.getFlavorProperties().get(2);
            assertEquals("hugePages", flavorProperty.getHpaFeature());
            assertEquals("false", flavorProperty.getMandatory());
            assertEquals("generic", flavorProperty.getArchitecture());
            assertEquals("v1", flavorProperty.getHpaVersion());
            assertEquals(0, flavorProperty.getDirectives().size());
            assertEquals(1, flavorProperty.getHpaFeatureAttributes().size());
            HpaFeatureAttribute hpaFeatreAttribute = flavorProperty.getHpaFeatureAttributes().get(0);
            assertEquals("memoryPageSize",hpaFeatreAttribute.getHpaAttributeKey());
            assertEquals("2",hpaFeatreAttribute.getHpaAttributeValue());
            assertEquals("",hpaFeatreAttribute.getOperator());
            assertEquals("MB",hpaFeatreAttribute.getUnit());
        } catch (Exception e) {
            fail("test should not thrown an exception here: " + e.getMessage());
        }
    }
}

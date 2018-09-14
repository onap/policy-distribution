/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.toscaparser.api.CapabilityAssignment;
import org.onap.sdc.toscaparser.api.CapabilityAssignments;
import org.onap.sdc.toscaparser.api.NodeTemplate;

/**
 * Extract concerned info from NodeTemplate, currently ONLY HPA Feature.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
public class ExtractFromNode {

    private static final Logger LOGGER = FlexLogger.getLogger(ExtractFromNode.class);
    private static final String VDU_TYPE = "tosca.nodes.nfv.Vdu.Compute";
    private static final String VDU_CP_TYPE = "tosca.nodes.nfv.VduCp";
    private static final String VIRTUAL_MEM_SIZE_PATH = "virtual_memory#virtual_mem_size";
    private static final String NUM_VIRTUAL_CPU_PATH = "virtual_cpu#num_virtual_cpu";
    private static final String CPU_ARCHITECTURE_PATH = "virtual_cpu#cpu_architecture";
    private static final String BASIC_CAPABILITIES = "BasicCapabilities";

    ISdcCsarHelper sdcCsarHelper;
    final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();

    public void setSdcCsarHelper(final ISdcCsarHelper sdcCsarHelper) {
        this.sdcCsarHelper = sdcCsarHelper;
    }

    /**
     * ExtractInfo from VNF , each VNF may includes more than one VDUs and CPs return new generated
     * PdpxPolicy if it has got Hpa feature info or else return null.
     *
     * @param node the NodeTemplate
     * 
     * @return the extracted info from input node
     * 
     * @throws PolicyDecodingException if extract fails
     */
    public Content extractInfo(final NodeTemplate node) throws PolicyDecodingException {

        LOGGER.debug("the meta data of this nodetemplate = " + sdcCsarHelper.getNodeTemplateMetadata(node));
        final List<NodeTemplate> lnodeChild = sdcCsarHelper.getNodeTemplateChildren(node);
        LOGGER.debug("the size of lnodeChild = " + lnodeChild.size());

        // Store all the VDUs under one VNF
        final List<NodeTemplate> lnodeVdu = new ArrayList<>();
        // Store all the Cps under one VNF
        final List<NodeTemplate> lnodeVduCp = new ArrayList<>();
        for (final NodeTemplate nodeChild : lnodeChild) {
            final String type = sdcCsarHelper.getTypeOfNodeTemplate(nodeChild);
            LOGGER.debug("the type of this nodeChild = " + type);
            LOGGER.debug("the meta data of this nodetemplate = " + sdcCsarHelper.getNodeTemplateMetadata(nodeChild));
            if (type.equalsIgnoreCase(VDU_TYPE)) {
                lnodeVdu.add(nodeChild);
            } else if (type.equalsIgnoreCase(VDU_CP_TYPE)) {
                lnodeVduCp.add(nodeChild);
            }
        }
        LOGGER.debug("the size of vdu is =" + lnodeVdu.size());
        LOGGER.debug("the size of cp is =" + lnodeVduCp.size());

        final Content content = new Content();
        extractInfoVdu(lnodeVdu, content);
        extractInfoVduCp(lnodeVduCp, content);
        if (content.getFlavorFeatures().isEmpty()) {
            return null;
        }

        return content;
    }


    /**
     * ExtractInfofromVdu, supported hpa features, All under the capability of
     * tosca.nodes.nfv.Vdu.Compute.
     *
     * @param lnodeVdu the list of Vdu node
     * 
     * @param content to be change based on lnodeVdu
     */
    public void extractInfoVdu(final List<NodeTemplate> lnodeVdu, final Content content) {
        // each VDU <=> FlavorFeature
        for (final NodeTemplate node : lnodeVdu) {
            final Attribute flavorAttribute = new Attribute();
            flavorAttribute.setAttributeName("flavorName");
            flavorAttribute.setAttributeValue("");
            final Directive flavorDirective = new Directive();
            flavorDirective.setType("flavor_directive");
            flavorDirective.getAttributes().add(flavorAttribute);
            final FlavorFeature flavorFeature = new FlavorFeature();
            flavorFeature.setId(sdcCsarHelper.getNodeTemplatePropertyLeafValue(node, "name"));
            flavorFeature.getDirectives().add(flavorDirective);

            final CapabilityAssignments capabilityAssignments = sdcCsarHelper.getCapabilitiesOf(node);
            final CapabilityAssignment capabilityAssignment =
                    capabilityAssignments.getCapabilityByName("virtual_compute");
            if (capabilityAssignment != null) {
                generateBasicCapability(capabilityAssignment, flavorFeature);
                generateHugePages(capabilityAssignment);
            }
            content.getFlavorFeatures().add(flavorFeature);
        }
    }

    /**
     * GenerateBasicCapability, supported hpa features, All under the capability of
     * tosca.nodes.nfv.Vdu.Compute.
     *
     * @param capabilityAssignment represents the capability of node
     * 
     * @param flavorFeature represents all the features of specified flavor
     */
    private void generateBasicCapability(final CapabilityAssignment capabilityAssignment,
            final FlavorFeature flavorFeature) {
        // the format is xxx MB/GB like 4096 MB
        final String virtualMemSize =
                sdcCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment, VIRTUAL_MEM_SIZE_PATH);
        if (virtualMemSize != null) {
            LOGGER.debug("the virtualMemSize = " + virtualMemSize);
            final HpaFeatureAttribute hpaFeatureAttribute =
                    generateHpaFeatureAttribute("virtualMemSize", virtualMemSize);
            final FlavorProperty flavorProperty = new FlavorProperty();
            flavorProperty.setHpaFeature(BASIC_CAPABILITIES);
            flavorProperty.getHpaFeatureAttributes().add(hpaFeatureAttribute);
            flavorFeature.getFlavorProperties().add(flavorProperty);
        }

        // the format is int like 2
        final String numVirtualCpu =
                sdcCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment, NUM_VIRTUAL_CPU_PATH);
        if (numVirtualCpu != null) {
            LOGGER.debug("the numVirtualCpu = " + numVirtualCpu);
            final HpaFeatureAttribute hpaFeatureAttribute = generateHpaFeatureAttribute("numVirtualCpu", numVirtualCpu);
            final String cpuArchitecture =
                    sdcCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment, CPU_ARCHITECTURE_PATH);
            final FlavorProperty flavorProperty = new FlavorProperty();
            flavorProperty.setHpaFeature(BASIC_CAPABILITIES);
            if (cpuArchitecture != null) {
                flavorProperty.setArchitecture(cpuArchitecture);
            }
            flavorProperty.getHpaFeatureAttributes().add(hpaFeatureAttribute);
            flavorFeature.getFlavorProperties().add(flavorProperty);
        }
    }

    /**
     * GenerateHpaFeatureAttribute based on the value of featureValue. the format:
     * "hpa-attribute-key": "pciVendorId", "hpa-attribute-value": "1234", "operator": "=", "unit":
     * "xxx".
     *
     * @param hpaAttributeKey get from the high layer tosca DM
     * 
     * @param featureValue get from the high layer tosca DM
     * 
     */
    private HpaFeatureAttribute generateHpaFeatureAttribute(final String hpaAttributeKey, final String featureValue) {

        final HpaFeatureAttribute hpaFeatureAttribute = new HpaFeatureAttribute();
        hpaFeatureAttribute.setHpaAttributeKey(hpaAttributeKey);
        final String tmp = featureValue.replace(" ", "");
        final String pattern = "(\\D*)(\\d+)(\\D*)";
        final Pattern r = Pattern.compile(pattern);
        final Matcher m = r.matcher(tmp);
        if (m.find()) {
            LOGGER.debug("operator = " + m.group(1));
            LOGGER.debug("value = " + m.group(2));
            LOGGER.debug("unit = " + m.group(3));
            hpaFeatureAttribute.setOperator(m.group(1));
            hpaFeatureAttribute.setHpaAttributeValue(m.group(2));
            hpaFeatureAttribute.setUnit(m.group(3));
        }
        return hpaFeatureAttribute;
    }

    /**
     * GenerateHugePages, supported hpa features, All under the capability of
     * tosca.nodes.nfv.Vdu.Compute. The format is a map like: {"schema-version": "0",
     * "schema-location": "", "platform-id": "generic", "mandatory": true, "configuration-value": "2
     * MB"}
     *
     * @param capabilityAssignment represents the capability of node
     * 
     * @param flavorFeature represents all the features of specified flavor
     */
    private void generateHugePages(final CapabilityAssignment capabilityAssignment) {
        // add HugePages support
    }

    /**
     * ExtractInfoVduCp, supported hpa features, under the virtual_network_interface_requirements of
     * tosca.nodes.nfv.VduCp.
     *
     * @param lnodeVduCp the list of VduCp node
     * 
     * @param content to be change based on lnodeVduCp
     */
    public void extractInfoVduCp(final List<NodeTemplate> lnodeVduCp, final Content content) {
        // to add VDU cp Hpa feature extract
    }

}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.toscaparser.api.CapabilityAssignment;
import org.onap.sdc.toscaparser.api.CapabilityAssignments;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.RequirementAssignment;
import org.onap.sdc.toscaparser.api.RequirementAssignments;
import org.onap.sdc.toscaparser.api.elements.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extract concerned info from NodeTemplate, currently ONLY HPA Feature.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
public class ExtractFromNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractFromNode.class);

    private static final String CONFIGURATION_VALUE = "configurationValue";
    private static final String VDU_TYPE = "tosca.nodes.nfv.Vdu.Compute";
    private static final String VDU_CP_TYPE = "tosca.nodes.nfv.VduCp";
    private static final String VIRTUAL_MEM_SIZE_PATH = "virtual_memory#virtual_mem_size";
    private static final String NUM_VIRTUAL_CPU_PATH = "virtual_cpu#num_virtual_cpu";
    private static final String CPU_ARCHITECTURE_PATH = "virtual_cpu#cpu_architecture";
    private static final String MEMORY_PAGE_SIZE_PATH = "virtual_memory#vdu_mem_requirements#memoryPageSize";
    private static final String NETWORK_INTERFACE_TYPE_PATH =
            "virtual_network_interface_requirements#network_interface_requirements#interfaceType";
    private static final String NETWORK_PCI_PATH =
            "virtual_network_interface_requirements#nic_io_requirements#logical_node_requirements";
    private static final String BASIC_CAPABILITIES_HPA_FEATURE = "basicCapabilities";
    private static final String HUGE_PAGES_HPA_FEATURE = "hugePages";
    private static final Map<String, String> NETWORK_HPA_FEATURE_MAP =
            ImmutableMap.of("SR-IOV", "sriovNICNetwork", "PCI-Passthrough", "pciePassthrough");
    private static final Pattern PATTERN = Pattern.compile("(\\D*)(\\d+)(\\D*)");
    private ISdcCsarHelper sdcCsarHelper;
    final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();

    public void setSdcCsarHelper(final ISdcCsarHelper sdcCsarHelper) {
        this.sdcCsarHelper = sdcCsarHelper;
    }

    /**
     * ExtractInfo from VNF , each VNF may includes more than one VDUs and CPs return new generated PdpxPolicy if it has
     * got Hpa feature info or else return null.
     *
     * @param node the NodeTemplate
     *
     * @return the extracted info from input node
     *
     * @throws PolicyDecodingException if extract fails
     */
    public Content extractInfo(final NodeTemplate node) throws PolicyDecodingException {
        final Metadata metaData = sdcCsarHelper.getNodeTemplateMetadata(node);
        final Metadata metaDataOfService = sdcCsarHelper.getServiceMetadata();
        LOGGER.debug("the meta data of this nodetemplate = {}", metaData);
        final List<NodeTemplate> lnodeChild = sdcCsarHelper.getNodeTemplateChildren(node);
        LOGGER.debug("the size of lnodeChild = {}", lnodeChild.size());

        // Store all the VDUs under one VNF
        final List<NodeTemplate> lnodeVdu = new ArrayList<>();
        // Store all the Cps under one VNF
        final List<NodeTemplate> lnodeVduCp = new ArrayList<>();
        for (final NodeTemplate nodeChild : lnodeChild) {
            final String type = sdcCsarHelper.getTypeOfNodeTemplate(nodeChild);
            LOGGER.debug("the type of this nodeChild = {}", type);
            LOGGER.debug("the meta data of this nodeChild = {}", sdcCsarHelper.getNodeTemplateMetadata(nodeChild));
            if (type.equalsIgnoreCase(VDU_TYPE)) {
                lnodeVdu.add(nodeChild);
            } else if (type.equalsIgnoreCase(VDU_CP_TYPE)) {
                lnodeVduCp.add(nodeChild);
            }
        }
        LOGGER.debug("the size of vdu is = {}", lnodeVdu.size());
        LOGGER.debug("the size of cp is = {}", lnodeVduCp.size());

        final Content content = new Content();
        content.getResources().add(metaData.getValue("name"));
        content.getServices().add(metaDataOfService.getValue("name"));
        content.setIdentity(content.getPolicyType() + "_" + metaData.getValue("name"));
        extractInfoVdu(lnodeVdu, content);
        extractInfoVduCp(lnodeVduCp, content);
        if (content.getFlavorFeatures().isEmpty()) {
            return null;
        }
        return content;
    }


    /**
     * ExtractInfofromVdu, supported hpa features, All under the capability of tosca.nodes.nfv.Vdu.Compute.
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
            flavorDirective.setType("flavor_directives");
            flavorDirective.getAttributes().add(flavorAttribute);
            final FlavorFeature flavorFeature = new FlavorFeature();
            flavorFeature.setId(node.toString());
            LOGGER.debug("the name of node = {}", node);
            flavorFeature.getDirectives().add(flavorDirective);

            final CapabilityAssignments capabilityAssignments = sdcCsarHelper.getCapabilitiesOf(node);
            final CapabilityAssignment capabilityAssignment =
                    capabilityAssignments.getCapabilityByName("virtual_compute");
            if (capabilityAssignment != null) {
                generateBasicCapability(capabilityAssignment, flavorFeature);
                generateHugePages(capabilityAssignment, flavorFeature);
            }
            content.getFlavorFeatures().add(flavorFeature);
        }
    }

    /**
     * GenerateBasicCapability, supported hpa features, All under the capability of tosca.nodes.nfv.Vdu.Compute.
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
            LOGGER.debug("the virtualMemSize = {}", virtualMemSize);
            final HpaFeatureAttribute hpaFeatureAttribute =
                    generateHpaFeatureAttribute("virtualMemSize", virtualMemSize);
            final FlavorProperty flavorProperty = new FlavorProperty();
            flavorProperty.setHpaFeature(BASIC_CAPABILITIES_HPA_FEATURE);
            flavorProperty.getHpaFeatureAttributes().add(hpaFeatureAttribute);
            flavorFeature.getFlavorProperties().add(flavorProperty);
        }

        // the format is int like 2
        final String numVirtualCpu =
                sdcCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment, NUM_VIRTUAL_CPU_PATH);
        if (numVirtualCpu != null) {
            LOGGER.debug("the numVirtualCpu = {}", numVirtualCpu);
            final HpaFeatureAttribute hpaFeatureAttribute = generateHpaFeatureAttribute("numVirtualCpu", numVirtualCpu);
            final String cpuArchitecture =
                    sdcCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment, CPU_ARCHITECTURE_PATH);
            final FlavorProperty flavorProperty = new FlavorProperty();
            flavorProperty.setHpaFeature(BASIC_CAPABILITIES_HPA_FEATURE);
            if (cpuArchitecture != null) {
                flavorProperty.setArchitecture(cpuArchitecture);
            }
            flavorProperty.getHpaFeatureAttributes().add(hpaFeatureAttribute);
            flavorFeature.getFlavorProperties().add(flavorProperty);
        }
    }

    /**
     * GenerateHpaFeatureAttribute based on the value of featureValue. the format: "hpa-attribute-key": "pciVendorId",
     * "hpa-attribute-value": "1234", "operator": "=", "unit": "xxx".
     *
     * @param hpaAttributeKey get from the high layer tosca DM
     *
     * @param featureValue get from the high layer tosca DM
     *
     */
    private HpaFeatureAttribute generateHpaFeatureAttribute(final String hpaAttributeKey, final String featureValue) {
        // based on input featureValue, return back a suitable hpaFeatureAttribute
        final HpaFeatureAttribute hpaFeatureAttribute = new HpaFeatureAttribute();
        hpaFeatureAttribute.setHpaAttributeKey(hpaAttributeKey);
        final String modifiedValue = featureValue.replace(" ", "");
        final Matcher matcher = PATTERN.matcher(modifiedValue);
        if (matcher.find()) {
            final String matcher1 = matcher.group(1);
            final String matcher2 = matcher.group(2);
            final String matcher3 = matcher.group(3);
            LOGGER.debug("operator {} , value = {} , unit = {}", matcher1, matcher2, matcher3);
            if (matcher.group(1).length() == 0) {
                hpaFeatureAttribute.setOperator("=");
            } else {
                hpaFeatureAttribute.setOperator(matcher1);
            }
            hpaFeatureAttribute.setHpaAttributeValue(matcher2);
            hpaFeatureAttribute.setUnit(matcher3);
        }
        return hpaFeatureAttribute;
    }

    /**
     * GenerateHugePages, supported hpa features, All under the capability of tosca.nodes.nfv.Vdu.Compute. The format is
     * a map like: {"schemaVersion": "0", "schemaSelector": "", "hardwarePlatform": "generic", "mandatory": "true",
     * "configurationValue": "2 MB"}
     *
     * @param capabilityAssignment represents the capability of node
     *
     * @param flavorFeature represents all the features of specified flavor
     */
    private void generateHugePages(final CapabilityAssignment capabilityAssignment, final FlavorFeature flavorFeature) {
        final String memoryPageSize =
                sdcCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment, MEMORY_PAGE_SIZE_PATH);
        LOGGER.debug("the memoryPageSize = {}", memoryPageSize);
        if (memoryPageSize != null) {
            final Map<String, String> retMap =
                    gson.fromJson(memoryPageSize, new TypeToken<HashMap<String, String>>() {}.getType());
            LOGGER.debug("the retMap = {}", retMap);
            final String memoryPageSizeValue = retMap.get(CONFIGURATION_VALUE);
            final String mandatory = retMap.get("mandatory");
            if (memoryPageSizeValue == null) {
                return;
            }
            final HpaFeatureAttribute hpaFeatureAttribute =
                    generateHpaFeatureAttribute("memoryPageSize", memoryPageSizeValue);
            final FlavorProperty flavorProperty = new FlavorProperty();
            flavorProperty.setHpaFeature(HUGE_PAGES_HPA_FEATURE);
            if (mandatory != null) {
                flavorProperty.setMandatory(mandatory);
            }
            flavorProperty.getHpaFeatureAttributes().add(hpaFeatureAttribute);
            flavorFeature.getFlavorProperties().add(flavorProperty);
        }
    }

    /**
     * ExtractInfoVduCp, supported hpa features, under the virtual_network_interface_requirements of
     * tosca.nodes.nfv.VduCp.
     *
     * @param lnodeVduCp the list of VduCp node
     *
     * @param content to be change based on lnodeVduCp
     * @throws PolicyDecodingException if extract CP fails
     */
    public void extractInfoVduCp(final List<NodeTemplate> lnodeVduCp, final Content content)
            throws PolicyDecodingException {
        // each CP will binds to a VDU so need the vdu flavor map info.
        final Map<String, FlavorFeature> vduFlavorMap = new HashMap<>();
        for (final FlavorFeature flavorFeature : content.getFlavorFeatures()) {
            LOGGER.debug("the id = {}", flavorFeature.getId());
            vduFlavorMap.put(flavorFeature.getId(), flavorFeature);
        }
        parseNodeVduCp(lnodeVduCp, vduFlavorMap);
    }

    /**
     * Parse the VduCp list.
     *
     * @param lnodeVduCp the lnodeVduCp
     * @param vduFlavorMap the vduFlavorMap
     * @throws PolicyDecodingException if any error occurs
     */
    private void parseNodeVduCp(final List<NodeTemplate> lnodeVduCp, final Map<String, FlavorFeature> vduFlavorMap)
            throws PolicyDecodingException {
        for (final NodeTemplate node : lnodeVduCp) {
            final String interfaceType =
                    sdcCsarHelper.getNodeTemplatePropertyLeafValue(node, NETWORK_INTERFACE_TYPE_PATH);
            LOGGER.debug("the interfaceType = {}", interfaceType);
            Map<String, Object> retMap = new HashMap<>();
            if (interfaceType != null) {
                retMap = gson.fromJson(interfaceType, new TypeToken<HashMap<String, Object>>() {}.getType());
                LOGGER.debug("the retMap = {}", retMap);
            }

            String networkHpaFeature;
            if (retMap.containsKey(CONFIGURATION_VALUE)
                    && NETWORK_HPA_FEATURE_MAP.containsKey(retMap.get(CONFIGURATION_VALUE).toString())) {
                final String interfaceTypeValue = retMap.get(CONFIGURATION_VALUE).toString();
                networkHpaFeature = NETWORK_HPA_FEATURE_MAP.get(interfaceTypeValue);
                LOGGER.debug(" the networkHpaFeature is = {}", networkHpaFeature);
            } else {
                LOGGER.debug(" no networkHpaFeature defined in interfaceType");
                continue;
            }

            final RequirementAssignments requriements =
                    sdcCsarHelper.getRequirementsOf(node).getRequirementsByName("virtual_binding");
            for (final RequirementAssignment requriement : requriements.getAll()) {
                final String nodeTemplateName = requriement.getNodeTemplateName();
                LOGGER.debug("getNodeTemplateName = {}", nodeTemplateName);
                if (nodeTemplateName == null) {
                    continue;
                }
                if (!vduFlavorMap.containsKey(nodeTemplateName)) {
                    throw new PolicyDecodingException("vdu Flavor Map should contains the key " + nodeTemplateName);
                }
                generateNetworkFeature(networkHpaFeature, node, vduFlavorMap.get(nodeTemplateName));
            }
        }
    }

    /**
     * GenerateNetworkFeature, all pci feature are grouped into FlavorFeature together. The format is a map like:
     * {"schemaVersion": "0", "schemaSelector": "", "hardwarePlatform": "generic", "mandatory": "true",
     * "configurationValue": "2 MB"}
     *
     * @param networkHpaFeature represents the specified Hpa feature
     * @param node represents the CP Node
     * @param flavorFeature represents all the features of specified flavor
     */
    private void generateNetworkFeature(final String networkHpaFeature, final NodeTemplate node,
            final FlavorFeature flavorFeature) {
        final FlavorProperty flavorProperty = new FlavorProperty();
        flavorProperty.setHpaFeature(networkHpaFeature);
        final String[] pciKeys = { "pciVendorId", "pciDeviceId", "pciNumDevices", "physicalNetwork" };
        for (final String pciKey : pciKeys) {
            LOGGER.debug("the pciKey = {}", pciKey);
            final String pciKeyPath = NETWORK_PCI_PATH + "#" + pciKey;
            final String pciValue = sdcCsarHelper.getNodeTemplatePropertyLeafValue(node, pciKeyPath);
            if (pciValue != null) {
                LOGGER.debug("the pciValue = {}", pciValue);
                final Map<String, String> retMap =
                        gson.fromJson(pciValue, new TypeToken<HashMap<String, String>>() {}.getType());
                final String pciConfigValue = retMap.get(CONFIGURATION_VALUE);
                if (pciConfigValue == null) {
                    return;
                }
                final HpaFeatureAttribute hpaFeatureAttribute = generateHpaFeatureAttribute(pciKey, pciConfigValue);
                flavorProperty.getHpaFeatureAttributes().add(hpaFeatureAttribute);
            }
        }
        flavorFeature.getFlavorProperties().add(flavorProperty);
    }
}

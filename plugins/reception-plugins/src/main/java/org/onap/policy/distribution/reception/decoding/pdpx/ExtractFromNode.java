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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.FileWriter;
import java.io.Writer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;

import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.impl.SdcCsarHelperImpl;
import org.onap.sdc.tosca.parser.impl.SdcPropertyNames;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;

import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.CapabilityAssignment;
import org.onap.sdc.toscaparser.api.CapabilityAssignments;
import org.onap.sdc.toscaparser.api.elements.Metadata;

/**
 * Extract concerned info from NodeTemplate, currently ONLY HPA Feature
 */
public class ExtractFromNode {

    private static final Logger LOGGER = FlexLogger.getLogger(ExtractFromNode.class);
    private static final String CONTENT_RESOURCES = "name";
    private static final String VDU_TYPE = "tosca.nodes.nfv.Vdu.Compute";
    private static final String VDU_CP_TYPE = "tosca.nodes.nfv.VduCp";
    private static final String VIRTUAL_MEM_SIZE_PATH = "virtual_memory#virtual_mem_size";
    private static final String NUM_VIRTUAL_CPU_PATH = "virtual_cpu#num_virtual_cpu";
    private static final String CPU_ARCHITECTURE_PATH = "virtual_cpu#cpu_architecture";
    private static final String NUMBER_OF_PAGES_PATH = "virtual_memory#vdu_memory_requirements#numberOfPages";
    private static final String BASIC_CAPABILITIES = "BasicCapabilities";
    

    ISdcCsarHelper sdcCsarHelper;
    final Gson gson = new GsonBuilder()
                            .serializeNulls()
                            .setPrettyPrinting()
                            .disableHtmlEscaping()
                            .create();


    public void setSdcCsarHelper(ISdcCsarHelper sdcCsarHelper) {
        this.sdcCsarHelper = sdcCsarHelper;
    }

    /*
     * ExtractInfo from VNF
     *      each VNF may includes more than one VDUs and CPs
     *      return new generated PdpxPolicy if it has got Hpa feature info or else return null
     */
    public PdpxPolicy extractInfo(NodeTemplate node) throws PolicyDecodingException {
        PdpxPolicy pdpxPolicy = new PdpxPolicy();
        Content content = pdpxPolicy.getContent();
        content.getPolicyScope().add("a");
        content.getPolicyScope().add("b");

        String outputFile = sdcCsarHelper.getNodeTemplateMetadata(node).getValue("name");
        outputFile += ".json";
        LOGGER.debug("the meta data of this nodetemplate = " + sdcCsarHelper.getNodeTemplateMetadata(node));
        LOGGER.debug("outputFile = " + outputFile);

        List<NodeTemplate> lnodeChild = sdcCsarHelper.getNodeTemplateChildren(node);
        LOGGER.debug("the size of lnodeChild = " + lnodeChild.size());

        //Store all the VDUs under one VNF
        List<NodeTemplate> lnodeVdu = new ArrayList<>();
        //Store all the Cps under one VNF
        List<NodeTemplate> lnodeVduCp = new ArrayList<>();
        for ( NodeTemplate nodeChild : lnodeChild) {
            String type = sdcCsarHelper.getTypeOfNodeTemplate(nodeChild);
            LOGGER.debug("the type of this nodeChild = " + type);
            LOGGER.debug("the meta data of this nodetemplate = " + sdcCsarHelper.getNodeTemplateMetadata(nodeChild));
            if( type.equalsIgnoreCase(VDU_TYPE)){
                lnodeVdu.add(nodeChild);
            } else if( type.equalsIgnoreCase(VDU_CP_TYPE)){
                lnodeVduCp.add(nodeChild);
            }
        }

        LOGGER.debug("the size of vdu is =" + lnodeVdu.size());
        LOGGER.debug("the size of cp is =" + lnodeVduCp.size());

        extractInfoVdu(lnodeVdu, content);
        extractInfoVduCp(lnodeVduCp, content);
        
        if(content.getFlavorFeatures().size() != 0){
            try(Writer writer = new FileWriter(outputFile)) {
                gson.toJson(pdpxPolicy, writer);

            }catch(Exception e) {
                LOGGER.error("can't write generated policies to file " + e);
                throw new PolicyDecodingException ("Exception caught when writing generated policies to file ", e);
            }
            return pdpxPolicy;
        }else {
            return null;   
        }
    }


    /*
     * extractInfofromVdu, supported hpa features, All under the capability of tosca.nodes.nfv.Vdu.Compute, the path is
     *      BasicCapabilities: 
     *          numVirtualCpu  (virtual_compute#properties#virtual_cpu#num_virtual_cpu)
     *          virtualMemSize (virtual_compute#properties#virtual_memory#virtual_mem_size)
     *
     */
    public void extractInfoVdu(final List<NodeTemplate> lnodeVdu, Content content) {
        //each VDU <=> FlavorFeature
        for ( NodeTemplate node : lnodeVdu) {
 //           String id = node.getProperties().get("name");
            String id = sdcCsarHelper.getNodeTemplatePropertyLeafValue(node, "name");
            FlavorFeature flavorFeature = new FlavorFeature();
            flavorFeature.setId(id);
            Attribute flavorAttribute = new Attribute();
            flavorAttribute.setAttribute_name("flavor_name");
            flavorAttribute.setAttribute_value("");
            Directive flavorDirective = new Directive();
            flavorDirective.setType("flavor_directive");
            flavorDirective.getAttributes().add(flavorAttribute);
            flavorFeature.getDirectives().add(flavorDirective);
            
            CapabilityAssignments capabilityAssignments = sdcCsarHelper.getCapabilitiesOf(node);
            CapabilityAssignment capabilityAssignment = capabilityAssignments.getCapabilityByName("virtual_compute");
            if(capabilityAssignment != null){
                generateBasicCapability(capabilityAssignment, flavorFeature);
                generateHugePages(capabilityAssignment, flavorFeature);
            }

            content.getFlavorFeatures().add(flavorFeature);  
        }
    }

    /*
     * generateBasicCapability, supported hpa features, All under the capability of tosca.nodes.nfv.Vdu.Compute, the path is
     *      BasicCapabilities: 
     *          numVirtualCpu  (virtual_compute#properties#virtual_cpu#num_virtual_cpu)
     *          virtualMemSize (virtual_compute#properties#virtual_memory#virtual_mem_size)
     */
    private void generateBasicCapability(final CapabilityAssignment capabilityAssignment, FlavorFeature flavorFeature){
            //the format is xxx MB/GB like 4096 MB
            String virtualMemSize = sdcCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment,
                VIRTUAL_MEM_SIZE_PATH);
            if(virtualMemSize != null){
                LOGGER.debug("the virtualMemSize = " + virtualMemSize);
                HpaFeatureAttribute hpaFeatureAttribute = generateHpaFeatureAttribute("virtualMemSize", virtualMemSize);
                FlavorProperty flavorProperty = new FlavorProperty();
                flavorProperty.setHpa_feature(BASIC_CAPABILITIES);
                flavorProperty.getHpa_feature_attributes().add(hpaFeatureAttribute);
                flavorFeature.getFlavorProperties().add(flavorProperty);
            }
            
            //the format is int like 2 
            String numVirtualCpu = sdcCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment,
                NUM_VIRTUAL_CPU_PATH);
            if(numVirtualCpu != null) {
                LOGGER.debug("the numVirtualCpu = " + numVirtualCpu);
                HpaFeatureAttribute hpaFeatureAttribute = generateHpaFeatureAttribute("numVirtualCpu", numVirtualCpu);
                String cpu_architecture = sdcCsarHelper.getCapabilityPropertyLeafValue
                        (capabilityAssignment,CPU_ARCHITECTURE_PATH);
                FlavorProperty flavorProperty = new FlavorProperty();
                flavorProperty.setHpa_feature(BASIC_CAPABILITIES);
                if (cpu_architecture != null) {
                    flavorProperty.setArchitecture(cpu_architecture);
                }
                flavorProperty.getHpa_feature_attributes().add(hpaFeatureAttribute);
                flavorFeature.getFlavorProperties().add(flavorProperty);
            }
    }

    /*
     * generateHpaFeatureAttribute based on the value of featureValue
     * the format is "hpa-attribute-key": "pciVendorId", "hpa-attribute-value": "1234", "operator": "=", "unit": "xxx"
     */
    private HpaFeatureAttribute generateHpaFeatureAttribute(final String hpaAttributeKey, final String featureValue){

        HpaFeatureAttribute hpaFeatureAttribute = new HpaFeatureAttribute();
        hpaFeatureAttribute.setHpa_attribute_key(hpaAttributeKey);
        String tmp = featureValue.replace(" ","");
        String pattern = "(\\D*)(\\d+)(\\D*)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(tmp);
        if(m.find()){
            LOGGER.debug("operator = " + m.group(1));
            LOGGER.debug("value = " + m.group(2));
            LOGGER.debug("unit = " + m.group(3));
            hpaFeatureAttribute.setOperator(m.group(1));
            hpaFeatureAttribute.setHpa_attribute_value(m.group(2));
            hpaFeatureAttribute.setUnit(m.group(3));
        }
        return hpaFeatureAttribute;
    }

    /*
     * generateHugePages, supported hpa features, All under the capability of tosca.nodes.nfv.Vdu.Compute, the path is
     *      hugePages:      
     *          memoryPageSize  (virtual_compute#properties#virtual_memory#vdu_memory_requirements#memoryPageSize)
     */
    private void generateHugePages(final CapabilityAssignment capabilityAssignment, FlavorFeature flavorFeature){
            //the format is a map like: {"schema-version": "0", "schema-location": "", "platform-id": "generic", 
            // "mandatory": true, "configuration-value": "2 MB"}
            String numberOfPages = sdcCsarHelper.getCapabilityPropertyLeafValue(capabilityAssignment,
                NUMBER_OF_PAGES_PATH);
            if(numberOfPages != null){
                LOGGER.debug("the virtualMemSize = " + numberOfPages);
            //TODO add HugePages support
            }
    }

    /* 
     * extractInfoVduCp, supposted hpa features, under the virtual_network_interface_requirements of
     * tosca.nodes.nfv.VduCp. 
     *      pciePassthrough or SriovNICNetwork depends on the 
     *          pciCount (virtual_network_interface_requirements#nic_io_requirements#logical_node_requirements)
     *          pciVendorId
     *          pciDeviceId
     *
     */
    @SuppressWarnings("unchecked")
    public void extractInfoVduCp(final List<NodeTemplate> lnodeVduCp, Content content) {
        for ( NodeTemplate node : lnodeVduCp) {
        //TODO to add VDU cp Hpa feature extract
        }
    }

}

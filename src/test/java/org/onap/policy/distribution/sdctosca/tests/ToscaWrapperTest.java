/*-
 *  ============LICENSE_START=======================================================
 *  ONAP Policy SDC Service Distribution
 *  ================================================================================
 *  Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ============LICENSE_END=========================================================
 */

package org.onap.policy.distribution.sdctosca.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.onap.policy.distribution.sdctosca.ToscaWrapper;
//import org.onap.sdc.toscaparser.api.elements.*;
//import org.onap.sdc.toscaparser.api.parameters.*;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.tosca.parser.impl.SdcTypes;

import org.onap.sdc.toscaparser.api.CapabilityAssignments;
import org.onap.sdc.toscaparser.api.CapabilityAssignment;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.Property;
import org.onap.sdc.toscaparser.api.RequirementAssignment;
import org.onap.sdc.toscaparser.api.RequirementAssignments;
import org.onap.sdc.toscaparser.api.elements.Metadata;
import org.onap.sdc.toscaparser.api.parameters.Input;


public class ToscaWrapperTest{

    @Test
    public void updateResourceStructureTest() {

        ToscaWrapper toscaWrapper = new ToscaWrapper();
        toscaWrapper.updateResourceStructure("service-sunny-flow.csar");
        ISdcCsarHelper helper = toscaWrapper.getSdcCsarHelper(); 

        Metadata serviceMetadata =   helper.getServiceMetadata();
        //aassertNotNull(serviceMetadata);
        System.out.println("the information of Metadata =" + serviceMetadata);

        List<Input> serviceInputs = helper.getServiceInputs();
        //assertEquals("1",serviceInputs.size());
        System.out.println("the size of Input=" + serviceInputs.size());
        for (Input input : serviceInputs) {
            System.out.println("name=" + input.getName() + ",type=" + input.getType() 
                + "requried=" + input.isRequired() + ",getDescription=" + input.getDescription());
        }
 
        //get service NodeTemplate
        //getServiceNodeTemplateBy[NodeName|SdcType|Type]
        //getServiceNodeTemplates
        //    List<NodeTemplate> typeList = helper.getServiceNodeTemplatesByType("org.openecomp.resource.vf.Fdnt");
        //    NodeTemplate nameNode = helper.getServiceNodeTemplateByNodeName();
        //    List<NodeTemplate> sdctypeList = getServiceNodeTemplateBySdcType();
        List<NodeTemplate> node1 = helper.getServiceNodeTemplatesByType("org.openecomp.resource.vf.ContrailRoute");
        System.out.println("size=" + node1.size() + ",the meta name=" + node1.get(0).getMetaData().getValue("name"));
    
        List<NodeTemplate> node2 = helper.getServiceNodeTemplateBySdcType(SdcTypes.VF);
        System.out.println("size=" + node2.size() + ",the meta name=" + node2.get(0).getMetaData().getValue("name"));
        
        NodeTemplate node3 = helper.getServiceNodeTemplateByNodeName("Allotted resource Contrail Route 1");
        System.out.println("name=" + node3.getName() + ",the meta name=" + node3.getMetaData().getValue("name"));
       
        List<NodeTemplate> allList = helper.getServiceNodeTemplates();
        System.out.println("the size of allList=" + allList.size());
        for (NodeTemplate node : allList) {
            System.out.println("name=" + node.getName()); 
            System.out.println("MedtaData=" + node.getMetaData()); 
            System.out.println("type=" + node.getType()); 
            ArrayList<Property> properties = node.getPropertiesObjects();
            System.out.println("size of properties=" + node.getPropertiesObjects().size()); 
            for (Property property: properties) {
                System.out.println("property=" + property.getName());
            }
        }
        
        NodeTemplate ndexVl = helper.getServiceNodeTemplateByNodeName("exVL");
        String networkrole = helper.getNodeTemplatePropertyLeafValue(ndexVl,"network_role");
        String useipv4 = helper.getNodeTemplatePropertyLeafValue(ndexVl,
                "network_assignments#ipv4_subnet_default_assignment#use_ipv4");
        System.out.println("the network_role=" + networkrole + "the use_ipv4=" + useipv4);
       
    
        List<NodeTemplate> vfs = helper.getServiceVfList();
        CapabilityAssignments capabilityAssignments = helper.getCapabilitiesOf(vfs.get(0));
        System.out.println("the content =" + capabilityAssignments
                    .getCapabilityByName("DNT_FW_RHRG.binding_DNT_FW_INT_DNS_TRUSTED_RVMI"));
    
    
        //each service includes several  NodeTemplate. 
        //each NodeTemplate includes several resources, each resource is a NodeTemplate
        List<NodeTemplate> cps = helper.getNodeTemplateBySdcType(vfs.get(0), SdcTypes.CP);
        RequirementAssignments requirementAssignments = helper.getRequirementsOf(cps.get(0));
        RequirementAssignment requirementAssignment = requirementAssignments.getRequirementsByName("binding")
                    .getAll().get(0);
        System.out.println("the content=" + requirementAssignment);
        System.out.println("the name of requirement=" + requirementAssignment.getName() 
                    + ",the nodeName=" + requirementAssignment.getNodeTemplateName());
        System.out.println("the name of capabilityName=" + requirementAssignment.getCapabilityName() 
                    + ",the relationship=" + requirementAssignment.getRelationship());
      
        return ;
    }

    public ToscaWrapperTest() {}
  
}

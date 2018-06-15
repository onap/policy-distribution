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

package org.onap.policy.distribution.sdctosca;

import java.io.File;
//import java.util.;

//import org.onap.sdc.toscaparser.api.*;
//import org.onap.sdc.toscaparser.api.elements.*;
//import org.onap.sdc.toscaparser.api.parameters.*;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
//import org.onap.sdc.tosca.parser.exceptions.SdcToscaParserException;

public class ToscaWrapper
{
    static SdcToscaParserFactory factory;
    static ISdcCsarHelper helper;

    public String  updateResourceStructure(String ArtifactName)
    {
    try {
        factory = SdcToscaParserFactory.getInstance();
        System.setProperty("policy.config.path", "src/test/resources/");
        File spoolFile = new File(System.getProperty("policy.config.path") + "ASDC/" + ArtifactName);
        System.out.println("PATH IS " + spoolFile.getAbsolutePath());
        helper = factory.getSdcCsarHelper(spoolFile.getAbsolutePath());

    } catch(Exception e){
        System.out.println("System out " + e.getMessage());
    }
    
    return "Hello Maven\n";
    }

    public ToscaWrapper(){
    }

    public ISdcCsarHelper getSdcCsarHelper() {
        return helper;
    }

    public void setSdcCsarHelper(ISdcCsarHelper sdcCsarHelper) {
      this.helper = sdcCsarHelper;
    }
}

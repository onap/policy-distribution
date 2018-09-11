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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.util.Collection;
import java.util.Collections;


import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;

import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.impl.SdcCsarHelperImpl;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;

import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.onap.sdc.toscaparser.api.elements.Metadata;

/**
 * Decodes PDP-X policies from a TOSCA file.
 */
public class PolicyDecoderCsarPdpx implements PolicyDecoder<Csar, PdpxPolicy> {

    private static final Logger LOGGER = FlexLogger.getLogger(PolicyDecoderCsarPdpx.class);

    @Override
    public Collection<PdpxPolicy> decode(Csar csar) throws PolicyDecodingException {
        // logic for generating the policies from the CSAR. 
        List<PdpxPolicy> lPdpxPolicy = new ArrayList<>();
        ISdcCsarHelper sdcCsarHelper = parseCsar(csar);
        List<NodeTemplate> lnodeVf = sdcCsarHelper.getServiceVfList();
        LOGGER.debug("the size of Vf = " + lnodeVf.size());
        ExtractFromNode extractFromNode = new ExtractFromNode();
        extractFromNode.setSdcCsarHelper(sdcCsarHelper);
        for ( NodeTemplate node : lnodeVf) {
            PdpxPolicy ret = extractFromNode.extractInfo(node);
            if (ret != null) {
                lPdpxPolicy.add(ret);
            }
        }
        return lPdpxPolicy;
    }

    @Override
    public boolean canHandle(PolicyInput policyInput) {
        return policyInput.getClass().isAssignableFrom(Csar.class);
    }

    /** 
     * Parse the input Csar by SDC tosca tool.
     *
     * @param csar represents the service TOSCA Csar
     * @return the object to represents the content of input csar
     * @throws PolicyDecodingException if parse fails
     */
    public ISdcCsarHelper parseCsar(Csar csar)  throws PolicyDecodingException {
        ISdcCsarHelper sdcCsarHelper ;
        try {

            SdcToscaParserFactory factory = SdcToscaParserFactory.getInstance();//Autoclosable

            LOGGER.debug("tosca File Path = " + csar.getCsarPath());

            File spoolFile = new File(csar.getCsarPath());

            sdcCsarHelper = factory.getSdcCsarHelper(spoolFile.getAbsolutePath());

        } catch (Exception e) {
            LOGGER.error("Exception got in parseTosca",e);
            throw new PolicyDecodingException ("Exception caught when passing the csar file to the parser ", e);
        }
        return sdcCsarHelper;
    }

}

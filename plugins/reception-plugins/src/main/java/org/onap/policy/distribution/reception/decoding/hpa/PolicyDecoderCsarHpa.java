/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2020 AT&T Inc.
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

package org.onap.policy.distribution.reception.decoding.hpa;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;
import org.onap.policy.models.tosca.authorative.concepts.ToscaTopologyTemplate;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decodes PDP-X policies from a CSAR file and creates ToscaServiceTemplate for Policy Lifecycle API's.
 */
public class PolicyDecoderCsarHpa implements PolicyDecoder<Csar, ToscaServiceTemplate> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyDecoderCsarHpa.class);
    //
    // This is initialized in configure() but then never used here
    //
    @SuppressWarnings("unused")
    private PolicyDecoderCsarHpaParameters decoderParameters;

    public static final String TOSCA_POLICY_SCOPE = "scope";
    public static final String TOSCA_POLICY_SERVICES = "services";
    public static final String TOSCA_POLICY_RESOURCES = "resources";
    public static final String TOSCA_POLICY_IDENTITY = "identity";
    public static final String TOSCA_POLICY_FLAVORFEATURES = "flavorfeatures";

    public static final String TOSCA_POLICY_HPA_OOF = "Optimization";

    @Override
    public Collection<ToscaServiceTemplate> decode(final Csar csar) throws PolicyDecodingException {
        final List<ToscaServiceTemplate> entities = new ArrayList<>();
        final ISdcCsarHelper sdcCsarHelper = parseCsar(csar);
        final List<NodeTemplate> lnodeVf = sdcCsarHelper.getServiceVfList();
        LOGGER.debug("the size of Vf = {}", lnodeVf.size());
        final ExtractFromNode extractFromNode = new ExtractFromNode();
        extractFromNode.setSdcCsarHelper(sdcCsarHelper);
        final String serviceName = sdcCsarHelper.getServiceMetadata().getValue("name");
        LOGGER.debug("the name of the service = {}", serviceName);
        for (final NodeTemplate node : lnodeVf) {
            final Content content = extractFromNode.extractInfo(node);
            if (content != null) {
                final ToscaServiceTemplate entity = new ToscaServiceTemplate();
                Map<String, Object> props = new LinkedHashMap<>();
                props.put(TOSCA_POLICY_SCOPE, content.getScope());
                props.put(TOSCA_POLICY_SERVICES, content.getServices());
                props.put(TOSCA_POLICY_RESOURCES, content.getResources());
                props.put(TOSCA_POLICY_IDENTITY, content.getIdentity());
                props.put(TOSCA_POLICY_FLAVORFEATURES, content.getFlavorFeatures());
                ToscaPolicy policy = new ToscaPolicy();
                policy.setProperties(props);
                Map<String, ToscaPolicy> map = new LinkedHashMap<>();
                String type = content.getPolicyType();
                map.put(type, policy);
                List<Map<String, ToscaPolicy>> policies = new ArrayList<>();
                policies.add(map);
                ToscaTopologyTemplate topologyTemplate = new ToscaTopologyTemplate();
                topologyTemplate.setPolicies(policies);
                entity.setToscaTopologyTemplate(topologyTemplate);
                entities.add(entity);
            }
        }
        return entities;
    }

    @Override
    public boolean canHandle(final PolicyInput policyInput) {
        return Csar.class.isAssignableFrom(policyInput.getClass());
    }

    /**
     * Parse the input Csar using SDC TOSCA parser.
     *
     * @param csar represents the service TOSCA Csar
     * @return the object to represents the content of input csar
     * @throws PolicyDecodingException if parse fails
     */
    public ISdcCsarHelper parseCsar(final Csar csar) throws PolicyDecodingException {
        ISdcCsarHelper sdcCsarHelper;
        try {
            final SdcToscaParserFactory factory = SdcToscaParserFactory.getInstance();
            LOGGER.debug("Csar File Path = {}", csar.getCsarPath());
            final File csarFile = new File(csar.getCsarPath());
            sdcCsarHelper = factory.getSdcCsarHelper(csarFile.getAbsolutePath());
        } catch (final Exception exp) {
            throw new PolicyDecodingException("Failed passing the csar file", exp);
        }
        return sdcCsarHelper;
    }

    @Override
    public void configure(final String parameterGroupName) {
        decoderParameters = ParameterService.get(parameterGroupName);
    }
}

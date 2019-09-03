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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntityKey;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyType;
import org.onap.policy.models.tosca.authorative.concepts.ToscaProperty;
import org.onap.sdc.tosca.parser.api.ISdcCsarHelper;
import org.onap.sdc.tosca.parser.impl.SdcToscaParserFactory;
import org.onap.sdc.toscaparser.api.NodeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decodes PDP-X policies from a CSAR file.
 */
public class PolicyDecoderCsarPdpx implements PolicyDecoder<Csar, ToscaServiceTemplate> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyDecoderCsarPdpx.class);
    private final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    private PolicyDecoderCsarPdpxConfigurationParameterGroup decoderParameters;

    public static final String TOSCA_POLICY_TYPE = "type";
    public static final String TOSCA_POLICY_SERVICE = "service";
    public static final String TOSCA_POLICY_ONAP_NAME = "onapName";
    public static final String TOSCA_POLICY_NAME = "policyName";
    public static final String TOSCA_POLICY_DESCRIPTION = "description";
    public static final String TOSCA_POLICY_TEMPLATE_VERSION = "templateVersion";
    public static final String TOSCA_POLICY_VERSION = "version";
    public static final String TOSCA_POLICY_PRIORITY = "priority";
    public static final String TOSCA_POLICY_RISK_LEVEL = "riskLevel";
    public static final String TOSCA_POLICY_RISK_TYPE = "riskType";
    public static final String TOSCA_POLICY_GUARD = "guard";
    public static final String TOSCA_POLICY_CONTENT = "content";

    @Override
    public Collection<ToscaServiceTemplate> decode(final Csar csar) throws PolicyDecodingException {
        final List<ToscaServiceTemplate> policies = new ArrayList<>();
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
                final ToscaServiceTemplate policy = new ToscaServiceTemplate();
                List<Map<String, ToscaPolicyType>>  policyTypes = new ArrayList<>();
                Map<String, ToscaPolicyType> map = new LinkedHashMap<>();
                ToscaPolicyType type = new ToscaPolicyType();
                Map<String, ToscaProperty> props = new LinkedHashMap<>();

                ToscaProperty policyName = new ToscaProperty();
                policyName.setDefaultValue(decoderParameters.getPolicyNamePrefix() + "." + content.getIdentity());
                props.put(TOSCA_POLICY_NAME, policyName);

                ToscaProperty onapName = new ToscaProperty();
                onapName.setDefaultValue(decoderParameters.getOnapName());
                props.put(TOSCA_POLICY_ONAP_NAME, onapName);

                ToscaProperty service = new ToscaProperty();
                service.setDefaultValue("hpaPolicy");
                props.put(TOSCA_POLICY_SERVICE, service);

                ToscaProperty description = new ToscaProperty();
                description.setDefaultValue("OOF Policy");
                props.put(TOSCA_POLICY_DESCRIPTION, description);

                ToscaProperty templateVersion = new ToscaProperty();
                templateVersion.setDefaultValue("OpenSource.version.1");
                props.put(TOSCA_POLICY_TEMPLATE_VERSION, templateVersion);

                ToscaProperty version = new ToscaProperty();
                version.setDefaultValue(decoderParameters.getVersion());
                props.put(TOSCA_POLICY_VERSION, version);

                ToscaProperty priority = new ToscaProperty();
                priority.setDefaultValue(decoderParameters.getPriority());
                props.put(TOSCA_POLICY_PRIORITY, priority);

                ToscaProperty riskLevel = new ToscaProperty();
                riskLevel.setDefaultValue(decoderParameters.getRiskLevel());
                props.put(TOSCA_POLICY_RISK_LEVEL, riskLevel);

                ToscaProperty riskType = new ToscaProperty();
                riskType.setDefaultValue(decoderParameters.getRiskType());
                props.put(TOSCA_POLICY_RISK_TYPE, riskType);

                ToscaProperty guard = new ToscaProperty();
                guard.setDefaultValue("False");
                props.put(TOSCA_POLICY_GUARD, guard);

                ToscaProperty c = new ToscaProperty();
                content.getGeography().add("US"); /* hardcoded "US" here due to the absence of geography in csar*/
                content.getServices().add(serviceName);
                c.setDefaultValue(gson.toJson(content));
                props.put(TOSCA_POLICY_CONTENT, c);
                type.setProperties(props);
                map.put(TOSCA_POLICY_TYPE, type);
                policyTypes.add(map);
                policy.setPolicyTypes(policyTypes);
                policies.add(policy);
            }
        }
        return policies;
    }

    @Override
    public boolean canHandle(final PolicyInput policyInput) {
        return policyInput.getClass().isAssignableFrom(Csar.class);
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
            final String message = "Failed passing the csar file";
            LOGGER.error(message, exp);
            throw new PolicyDecodingException(message, exp);
        }
        return sdcCsarHelper;
    }

    @Override
    public void configure(final String parameterGroupName) {
        decoderParameters = ParameterService.get(parameterGroupName);
    }
}

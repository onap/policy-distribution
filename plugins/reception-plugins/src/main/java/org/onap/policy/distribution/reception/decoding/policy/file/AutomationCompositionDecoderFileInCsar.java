/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021-2022 Nordix Foundation.
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

package org.onap.policy.distribution.reception.decoding.policy.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.util.ReceptionUtil;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;

/**
 * This class extracts acm information from a CSAR file.
 *
 * @author Sirisha Manchikanti (sirisha.manchikanti@est.tech)
 */
public class AutomationCompositionDecoderFileInCsar implements PolicyDecoder<Csar, ToscaEntity> {

    private AutomationCompositionDecoderFileInCsarParameterGroup decoderParameters;
    private static final String NODE_TYPES = "nodes.yml";
    private static final String DATA_TYPES = "data.yml";

    /**
     * {@inheritDoc}.
     */
    @Override
    public void configure(final String parameterGroupName) {
        decoderParameters = ParameterService.get(parameterGroupName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean canHandle(final PolicyInput policyInput) {
        return policyInput.getClass().isAssignableFrom(Csar.class);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Collection<ToscaEntity> decode(final Csar csar) throws PolicyDecodingException {
        final Collection<ToscaEntity> automationCompositionList = new ArrayList<>();

        try {
            Map<String, ToscaServiceTemplate> templates = new HashMap<>();
            ReceptionUtil.unzip(csar.getCsarFilePath(), templates,
                decoderParameters.getAutomationCompositionType(), NODE_TYPES, DATA_TYPES);

            var node = templates.get(NODE_TYPES);
            var data = templates.get(DATA_TYPES);

            templates.forEach((entry, t) -> {
                if (entry.contains(decoderParameters.getAutomationCompositionType())
                    && t.getToscaTopologyTemplate() != null) {
                    t.setNodeTypes(node != null ? node.getNodeTypes() : null);
                    t.setDataTypes(data != null ? data.getDataTypes() : null);

                    automationCompositionList.add(t);
                }
            });

        } catch (final CoderException exp) {
            throw new PolicyDecodingException("Failed decoding the acm", exp);
        }

        return automationCompositionList;
    }
}

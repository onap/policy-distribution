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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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

        try (var zipFile = new ZipFile(csar.getCsarFilePath())) {
            final List<? extends ZipEntry> entries = zipFile.stream()
                .filter(entry -> entry.getName().contains(decoderParameters.getAutomationCompositionType()))
                .collect(Collectors.toList());

            for (ZipEntry entry : entries) {
                ReceptionUtil.validateZipEntry(entry.getName(), csar.getCsarFilePath(), entry.getSize());
                final ToscaServiceTemplate automationComposition = ReceptionUtil.decodeFile(zipFile, entry);

                if (null != automationComposition.getToscaTopologyTemplate()) {
                    validateTypes(zipFile, NODE_TYPES)
                        .ifPresent(node -> automationComposition.setNodeTypes(node.getNodeTypes()));

                    validateTypes(zipFile, DATA_TYPES)
                        .ifPresent(data -> automationComposition.setDataTypes(data.getDataTypes()));

                    automationCompositionList.add(automationComposition);
                }
            }
        } catch (final IOException | CoderException exp) {
            throw new PolicyDecodingException("Failed decoding the acm", exp);
        }

        return automationCompositionList;
    }

    /**
     * Decode and validate if node or data type is available withing ACM csar file.
     *
     * @param zipFile full csar file
     * @return tosca template with parsed node/data type
     * @throws CoderException if file can't be parsed
     */
    private Optional<ToscaServiceTemplate> validateTypes(final ZipFile zipFile, String type)
        throws CoderException {

        try {
            ToscaServiceTemplate template = null;
            final Optional<? extends ZipEntry> file = zipFile.stream()
                .filter(entry -> entry.getName().contains(type)).findFirst();

            if (file.isPresent()) {
                template = ReceptionUtil.decodeFile(zipFile, file.get());
            }
            return Optional.ofNullable(template);
        } catch (final IOException | CoderException exp) {
            throw new CoderException("Couldn't decode " + type + " type", exp);
        }
    }
}

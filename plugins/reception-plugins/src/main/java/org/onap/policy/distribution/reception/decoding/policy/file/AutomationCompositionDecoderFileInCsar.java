/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Nordix Foundation.
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
import java.util.Enumeration;
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
        ToscaServiceTemplate nodeTypes = null;
        ToscaServiceTemplate dataTypes = null;

        try (var zipFile = new ZipFile(csar.getCsarFilePath())) {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                //
                // Sonar will flag this as a Security Hotspot
                // "Expanding archive files is security-sensitive"
                // isZipEntryValid ensures the file being read exists in the archive
                //
                final ZipEntry entry = entries.nextElement(); // NOSONAR
                final String entryName = entry.getName();

                // Store node_types
                if (entryName.contains(NODE_TYPES)) {
                    nodeTypes = ReceptionUtil.decodeFile(zipFile, entry);
                }

                // Store data_types
                if (entryName.contains(DATA_TYPES)) {
                    dataTypes = ReceptionUtil.decodeFile(zipFile, entry);
                }

                if (entryName.contains(decoderParameters.getAutomationCompositionType())) {
                    ReceptionUtil.validateZipEntry(entryName, csar.getCsarFilePath(), entry.getSize());
                    final ToscaServiceTemplate automationComposition = ReceptionUtil.decodeFile(zipFile, entry);
                    if (null != automationComposition.getToscaTopologyTemplate()) {
                        if (null != nodeTypes) {
                            automationComposition.setNodeTypes(nodeTypes.getNodeTypes());
                        }
                        if (null != dataTypes) {
                            automationComposition.setDataTypes(dataTypes.getDataTypes());
                        }
                        automationCompositionList.add(automationComposition);
                    }
                }
            }
        } catch (final IOException | CoderException exp) {
            throw new PolicyDecodingException("Failed decoding the acm", exp);
        }

        return automationCompositionList;
    }
}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.coder.StandardYamlCoder;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;

/**
 * This class extracts controlloop information from a CSAR file.
 *
 * @author Sirisha Manchikanti (sirisha.manchikanti@est.tech)
 */
public class ControlLoopDecoderFileInCsar implements PolicyDecoder<Csar, ToscaEntity> {

    private ControlLoopDecoderFileInCsarParameterGroup decoderParameters;
    private StandardCoder coder;
    private StandardYamlCoder yamlCoder;
    private static final long MAX_FILE_SIZE = 512L * 1024;
    private static final String NODE_TYPES = "nodes.yml";
    private static final String DATA_TYPES = "data.yml";

    /**
     * {@inheritDoc}.
     */
    @Override
    public void configure(final String parameterGroupName) {
        decoderParameters = ParameterService.get(parameterGroupName);
        coder = new StandardCoder();
        yamlCoder = new StandardYamlCoder();
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
        final Collection<ToscaEntity> controlLoopList = new ArrayList<>();
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

                // Store node_types
                if (entry.getName().contains(NODE_TYPES)) {
                    nodeTypes = decodeFile(zipFile, entry);
                }

                // Store data_types
                if (entry.getName().contains(DATA_TYPES)) {
                    dataTypes = decodeFile(zipFile, entry);
                }

                if (isZipEntryValid(entry.getName(), csar.getCsarFilePath(), entry.getSize())) {
                    final ToscaServiceTemplate controlLoop =
                            decodeFile(zipFile, entry);
                    if (null != controlLoop.getToscaTopologyTemplate()) {
                        if (null != nodeTypes) {
                            controlLoop.setNodeTypes(nodeTypes.getNodeTypes());
                        }
                        if (null != dataTypes) {
                            controlLoop.setDataTypes(dataTypes.getDataTypes());
                        }
                        controlLoopList.add(controlLoop);
                    }
                }
            }
        } catch (final IOException | CoderException exp) {
            throw new PolicyDecodingException("Failed decoding the controlloop", exp);
        }

        return controlLoopList;
    }

    /**
     * Method to filter out ControlLoops. In addition,
     * ensures validation of entries in the Zipfile. Attempts to solve path
     * injection java security issues.
     *
     * @param entryName name of the ZipEntry to check
     * @param csarPath Absolute path to the csar the ZipEntry is in
     * @param entrySize size of the ZipEntry
     * @return true if no injection detected, and it is a controlloop file.
     * @throws PolicyDecodingException if the file size is too large
     */
    private boolean isZipEntryValid(String entryName, String csarPath, long entrySize) throws PolicyDecodingException {
        if (entryName.contains(decoderParameters.getControlLoop())) {
            //
            // Check file size
            //
            if (entrySize > MAX_FILE_SIZE) {
                throw new PolicyDecodingException("Zip entry for " + entryName + " is too large " + entrySize);
            }
            //
            // Now ensure that there is no path injection
            //
            var path = Path.of(csarPath, entryName).normalize();
            //
            // Throw an exception if path is outside the csar
            //
            if (! path.startsWith(csarPath)) {
                throw new PolicyDecodingException("Potential path injection for zip entry " + entryName);
            }
            return true;
        }

        return false;
    }

    /**
     * Method to decode either a json or yaml file into an object.
     *
     * @param zipFile the zip file
     * @param entry the entry to read in the zip file.
     * @return the decoded ToscaServiceTemplate object.
     * @throws CoderException IOException if the file decoding fails.
     */
    private ToscaServiceTemplate decodeFile(ZipFile zipFile, final ZipEntry entry) throws IOException, CoderException {
        ToscaServiceTemplate controlLoop = null;
        if (entry.getName().endsWith(".json")) {
            controlLoop = coder.decode(zipFile.getInputStream(entry), ToscaServiceTemplate.class);
        } else if (entry.getName().endsWith(".yml")) {
            controlLoop = yamlCoder.decode(zipFile.getInputStream(entry), ToscaServiceTemplate.class);
        }
        return controlLoop;
    }
}

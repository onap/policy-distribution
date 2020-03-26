/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.distribution.reception.decoding.policy.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;

/**
 * This class extracts policy files from a CSAR file.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class PolicyDecoderFileInCsarToPolicy implements PolicyDecoder<Csar, ToscaEntity> {

    private PolicyDecoderFileInCsarToPolicyParameterGroup decoderParameters;
    private StandardCoder coder;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void configure(final String parameterGroupName) {
        decoderParameters = ParameterService.get(parameterGroupName);
        coder = new StandardCoder();
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
        final Collection<ToscaEntity> policyList = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(csar.getCsarPath())) {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                if (entry.getName().contains(decoderParameters.getPolicyTypeFileName())
                        || entry.getName().contains(decoderParameters.getPolicyFileName())) {
                    final ToscaServiceTemplate policy =
                            coder.decode(zipFile.getInputStream(entry), ToscaServiceTemplate.class);
                    policyList.add(policy);
                }
            }
        } catch (final IOException | CoderException exp) {
            throw new PolicyDecodingException("Failed decoding the policy", exp);
        }

        return policyList;
    }
}

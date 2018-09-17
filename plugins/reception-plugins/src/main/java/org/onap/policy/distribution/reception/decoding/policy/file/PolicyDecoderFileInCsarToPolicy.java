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

package org.onap.policy.distribution.reception.decoding.policy.file;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.model.PolicyAsString;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;

/**
 * This class extracts policy files from a CSAR file.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class PolicyDecoderFileInCsarToPolicy implements PolicyDecoder<Csar, PolicyAsString> {

    private static final Logger LOGGER = FlexLogger.getLogger(PolicyDecoderFileInCsarToPolicy.class);
    PolicyDecoderFileInCsarToPolicyParameterGroup decoderParameters;

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
    @SuppressWarnings("squid:S2093")
    public Collection<PolicyAsString> decode(final Csar csar) throws PolicyDecodingException {
        final Collection<PolicyAsString> policyList = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(csar.getCsarPath())) {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                if (entry.getName().contains(decoderParameters.getPolicyFileName())) {
                    final StringWriter writer = new StringWriter();
                    IOUtils.copy(zipFile.getInputStream(entry), writer, "UTF-8");
                    final PolicyAsString poilcy = new PolicyAsString(decoderParameters.getPolicyFileName(),
                            decoderParameters.getPolicyType(), writer.toString());
                    policyList.add(poilcy);
                }
            }
        } catch (final IOException exp) {
            final String message = "Failed decoding the policy";
            LOGGER.error(message, exp);
            throw new PolicyDecodingException(message, exp);
        }

        return policyList;
    }
}

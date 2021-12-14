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

package org.onap.policy.distribution.reception.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.coder.StandardYamlCoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;

/**
 * This class extracts and validates information from a CSAR file.
 *
 * @author Sirisha Manchikanti (sirisha.manchikanti@est.tech)
 */
public class ReceptionUtil {

    private static StandardCoder coder = new StandardCoder();
    private static StandardYamlCoder yamlCoder = new StandardYamlCoder();
    private static final long MAX_FILE_SIZE = 512L * 1024;

    /**
     * Method to ensure validation of entries in the Zipfile. Attempts to solve path
     * injection java security issues.
     *
     * @param entryName name of the ZipEntry to check
     * @param csarPath Absolute path to the csar the ZipEntry is in
     * @param entrySize size of the ZipEntry
     * @throws PolicyDecodingException if the file size is too large
     */
    public static void validateZipEntry(String entryName, String csarPath, long entrySize)
            throws PolicyDecodingException {
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
    }

    /**
     * Method to decode either a json or yaml file into an object.
     *
     * @param zipFile the zip file
     * @param entry the entry to read in the zip file.
     * @return the decoded ToscaServiceTemplate object.
     * @throws CoderException IOException if the file decoding fails.
     */
    public static ToscaServiceTemplate decodeFile(ZipFile zipFile, final ZipEntry entry)
            throws IOException, CoderException {
        ToscaServiceTemplate toscaServiceTemplate = null;
        if (entry.getName().endsWith(".json")) {
            toscaServiceTemplate = coder.decode(zipFile.getInputStream(entry), ToscaServiceTemplate.class);
        } else if (entry.getName().endsWith(".yml")) {
            toscaServiceTemplate = yamlCoder.decode(zipFile.getInputStream(entry), ToscaServiceTemplate.class);
        }
        return toscaServiceTemplate;
    }
}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Map;
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

    private ReceptionUtil() throws InvalidClassException {
        throw new InvalidClassException("Can't instantiate a helper class!");
    }

    private static final StandardCoder coder = new StandardCoder();
    private static final StandardYamlCoder yamlCoder = new StandardYamlCoder();
    private static final long THRESHOLD_SIZE = 512L * 1024;
    private static final int THRESHOLD_ENTRIES = 10000;
    private static final double THRESHOLD_RATIO = 20;

    /**
     * Method to ensure validation of entries in the Zipfile. Attempts to solve path
     * injection java security issues.
     *
     * @param entryName name of the ZipEntry to check
     * @param csarPath  Absolute path to the csar the ZipEntry is in
     * @throws PolicyDecodingException if the file size is too large
     */
    public static void validateZipEntry(String entryName, String csarPath)
        throws PolicyDecodingException {
        //
        // Now ensure that there is no path injection
        //
        var path = Path.of(csarPath, entryName).normalize();
        //
        // Throw an exception if path is outside the csar
        //
        if (!path.startsWith(csarPath)) {
            throw new PolicyDecodingException("Potential path injection for zip entry " + entryName);
        }
    }

    /**
     * Method to decode either a json or yaml file into an object.
     *
     * @param zipEntryName the zip file name.
     * @param entryData    the data from entry to decode.
     * @return the decoded ToscaServiceTemplate object.
     * @throws CoderException IOException if the file decoding fails.
     */
    public static ToscaServiceTemplate decodeFile(String zipEntryName, InputStream entryData) throws CoderException {
        ToscaServiceTemplate toscaServiceTemplate = null;
        if (zipEntryName.endsWith(".json")) {
            toscaServiceTemplate = coder.decode(entryData, ToscaServiceTemplate.class);
        } else if (zipEntryName.endsWith(".yml")) {
            toscaServiceTemplate = yamlCoder.decode(entryData, ToscaServiceTemplate.class);
        }
        return toscaServiceTemplate;
    }

    /**
     * Unzip the csar file following the security recommendations from sonar cloud to avoid a zip bomb attack.
     *
     * @param csarFilename      csar file which is a zip file.
     * @param unzippedTemplates the templates which should be unzipped.
     * @param filter            contains the keywords for the entry name.
     * @throws PolicyDecodingException in case zip file can't be read
     * @throws CoderException          in case files can't be decoded to a template
     */
    public static void unzip(String csarFilename, Map<String, ToscaServiceTemplate> unzippedTemplates, String... filter)
        throws PolicyDecodingException, CoderException {
        try (var zipFile = new ZipFile(csarFilename)) {
            int totalSizeArchive = 0;
            int totalEntryArchive = 0;

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                totalEntryArchive++;

                if (checkEntryFilter(entry.getName(), filter)) {
                    validateZipEntry(entry.getName(), csarFilename);
                    InputStream entryData = new BufferedInputStream(zipFile.getInputStream(entry));

                    int bufferedBytes;
                    byte[] buffer = new byte[2048];
                    int totalSizeEntry = 0;

                    boolean isValidThreshold = true;
                    while ((bufferedBytes = entryData.read(buffer)) > 0 && isValidThreshold) {
                        totalSizeEntry += bufferedBytes;
                        totalSizeArchive += bufferedBytes;

                        double compressionRatio = Math.floorDiv(totalSizeEntry, entry.getCompressedSize());
                        isValidThreshold = compressionRatio <= THRESHOLD_RATIO;
                    }

                    unzippedTemplates.put(entry.getName(), decodeFile(entry.getName(), zipFile.getInputStream(entry)));
                }

                if (totalSizeArchive > THRESHOLD_SIZE || totalEntryArchive > THRESHOLD_ENTRIES) {
                    // the uncompressed data size is too much for the application resource capacity
                    break;
                }
            }
        } catch (IOException exception) {
            throw new PolicyDecodingException("Couldn't read the zipFile", exception);
        }
    }

    /**
     * Check if entry is the one to be unzipped based on the filter.
     *
     * @param entryName zip file entry name.
     * @param filter    the list of filter to be looked up on entry names.
     * @return true if entry is in filter, false otherwise.
     */
    public static boolean checkEntryFilter(String entryName, String... filter) {
        for (String f : filter) {
            if (entryName.contains(f)) {
                return true;
            }
        }
        return false;
    }
}

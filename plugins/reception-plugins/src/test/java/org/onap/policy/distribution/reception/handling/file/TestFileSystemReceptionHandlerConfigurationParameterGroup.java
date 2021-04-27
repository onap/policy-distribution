/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Nordix Foundation
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.distribution.reception.handling.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.onap.policy.common.parameters.ValidationResult;

/**
 * Class to perform unit test of {@link FileSystemReceptionHandlerConfigurationParameterGroup}.
 *
 */
public class TestFileSystemReceptionHandlerConfigurationParameterGroup {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testFileSystemConfiguration() throws IOException {
        FileSystemReceptionHandlerConfigurationParameterGroup configParameters = null;
        String validPath = null;

        validPath = tempFolder.getRoot().getAbsolutePath();

        configParameters = new FileSystemReceptionHandlerConfigurationParameterGroup();
        configParameters.setWatchPath(validPath);
        configParameters.setMaxThread(2);

        final ValidationResult validationResult = configParameters.validate();
        assertTrue(validationResult.isValid());
        assertEquals(validPath, configParameters.getWatchPath());
        assertEquals(2, configParameters.getMaxThread());
    }

    @Test
    public void testInvalidFileSystemConfiguration() throws IOException {
        FileSystemReceptionHandlerConfigurationParameterGroup configParameters = null;
        final Gson gson = new GsonBuilder().create();
        configParameters = gson.fromJson(new FileReader("src/test/resources/handling-sdcInvalid.json"),
                FileSystemReceptionHandlerConfigurationParameterGroup.class);
        final ValidationResult validationResult = configParameters.validate();
        assertFalse(validationResult.isValid());

    }

    @Test
    public void testFileSystemReceptionHandlerConfigurationParameterBuilderWithInvalidPath() throws IOException {
        final String invalidPath = tempFolder.newFile("foobar").getAbsolutePath();

        final FileSystemReceptionHandlerConfigurationParameterGroup configParameters =
                new FileSystemReceptionHandlerConfigurationParameterGroup();
        configParameters.setWatchPath(invalidPath);

        final ValidationResult validateResult = configParameters.validate();
        assertFalse(validateResult.isValid());
        assertThat(validateResult.getResult()).contains("is not a valid directory");
    }
}

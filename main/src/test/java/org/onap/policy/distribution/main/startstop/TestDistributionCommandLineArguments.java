/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
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

package org.onap.policy.distribution.main.startstop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.onap.policy.distribution.main.PolicyDistributionRuntimeException;

/**
 * Class to perform unit test of DistributionCommandLineArguments.
 *
 * @author Adheli Tavares (adheli.tavares@est.tech)
 *
 */
public class TestDistributionCommandLineArguments {

    @Test
    public void testDistributionOnlyFileName() {
        String[] testArgs = {"src/test/resources/parameters/DistributionConfigParameters.json"};
        assertThrows(PolicyDistributionRuntimeException.class, () -> new DistributionCommandLineArguments(testArgs));
    }

    @Test
    public void testDistributionCommandLineArgumentsException() {
        String[] wrongParams = {"arg1", "nothing", "{\"someJson\":1}"};
        assertThrows(PolicyDistributionRuntimeException.class, () -> new DistributionCommandLineArguments(wrongParams));
    }

    @Test
    public void testValidateFileNameEmpty() {
        String[] argsOnlyKeyNoValue = {"-c", ""};
        assertValidate(argsOnlyKeyNoValue, "policy-distribution configuration file was not specified as an argument");
    }

    @Test
    public void testValidateFileNameDoesNotExist() {
        String[] fileNameNotExistentArgs = {"-c", "someFileName.json"};
        assertValidate(fileNameNotExistentArgs,
                "policy-distribution configuration file \"someFileName.json\" does not exist");
    }

    @Test
    public void testValidateFileNameIsNotFile() {
        String[] folderAsFileNameArgs = {"-c", "src/test/resources/parameters"};
        assertValidate(folderAsFileNameArgs,
                "policy-distribution configuration file \"src/test/resources/parameters\" is not a normal file");
    }

    @Test
    public void testDistributionVersion() {
        String[] testArgs = {"-v"};
        DistributionCommandLineArguments sutArgs = new DistributionCommandLineArguments(testArgs);
        assertThat(sutArgs.version()).startsWith("ONAP Policy Framework Distribution Service");
    }

    private void assertValidate(String[] testArgs, String expectedErrorMsg) {
        DistributionCommandLineArguments sutArgs = new DistributionCommandLineArguments(testArgs);
        assertThatThrownBy(() -> sutArgs.validate()).hasMessage(expectedErrorMsg);
    }
}

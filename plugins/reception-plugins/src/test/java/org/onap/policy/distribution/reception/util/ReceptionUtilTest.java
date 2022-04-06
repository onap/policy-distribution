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

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Class for testing {@link ReceptionUtil}.
 */

public class ReceptionUtilTest {

    @Test
    public void testValidateZipEntry_InvalidSize() {
        long invalidFileSize = 512L * 2048;

        Assertions.assertThatThrownBy(() ->
                ReceptionUtil.validateZipEntry("entryName", "csarPath", invalidFileSize))
            .hasMessage("Zip entry for entryName is too large " + invalidFileSize);
    }
}

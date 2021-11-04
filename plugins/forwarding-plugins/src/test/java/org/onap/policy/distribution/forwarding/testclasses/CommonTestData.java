/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2021 Nordix Foundation.
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

package org.onap.policy.distribution.forwarding.testclasses;

import java.io.File;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;

/**
 * Class to create parameters for test cases.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@est.tech)
 */
public class CommonTestData {

    /**
     * Returns PolicyForwarderParameters for test cases.
     *
     * @param fileName the file name to load the parameters
     * @param clazz the parameter class to be returned
     * @return the specific PolicyForwarderParameters object
     */
    public static <T> T getPolicyForwarderParameters(final String fileName, final Class<T> clazz) {
        final StandardCoder coder = new StandardCoder();
        try {
            return coder.decode(new File(fileName), clazz);
        } catch (final CoderException exp) {
            throw new RuntimeException("cannot read/decode " + fileName, exp);
        }
    }
}

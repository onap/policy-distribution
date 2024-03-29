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

package org.onap.policy.distribution.model;

import com.openpojo.reflection.filters.FilterPackageInfo;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Test;

/**
 * Class to perform unit testing of all policy models.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
class TestModels {

    @Test
    void testAllModels() {
        final var validator = ValidatorBuilder.create().with(new SetterTester()).with(new GetterTester()).build();
        validator.validate(PolicyInput.class.getPackage().getName(), new FilterPackageInfo());
    }
}

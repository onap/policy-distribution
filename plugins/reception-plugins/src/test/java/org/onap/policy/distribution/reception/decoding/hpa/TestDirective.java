/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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

package org.onap.policy.distribution.reception.decoding.hpa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Class to perform unit test for Directive 0f {@link Directive}.
 *
 */
public class TestDirective {

    @Test
    public void testDirective() {
        final String type = "dummytype";

        final Directive directive = new Directive();
        directive.setType(type);

        validateReport(type, directive);
    }

    private void validateReport(final String type, final Directive directive) {
        assertEquals(type, directive.getType());
        assertEquals(0, directive.getAttributes().size());
        Attribute attribute = new Attribute();
        directive.getAttributes().add(attribute);
        assertEquals(1, directive.getAttributes().size());
        directive.getAttributes().remove(attribute);
        assertEquals(0, directive.getAttributes().size());
    }
}

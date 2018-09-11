/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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

package org.onap.policy.distribution.reception.decoding.pdpx;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Class to perform unit test for Attribute 0f {@link Attribute}.
 *
 */
public class TestAttribute {

    @Test
    public void testAttribute() {
        final String attributeName = "dummyName";
        final String attributeValue = "dummyValue";

        final Attribute attribute = new Attribute();
        attribute.setAttributeName(attributeName);
        attribute.setAttributeValue(attributeValue);

        validateReport(attributeName,attributeValue,attribute);
    }

    private void validateReport(final String name, final String value, final Attribute attribute) {
        assertEquals(name, attribute.getAttributeName());
        assertEquals(value, attribute.getAttributeValue());
    }
}

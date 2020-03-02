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

package org.onap.policy.distribution.reception.decoding.hpa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.policy.distribution.reception.decoding.hpa.HpaFeatureAttribute;

/**
 * Class to perform unit test for HpaFeatureAttribute 0f {@link HpaFeatureAttribute}.
 *
 */
public class TestHpaFeatureAttribute {

    @Test
    public void testHpaFeatureAttribute() {
        final String hpaAttributeKey = "dummykey";
        final String hpaAttributeValue = "4096";
        final String operator = ">=";
        final String unit = "MB";

        final HpaFeatureAttribute hpaFeatureAttribute = new HpaFeatureAttribute();
        hpaFeatureAttribute.setHpaAttributeKey(hpaAttributeKey);
        hpaFeatureAttribute.setHpaAttributeValue(hpaAttributeValue);
        hpaFeatureAttribute.setOperator(operator);
        hpaFeatureAttribute.setUnit(unit);

        validateReport(hpaAttributeKey, hpaAttributeValue, operator, unit, hpaFeatureAttribute);
    }

    private void validateReport(final String hpaAttributeKey, final String hpaAttributeValue, final String operator,
            final String unit, final HpaFeatureAttribute hpaFeatureAttribute) {
        assertEquals(hpaAttributeKey, hpaFeatureAttribute.getHpaAttributeKey());
        assertEquals(hpaAttributeValue, hpaFeatureAttribute.getHpaAttributeValue());
        assertEquals(operator, hpaFeatureAttribute.getOperator());
        assertEquals(unit, hpaFeatureAttribute.getUnit());
    }
}

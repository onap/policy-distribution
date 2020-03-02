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
import org.onap.policy.distribution.reception.decoding.hpa.Directive;
import org.onap.policy.distribution.reception.decoding.hpa.FlavorFeature;
import org.onap.policy.distribution.reception.decoding.hpa.FlavorProperty;

/**
 * Class to perform unit test for FlavorFeature 0f {@link FlavorFeature}.
 *
 */
public class TestFlavorFeature {

    @Test
    public void testFlavorFeature() {
        final String id = "dummyid";
        final String type = "dummytype";

        final FlavorFeature flavorFeature = new FlavorFeature();
        flavorFeature.setId(id);
        flavorFeature.setType(type);

        validateReport(id, type, flavorFeature);
    }

    private void validateReport(final String id, final String type, final FlavorFeature flavorFeature) {
        assertEquals(id, flavorFeature.getId());
        assertEquals(type, flavorFeature.getType());

        assertEquals(0, flavorFeature.getDirectives().size());
        Directive directive = new Directive();
        flavorFeature.getDirectives().add(directive);
        assertEquals(1, flavorFeature.getDirectives().size());
        flavorFeature.getDirectives().remove(directive);
        assertEquals(0, flavorFeature.getDirectives().size());

        assertEquals(0, flavorFeature.getFlavorProperties().size());
        FlavorProperty flavorProperty = new FlavorProperty();
        flavorFeature.getFlavorProperties().add(flavorProperty);
        assertEquals(1, flavorFeature.getFlavorProperties().size());
        flavorFeature.getFlavorProperties().remove(flavorProperty);
        assertEquals(0, flavorFeature.getFlavorProperties().size());
    }
}

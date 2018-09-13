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
 * Class to perform unit test for Content 0f {@link Content}.
 *
 */
public class TestContent {

    @Test
    public void testContent() {
        final String resources = "dummyresource";
        final String identity = "dummyidentity";
        final String policyType = "optimization";

        final Content content = new Content();
        content.setResources(resources);
        content.setIdentity(identity);
        content.setPolicyType(policyType);

        validateReport(resources, identity, policyType,content);
    }

    private void validateReport(final String resources, final String identity, final String policyType, 
                                final Content content) {
        assertEquals(resources, content.getResources());
        assertEquals(identity, content.getIdentity());
        assertEquals(policyType, content.getPolicyType());
        assertEquals(0, content.getPolicyScope().size());
        content.getPolicyScope().add("vFW");
        assertEquals(1, content.getPolicyScope().size());
        content.getPolicyScope().remove("vFW");
        assertEquals(0, content.getPolicyScope().size());
        assertEquals(0, content.getFlavorFeatures().size());
        FlavorFeature flavorFeature = new FlavorFeature();
        content.getFlavorFeatures().add(flavorFeature);
        assertEquals(1, content.getFlavorFeatures().size());
        content.getFlavorFeatures().remove(flavorFeature);
        assertEquals(0, content.getFlavorFeatures().size());
    }
}

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
 * Class to perform unit test for FlavorProperty 0f {@link FlavorProperty}.
 *
 */
public class TestFlavorProperty {

    @Test
    public void testFlavorProperty() {
        final String hpaFeature = "dummyid";
        final String mandatory = "false";
        final String architecture = "generic";
        final String hpaVersion = "v1";

        final FlavorProperty flavorProperty = new FlavorProperty();
        flavorProperty.setHpa_feature(hpaFeature);
        flavorProperty.setMandatory(mandatory);
        flavorProperty.setArchitecture(architecture);
        flavorProperty.setHpa_version(hpaVersion);

        validateReport(hpaFeature,mandatory,architecture,hpaVersion,flavorProperty);
    }

    private void validateReport(final String hpaFeature, final String mandatory, final String architecture,
                                final String hpaVersion, final FlavorProperty flavorProperty) {
        assertEquals(hpaFeature, flavorProperty.getHpa_feature());
        assertEquals(mandatory, flavorProperty.getMandatory());
        assertEquals(architecture, flavorProperty.getArchitecture());
        assertEquals(hpaVersion, flavorProperty.getHpa_version());

        assertEquals(0, flavorProperty.getDirectives().size());
        Directive directive = new Directive();
        flavorProperty.getDirectives().add(directive);
        assertEquals(1, flavorProperty.getDirectives().size());
        flavorProperty.getDirectives().remove(directive);
        assertEquals(0, flavorProperty.getDirectives().size());

        assertEquals(0, flavorProperty.getHpa_feature_attributes().size());
        HpaFeatureAttribute hpaFeatureAttribute = new HpaFeatureAttribute();
        flavorProperty.getHpa_feature_attributes().add(hpaFeatureAttribute);
        assertEquals(1, flavorProperty.getHpa_feature_attributes().size());
        flavorProperty.getHpa_feature_attributes().remove(hpaFeatureAttribute);
        assertEquals(0, flavorProperty.getHpa_feature_attributes().size());
    }
}

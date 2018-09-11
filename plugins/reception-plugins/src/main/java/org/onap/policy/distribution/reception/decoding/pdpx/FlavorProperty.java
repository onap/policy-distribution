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

import java.util.List;
import java.util.ArrayList;

/**
 * The FlavorProperty includes all the properties of Flavor.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
class FlavorProperty{
    private String hpa_feature;
    private String mandatory = "true";
    private String architecture = "generic";
    private String hpa_version = "v1";
    private List<Directive> directives = new ArrayList<>();
    private List<HpaFeatureAttribute> hpa_feature_attributes = new ArrayList<>();

    public void setHpa_feature(String hpa_feature) {
        this.hpa_feature = hpa_feature;
    }

    public String getHpa_feature() {
        return hpa_feature;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setHpa_version(String hpa_version) {
        this.hpa_version = hpa_version;
    }

    public String getHpa_version() {
        return hpa_version;
    }

    public List<Directive> getDirectives() {
        return directives;
    }

    public List<HpaFeatureAttribute> getHpa_feature_attributes() {
        return hpa_feature_attributes;
    }

    @Override
    public String toString() {
        return "{ hpa_feature:" + hpa_feature + ", mandatory = " + mandatory + ",architecture:" + architecture
            + ", hpa_feature_attributes : [" + hpa_feature_attributes + "]";
    }

}


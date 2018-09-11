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

import com.google.gson.annotations.SerializedName;

/**
 * The FlavorProperty includes all the properties of Flavor.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
class FlavorProperty{
    @SerializedName(value = "hpa-feature")
    private String hpaFeature;
    private String mandatory = "true";
    private String architecture = "generic";
    @SerializedName(value = "hpa-version")
    private String hpaVersion = "v1";
    private List<Directive> directives = new ArrayList<>();
    @SerializedName(value = "hpa-feature-attributes")
    private List<HpaFeatureAttribute> hpaFeatureAttributes = new ArrayList<>();

    public void setHpaFeature(String hpaFeature) {
        this.hpaFeature = hpaFeature;
    }

    public String getHpaFeature() {
        return hpaFeature;
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

    public void setHpaVersion(String hpaVersion) {
        this.hpaVersion = hpaVersion;
    }

    public String getHpaVersion() {
        return hpaVersion;
    }

    public List<Directive> getDirectives() {
        return directives;
    }

    public List<HpaFeatureAttribute> getHpaFeatureAttributes() {
        return hpaFeatureAttributes;
    }

    @Override
    public String toString() {
        return "{ hpaFeature:" + hpaFeature + ", mandatory = " + mandatory + ",architecture:" + architecture
            + ", hpaFeatureAttributes : [" + hpaFeatureAttributes + "]";
    }

}


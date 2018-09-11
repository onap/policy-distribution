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
 * The content acts the high level abstraction which to be used by OOF to do Optimization.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
class Content {
    private String resources;
    private String identity;
    private List<String> policyScope = new ArrayList<>();
    private String policyType;
    private List<FlavorFeature> flavorFeatures = new ArrayList<>();

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getResources() {
        return resources;
    }   

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public List<String> getPolicyScope() {
        return policyScope;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getPolicyType() {
        return policyType;
    }

    public List<FlavorFeature> getFlavorFeatures() {
        return flavorFeatures;
    }

}


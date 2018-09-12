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
 * The FlavorFeature includes all the specified flavor infos used in multicloud, it represents one VDU of TOSCA.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
class FlavorFeature {
    private String id ;
    private String type = "tosca.node.nfv.Vdu.Compute";
    private List<Directive> directives = new ArrayList<>();
    private List<FlavorProperty> flavorProperties = new ArrayList<>();
    
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public List<FlavorProperty>  getFlavorProperties() {
        return flavorProperties;
    }

    public List<Directive> getDirectives() {
        return directives;
    }

    @Override
    public String toString() {
        return "{ id = " + id + ", type = " + type + ", \n" + "directivies:["+directives + ",\n"
            + flavorProperties + "}";
    }
}


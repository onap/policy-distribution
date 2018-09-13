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

import java.util.ArrayList;
import java.util.List;


/**
 * The attribute acts an abstraction to indicate OOF which supports two different Models (Heat and TOSCA), two areas are
 * wrapped: in the VNFC level to indicate the flavor, in the hpa_feature level to contains specified information.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
class Directive {
    private String type;
    private List<Attribute> attributes = new ArrayList<>();

    public void setType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
}


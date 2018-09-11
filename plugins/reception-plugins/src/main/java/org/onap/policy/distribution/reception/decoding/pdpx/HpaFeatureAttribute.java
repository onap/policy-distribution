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

/**
 * The HpaFeatureAttribute represents a way to provide match expression.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
class HpaFeatureAttribute{
    private String hpa_attribute_key;
    private String hpa_attribute_value;
    private String operator;
    private String unit;

    public HpaFeatureAttribute(){}

    public void setHpa_attribute_key(String hpa_attribute_key) {
        this.hpa_attribute_key = hpa_attribute_key;
    }
    
    public String getHpa_attribute_key() {
        return hpa_attribute_key;
    }

    public void setHpa_attribute_value(String hpa_attribute_value) {
        this.hpa_attribute_value = hpa_attribute_value;
    }

    public String getHpa_attribute_value() {
        return hpa_attribute_value;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }
}

    


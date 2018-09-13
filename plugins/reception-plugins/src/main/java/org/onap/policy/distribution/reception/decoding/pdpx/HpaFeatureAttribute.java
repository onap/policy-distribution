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

import com.google.gson.annotations.SerializedName;

/**
 * The HpaFeatureAttribute represents a way to provide match expression.
 *
 * @author Libo Zhu (libo.zhu@intel.com)
 */
class HpaFeatureAttribute {
    @SerializedName(value = "hpa-attribute-key")
    private String hpaAttributeKey;
    @SerializedName(value = "hap-attribute-value")
    private String hpaAttributeValue;
    private String operator;
    private String unit;

    public void setHpaAttributeKey(final String hpaAttributeKey) {
        this.hpaAttributeKey = hpaAttributeKey;
    }

    public String getHpaAttributeKey() {
        return hpaAttributeKey;
    }

    public void setHpaAttributeValue(final String hpaAttributeValue) {
        this.hpaAttributeValue = hpaAttributeValue;
    }

    public String getHpaAttributeValue() {
        return hpaAttributeValue;
    }

    public void setOperator(final String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public void setUnit(final String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }
}



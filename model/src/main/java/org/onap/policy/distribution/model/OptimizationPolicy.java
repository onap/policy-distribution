/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.distribution.model;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.apache.commons.lang3.NotImplementedException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;

/**
 * An optimization policy.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class OptimizationPolicy extends ToscaEntity {

    private static final String OPTIMIZATION = "Optimization";
    private String policyName;
    private String policyDescription;
    private String onapName;
    private String configBody;
    private String configBodyType;
    private Date timetolive;
    private String guard;
    private String riskLevel;
    private String riskType;

    @Override
    public String getName() {
        return policyName;
    }

    @Override
    public void setName(final String name) {
        this.policyName = name;
    }

    @Override
    public String getVersion() {
        // Utilizing this method to return the policy type instead of version for the old model.
        return OPTIMIZATION;
    }

    @Override
    public void setVersion(final String version) {
        throw new NotImplementedException("Not supported");
    }
}

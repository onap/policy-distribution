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

package org.onap.policy.distribution.reception.decoding.pdpx;

import org.onap.policy.distribution.model.Policy;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;

/**
 * A PDP-X Policy, decoded by a {@link PolicyDecoder}.
 */
public class PdpxPolicy implements Policy {

    private static final String OPTIMIZATION = "Optimization";
    private String service;
    private String policyName;
    private String description;
    private String templateVersion;
    private String version;
    private String priority;
    private String riskType;
    private String riskLevel;
    private String guard;
    private Content content = new Content();

    @Override
    public String getPolicyName() {
        return policyName;
    }

    @Override
    public String getPolicyType() {
        return OPTIMIZATION;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getService() {
        return service;
    }    

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTemplateVersion(String templateVersion) {
        this.templateVersion = templateVersion;
    }

    public String getTemplateVersion() {
        return templateVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getGuard() {
        return guard;
    }

    public void setGuard(String guard) {
        this.guard = guard;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public Content getContent(){
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }    

}

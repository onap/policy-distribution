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

    private String guard;
    private String service;
    private String policyName;
    private String description;
    private String templateVersion;
    private String version;
    private String riskType;
    private String priority;
    private String riskLevel;
    private Content content = new Content();

    @Override
    public String getPolicyName() {
        return policyName;
    }

    @Override
    public String getPolicyType() {
        return content.getPolicyType();
    }

    public String getGuard() {
        return guard;
    }

    public String getService() {
        return service;
    }

    public String getDescription() {
        return description;
    }

    public String getTemplateVersion() {
        return templateVersion;
    }

    public String getVersion() {
        return version;
    }

    public String getRiskType() {
        return riskType;
    }

    public String getPriority() {
        return priority;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public Content getContent() {
        return content;
    }

    public void setGuard(final String guard) {
        this.guard = guard;
    }

    public void setService(final String service) {
        this.service = service;
    }

    public void setPolicyName(final String policyName) {
        this.policyName = policyName;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setTemplateVersion(final String templateVersion) {
        this.templateVersion = templateVersion;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public void setRiskType(final String riskType) {
        this.riskType = riskType;
    }

    public void setPriority(final String priority) {
        this.priority = priority;
    }

    public void setRiskLevel(final String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public void setContent(final Content content) {
        this.content = content;
    }
}

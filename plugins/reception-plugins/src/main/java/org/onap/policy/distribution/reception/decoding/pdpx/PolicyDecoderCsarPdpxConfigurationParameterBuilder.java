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

/**
 * This class builds an instance of {@link PolicyDecodeCsarPdpxConfigurationParameterGroup} class.
 */
public class PolicyDecoderCsarPdpxConfigurationParameterBuilder {

    private String policyNamePrefix;
    private String onapName;
    private String version;
    private String priority;
    private String riskType;
    private String riskLevel;

    public PolicyDecoderCsarPdpxConfigurationParameterBuilder setPolicyNamePrefix(final String policyNamePrefix) {
        this.policyNamePrefix = policyNamePrefix;
        return this;
    }

    public PolicyDecoderCsarPdpxConfigurationParameterBuilder setOnapName(final String onapName) {
        this.onapName = onapName;
        return this;
    }

    public PolicyDecoderCsarPdpxConfigurationParameterBuilder setVersion(final String version) {
        this.version = version;
        return this;
    }

    public PolicyDecoderCsarPdpxConfigurationParameterBuilder setPriority(final String priority) {
        this.priority = priority;
        return this;
    }

    public PolicyDecoderCsarPdpxConfigurationParameterBuilder setRiskType(final String riskType) {
        this.riskType = riskType;
        return this;
    }

    public PolicyDecoderCsarPdpxConfigurationParameterBuilder setRiskLevel(final String riskLevel) {
        this.riskLevel = riskLevel;
        return this;
    }

    public PolicyDecoderCsarPdpxConfigurationParameterGroup build() {
        return new PolicyDecoderCsarPdpxConfigurationParameterGroup(this);
    }

    public String getPolicyNamePrefix() {
        return policyNamePrefix;
    }

    public String getOnapName() {
        return onapName;
    }

    public String getVersion() {
        return version;
    }

    public String getPriority() {
        return priority;
    }

    public String getRiskType() {
        return riskType;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

}



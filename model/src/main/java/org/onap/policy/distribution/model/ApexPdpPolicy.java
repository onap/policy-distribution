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

import java.io.InputStream;

/**
 * This class represents an apex-pdp policy which can be decoded by a relevant {@link PolicyDecoder}.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class ApexPdpPolicy implements Policy {
    private String policyName;
    private InputStream policyInputStream;

    /**
     * Constructor for creating instance of {@link ApexPdpPolicy}.
     *
     * @param policyName the policy file name
     * @param policyInputStream the input stream
     */
    public ApexPdpPolicy(final String policyName, final InputStream policyInputStream) {
        this.policyName = policyName;
        this.policyInputStream = policyInputStream;
    }

    /**
     * Returns the policyInputStream of this ApexPdpPolicy instance.
     *
     * @return the policyInputStream
     */
    public InputStream getPolicyInputStream() {
        return policyInputStream;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getPolicyName() {
        return policyName;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getPolicyType() {
        return "Method not supported";
    }
}

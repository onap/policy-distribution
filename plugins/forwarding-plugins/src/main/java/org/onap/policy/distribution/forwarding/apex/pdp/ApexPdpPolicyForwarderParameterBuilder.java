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

package org.onap.policy.distribution.forwarding.apex.pdp;

/**
 * This builder holds all the parameters needed to build an instance of {@link ApexPdpPolicyForwarderParameterGroup}
 * class.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class ApexPdpPolicyForwarderParameterBuilder {

    private String hostname;
    private int port;
    private boolean ignoreConflicts;
    private boolean forceUpdate;

    /**
     * Set host name to this {@link ApexPdpPolicyForwarderParameterBuilder} instance.
     *
     * @param hostname the host name
     */
    public ApexPdpPolicyForwarderParameterBuilder setHostname(final String hostname) {
        this.hostname = hostname;
        return this;
    }

    /**
     * Set port to this {@link ApexPdpPolicyForwarderParameterBuilder} instance.
     *
     * @param port the port number
     */
    public ApexPdpPolicyForwarderParameterBuilder setPort(final int port) {
        this.port = port;
        return this;
    }

    /**
     * Set ignore conflicts flag to this {@link ApexPdpPolicyForwarderParameterBuilder} instance.
     *
     * @param ignoreConflicts the ignore conflicts flag
     */
    public ApexPdpPolicyForwarderParameterBuilder setIgnoreConflicts(final boolean ignoreConflicts) {
        this.ignoreConflicts = ignoreConflicts;
        return this;
    }

    /**
     * Set force update flag to this {@link ApexPdpPolicyForwarderParameterBuilder} instance.
     *
     * @param forceUpdate the force update flag
     */
    public ApexPdpPolicyForwarderParameterBuilder setForceUpdate(final boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
        return this;
    }

    /**
     * Returns the host name of this {@link ApexPdpPolicyForwarderParameterBuilder} instance.
     *
     * @return the host name
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Returns the port of this {@link ApexPdpPolicyForwarderParameterBuilder} instance.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the ignore conflicts flag of this {@link ApexPdpPolicyForwarderParameterBuilder} instance.
     *
     * @return the ignoreConflicts
     */
    public boolean isIgnoreConflicts() {
        return ignoreConflicts;
    }

    /**
     * Returns the force update flag of this {@link ApexPdpPolicyForwarderParameterBuilder} instance.
     *
     * @return the forceUpdate
     */
    public boolean isForceUpdate() {
        return forceUpdate;
    }
}

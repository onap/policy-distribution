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

package org.onap.policy.distribution.main.testclasses;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.distribution.main.parameters.PolicyForwarderConfigurationParameterGroup;

/**
 * Dummy policy forwarder parameter group.
 */
public class DummyPolicyForwarderParameterGroup extends PolicyForwarderConfigurationParameterGroup {

    private boolean useHttps;
    private String hostname;
    private int port;
    private String userName;
    private String password;
    private boolean isManaged;

    public boolean isUseHttps() {
        return useHttps;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isManaged() {
        return isManaged;
    }

    /**
     * Builder for DummyPolicyForwarderParameterGroup.
     */
    public static class DummyPolicyForwarderParameterGroupBuilder {
        private boolean useHttps;
        private String hostname;
        private int port;
        private String userName;
        private String password;
        private boolean isManaged;

        public DummyPolicyForwarderParameterGroupBuilder setUseHttps(final boolean useHttps) {
            this.useHttps = useHttps;
            return this;
        }

        public DummyPolicyForwarderParameterGroupBuilder setHostname(final String hostname) {
            this.hostname = hostname;
            return this;
        }

        public DummyPolicyForwarderParameterGroupBuilder setPort(final int port) {
            this.port = port;
            return this;
        }

        public DummyPolicyForwarderParameterGroupBuilder setUserName(final String userName) {
            this.userName = userName;
            return this;
        }

        public DummyPolicyForwarderParameterGroupBuilder setPassword(final String password) {
            this.password = password;
            return this;
        }

        public DummyPolicyForwarderParameterGroupBuilder setIsManaged(final boolean isManaged) {
            this.isManaged = isManaged;
            return this;
        }

        /**
         * Creates a new DummyPolicyForwarderParameterGroup instance.
         */
        public DummyPolicyForwarderParameterGroup build() {
            return new DummyPolicyForwarderParameterGroup(this);
        }
    }

    /**
     * Construct an instance.
     *
     * @param builder the builder create the instance from
     */
    private DummyPolicyForwarderParameterGroup(final DummyPolicyForwarderParameterGroupBuilder builder) {
        this.useHttps = builder.useHttps;
        this.hostname = builder.hostname;
        this.port = builder.port;
        this.userName = builder.userName;
        this.password = builder.password;
        this.isManaged = builder.isManaged;
    }

    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult validationResult = new GroupValidationResult(this);
        return validationResult;
    }

}

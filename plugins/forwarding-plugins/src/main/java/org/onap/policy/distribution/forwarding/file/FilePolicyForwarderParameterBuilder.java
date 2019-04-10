/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Intel Corp. All rights reserved.
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

package org.onap.policy.distribution.forwarding.file;

/**
 * This builder holds all the parameters needed to build an instance of {@link FilePolicyForwarderParameterGroup}
 * class.
 */
public class FilePolicyForwarderParameterBuilder {

    private String path;
    private boolean verbose = false;

    /**
     * Set path to this {@link FilePolicyForwarderParameterBuilder} instance.
     *
     * @param path the directory to store the policies
     */
    public FilePolicyForwarderParameterBuilder setPath(final String path) {
        this.path = path;
        return this;
    }


    /**
     * Set verbose flag to this {@link FilePolicyForwarderParameterBuilder} instance.
     *
     * @param verbose the verbose flag
     */
    public FilePolicyForwarderParameterBuilder setVerbose(final boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    /**
     * Returns the path of this {@link FilePolicyForwarderParameterBuilder} instance.
     *
     * @return the directory
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the verbose flag of this {@link FilePolicyForwarderParameterBuilder} instance.
     *
     * @return the verbose
     */
    public boolean isVerbose() {
        return verbose;
    }
}

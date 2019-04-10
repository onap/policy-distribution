/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel Corp. All rights reserved.
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

package org.onap.policy.distribution.reception.handling.file;

/**
 * This class builds an instance of {@link FileSystemReceptionHandlerConfigurationParameterGroup} class.
 *
 */
public class FileSystemReceptionHandlerConfigurationParameterBuilder {

    private String watchPath;
    private int maxThread = 1;

    /**
     * Set watchPath to this {@link FileSystemReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param watchPath the watchPath
     */
    public FileSystemReceptionHandlerConfigurationParameterBuilder setWatchPath(final String watchPath) {
        this.watchPath = watchPath;
        return this;
    }

    /**
     * Set maxThread to this {@link FileSystemReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @param maxThread the max thread number in the thread pool
     */
    public FileSystemReceptionHandlerConfigurationParameterBuilder setMaxThread(final int maxThread) {
        this.maxThread = maxThread;
        return this;
    }

    /**
     * Returns the watchPath of this {@link FileSystemReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the watchPath
     */
    public String getWatchPath() {
        return watchPath;
    }

    /**
     * Returns the maxThread of this {@link FileSystemReceptionHandlerConfigurationParameterBuilder} instance.
     *
     * @return the maxThread
     */
    public int getMaxThread() {
        return maxThread;
    }
}



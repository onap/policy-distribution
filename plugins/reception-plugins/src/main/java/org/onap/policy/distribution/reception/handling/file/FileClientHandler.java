/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

import java.io.IOException;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

/**
 * This class implements Runnable interface for creating new thread which will be used as file watcher.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@est.tech)
 */
public class FileClientHandler implements Runnable {

    private static final Logger LOGGER = FlexLogger.getLogger(FileClientHandler.class);

    private FileSystemReceptionHandler fileReceptionHandler;
    private String watchPath;

    /**
     * Constructs an instance of {@link FileClientHandler} class.
     *
     * @param fileReceptionHandler the fileReceptionHandler
     */
    public FileClientHandler(final FileSystemReceptionHandler fileReceptionHandler, final String watchPath) {
        this.fileReceptionHandler = fileReceptionHandler;
        this.watchPath = watchPath;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void run() {
        try {
            fileReceptionHandler.initFileWatcher(watchPath);
        } catch (final IOException ex) {
            LOGGER.error(ex);
        }
    }
}

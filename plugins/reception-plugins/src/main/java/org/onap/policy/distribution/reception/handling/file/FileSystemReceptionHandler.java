/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel Corp. All rights reserved.
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

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;
import org.onap.policy.distribution.reception.statistics.DistributionStatisticsManager;

/**
 * Handles reception of inputs from File System which can be used to decode policies.
 */
public class FileSystemReceptionHandler extends AbstractReceptionHandler {

    private static final Logger LOGGER = FlexLogger.getLogger(FileSystemReceptionHandler.class);
    private boolean running = false;

    /**
     * {@inheritDoc}.
     */
    @Override
    protected void initializeReception(final String parameterGroupName) {
        LOGGER.debug("FileSystemReceptionHandler init...");
        try {
            final FileSystemReceptionHandlerConfigurationParameterGroup handlerParameters =
                    ParameterService.get(parameterGroupName);
            final FileClientHandler fileClientHandler = new FileClientHandler(this, handlerParameters.getWatchPath());
            final Thread fileWatcherThread = new Thread(fileClientHandler);
            fileWatcherThread.start();
        } catch (final Exception ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void destroy() {
        running = false;
    }

    /**
     * Method to check the running status of file watcher thread.
     *
     * @return the running status
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Initialize the file watcher thread.
     *
     * @param watchPath Path to watch
     */
    public void initFileWatcher(final String watchPath) throws IOException {
        try (final WatchService watcher = FileSystems.getDefault().newWatchService()) {
            final Path dir = Paths.get(watchPath);
            dir.register(watcher, ENTRY_CREATE);
            LOGGER.debug("Watch Service registered for dir: " + dir.getFileName());
            startWatchService(watcher, dir);
        } catch (final InterruptedException ex) {
            LOGGER.debug(ex);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Method to keep watching the given path for any new file created.
     *
     * @param watcher the watcher
     * @param dir the watch directory
     * @throws InterruptedException if it occurs
     */
    @SuppressWarnings("unchecked")
    protected void startWatchService(final WatchService watcher, final Path dir) throws InterruptedException {
        WatchKey key;
        running = true;
        while (running) {
            key = watcher.take();

            for (final WatchEvent<?> event : key.pollEvents()) {
                final WatchEvent<Path> ev = (WatchEvent<Path>) event;
                final Path fileName = ev.context();
                LOGGER.debug("new CSAR found: " + fileName);
                DistributionStatisticsManager.updateTotalDistributionCount();
                createPolicyInputAndCallHandler(dir.toString() + File.separator + fileName.toString());
                LOGGER.debug("CSAR complete: " + fileName);
            }
            final boolean valid = key.reset();
            if (!valid) {
                LOGGER.error("Watch key no longer valid!");
                break;
            }
        }
    }

    /**
     * Method to create policy input & call policy handlers.
     *
     * @param fileName the filename
     */
    protected void createPolicyInputAndCallHandler(final String fileName) {
        try {
            final Csar csarObject = new Csar(fileName);
            DistributionStatisticsManager.updateTotalDownloadCount();
            // sleep for a second to avoid IOException due to race condition
            // between main thread and watcher thread.
            TimeUnit.SECONDS.sleep(1);
            inputReceived(csarObject);
            DistributionStatisticsManager.updateDownloadSuccessCount();
            DistributionStatisticsManager.updateDistributionSuccessCount();
        } catch (final PolicyDecodingException | InterruptedException ex) {
            DistributionStatisticsManager.updateDownloadFailureCount();
            DistributionStatisticsManager.updateDistributionFailureCount();
            LOGGER.error(ex);
            Thread.currentThread().interrupt();
        }
    }
}

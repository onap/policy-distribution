/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel Corp. All rights reserved.
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2019, 2021 AT&T Intellectual Property. All rights reserved.
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipFile;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;
import org.onap.policy.distribution.reception.statistics.DistributionStatisticsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles reception of inputs from File System which can be used to decode policies.
 */
public class FileSystemReceptionHandler extends AbstractReceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemReceptionHandler.class);
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
            final var fileClientHandler =
                    new FileClientHandler(this, handlerParameters.getWatchPath(), handlerParameters.getMaxThread());
            final var fileWatcherThread = new Thread(fileClientHandler);
            fileWatcherThread.start();
        } catch (final Exception ex) {
            LOGGER.error("FileSystemReceptionHandler initialization failed", ex);
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
    public void initFileWatcher(final String watchPath, final int maxThread) throws IOException {
        try (final var watcher = FileSystems.getDefault().newWatchService()) {
            final var dir = Paths.get(watchPath);
            dir.register(watcher, ENTRY_CREATE);
            LOGGER.debug("Watch Service registered for dir: {}", dir.getFileName());
            startWatchService(watcher, dir, maxThread);
        } catch (final Exception ex) {
            LOGGER.error("FileWatcher initialization failed", ex);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Method to keep watching the given path for any new file created.
     *
     * @param watcher the watcher
     * @param dir the watch directory
     * @param maxThread the max thread number
     * @throws InterruptedException if it occurs
     */
    protected void startWatchService(final WatchService watcher, final Path dir, final int maxThread)
            throws InterruptedException {
        WatchKey key;
        final ExecutorService pool = Executors.newFixedThreadPool(maxThread);

        try {
            running = true;
            while (running) {
                key = watcher.take();
                processFileEvents(dir, key, pool);
                final boolean valid = key.reset();
                if (!valid) {
                    LOGGER.error("Watch key no longer valid!");
                    break;
                }
            }
        } finally {
            pool.shutdown();
        }
    }

    private void processFileEvents(final Path dir, final WatchKey key, final ExecutorService pool) {
        for (final WatchEvent<?> event : key.pollEvents()) {
            @SuppressWarnings("unchecked")
            final WatchEvent<Path> ev = (WatchEvent<Path>) event;
            final Path fileName = ev.context();
            pool.execute(() -> {
                LOGGER.debug("new CSAR found: {}", fileName);
                DistributionStatisticsManager.updateTotalDistributionCount();
                final String fullFilePath = dir.toString() + File.separator + fileName.toString();
                try {
                    waitForFileToBeReady(fullFilePath);
                    createPolicyInputAndCallHandler(fullFilePath);
                    LOGGER.debug("CSAR complete: {}", fileName);
                } catch (final InterruptedException e) {
                    LOGGER.error("waitForFileToBeReady interrupted", e);
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    /**
     * Method to create policy input & call policy handlers.
     *
     * @param fileName the filename
     */
    protected void createPolicyInputAndCallHandler(final String fileName) {
        try {
            final var csarObject = new Csar(fileName);
            DistributionStatisticsManager.updateTotalDownloadCount();
            inputReceived(csarObject);
            DistributionStatisticsManager.updateDownloadSuccessCount();
            DistributionStatisticsManager.updateDistributionSuccessCount();
        } catch (final PolicyDecodingException ex) {
            DistributionStatisticsManager.updateDownloadFailureCount();
            DistributionStatisticsManager.updateDistributionFailureCount();
            LOGGER.error("Policy creation failed", ex);
        }
    }

    private void waitForFileToBeReady(final String fullFilePath) throws InterruptedException {
        var flag = true;
        while (flag) {
            TimeUnit.MILLISECONDS.sleep(100);
            try (var zipFile = new ZipFile(fullFilePath)) {
                flag = false;
            } catch (final IOException exp) {
                LOGGER.error("file is not ready for reading, wait for sometime and try again", exp);
            }
        }
    }
}

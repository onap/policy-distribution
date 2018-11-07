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

package org.onap.policy.distribution.reception.handling.sdc;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import java.io.IOException;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;

/**
 * Handles reception of inputs from File System which can be used to decode policies.
 */
public class FileSystemReceptionHandler extends AbstractReceptionHandler {
    private boolean running = true;
    private static final Logger LOGGER = FlexLogger.getLogger(FileSystemReceptionHandler.class);

    @Override
    protected void initializeReception(final String parameterGroupName) {
	LOGGER.debug("FileSystemReceptionHandler init...");
        try {
            final FileSystemReceptionHandlerConfigurationParameterGroup handlerParameters =
		                    ParameterService.get(parameterGroupName);
            main(handlerParameters.getWatchPath());
        } catch (final PolicyDecodingException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        // Tear down subscription etc
	running = false;
    }

    @SuppressWarnings("unchecked")
    public void main(String watchPath) throws PolicyDecodingException {
        try {
            final WatchService watcher = FileSystems.getDefault().newWatchService();
            final Path dir = Paths.get(watchPath);

            dir.register(watcher, ENTRY_CREATE);
            LOGGER.debug("Watch Service registered for dir: " + dir.getFileName());
            while (running) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (final InterruptedException ex) {
                    ex.printStackTrace();
                    return;
                }
                for (final WatchEvent<?> event : key.pollEvents()) {
                    final WatchEvent.Kind<?> kind = event.kind();
                    final WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    final Path fileName = ev.context();
                    LOGGER.debug("new CSAR found: " + kind.name() + ": " + fileName);
                    createPolicyInputAndCallHandler(dir.toString() + File.separator + fileName.toString());
                    LOGGER.debug("CSAR complete: " + kind.name() + ": " + fileName);
                }
                final boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (final Exception ex) {
            System.err.println(ex);
        }
    }

    private void createPolicyInputAndCallHandler(final String fileName) throws PolicyDecodingException {
        final Csar csarObject = new Csar(fileName);
        inputReceived(csarObject);
    }
}

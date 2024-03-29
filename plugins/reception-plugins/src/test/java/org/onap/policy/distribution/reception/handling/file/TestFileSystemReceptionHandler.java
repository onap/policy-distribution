/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
 *  Copyright (C) 2019-2020, 2022 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.reception.statistics.DistributionStatisticsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to perform unit test of {@link FileSystemReceptionHandler}.
 */
class TestFileSystemReceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestFileSystemReceptionHandler.class);

    @TempDir
    public File tempFolder;

    private FileSystemReceptionHandlerConfigurationParameterGroup pssdConfigParameters;
    private FileSystemReceptionHandler fileSystemHandler;


    /**
     * Setup for the test cases.
     *
     * @throws IOException if it occurs
     * @throws SecurityException if it occurs
     * @throws IllegalArgumentException if it occurs
     */
    @BeforeEach
    final void init() throws IOException, SecurityException, IllegalArgumentException {
        DistributionStatisticsManager.resetAllStatistics();

        final var gson = new GsonBuilder().create();
        pssdConfigParameters = gson.fromJson(new FileReader("src/test/resources/handling-filesystem.json"),
                FileSystemReceptionHandlerConfigurationParameterGroup.class);
        ParameterService.register(pssdConfigParameters);
        fileSystemHandler = new FileSystemReceptionHandler();
    }

    @AfterEach
    void teardown() {
        ParameterService.deregister(pssdConfigParameters);
    }

    @Test
    final void testInit() throws IOException {
        var sypHandler = spy(fileSystemHandler);
        Mockito.doNothing().when(sypHandler).initFileWatcher(Mockito.isA(String.class),
                Mockito.anyInt());
        assertThatCode(() -> sypHandler.initializeReception(pssdConfigParameters.getName()))
            .doesNotThrowAnyException();
    }

    @Test
    final void testDestroy() throws IOException {
        final var sypHandler = spy(fileSystemHandler);
        Mockito.doNothing().when(sypHandler).initFileWatcher(Mockito.isA(String.class),
                Mockito.anyInt());
        assertThatCode(() -> {
            sypHandler.initializeReception(pssdConfigParameters.getName());
            sypHandler.destroy();
        }).doesNotThrowAnyException();
    }

    @Test
    void testMain() throws IOException {
        final var lock = new Object();
        final var watchPath = tempFolder.getAbsolutePath();

        class Processed {
            public boolean processed = false;
        }

        final var cond = new Processed();

        final var sypHandler = Mockito.spy(fileSystemHandler);
        Mockito.doAnswer((Answer<Object>) invocation -> {
            synchronized (lock) {
                cond.processed = true;
                lock.notifyAll();
            }
            return null;
        }).when(sypHandler).createPolicyInputAndCallHandler(Mockito.isA(String.class));

        final var th = new Thread(() -> {
            try {
                sypHandler.initFileWatcher(watchPath, 2);
            } catch (final IOException ex) {
                LOGGER.error("testMain failed", ex);
            }
        });

        th.start();
        try {
            // wait until internal watch service started or counter reached
            final var counter = new AtomicInteger();
            counter.set(0);
            synchronized (lock) {
                while (!sypHandler.isRunning() && counter.getAndIncrement() < 10) {
                    lock.wait(1000);
                }
            }
            Files.copy(Paths.get("src/test/resources/hpaPolicyHugePage.csar"),
                    Paths.get(watchPath + File.separator + "hpaPolicyHugePage.csar"));
            // wait until mock method triggered or counter reached
            counter.set(0);
            synchronized (lock) {
                while (!cond.processed && counter.getAndIncrement() < 10) {
                    lock.wait(1000);
                }
            }
            sypHandler.destroy();
            th.interrupt();
            th.join();
        } catch (final InterruptedException ex) {
            LOGGER.error("testMain failed", ex);
        }
        verify(sypHandler, Mockito.times(1)).createPolicyInputAndCallHandler(Mockito.isA(String.class));

    }
}

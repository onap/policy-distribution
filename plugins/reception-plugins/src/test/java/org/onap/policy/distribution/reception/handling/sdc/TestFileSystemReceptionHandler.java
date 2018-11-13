/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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

import static org.junit.Assert.fail;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.distribution.reception.statistics.DistributionStatisticsManager;

/**
 * Class to perform unit test of {@link FileSystemReceptionHandler}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestFileSystemReceptionHandler {

    private static final Logger LOGGER = FlexLogger.getLogger(TestFileSystemReceptionHandler.class);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private FileSystemReceptionHandlerConfigurationParameterGroup pssdConfigParameters;
    private FileSystemReceptionHandler fileSystemHandler;


    /**
     * Setup for the test cases.
     *
     * @throws IOException if it occurs
     * @throws SecurityException if it occurs
     * @throws NoSuchFieldException if it occurs
     * @throws IllegalAccessException if it occurs
     * @throws IllegalArgumentException if it occurs
     */
    @Before
    public final void init() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
        DistributionStatisticsManager.resetAllStatistics();

        final Gson gson = new GsonBuilder().create();
        pssdConfigParameters = gson.fromJson(new FileReader("src/test/resources/handling-filesystem.json"),
                FileSystemReceptionHandlerConfigurationParameterGroup.class);
        ParameterService.register(pssdConfigParameters);
        fileSystemHandler = new FileSystemReceptionHandler();
    }

    @After
    public void teardown() {
        ParameterService.deregister(pssdConfigParameters);
    }

    @Test
    public final void testInit() throws IOException {
        final FileSystemReceptionHandler sypHandler = Mockito.spy(fileSystemHandler);
        Mockito.doNothing().when(sypHandler).main(Mockito.isA(String.class));
        sypHandler.initializeReception(pssdConfigParameters.getName());
        Mockito.verify(sypHandler, Mockito.times(1)).main(Mockito.isA(String.class));
    }

    @Test
    public final void testDestroy() throws IOException {
        try {
            final FileSystemReceptionHandler sypHandler = Mockito.spy(fileSystemHandler);
            Mockito.doNothing().when(sypHandler).main(Mockito.isA(String.class));
            sypHandler.initializeReception(pssdConfigParameters.getName());
            sypHandler.destroy();
        } catch (final Exception exp) {
            LOGGER.error(exp);
            fail("Test should not throw any exception");
        }

    }

    @Test
    public void testMain() throws IOException, PolicyDecodingException {
        final Object lock = new Object();
        final String watchPath = tempFolder.getRoot().getAbsolutePath().toString();

        class Processed {
            public boolean processed = false;
        }

        Processed cond = new Processed();

        final FileSystemReceptionHandler sypHandler = Mockito.spy(fileSystemHandler);
        Mockito.doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                synchronized (lock) {
                    cond.processed = true;
                    lock.notifyAll();
                }
                return null;
            }
        }).when(sypHandler).createPolicyInputAndCallHandler(Mockito.isA(String.class));

        Thread th = new Thread(() -> {
            try {
                sypHandler.main(watchPath);
            } catch (IOException ex) {
                LOGGER.error(ex);
            }
        });

        th.start();
        try {
            //wait until internal watch service started or counter reached
            AtomicInteger counter = new AtomicInteger();
            counter.set(0);
            synchronized (lock) {
                while (!sypHandler.isRunning() && counter.getAndIncrement() < 10) {
                    lock.wait(1000);
                }
            }
            Files.copy(Paths.get("src/test/resources/hpaPolicyHugePage.csar"),
                Paths.get(watchPath + File.separator + "hpaPolicyHugePage.csar"));
            //wait until mock method triggered or counter reached
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
            LOGGER.error(ex);
        }
        Mockito.verify(sypHandler, Mockito.times(1))
            .createPolicyInputAndCallHandler(Mockito.isA(String.class));

    }
}


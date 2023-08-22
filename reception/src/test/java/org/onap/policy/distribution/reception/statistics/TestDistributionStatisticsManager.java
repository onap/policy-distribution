/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
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

package org.onap.policy.distribution.reception.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Class to perform unit test of DistributionStatisticsManager.
 *
 * @author Adheli Tavares (adheli.tavares@est.tech)
 * */
class TestDistributionStatisticsManager {


    @BeforeEach
    void reset() {
        DistributionStatisticsManager.resetAllStatistics();
    }

    @Test
    void testUpdateTotalDistributionCount() {
        DistributionStatisticsManager.updateTotalDistributionCount();
        assertEquals(1L, DistributionStatisticsManager.getTotalDistributionCount());
    }

    @Test
    void testUpdateDistributionSuccessCount() {
        DistributionStatisticsManager.updateDistributionSuccessCount();
        assertEquals(1L, DistributionStatisticsManager.getDistributionSuccessCount());
    }

    @Test
    void testUpdateDistributionFailureCount() {
        DistributionStatisticsManager.updateDistributionFailureCount();
        assertEquals(1L, DistributionStatisticsManager.getDistributionFailureCount());
    }

    @Test
    void testUpdateTotalDownloadCount() {
        DistributionStatisticsManager.updateTotalDownloadCount();
        assertEquals(1L, DistributionStatisticsManager.getTotalDownloadCount());
    }

    @Test
    void testUpdateDownloadSuccessCount() {
        DistributionStatisticsManager.updateDownloadSuccessCount();
        assertEquals(1L, DistributionStatisticsManager.getDownloadSuccessCount());
    }

    @Test
    void testUpdateDownloadFailureCount() {
        DistributionStatisticsManager.updateDownloadFailureCount();
        assertEquals(1L, DistributionStatisticsManager.getDownloadFailureCount());
    }

    @Test
    void testGetTotalDistributionCount() {
        assertEquals(0L, DistributionStatisticsManager.getTotalDistributionCount());
    }

    @Test
    void testGetDistributionSuccessCount() {
        assertEquals(0L, DistributionStatisticsManager.getDistributionSuccessCount());
    }

    @Test
    void testGetDistributionFailureCount() {
        assertEquals(0L, DistributionStatisticsManager.getDistributionFailureCount());
    }

    @Test
    void testGetTotalDownloadCount() {
        assertEquals(0L, DistributionStatisticsManager.getTotalDownloadCount());
    }

    @Test
    void testGetDownloadSuccessCount() {
        assertEquals(0L, DistributionStatisticsManager.getDownloadSuccessCount());
    }

    @Test
    void testGetDownloadFailureCount() {
        assertEquals(0L, DistributionStatisticsManager.getDownloadFailureCount());
    }
}

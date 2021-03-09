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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Class to perform unit test of DistributionStatisticsManager.
 *
 * @author Adheli Tavares (adheli.tavares@est.tech)
 * */
public class TestDistributionStatisticsManager {


    @Before
    public void reset() {
        DistributionStatisticsManager.resetAllStatistics();
    }

    @Test
    public void testUpdateTotalDistributionCount() {
        DistributionStatisticsManager.updateTotalDistributionCount();
        assertEquals(1L, DistributionStatisticsManager.getTotalDistributionCount());
    }

    @Test
    public void testUpdateDistributionSuccessCount() {
        DistributionStatisticsManager.updateDistributionSuccessCount();
        assertEquals(1L, DistributionStatisticsManager.getDistributionSuccessCount());
    }

    @Test
    public void testUpdateDistributionFailureCount() {
        DistributionStatisticsManager.updateDistributionFailureCount();
        assertEquals(1L, DistributionStatisticsManager.getDistributionFailureCount());
    }

    @Test
    public void testUpdateTotalDownloadCount() {
        DistributionStatisticsManager.updateTotalDownloadCount();
        assertEquals(1L, DistributionStatisticsManager.getTotalDownloadCount());
    }

    @Test
    public void testUpdateDownloadSuccessCount() {
        DistributionStatisticsManager.updateDownloadSuccessCount();
        assertEquals(1L, DistributionStatisticsManager.getDownloadSuccessCount());
    }

    @Test
    public void testUpdateDownloadFailureCount() {
        DistributionStatisticsManager.updateDownloadFailureCount();
        assertEquals(1L, DistributionStatisticsManager.getDownloadFailureCount());
    }

    @Test
    public void testGetTotalDistributionCount() {
        assertEquals(0L, DistributionStatisticsManager.getTotalDistributionCount());
    }

    @Test
    public void testGetDistributionSuccessCount() {
        assertEquals(0L, DistributionStatisticsManager.getDistributionSuccessCount());
    }

    @Test
    public void testGetDistributionFailureCount() {
        assertEquals(0L, DistributionStatisticsManager.getDistributionFailureCount());
    }

    @Test
    public void testGetTotalDownloadCount() {
        assertEquals(0L, DistributionStatisticsManager.getTotalDownloadCount());
    }

    @Test
    public void testGetDownloadSuccessCount() {
        assertEquals(0L, DistributionStatisticsManager.getDownloadSuccessCount());
    }

    @Test
    public void testGetDownloadFailureCount() {
        assertEquals(0L, DistributionStatisticsManager.getDownloadFailureCount());
    }
}

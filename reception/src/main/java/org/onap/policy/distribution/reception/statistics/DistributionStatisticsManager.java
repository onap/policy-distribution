/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
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

import io.prometheus.client.Counter;

/**
 * Class to hold statistical data for distribution component.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class DistributionStatisticsManager {

    private static final Counter TOTAL_DISTRIBUTION_RECEIVED_COUNT = Counter.build()
                    .name("total_distribution_received_count")
                    .help("Total number of distribution received.").register();
    private static final Counter DISTRIBUTION_SUCCESS_COUNT = Counter.build()
                    .name("distribution_success_count")
                    .help("Total number of distribution successfully processed.").register();

    private static final Counter DISTRIBUTION_FAILURE_COUNT = Counter.build()
                    .name("distribution_failure_count")
                    .help("Total number of distribution failures.").register();

    private static final Counter TOTAL_DOWNLOAD_RECEIVED_COUNT = Counter.build()
                    .name("total_download_received_count")
                    .help("Total number of download received.").register();

    private static final Counter DOWNLOAD_SUCCESS_COUNT = Counter.build()
                    .name("download_success_count")
                    .help("Total number of download successfully processed.").register();

    private static final Counter DOWNLOAD_FAILURE_COUNT = Counter.build()
                    .name("download_failure_count")
                    .help("Total number of download failures.").register();

    private DistributionStatisticsManager() {
        throw new IllegalStateException("Instantiation of the class is not allowed");
    }

    /**
     * Method to update the total distribution count.
     *
     */
    public static void updateTotalDistributionCount() {
        TOTAL_DISTRIBUTION_RECEIVED_COUNT.inc();
    }

    /**
     * Method to update the distribution success count.
     *
     */
    public static void updateDistributionSuccessCount() {
        DISTRIBUTION_SUCCESS_COUNT.inc();
    }

    /**
     * Method to update the distribution failure count.
     *
     */
    public static void updateDistributionFailureCount() {
        DISTRIBUTION_FAILURE_COUNT.inc();
    }

    /**
     * Method to update the total download count.
     *
     */
    public static void updateTotalDownloadCount() {
        TOTAL_DOWNLOAD_RECEIVED_COUNT.inc();
    }

    /**
     * Method to update the download success count.
     *
     */
    public static void updateDownloadSuccessCount() {
        DOWNLOAD_SUCCESS_COUNT.inc();
    }

    /**
     * Method to update the download failure count.
     *
     */
    public static void updateDownloadFailureCount() {
        DOWNLOAD_FAILURE_COUNT.inc();
    }

    /**
     * Returns the current value of totalDistributionCount.
     *
     * @return the totalDistributionCount
     */
    public static long getTotalDistributionCount() {
        return (long) TOTAL_DISTRIBUTION_RECEIVED_COUNT.get();
    }

    /**
     * Returns the current value of distributionSuccessCount.
     *
     * @return the distributionSuccessCount
     */
    public static long getDistributionSuccessCount() {
        return (long) DISTRIBUTION_SUCCESS_COUNT.get();
    }

    /**
     * Returns the current value of distributionFailureCount.
     *
     * @return the distributionFailureCount
     */
    public static long getDistributionFailureCount() {
        return (long) DISTRIBUTION_FAILURE_COUNT.get();
    }

    /**
     * Returns the current value of totalDownloadCount.
     *
     * @return the totalDownloadCount
     */
    public static long getTotalDownloadCount() {
        return (long) TOTAL_DOWNLOAD_RECEIVED_COUNT.get();
    }

    /**
     * Returns the current value of downloadSuccessCount.
     *
     * @return the downloadSuccessCount
     */
    public static long getDownloadSuccessCount() {
        return (long) DOWNLOAD_SUCCESS_COUNT.get();
    }

    /**
     * Returns the current value of downloadFailureCount.
     *
     * @return the downloadFailureCount
     */
    public static long getDownloadFailureCount() {
        return (long) DOWNLOAD_FAILURE_COUNT.get();
    }

    /**
     * Reset all the statistics counts to 0.
     */
    public static void resetAllStatistics() {
        TOTAL_DISTRIBUTION_RECEIVED_COUNT.clear();
        DISTRIBUTION_SUCCESS_COUNT.clear();
        DISTRIBUTION_FAILURE_COUNT.clear();
        TOTAL_DOWNLOAD_RECEIVED_COUNT.clear();
        DOWNLOAD_SUCCESS_COUNT.clear();
        DOWNLOAD_FAILURE_COUNT.clear();
    }
}

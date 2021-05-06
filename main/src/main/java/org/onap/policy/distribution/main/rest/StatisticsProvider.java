/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.distribution.main.rest;

import org.onap.policy.distribution.main.startstop.DistributionActivator;
import org.onap.policy.distribution.reception.statistics.DistributionStatisticsManager;

/**
 * Class to fetch statistics of distribution service.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class StatisticsProvider {

    /**
     * Returns the current statistics of distribution service.
     *
     * @return Report containing statistics of distribution service
     */
    public StatisticsReport fetchCurrentStatistics() {
        final var report = new StatisticsReport();
        report.setCode(DistributionActivator.isAlive() ? 200 : 500);
        report.setTotalDistributionCount(DistributionStatisticsManager.getTotalDistributionCount());
        report.setDistributionSuccessCount(DistributionStatisticsManager.getDistributionSuccessCount());
        report.setDistributionFailureCount(DistributionStatisticsManager.getDistributionFailureCount());
        report.setTotalDownloadCount(DistributionStatisticsManager.getTotalDownloadCount());
        report.setDownloadSuccessCount(DistributionStatisticsManager.getDownloadSuccessCount());
        report.setDownloadFailureCount(DistributionStatisticsManager.getDownloadFailureCount());
        return report;
    }
}

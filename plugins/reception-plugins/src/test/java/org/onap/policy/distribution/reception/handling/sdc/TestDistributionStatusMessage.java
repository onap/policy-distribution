/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.onap.sdc.utils.DistributionStatusEnum;

class TestDistributionStatusMessage {

    private static final String ARTIFACT_URL = "http://dummyurl";
    private static final String CONSUMER_ID = "dummyId";
    private static final String DISTRIBUTION_ID = "dummyDistribution";

    @Test
    void testDistributionStatusMessage_Download() {
        final var timestamp = System.currentTimeMillis();

        final var message = DistributionStatusMessage.builder().artifactUrl(ARTIFACT_URL)
                        .consumerId(CONSUMER_ID).distributionId(DISTRIBUTION_ID)
                        .distributionStatus(DistributionStatusEnum.DOWNLOAD_OK).timestamp(timestamp).build();
        assertEquals(ARTIFACT_URL, message.getArtifactURL());
        assertEquals(CONSUMER_ID, message.getConsumerID());
        assertEquals(DISTRIBUTION_ID, message.getDistributionID());
        assertEquals(DistributionStatusEnum.DOWNLOAD_OK, message.getStatus());
        assertEquals(timestamp, message.getTimestamp());
    }

    @Test
    void testDistributionStatusMessage_Deploy() {
        final var timestamp = System.currentTimeMillis();

        final var message = DistributionStatusMessage.builder().artifactUrl(ARTIFACT_URL)
                        .consumerId(CONSUMER_ID).distributionId(DISTRIBUTION_ID)
                        .distributionStatus(DistributionStatusEnum.DEPLOY_OK).timestamp(timestamp).build();
        assertEquals(ARTIFACT_URL, message.getArtifactURL());
        assertEquals(CONSUMER_ID, message.getConsumerID());
        assertEquals(DISTRIBUTION_ID, message.getDistributionID());
        assertEquals(DistributionStatusEnum.DEPLOY_OK, message.getStatus());
        assertEquals(timestamp, message.getTimestamp());
    }
}

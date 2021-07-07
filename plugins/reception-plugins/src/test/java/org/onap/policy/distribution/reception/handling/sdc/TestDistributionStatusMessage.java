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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.sdc.utils.DistributionStatusEnum;

public class TestDistributionStatusMessage {

    private final String artifactUrl = "http://dummyurl";
    private final String consumerId = "dummyId";
    private final String distributionId = "dummyDistribution";

    @Test
    public void testDistributionStatusMessage_Download() {
        final long timestamp = System.currentTimeMillis();

        final DistributionStatusMessage message = DistributionStatusMessage.builder().artifactUrl(artifactUrl)
                        .consumerId(consumerId).distributionId(distributionId)
                        .distributionStatus(DistributionStatusEnum.DOWNLOAD_OK).timestamp(timestamp).build();
        assertEquals(artifactUrl, message.getArtifactURL());
        assertEquals(consumerId, message.getConsumerID());
        assertEquals(distributionId, message.getDistributionID());
        assertEquals(DistributionStatusEnum.DOWNLOAD_OK, message.getStatus());
        assertEquals(timestamp, message.getTimestamp());
    }

    @Test
    public void testDistributionStatusMessage_Deploy() {
        final long timestamp = System.currentTimeMillis();

        final DistributionStatusMessage message = DistributionStatusMessage.builder().artifactUrl(artifactUrl)
                        .consumerId(consumerId).distributionId(distributionId)
                        .distributionStatus(DistributionStatusEnum.DEPLOY_OK).timestamp(timestamp).build();
        assertEquals(artifactUrl, message.getArtifactURL());
        assertEquals(consumerId, message.getConsumerID());
        assertEquals(distributionId, message.getDistributionID());
        assertEquals(DistributionStatusEnum.DEPLOY_OK, message.getStatus());
        assertEquals(timestamp, message.getTimestamp());
    }
}

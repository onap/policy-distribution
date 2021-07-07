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

public class TestComponentDoneStatusMessage {

    private static final String CONSUMER_ID = "dummyId";
    private static final String DISTRIBUTION_ID = "dummyDistribution";

    @Test
    public void testComponentDoneStatusMessage_Success() {
        final long timestamp = System.currentTimeMillis();
        final ComponentDoneStatusMessage message = ComponentDoneStatusMessage.builder().consumerId(CONSUMER_ID)
                        .distributionId(DISTRIBUTION_ID).distributionStatus(DistributionStatusEnum.COMPONENT_DONE_OK)
                        .timestamp(timestamp).build();
        assertEquals("POLICY", message.getComponentName());
        assertEquals(CONSUMER_ID, message.getConsumerID());
        assertEquals(DISTRIBUTION_ID, message.getDistributionID());
        assertEquals(DistributionStatusEnum.COMPONENT_DONE_OK, message.getStatus());
        assertEquals(timestamp, message.getTimestamp());
    }

    @Test
    public void testComponentDoneStatusMessage_Failure() {
        final long timestamp = System.currentTimeMillis();
        final ComponentDoneStatusMessage message = ComponentDoneStatusMessage.builder().consumerId(CONSUMER_ID)
                        .distributionId(DISTRIBUTION_ID).distributionStatus(DistributionStatusEnum.COMPONENT_DONE_ERROR)
                        .timestamp(timestamp).build();
        assertEquals("POLICY", message.getComponentName());
        assertEquals(CONSUMER_ID, message.getConsumerID());
        assertEquals(DISTRIBUTION_ID, message.getDistributionID());
        assertEquals(DistributionStatusEnum.COMPONENT_DONE_ERROR, message.getStatus());
        assertEquals(timestamp, message.getTimestamp());
    }
}

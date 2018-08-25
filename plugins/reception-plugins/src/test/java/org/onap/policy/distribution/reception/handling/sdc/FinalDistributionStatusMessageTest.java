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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.sdc.utils.DistributionStatusEnum;

/*-
 * Tests for FinalDistributionStatusMessage class
 *
 */
public class FinalDistributionStatusMessageTest {
    
    @Test
    public void testFinalDistributionStatusMessage() {
        final String distributionId = "AA97B177-9383-4934-8543-0F91A7A02836";
        final DistributionStatusEnum distributionStatusEnum = DistributionStatusEnum.DISTRIBUTION_COMPLETE_ERROR;
        final long timestampL = 7777L;
        final String consumerId = "Policy";
        final FinalDistributionStatusMessage message = new FinalDistributionStatusMessage(distributionId, 
                                                           distributionStatusEnum, timestampL, consumerId);      
        validateMessage(distributionId, distributionStatusEnum, timestampL, consumerId, message);
    }

    private void validateMessage(final String distributionId, final DistributionStatusEnum distributionStatusEnum, 
            final long timestampL, final String consumerId,
            final FinalDistributionStatusMessage message) {
        assertEquals(consumerId, message.getConsumerID());
        assertEquals(distributionId, message.getDistributionID());
        assertEquals(distributionStatusEnum, message.getStatus());
        assertEquals(timestampL, message.getTimestamp());
    }
}

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

import org.onap.sdc.api.consumer.IFinalDistrStatusMessage;
import org.onap.sdc.utils.DistributionStatusEnum;

/**
  * Used to make up the final distribution status message which to be sent back to DMAAP/SDC
  *
  */
public class FinalDistributionStatusMessage implements IFinalDistrStatusMessage {

	private String consumerID;
	
	private String distributionID;
	
	private DistributionStatusEnum status;
	
	private long timestamp;
	
	public FinalDistributionStatusMessage (String distributionId, final DistributionStatusEnum distributionStatusEnum, 
                                            final long timestampL, String consumerId) {
		this.consumerID = consumerId;
		this.distributionID = distributionId;
		this.status = distributionStatusEnum;
		this.timestamp = timestampL;
	}

    @Override
    public String getDistributionID() {
        return distributionID;
    }

    @Override
	public DistributionStatusEnum getStatus() {
		return status;
	}

    @Override
	public long getTimestamp() {
		return timestamp;
	}

    @Override
	public String getConsumerID() {
		return consumerID;
	}

}

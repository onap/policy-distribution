/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.sdc.api.consumer.INotificationCallback;
import org.onap.sdc.api.notification.INotificationData;

/**
 * Class to provide an implementation of INotificationCallback interface for receiving the incoming distribution
 * notifications from SDC.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class SdcNotificationCallBack implements INotificationCallback {

    private static final Logger LOGGER = FlexLogger.getLogger(SdcNotificationCallBack.class);

    private SdcReceptionHandler sdcReceptionHandler;

    public SdcNotificationCallBack(final SdcReceptionHandler sdcReceptionHandler) {
        this.sdcReceptionHandler = sdcReceptionHandler;
    }

    @Override
    public void activateCallback(final INotificationData notificationData) {
        LOGGER.debug("Receieved the notification from SDC with ID: " + notificationData.getDistributionID());
        sdcReceptionHandler.processCsarServiceArtifacts(notificationData);
        LOGGER.debug("Processed the notification from SDC with ID: " + notificationData.getDistributionID());
    }
}

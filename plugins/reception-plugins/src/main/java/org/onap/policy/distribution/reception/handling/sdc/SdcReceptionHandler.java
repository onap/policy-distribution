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

import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;

/**
 * Handles reception of inputs from ONAP Service Design and Creation (SDC) from which policies may
 * be decoded.
 */
public class SdcReceptionHandler extends AbstractReceptionHandler {

    @Override
    protected void initializeReception(String parameterGroupName) {
        // Set up subscription to SDC
    }

    // Add functionality for receiving SDC distibutions and invoking AbstractReceptionHandler
    // inputReceived()

    @Override
    public void destroy() {
        // Tear down subscription etc
    }

}

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
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.reception.decoding.PluginInitializationException;
import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.PssdControllerException;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;

/**
 * Handles reception of inputs from ONAP Service Design and Creation (SDC) from which policies may be decoded.
 */
public class SdcReceptionHandler extends AbstractReceptionHandler {

    private static final Logger LOGGER = FlexLogger.getLogger(SdcReceptionHandler.class);
    private PssdController pssdController;

    @Override
    protected void initializeReception(final String parameterGroupName) throws PluginInitializationException {
        try {
            final ReceptionHandlerParameters receptionHandlerParameters =
                    (ReceptionHandlerParameters) ParameterService.get(parameterGroupName);
            pssdController = new PssdController();
            pssdController.initializeSdcClient(receptionHandlerParameters.getPssdConfigurationParametersGroup());
            pssdController.startSdcClient();
        } catch (final PssdControllerException exp) {
            LOGGER.error(exp.getMessage(), exp);
            throw new PluginInitializationException(exp.getMessage(), exp);
        }
    }

    // Add functionality for receiving SDC distibutions and invoking AbstractReceptionHandler
    // inputReceived()

    @Override
    public void destroy() throws PluginInitializationException {
        try {
            pssdController.stopSdcClient();
        } catch (final PssdControllerException exp) {
            LOGGER.error(exp.getMessage(), exp);
            throw new PluginInitializationException(exp.getMessage(), exp);
        }
    }

}

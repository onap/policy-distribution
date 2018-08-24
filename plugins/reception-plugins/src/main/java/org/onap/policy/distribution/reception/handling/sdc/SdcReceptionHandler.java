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

import java.io.IOException;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.distribution.reception.handling.AbstractReceptionHandler;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerParameters;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.PssdControllerException;
import org.onap.policy.distribution.reception.handling.sdc.exceptions.PssdParametersException;

/**
 * Handles reception of inputs from ONAP Service Design and Creation (SDC) from which policies may
 * be decoded.
 */
public class SdcReceptionHandler extends AbstractReceptionHandler {
    private static final Logger LOGGER = FlexLogger.getLogger(SdcReceptionHandler.class);
    private PssdController pssdController;

    @Override
    protected void initializeReception(String parameterGroupName) {
        // Set up subscription to SDC
        final ReceptionHandlerParameters receptionHandlerParameters =
                (ReceptionHandlerParameters) ParameterService.get(parameterGroupName);
        if(pssdController == null){
            pssdController = new PssdController(receptionHandlerParameters.getName());
        }
        // Set up subscription to SDC
        try{
            pssdController.initPssd(receptionHandlerParameters.getPssdConfigurationParametersGroup());
        } catch (PssdParametersException | PssdControllerException | IOException e){
            LOGGER.error(e.getMessage(), e);
            return;
        }
        LOGGER.debug("init Pssd successfully");
    }

    // Add functionality for receiving SDC distibutions and invoking AbstractReceptionHandler
    // inputReceived()

    @Override
    public void destroy() {
        // Tear down subscription etc
        if(pssdController !=null){
            try{
                pssdController.closePssd();
            } catch (PssdControllerException e){
                LOGGER.warn("fail to tear down, try it later", e);
                return;
            }
            LOGGER.debug("destroy Pssd successfully");
            pssdController = null; 
        }
    }

}

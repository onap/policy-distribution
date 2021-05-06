/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.distribution.main.parameters;

import com.google.gson.GsonBuilder;
import java.io.FileReader;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.distribution.main.PolicyDistributionException;
import org.onap.policy.distribution.main.startstop.DistributionCommandLineArguments;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.PolicyDecoderConfigurationParametersJsonAdapter;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParametersJsonAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles reading, parsing and validating of policy distribution parameters from JSON files.
 */
public class DistributionParameterHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributionParameterHandler.class);

    /**
     * Read the parameters from the parameter file.
     *
     * @param arguments the arguments passed to policy distribution
     * @return the parameters read from the configuration file
     * @throws PolicyDistributionException on parameter exceptions
     */
    public DistributionParameterGroup getParameters(final DistributionCommandLineArguments arguments)
            throws PolicyDistributionException {
        DistributionParameterGroup distributionParameterGroup = null;

        // Read the parameters
        try {
            // Read the parameters from JSON using Gson
            final var gson = new GsonBuilder()
                    .registerTypeAdapter(PolicyForwarderConfigurationParameterGroup.class,
                            new PolicyForwarderConfigurationParametersJsonAdapter())
                    .registerTypeAdapter(ReceptionHandlerConfigurationParameterGroup.class,
                            new ReceptionHandlerConfigurationParametersJsonAdapter())
                    .registerTypeAdapter(PolicyDecoderConfigurationParameterGroup.class,
                            new PolicyDecoderConfigurationParametersJsonAdapter())
                    .create();
            distributionParameterGroup = gson.fromJson(new FileReader(arguments.getFullConfigurationFilePath()),
                    DistributionParameterGroup.class);
        } catch (final Exception e) {
            final String errorMessage = "error reading parameters from \"" + arguments.getConfigurationFilePath()
                    + "\"\n" + "(" + e.getClass().getSimpleName() + "):" + e.getMessage();
            throw new PolicyDistributionException(errorMessage, e);
        }

        // The JSON processing returns null if there is an empty file
        if (distributionParameterGroup == null) {
            final String errorMessage = "no parameters found in \"" + arguments.getConfigurationFilePath() + "\"";
            LOGGER.error(errorMessage);
            throw new PolicyDistributionException(errorMessage);
        }

        // validate the parameters
        final ValidationResult validationResult = distributionParameterGroup.validate();
        if (!validationResult.isValid()) {
            String returnMessage =
                    "validation error(s) on parameters from \"" + arguments.getConfigurationFilePath() + "\"\n";
            returnMessage += validationResult.getResult();

            LOGGER.error(returnMessage);
            throw new PolicyDistributionException(returnMessage);
        }

        return distributionParameterGroup;
    }
}

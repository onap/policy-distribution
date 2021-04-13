/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 AT&T Intellectual Property.
 *  Modifications Copyright (C) 2021 Nordix Foundation.
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

package org.onap.policy.distribution.main.startstop;

import org.onap.policy.common.utils.cmd.CommandLineArgumentsHandler;
import org.onap.policy.common.utils.cmd.CommandLineException;
import org.onap.policy.common.utils.resources.MessageConstants;
import org.onap.policy.distribution.main.PolicyDistributionRuntimeException;

/**
 * This class reads and handles command line parameters for the policy distribution main program.
 */
public class DistributionCommandLineArguments extends CommandLineArgumentsHandler {

    /**
     * Construct the options for the CLI editor from super.
     */
    public DistributionCommandLineArguments() {
        super(Main.class.getName(), MessageConstants.POLICY_DISTRIBUTION);
    }

    /**
     * Construct the options for the CLI editor and parse in the given arguments.
     *
     * @param args The command line arguments
     */
    public DistributionCommandLineArguments(final String[] args) {
        this();

        try {
            parse(args);
        } catch (final CommandLineException e) {
            throw new PolicyDistributionRuntimeException("parse error on policy distribution parameters", e);
        }
    }
}

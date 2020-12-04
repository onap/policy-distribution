/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2020 AT&T Corp.
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

import org.onap.policy.common.parameters.ParameterGroup;
import org.onap.policy.common.parameters.ParameterGroupImpl;
import org.onap.policy.distribution.forwarding.PolicyForwarder;

/**
 * Base class of all {@link ParameterGroup} classes for configuration parameters for {@link PolicyForwarder} classes.
 */
public abstract class PolicyForwarderConfigurationParameterGroup extends ParameterGroupImpl {

    protected PolicyForwarderConfigurationParameterGroup(final String name) {
        super(name);
    }
}

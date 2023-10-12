/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.distribution.forwarding.testclasses;

import java.util.List;
import org.onap.policy.common.endpoints.http.server.RestServer;
import org.onap.policy.common.endpoints.parameters.RestServerParameters;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.onap.policy.distribution.forwarding.PolicyForwardingException;
import org.onap.policy.distribution.forwarding.lifecycle.api.LifecycleApiPolicyForwarder;

/**
 * The class for starting/stopping simulator for testing {@link LifecycleApiPolicyForwarder} .
 *
 * @author Ram Krishna Verma (ram.krishna.verma@est.tech)
 */
public class LifecycleApiSimulatorMain {
    private RestServer restServer;

    /**
     * Starts the simulator.
     *
     * @throws PolicyForwardingException if error occurs
     * @throws CoderException if error occurs
     */
    public void startLifecycleApiSimulator() throws PolicyForwardingException, CoderException {
        final StandardCoder standardCoder = new StandardCoder();
        final RestServerParameters restServerParameters = standardCoder.decode(
                ResourceUtils.getResourceAsString("src/test/resources/parameters/RestServerParameters.json"),
                RestServerParameters.class);
        restServer = new RestServer(restServerParameters, List.of(),
                List.of(LifecycycleApiSimulatorEndpoint.class));
        if (!restServer.start()) {
            throw new PolicyForwardingException("Failed to start rest simulator. Check log for more details...");
        }
    }

    /**
     * Shut down Execution.
     */
    public void stopLifecycleApiSimulator() {
        if (restServer != null) {
            restServer.stop();
        }
    }
}

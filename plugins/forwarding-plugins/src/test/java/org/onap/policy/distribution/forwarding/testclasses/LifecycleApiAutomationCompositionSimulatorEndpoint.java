/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Nordix Foundation.
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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;

/**
 * Class to provide rest end points for LifecycleApiAutomationCompositionSimulator.
 *
 * @author Sirisha Manchikanti (sirisha.manchikanti@est.tech)
 */
@Path("/onap")
@Produces(MediaType.APPLICATION_JSON)
public class LifecycleApiAutomationCompositionSimulatorEndpoint {

    /**
     * AutomationComposition commissioning end-point.
     *
     * @param body the post body
     * @return the response object
     */
    @POST
    @Path("/acm/v2/commission")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response commissionAutomationComposition(final ToscaServiceTemplate body) {
        return Response.status(Response.Status.OK).entity(body).build();
    }
}

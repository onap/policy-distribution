/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada.
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

import io.swagger.annotations.ApiParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.onap.policy.models.pap.concepts.PdpDeployPolicies;
import org.onap.policy.models.pap.concepts.PdpGroupDeployResponse;
import org.onap.policy.models.tosca.authorative.concepts.ToscaServiceTemplate;

/**
 * Class to provide rest end points for LifecycycleApiSimulator.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@est.tech)
 */
@Path("/policy")
@Produces(MediaType.APPLICATION_JSON)
public class LifecycycleApiSimulatorEndpoint {

    /**
     * Create policy type endpoint.
     *
     * @param body the post body
     * @return the response object
     */
    @POST
    @Path("/api/v1/policytypes")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPolicyTypes(final ToscaServiceTemplate body) {
        return Response.status(Response.Status.OK).entity(body).build();
    }

    /**
     * Create policy endpoint.
     *
     * @param policyTypeId the policy type id
     * @param policyTypeVersion the policy type version
     * @param body the post body
     * @return the response object
     */
    @POST
    @Path("/api/v1/policies")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPolicies(@PathParam("policyTypeId") final String policyTypeId,
            @PathParam("policyTypeVersion") final String policyTypeVersion,
            @ApiParam(value = "Entity body of policy", required = true) final ToscaServiceTemplate body) {
        if ("onap.policies.acm.operational.ApexFailure".equals(policyTypeId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(Response.Status.OK).entity(body).build();
        }
    }

    /**
     * Deploy policy endpoint.
     *
     * @param policies the post body
     * @return the response object
     */
    @POST
    @Path("/pap/v1/pdps/policies")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deployPolicies(final PdpDeployPolicies policies) {
        return Response.status(Response.Status.OK).entity(new PdpGroupDeployResponse()).build();
    }
}

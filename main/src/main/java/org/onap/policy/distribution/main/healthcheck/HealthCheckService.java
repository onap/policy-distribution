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

package org.onap.policy.distribution.main.healthcheck;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Class to represent health check REST service.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
@Path("/")
@Api
@Produces(MediaType.APPLICATION_JSON)
@SwaggerDefinition(
        info = @Info(description = "Policy SSD Healthcheck Service", version = "v1.0",
                title = "Policy SSD Healthcheck"),
        consumes = { MediaType.APPLICATION_JSON }, produces = { MediaType.APPLICATION_JSON },
        schemes = { SwaggerDefinition.Scheme.HTTP },
        tags = { @Tag(name = "policy-ssd-healthcheck", description = "Policy SSD Healthcheck Operations") })
public class HealthCheckService {

    @GET
    @Path("healthcheck")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Perform a system healthcheck", notes = "Provides healthy status of the Policy SSD component",
            response = HealthCheckReport.class)
    public Response healthcheck() {
        return Response.status(Response.Status.OK).entity(HealthCheckMonitor.healthCheckMonitor.healthCheck()).build();
    }
}

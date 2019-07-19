/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy API
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.distribution.main.rest.aaf;

import org.onap.policy.common.endpoints.http.server.aaf.AafGranularAuthFilter;

/**
 * Distribution AAF authorization filter.
 */
public class AafDistributionFilter extends AafGranularAuthFilter {

    public static final String AAF_NODETYPE = "policy-distribution";
    public static final String AAF_ROOT_PERMISSION = DEFAULT_NAMESPACE + "." + AAF_NODETYPE;

    @Override
    public String getPermissionTypeRoot() {
        return AAF_ROOT_PERMISSION;
    }
}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
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

import java.util.List;
import lombok.Getter;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;

/**
 * This class handles reading, parsing and validating of the Policy SDC Service Distribution parameters from Json
 * format, which strictly adheres to the interface:IConfiguration, defined by SDC SDK.
 */
@Getter
public class SdcReceptionHandlerConfigurationParameterGroup extends ReceptionHandlerConfigurationParameterGroup {
    private @NotNull @NotBlank String asdcAddress;
    private @NotNull @NotBlank List<String> messageBusAddress;
    private @NotNull @NotBlank String user;
    private @NotNull @NotBlank String password;
    private @NotNull @NotBlank int pollingInterval;
    private @NotNull @NotBlank int pollingTimeout;
    private @NotNull @NotBlank int retryDelay;
    private @NotNull @NotBlank String consumerId;
    private @NotNull @NotBlank List<String> artifactTypes;
    private @NotNull @NotBlank String consumerGroup;
    private @NotNull @NotBlank String environmentName;
    private String keyStorePath;
    private String keyStorePassword;
    private boolean activeServerTlsAuth;
    private boolean isFilterInEmptyResources;
    private boolean isUseHttpsWithDmaap;
    private boolean isUseHttpsWithSdc;

    public SdcReceptionHandlerConfigurationParameterGroup() {
        super(SdcReceptionHandlerConfigurationParameterGroup.class.getSimpleName());
    }
}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.distribution.reception.handling.file;

import java.io.File;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.BeanValidator;
import org.onap.policy.common.parameters.ObjectValidationResult;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.distribution.reception.parameters.ReceptionHandlerConfigurationParameterGroup;

/**
 * This class handles reading, parsing and validating of the Policy SDC Service Distribution parameters from Json
 * format, which strictly adheres to the interface:IConfiguration, defined by SDC SDK.
 */
@Getter
@Setter
@NotNull
@NotBlank
public class FileSystemReceptionHandlerConfigurationParameterGroup extends ReceptionHandlerConfigurationParameterGroup {

    private String watchPath;
    private int maxThread;

    public FileSystemReceptionHandlerConfigurationParameterGroup() {
        super(FileSystemReceptionHandlerConfigurationParameterGroup.class.getSimpleName());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public BeanValidationResult validate() {
        final BeanValidationResult validationResult = new BeanValidator().validateTop(getClass().getSimpleName(), this);
        validationResult.addResult(validatePathElement(watchPath, "watchPath"));
        return validationResult;
    }


    /**
     * Validate the string element.
     *
     * @param element the element to validate
     * @param elementName the element name for error message
     */
    private ValidationResult validatePathElement(final String element, final String elementName) {
        if (element != null) {
            final var file = new File(element);
            if (file.exists() && file.isDirectory()) {
                return null;
            }
        }

        return new ObjectValidationResult(elementName, element, ValidationStatus.INVALID, "is not a valid directory");
    }
}

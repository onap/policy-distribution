/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Intel. All rights reserved.
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

package org.onap.policy.distribution.reception.handling.sdc.exceptions;

/**
 * Exception of the Pssd controller.
 */
public class PssdParametersException extends Exception {

    /**
     * serialization id.
     */
    private static final long serialVersionUID = -8507246953751956974L;

    /**
     * Constructor for creating PssdParametersException using message.
     *
     * @param message The message to dump
     */
    public PssdParametersException(final String message) {
        super(message);

    }

    /**
     * Constructor for creating PssdParametersException using message and exception.
     *
     * @param message The message to dump
     * @param ex the exception that caused this exception to be thrown
     */
    public PssdParametersException(final String message, final Exception ex) {
        super(message, ex);

    }
}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.distribution.reception.parameters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class deserialises policy decoder parameters from JSON.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class PolicyDecoderConfigurationParametersJsonAdapter
        implements JsonDeserializer<PolicyDecoderConfigurationParameterGroup> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyDecoderConfigurationParametersJsonAdapter.class);

    private static final String PARAMETER_CLASS_NAME = "parameterClassName";
    private static final String POLICY_DECODER_PARAMETERS = "parameters";

    @Override
    public PolicyDecoderConfigurationParameterGroup deserialize(final JsonElement json, final Type typeOfT,
            final JsonDeserializationContext context) {
        final var jsonObject = json.getAsJsonObject();

        final String policyDecoderParameterClassName = getParameterGroupClassName(jsonObject);
        final Class<?> policyDecoderParameterClass = getParameterGroupClass(policyDecoderParameterClassName);

        return context.deserialize(jsonObject.get(POLICY_DECODER_PARAMETERS), policyDecoderParameterClass);
    }

    private String getParameterGroupClassName(final JsonObject jsonObject) {
        final var classNameJsonPrimitive = ((JsonPrimitive) jsonObject.get(PARAMETER_CLASS_NAME));

        if (classNameJsonPrimitive == null || classNameJsonPrimitive.getAsString().length() == 0) {
            final String errorMessage = "parameter \"" + PARAMETER_CLASS_NAME + "\" value \""
                    + (classNameJsonPrimitive != null ? classNameJsonPrimitive.getAsString() : "null")
                    + "\" invalid in JSON file";
            LOGGER.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        return classNameJsonPrimitive.getAsString().replaceAll("\\s+", "");
    }

    private Class<?> getParameterGroupClass(final String policyDecoderParameterClassName) {
        Class<?> policyDecoderParameterClass = null;
        try {
            policyDecoderParameterClass = Class.forName(policyDecoderParameterClassName);
        } catch (final ClassNotFoundException exp) {
            final String errorMessage = "parameter \"" + PARAMETER_CLASS_NAME + "\" value \""
                    + policyDecoderParameterClassName + "\", could not find class";
            throw new IllegalArgumentException(errorMessage, exp);
        }
        return policyDecoderParameterClass;
    }

}

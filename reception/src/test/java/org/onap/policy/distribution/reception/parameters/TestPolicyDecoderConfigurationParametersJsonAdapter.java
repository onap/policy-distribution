/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Test;
import org.onap.policy.distribution.reception.testclasses.DummyPolicyDecoderParameterGroup;

/**
 * Class for unit testing PolicyDecoderConfigurationParametersJsonAdapter class.
 *
 * @author Adheli Tavares (adheli.tavares@est.tech)
 *
 */
public class TestPolicyDecoderConfigurationParametersJsonAdapter {

    @Test
    public void testDeserialize() throws JsonSyntaxException, IOException {
        final String validJsonFile = "src/test/resources/PolicyDecoderConfiguration.json";
        final JsonElement mockJsonElement = JsonParser.parseString(getJsonValues(validJsonFile));

        Gson gson = new GsonBuilder().registerTypeAdapter(PolicyDecoderConfigurationParameterGroup.class,
                new PolicyDecoderConfigurationParametersJsonAdapter()).create();

        PolicyDecoderConfigurationParameterGroup result =
                gson.fromJson(mockJsonElement, PolicyDecoderConfigurationParameterGroup.class);

        assertNotNull(result);
        assertEquals(result.getClass(), DummyPolicyDecoderParameterGroup.class);
    }

    @Test
    public void testDeserialize_shouldThrowExceptionEmptyClassName() throws JsonSyntaxException, IOException {
        final String jsonFile = "src/test/resources/EmptyClassName.json";
        String expectedErrorMsg = "parameter \"parameterClassName\" value \"\" invalid in JSON file";

        validateParsing(jsonFile, expectedErrorMsg);
    }

    @Test
    public void testDeserialize_shouldThrowExceptionNullClassName() throws JsonSyntaxException, IOException {
        final String jsonFile = "src/test/resources/NullClassName.json";
        String expectedErrorMsg = "parameter \"parameterClassName\" value \"null\" invalid in JSON file";

        validateParsing(jsonFile, expectedErrorMsg);
    }

    @Test
    public void testDeserialize_shouldThrowExceptionWrongClassName() throws JsonSyntaxException, IOException {
        final String jsonFile = "src/test/resources/WrongClassName.json";
        String expectedErrorMsg = "parameter \"parameterClassName\" value "
                + "\"org.onap.policy.distribution.reception.testclasses.NotExistentClass\", could not find class";

        validateParsing(jsonFile, expectedErrorMsg);
    }

    private void validateParsing(final String jsonFile, String expectedErrorMsg) throws IOException {
        final JsonElement mockJsonElement = JsonParser.parseString(getJsonValues(jsonFile));

        Gson gson = new GsonBuilder().registerTypeAdapter(PolicyDecoderConfigurationParameterGroup.class,
                new PolicyDecoderConfigurationParametersJsonAdapter()).create();

        assertThatThrownBy(() -> gson.fromJson(mockJsonElement, PolicyDecoderConfigurationParameterGroup.class))
                .isInstanceOf(IllegalArgumentException.class).hasMessage(expectedErrorMsg);
    }

    private String getJsonValues(String path) throws IOException {
        return new String(Files.readAllBytes(new File(path).toPath()), StandardCharsets.UTF_8);
    }

}

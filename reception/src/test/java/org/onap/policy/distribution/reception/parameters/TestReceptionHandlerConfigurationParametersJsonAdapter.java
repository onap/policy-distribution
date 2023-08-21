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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.onap.policy.distribution.reception.testclasses.DummyReceptionHandlerParameterGroup;

/**
 * Class for unit testing ReceptionHandlerConfigurationParametersJsonAdapter class.
 *
 * @author Adheli Tavares (adheli.tavares@est.tech)
 *
 */
class TestReceptionHandlerConfigurationParametersJsonAdapter {

    @Test
    void testDeserialize() throws JsonSyntaxException, IOException {
        final var validJsonFile = "src/test/resources/ReceptionHandlerConfiguration.json";
        final var mockJsonElement = JsonParser.parseString(getJsonValues(validJsonFile));

        var gson = new GsonBuilder().registerTypeAdapter(ReceptionHandlerConfigurationParameterGroup.class,
                new ReceptionHandlerConfigurationParametersJsonAdapter()).create();

        var result =
                gson.fromJson(mockJsonElement, ReceptionHandlerConfigurationParameterGroup.class);

        assertNotNull(result);
        assertEquals(result.getClass(), DummyReceptionHandlerParameterGroup.class);
    }

    @Test
    void testDeserialize_shouldThrowExceptionEmptyClassName() throws JsonSyntaxException, IOException {
        final var jsonFile = "src/test/resources/EmptyClassName.json";
        var expectedErrorMsg = "parameter \"parameterClassName\" value \"\" invalid in JSON file";

        validateParsing(jsonFile, expectedErrorMsg);
    }

    @Test
    void testDeserialize_shouldThrowExceptionNullClassName() throws JsonSyntaxException, IOException {
        final var jsonFile = "src/test/resources/NullClassName.json";
        var expectedErrorMsg = "parameter \"parameterClassName\" value \"null\" invalid in JSON file";

        validateParsing(jsonFile, expectedErrorMsg);
    }

    @Test
    void testDeserialize_shouldThrowExceptionWrongClassName() throws JsonSyntaxException, IOException {
        final var jsonFile = "src/test/resources/WrongClassName.json";
        var expectedErrorMsg = "parameter \"parameterClassName\" value "
                + "\"org.onap.policy.distribution.reception.testclasses.NotExistentClass\", could not find class";

        validateParsing(jsonFile, expectedErrorMsg);
    }

    private void validateParsing(final String jsonFile, String expectedErrorMsg) throws IOException {
        final var mockJsonElement = JsonParser.parseString(getJsonValues(jsonFile));

        var gson = new GsonBuilder().registerTypeAdapter(ReceptionHandlerConfigurationParameterGroup.class,
                new ReceptionHandlerConfigurationParametersJsonAdapter()).create();

        assertThatThrownBy(() -> gson.fromJson(mockJsonElement, ReceptionHandlerConfigurationParameterGroup.class))
                .isInstanceOf(IllegalArgumentException.class).hasMessage(expectedErrorMsg);
    }

    private String getJsonValues(String path) throws IOException {
        return Files.readString(new File(path).toPath());
    }
}

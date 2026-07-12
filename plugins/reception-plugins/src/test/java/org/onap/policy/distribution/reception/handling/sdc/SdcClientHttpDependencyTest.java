/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2026 Deutsche Telekom. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

/**
 * Guards the runtime classpath of the SDC distribution client.
 *
 * <p>sdc-distribution-client uses Apache HttpClient 4.x (the {@code org.apache.http}
 * namespace) when it opens its connection to SDC in
 * {@code DistributionClientImpl.createSdcConnector()}. The policy parent only manages
 * httpclient5, so unless reception-plugins declares httpclient 4.x explicitly it is
 * absent from the packaged image and the client fails at runtime with
 * {@code NoClassDefFoundError: org/apache/http/client/methods/HttpUriRequest}.
 *
 * <p>The functional tests mock the distribution client, so they never load these
 * classes; this test loads them directly to ensure the dependency stays on the
 * classpath.
 */
class SdcClientHttpDependencyTest {

    @Test
    void apacheHttpClient4ClassesArePresentOnClasspath() {
        assertThatCode(() -> {
            Class.forName("org.apache.http.client.methods.HttpUriRequest");
            Class.forName("org.apache.http.HttpHost");
            Class.forName("org.apache.http.impl.client.CloseableHttpClient");
        }).doesNotThrowAnyException();
    }
}

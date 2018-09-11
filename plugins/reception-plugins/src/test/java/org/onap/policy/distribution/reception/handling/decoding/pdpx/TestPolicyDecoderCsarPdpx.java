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

package org.onap.policy.distribution.reception.decoding.pdpx;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.onap.policy.distribution.model.Csar;

/**
 * Class to perform unit test of {@link PolicyDecoderCsarPdpx}.
 *
 */
public class TestPolicyDecoderCsarPdpx {

    @Test
    public void testHpaPolicy2Vnf() throws IOException {
        Csar csar = new Csar("src/test/resources/service-TestNs8-csar.csar");

        PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        try {
            Collection<PdpxPolicy> ret = policyDecoderCsarPdpx.decode(csar);
            System.out.println("size = " + ret.size());
            assertEquals(2, ret.size());
        } catch (Exception e) {
            System.out.println("Test out " + e.getMessage());
        }
    }

    @Test
    public void testHpaPolicyFeature() throws IOException {
        Csar csar = new Csar("src/test/resources/hpaPolicySRIOV.csar");

        PolicyDecoderCsarPdpx policyDecoderCsarPdpx = new PolicyDecoderCsarPdpx();
        try {
            Collection<PdpxPolicy> ret = policyDecoderCsarPdpx.decode(csar);
            policyDecoderCsarPdpx.decode(csar);
            System.out.println("size = " + ret.size());
            assertEquals(2, ret.size());
        } catch (Exception e) {
            System.out.println("Test out " + e.getMessage());
        }
    }
}

/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import java.util.Arrays;
import java.util.Collection;
import org.onap.policy.distribution.model.Csar;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;

/**
 * Class to create a dummy decoder for test cases.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class DummyDecoder implements PolicyDecoder<Csar, DummyPolicy> {

    private DummyPolicy decodedPolicy;

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean canHandle(final PolicyInput policyInput) {
        return policyInput.getClass().isAssignableFrom(Csar.class);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Collection<DummyPolicy> decode(final Csar input) throws PolicyDecodingException {
        final DummyPolicy dummyPolicy = new DummyPolicy(input.getCsarFilePath());
        decodedPolicy = dummyPolicy;
        return Arrays.asList(dummyPolicy);
    }

    /**
     * Returns the policy decoded by this decoder.
     *
     * @return the policy
     */
    public DummyPolicy getDecodedPolicy() {
        return decodedPolicy;
    }

    @Override
    public void configure(final String parameterGroupName) {
    }
}

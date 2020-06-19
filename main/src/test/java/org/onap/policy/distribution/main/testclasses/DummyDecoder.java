/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.distribution.main.testclasses;

import java.util.Collection;
import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;
import org.onap.policy.distribution.reception.decoding.PolicyDecodingException;
import org.onap.policy.models.tosca.authorative.concepts.ToscaEntity;

/**
 * Class to create a dummy decoder for test cases.
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class DummyDecoder implements PolicyDecoder<PolicyInput, ToscaEntity> {

    private boolean canHandleValue;
    private Collection<ToscaEntity> policesToReturn;

    public DummyDecoder() {
        this.canHandleValue = false;
        this.policesToReturn = null;
    }

    public DummyDecoder(final boolean canHandleValue, final Collection<ToscaEntity> policesToReturn) {
        this.canHandleValue = canHandleValue;
        this.policesToReturn = policesToReturn;
    }

    @Override
    public boolean canHandle(final PolicyInput policyInput) {
        return canHandleValue;
    }

    @Override
    public Collection<ToscaEntity> decode(final PolicyInput input) throws PolicyDecodingException {
        return policesToReturn;
    }

    @Override
    public void configure(final String parameterGroupName) {
    }
}

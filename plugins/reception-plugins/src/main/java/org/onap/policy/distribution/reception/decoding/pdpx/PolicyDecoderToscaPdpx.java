/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import java.util.Collection;
import java.util.Collections;

import org.onap.policy.distribution.model.PolicyInput;
import org.onap.policy.distribution.model.Tosca;
import org.onap.policy.distribution.reception.decoding.PolicyDecoder;

/**
 * Decodes PDP-X policies from a TOSCA file.
 */
public class PolicyDecoderToscaPdpx implements PolicyDecoder<Tosca, PdpxPolicy> {

    @Override
    public Collection<PdpxPolicy> decode(final Tosca tosca) {
        // Add logic for generating the policies from the TOSCA
        return Collections.emptySet();
    }

    @Override
    public boolean canHandle(final PolicyInput policyInput) {
        return policyInput.getClass().isAssignableFrom(Tosca.class);
    }

    @Override
    public void configure(final String parameterGroupName) {
        throw new UnsupportedOperationException("The method is not supprted");
    }

}

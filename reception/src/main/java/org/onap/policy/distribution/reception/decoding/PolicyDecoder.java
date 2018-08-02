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

package org.onap.policy.distribution.reception.decoding;

import java.util.Collection;
import org.onap.policy.distribution.model.Policy;
import org.onap.policy.distribution.model.PolicyInput;

/**
 * Decodes polices from a given input.
 *
 * @param <T> the type of policy that will be created
 * @param <S> the type of input to be decoded
 */
public interface PolicyDecoder<S extends PolicyInput, T extends Policy> {

    /**
     * Can the decoder handle input of the specified type.
     * 
     * @param policyInput the type
     * @return <code>true</code if the decoder can handle the specified type
     */
    boolean canHandle(PolicyInput policyInput);

    /**
     * Decode policies from the given input
     * 
     * @param input the input
     * @return the generated policies
     * @throws PolicyDecodingException if an error occurs during decoding
     */
    Collection<T> decode(S input) throws PolicyDecodingException;

}

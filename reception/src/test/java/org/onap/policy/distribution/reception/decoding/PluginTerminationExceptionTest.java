/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Class to perform unit test of {@link PluginTerminationException}
 *
 * @author Ram Krishna Verma (ram.krishna.verma@ericsson.com)
 */
public class PluginTerminationExceptionTest {

    @Test
    public void testPluginTerminationExceptionString() {
        final PluginTerminationException pluginTerminationException = new PluginTerminationException("error message");
        assertEquals("error message", pluginTerminationException.getMessage());
    }

    @Test
    public void testPluginTerminationExceptionStringThrowable() {
        final Exception cause = new IllegalArgumentException();
        final PluginTerminationException pluginTerminationException =
                new PluginTerminationException("error message", cause);
        assertEquals("error message", pluginTerminationException.getMessage());
        assertEquals(cause, pluginTerminationException.getCause());
    }

}

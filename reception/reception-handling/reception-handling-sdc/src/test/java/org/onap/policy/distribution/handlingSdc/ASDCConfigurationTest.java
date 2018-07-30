/*-
 * ﻿============LICENSE_START=======================================================
 * org.onap.policy
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * Copyright © 2017-2018 European Software Marketing Ltd.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.policy.distribution.handlingSdc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.eclipse.jetty.util.security.Password;
import org.junit.Test;
import org.onap.sdc.utils.ArtifactTypeEnum;
//import org.onap.policy.common.logging.flexlogger.FlexLogger;
//import org.onap.policy.common.logging.flexlogger.Logger;


/**
 * Tests for ASDCConfiguration class
 *
 */
public class ASDCConfigurationTest {
 //   private static Logger logger = FlexLogger.getLogger(ASDCConfigurationTest.class);
    
    @Test
    public void testYangModelArtifactType() {
  //    logger.debug("tearDown: enter");
        Properties props = new Properties();
        props.setProperty("hs.distribution.ARTIFACT_TYPES", "MODEL_INVENTORY_PROFILE,MODEL_QUERY_SPEC,VNF_CATALOG");
        ASDCConfiguration config = new ASDCConfiguration(props, null);

        List<String> types = config.getRelevantArtifactTypes();

  //    logger.debug("ArtifactType: " + types.get(0));
        assertEquals(0, types.get(0).compareToIgnoreCase(ArtifactTypeEnum.MODEL_INVENTORY_PROFILE.toString()));

  //    logger.debug("ArtifactType: " + types.get(1));
        assertEquals(0, types.get(1).compareToIgnoreCase(ArtifactTypeEnum.MODEL_QUERY_SPEC.toString()));

  //    logger.debug("ArtifactType: " + types.get(2));
        assertEquals(0, types.get(2).compareToIgnoreCase(ArtifactTypeEnum.VNF_CATALOG.toString()));

  //    logger.debug("size= " + types.size());
        assertEquals(3, types.size());
 //     logger.debug("tearDown: end");
    }

    @Test
    public void testMsgBusAddrs() {
        Properties props = new Properties();
        props.setProperty("hs.distribution.MSG_BUS_ADDRESSES", "host1.onap.com:3904,host2.onap.com:3904");
        ASDCConfiguration config = new ASDCConfiguration(props, null);

        List<String> addrs = config.getMsgBusAddress();

        assertEquals(2, addrs.size());
        assertEquals(0, addrs.get(0).compareToIgnoreCase("host1.onap.com:3904"));
        assertEquals(0, addrs.get(1).compareToIgnoreCase("host2.onap.com:3904"));
    }

    @Test
    public void testDecryptPassword() {
        String password = "youshallnotpass";
        ASDCConfiguration config =
                createObfuscatedTestConfig(ASDCConfiguration.PROP_HS_DISTRIBUTION_PASSWORD, password);
        assertEquals(password, config.getPassword());
    }

    @Test
    public void testDecryptKeystorePassword() {
        String password = "youshallnotpass";
        ASDCConfiguration config =
                createObfuscatedTestConfig(ASDCConfiguration.PROP_HS_DISTRIBUTION_KEYSTORE_PASSWORD, password);
        assertEquals(password, config.getKeyStorePassword());
    }

    /**
     * @param propertyName
     * @param propertyValue
     * @return a new ASDCConfiguration object containing a single obfuscated property value
     */
    private ASDCConfiguration createObfuscatedTestConfig(String propertyName, String propertyValue) {
        Properties props = new Properties();
        props.put(propertyName, Password.obfuscate(propertyValue));
        return new ASDCConfiguration(props, null);
    }
}

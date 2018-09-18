#!/bin/bash
#
# ============LICENSE_START=======================================================
#  Copyright (C) 2018 Ericsson. All rights reserved.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
# ============LICENSE_END=========================================================
#

JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/
POLICY_DISTRIBUTION_HOME=/opt/app/policy/distribution

CONFIG_FILE=$1
if [ -z "$CONFIG_FILE" ]
  then
    CONFIG_FILE=$POLICY_DISTRIBUTION_HOME/etc/defaultConfig.json
fi

$JAVA_HOME/bin/java -cp "$POLICY_DISTRIBUTION_HOME/etc:$POLICY_DISTRIBUTION_HOME/lib/*" org.onap.policy.distribution.main.startstop.Main -c $CONFIG_FILE

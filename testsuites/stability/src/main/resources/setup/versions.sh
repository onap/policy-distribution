#!/bin/bash
# ============LICENSE_START=======================================================
#  Copyright (c) 2021 Nordix Foundation.
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

# update versions here
export POLICY_MARIADB_VER=10.5.8
export POLICY_DB_MIGRATOR=2.3.1-SNAPSHOT
export POLICY_API_VERSION=2.5.1-SNAPSHOT
export POLICY_PAP_VERSION=2.5.1-SNAPSHOT
export POLICY_MODELS_SIMULATOR=2.5.1-SNAPSHOT
export POLICY_DIST_VERSION=2.6.1-SNAPSHOT

echo "                MariaDB Version: ${POLICY_MARIADB_VER}"
echo "     Policy DB Migrator Version: ${POLICY_DB_MIGRATOR}"
echo "             Policy API Version: ${POLICY_API_VERSION}"
echo "             Policy PAP Version: ${POLICY_PAP_VERSION}"
echo "Policy Models Simulator Version: ${POLICY_MODELS_SIMULATOR}"
echo "    Policy Distribution Version: ${POLICY_DIST_VERSION}"

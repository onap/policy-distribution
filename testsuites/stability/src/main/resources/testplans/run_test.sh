#!/bin/bash
# ============LICENSE_START=======================================================
#  Copyright (c) 2021-2023 Nordix Foundation.
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

if [ $# -ne 1 ]; then
    echo "Usage: $0 <duration>"
    exit 1
fi

duration="$1"

echo "Starting stability test against distribution component..."

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JMETER_HOME=~/jmeter/apache-jmeter-5.5

POLICY_API_IP=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' policy-api)
POLICY_PAP_IP=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' policy-pap)
POLICY_DISTRIBUTION_IP=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' policy-distribution)

${JMETER_HOME}/bin/jmeter -n -t "${DIR}"/stability.jmx -Jduration="${duration}" \
    -Japihost="${POLICY_API_IP}" \
    -Jpaphost="${POLICY_PAP_IP}" \
    -Jdisthost="${POLICY_DISTRIBUTION_IP}" -l distribution_stability.jtl &

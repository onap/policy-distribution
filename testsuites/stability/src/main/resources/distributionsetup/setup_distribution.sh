#!/bin/bash
# ============LICENSE_START=======================================================
#  Copyright (c) 2020-2021 Nordix Foundation.
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

# the directory of the script
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
echo ${DIR}

if [ "$#" -lt 2 ]; then
  echo "PAP and PolicyAPI IPs should be passed as two parameters. PAP IP goes first."
  exit 1
else
  PAP=$1
  echo "PAP IP: ${PAP}"
  API=$2
  echo "Policy API IP: $API"
fi

docker run -v /tmp/policydistribution/distributionmount:/home/policydistribution --add-host policy-api:${API} --add-host policy-pap:${PAP} -p 6969:6969 -p 9090:9090  --name policy-distribution -v ${DIR}/distribution/bin/policy-dist.sh:/opt/app/policy/distribution/bin/policy-dist.sh -v ${DIR}/distribution/etc/defaultConfig.json:/opt/app/policy/distribution/etc/defaultConfig.json -v ${DIR}/distribution/etc/logback.xml:/opt/app/policy/distribution/etc/logback.xml -d --rm nexus3.onap.org:10001/onap/policy-distribution:2.5.2-SNAPSHOT

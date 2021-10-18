# ============LICENSE_START=======================================================
#  Copyright (c) 2020 Nordix Foundation.
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
if [  $# -le 3 ]
  then
    echo "Usage ./clearuppolicies.sh \$POLICY_API_IP \$POLICY_API_PORT \$POLICY_PAP_IP \$POLICY_PAP_PORT"
    exit 1
fi
POLICY_API_IP=$1
POLICY_API_PORT=$2
POLICY_PAP_IP=$3
POLICY_PAP_PORT=$4

send_delete_request(){
  ARG=$(echo $@ | sed 's/ //g')
  echo $ARG
  curl -k -u 'healthcheck:zb!XztG34' -X DELETE "$ARG"
}

COUNTER=1
while [ $COUNTER != 11 ]
do
  send_delete_request https://"$POLICY_PAP_IP":"$POLICY_PAP_PORT"/policy/pap/v1/pdps/policies/\
  operational.apex.sampledomain.test$COUNTER
  send_delete_request https://"$POLICY_API_IP":"$POLICY_API_PORT"/policy/api/v1/policytypes/operational\
  .apex.sampledomain.test$COUNTER/versions/1.0.0/policies/operational.apex.sampledomain.test$COUNTER/versions/1.0.0
  COUNTER=$((COUNTER +1))
done

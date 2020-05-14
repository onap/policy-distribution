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
    echo "Usage ./addcsars.sh \$POLICYAPIIP \$POLICYAPIIPPORT \$POLICYPAPIP \$POLICYPAPIPPORT"
    exit 1
fi
POLICYAPIIP=$1
POLICYAPIPORT=$2
POLICYPAPIP=$3
POLICYPAPIPPORT=$4

COUNTER=1
while [ $COUNTER != 11 ]
do
  echo https://"$POLICYPAPIP":"$POLICYPAPIPPORT"/policy/pap/v1/pdps/policies/operational.apex.sampledomain.test$COUNTER
  curl -k -u 'healthcheck:zb!XztG34' -X DELETE https://"$POLICYPAPIP":"$POLICYPAPIPPORT"/policy/pap/v1/pdps/policies/operational.apex.sampledomain.test$COUNTER
  COUNTER=$((COUNTER +1))
done

COUNTER=1
while [ $COUNTER != 11 ]
do
  echo https://"$POLICYAPIIP":"$POLICYAPIPORT"/policy/api/v1/policytypes/operational.apex.sampledomain.test$COUNTER/versions/1.0.0/policies/operational.apex.sampledomain.test$COUNTER/versions/1.0.0
  curl -k -u 'healthcheck:zb!XztG34' --output /dev/null -X DELETE https://"$POLICYAPIIP":"$POLICYAPIPORT"/policy/api/v1/policytypes/operational.apex.sampledomain.test$COUNTER/versions/1.0.0/policies/operational.apex.sampledomain.test$COUNTER/versions/1.0.0
  COUNTER=$((COUNTER +1))
done
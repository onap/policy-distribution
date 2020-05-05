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
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "${DIR}"

CSARDIRECTORYCONTENTS=$(ls -l /tmp/policydistribution/distributionmount)
TARGETDIRECTORY=/tmp/policydistribution/distributionmount

echo "Contents of $TARGETDIRECTORY are : $CSARDIRECTORYCONTENTS"

FILE=/tmp/policydistribution/distributionmount/sample_csar_with_apex_policy.csar

if test -f "$FILE";
then
    echo "$FILE exists, Removing it"
    rm /tmp/policydistribution/distributionmount/sample_csar_with_apex_policy.csar
fi

if test ! -f "$FILE"
then
  CSARDIRECTORYCONTENTS=$(ls -l /tmp/policydistribution/distributionmount)
  echo "Contents of $TARGETDIRECTORY are : $CSARDIRECTORYCONTENTS"
  echo "Exiting Script"
  exit 0
else
  echo "File not deleted correctly"
  exit 1
fi




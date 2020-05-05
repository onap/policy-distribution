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

if [  $# -le 0 ]
  then
		echo "Path to test CSAR must be provided as an argument to this function"
		exit 1
  fi
CSARLOCATION=$1

CSARDIRECTORYCONTENTS=$(ls -l /tmp/policydistribution/distributionmount)
TARGETDIRECTORY=/tmp/policydistribution/distributionmount
FILE=/tmp/policydistribution/distributionmount/sample_csar_with_apex_policy.csar

echo "Contents of $TARGETDIRECTORY are : $CSARDIRECTORYCONTENTS"

if test -f "$FILE";
  then
      echo "$FILE already exists"
      exit 1
  else
    echo "Adding csar to $TARGETDIRECTORY"
    cp $CSARLOCATION /tmp/policydistribution/distributionmount
    CSARDIRECTORYCONTENTS=$(ls -l /tmp/policydistribution/distributionmount)
    echo "Directory Contents: $CSARDIRECTORYCONTENTS"
fi

if test -f "$FILE"
  then
    echo "CSAR Copied Successfully"
    exit 0
  else
    echo "CSAR Copy Unsuccessful"
    exit 1
fi
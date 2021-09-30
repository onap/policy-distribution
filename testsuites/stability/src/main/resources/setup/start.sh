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

# load up all the image version
source "$(pwd)"/versions.sh

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORK_DIR=$(mktemp -d -p "$DIR")
echo "${WORK_DIR}"

cd "${WORK_DIR}" || exit

# check if tmp dir was created
if [[ ! "$WORK_DIR" || ! -d "$WORK_DIR" ]]; then
  echo "Could not create temp dir"
  exit 1
fi

# bring down maven
curl -O -s -S https://dlcdn.apache.org/maven/maven-3/3.8.3/binaries/apache-maven-3.8.3-bin.tar.gz
tar -xzvf apache-maven-3.8.3-bin.tar.gz
MAVEN="${WORK_DIR}"/apache-maven-3.8.3/bin/mvn
$MAVEN -v
echo ""

# clone oparent for maven settings and models for building pdp/simulator
git clone http://gerrit.onap.org/r/oparent
git clone --depth 1 https://gerrit.onap.org/r/policy/models -b master

cd models/models-sim/policy-models-sim-pdp || exit
$MAVEN clean install -DskipTests --settings "${WORK_DIR}"/oparent/settings.xml
bash ./src/main/package/docker/docker_build.sh

cd "$DIR" || exit
rm -rf "${WORK_DIR}"
echo ""

sudo mkdir -p /tmp/policydistribution/distributionmount
sudo chmod -R a+trwx /tmp

# start containers on the background
docker-compose up --detach
echo ""

# check if all containers are up
docker ps

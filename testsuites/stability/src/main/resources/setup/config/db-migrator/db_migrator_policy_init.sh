#!/bin/sh
# ============LICENSE_START====================================================
#  Copyright (C) 2021 Nordix Foundation.
# =============================================================================
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
# ============LICENSE_END======================================================

export POLICY_HOME=/opt/app/policy
export SQL_USER=policy_user
export SQL_PASSWORD=policy_user
export SQL_DB=policyadmin
export SQL_HOST=mariadb

/opt/app/policy/bin/prepare_upgrade.sh "${SQL_DB}"

/opt/app/policy/bin/db-migrator -s "${SQL_DB}" -o upgrade
rc=$?

/opt/app/policy/bin/db-migrator -s "${SQL_DB}" -o report

nc -l -p 6824

exit $rc

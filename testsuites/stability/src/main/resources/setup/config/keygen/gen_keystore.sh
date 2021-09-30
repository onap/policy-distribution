#!/bin/bash
#
# ===========LICENSE_START====================================================
#  Copyright (C) 2021 Nordix Foundation.
# ============================================================================
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
# ============LICENSE_END=====================================================
#

#
# Generates a self-signed keystore for use by the various policy docker
# images.
#

TRUST_STORE_FILE=policy-truststore
KEYFILE=ks.jks
PASS=Pol1cy_0nap
TS_ALIAS=onap.policy.csit.root.ca
KEY_ALIAS="policy@policy.onap.org"
dn="C=US, O=ONAP, OU=OSAAF, OU=policy@policy.onap.org:DEV, CN=policy"

openssl req -new -keyout cakey.pem -out careq.pem -passout "pass:${PASS}" \
        -subj "/C=US/ST=New Jersey/OU=ONAP/CN=policy.onap"

openssl x509 -signkey cakey.pem -req -days 3650 -in careq.pem \
        -out caroot.cer -extensions v3_ca -passin "pass:${PASS}"

keytool -import -noprompt -trustcacerts -alias ${TS_ALIAS} \
        -file caroot.cer -keystore "${TRUST_STORE_FILE}" -storepass "${PASS}"

chmod 644 "$TRUST_STORE_FILE"

echo "Generating key..."

keytool -delete -alias "${KEY_ALIAS}" -keystore "${KEYFILE}" -storepass "${PASS}"

rm -f "${KEYFILE}"

keytool -genkeypair -alias "${KEY_ALIAS}" -validity 30 \
        -keyalg RSA -dname "${dn}" -keystore "${KEYFILE}" \
        -keypass "${PASS}" -storepass "${PASS}"

keytool -certreq -alias "${KEY_ALIAS}" -keystore ks.jks -file ks.csr \
        -storepass "${PASS}"

openssl x509 -CA caroot.cer -CAkey cakey.pem -CAserial caserial.txt \
        -req -in ks.csr -out ks.cer -passin "pass:${PASS}" \
        -extfile dns.txt -days 30

keytool -import -noprompt -file caroot.cer -keystore ks.jks \
        -storepass "${PASS}"

keytool -import -alias "${KEY_ALIAS}" -file ks.cer -keystore ks.jks \
        -storepass "${PASS}"

chmod 644 "$KEYFILE"

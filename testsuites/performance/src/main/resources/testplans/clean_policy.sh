#!/bin/bash

function print_usage_and_exit {
  [ -z "$1" ] || echo "Error: $1"
  echo "Usage: $0  [ <dir> | <rulename> ]"
  echo "    - <dir> directory where the s3p csar files stores, policies related to them will be cleaned from PDP/PAP"
  echo "    - <rulename> rulename to be cleaned"
  exit 1
}

[ "$#" -ne 1 ] && print_usage_and_exit

rules=()
if [ -d $1 ]; then
    files=(`find "$1" -maxdepth 1 -name "*.csar" -printf "%f\n"`)
    for i in ${files[@]}; do
	fn=`echo $i | cut -d '.' -f 1`
        rules+=("oofCasablanca.Config_OOF_Optimization_${fn}.1.xml")
    done
else
  rules+=($1)
fi

for NAME in ${rules[@]}; do
    BODY="{\"policyComponent\":\"PDP\",\"policyType\":\"Optimization\",\"pdpGroup\":\"default\",\"policyName\":\"${NAME}\"}"
    rescode=`curl --silent --output /dev/null --write-out %{http_code} -k -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'ClientAuth: cHl0aG9uOnRlc3Q=' -H 'Authorization: Basic dGVzdHBkcDphbHBoYTEyMw==' -H 'Environment: TEST' -X DELETE -d $BODY https://pdp:8081/pdp/api/deletePolicy`
    if [ "$rescode" == "200" ]; then
        echo "delete $NAME in PDP success"
    else
        echo "delete $NAME in PDP FAIL with rescode $rescode"
    fi

    BODY="{\"policyName\":\"${NAME}\",\"policyComponent\":\"PAP\",\"policyType\":\"Optimization\",\"deleteCondition\":\"ALL\"}"
    rescode=`curl --silent --output /dev/null --write-out %{http_code} -k -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'ClientAuth: cHl0aG9uOnRlc3Q=' -H 'Authorization: Basic dGVzdHBkcDphbHBoYTEyMw==' -H 'Environment: TEST' -X DELETE -d $BODY https://pdp:8081/pdp/api/deletePolicy`
    if [ "$rescode" == "200" ]; then
        echo "delete $NAME in PAP success"
    else
        echo "delete $NAME in PAP FAIL with rescode $rescode"
    fi
done


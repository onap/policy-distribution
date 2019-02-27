#!/bin/bash

function print_usage_and_exit {
  echo "Usage: $0  <type> <log file name or rule name>"
  echo "    - <type>: s for stability log , p for perf log , or other for simple policy name"
  echo "    - <log file name or rule>"
  exit 1
}

[ "$#" -ne 2 ] && print_usage_and_exit
case $1 in
[sS])
  type='s'
  ;;
[pP])
  type='p'
  ;;
*)
  type='simple'
  ;;
esac

rules=()
if [ -f $2 ] && [ $type == 's' ]; then
    resids=(`grep -o "get policy failed for resource [0-9]\+" $2 | cut -d ' ' -f 6`)
    for i in ${resids[@]}; do
        rules+=("oofCasablanca.Config_OOF_Optimization_s3p_$i.*")
    done
elif [ -f $2 ] && [ $type == 'p' ]; then
    idx=`grep -o "Fail at idx [0-9]\+" $2 | cut -d ' ' -f 4`
    total=`cat perf_data.csv | wc -l`
    while [ "s$idx" > "s" ] && [ $idx -lt $total ]; do
        rules+=("oofCasablanca.Config_OOF_Optimization_s3p_$idx.*")
        idx=$((idx+1))
    done
else
  rules+=($2)
fi

for NAME in ${rules[@]}; do
    BODY="{\"policyName\":\"${NAME}\"}"
    rescode=`curl --silent --write-out %{http_code} -k -H 'Content-Type: application/json' -H 'Accept: application/json' -H 'ClientAuth: cHl0aG9uOnRlc3Q=' -H 'Authorization: Basic dGVzdHBkcDphbHBoYTEyMw==' -H 'Environment: TEST' -X POST -d $BODY --output /dev/null https://pdp:8081/pdp/api/listConfig`
    if [ "$rescode" == '200' ]; then
        echo "list policy $NAME in PDP success"
    else
        echo "list policy $NAME in PDP FAIL with rescode $rescode"
    fi
done


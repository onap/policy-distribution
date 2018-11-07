#!/usr/bin/env bash

function print_usage_and_exit {
    [ -z "$1" ] || echo "Error: $1"
    echo "Usage: $0 <output_dir> <total>"
    echo "    - <output_dir>: directory where the generated csar file will be put into"
    echo "    - <total>: total number of csar files to be generated"
    exit 1
}

[ "$#" -ne 2 ] && print_usage_and_exit
OUTPUT=$1
TOTAL=$2

[ -d $OUTPUT ] || mkdir -p $OUTPUT
[ -d $OUTPUT ] || print_usage_and_exit "$OUTPUT is not a valid directory"
[[ $TOTAL =~ ^[0-9]+$ ]] || print_usage_and_exit "$SEED is not a integer"


ROOT_DIR=`dirname $(readlink -f $0)`
TMP_DIR=$ROOT_DIR/perf_tmp

python $ROOT_DIR/generate_perf.py --dest $TMP_DIR --total $TOTAL --out $ROOT_DIR/perf_data.csv

rm -f $OUTPUT/*.csar
cp -fr $TMP_DIR/*.csar $OUTPUT/

rm -rf $TMP_DIR

#!/usr/bin/env bash

function print_usage_and_exit {
    [ -z "$1" ] || echo "Error: $1"
    echo "Usage: $0 <output_dir> <random_number>"
    echo "    - <output_dir>: directory where the generated csar file will be put into"
    echo "    - <random_number>: random number to choose which csar template to use"
    exit 1
}

[ "$#" -ne 2 ] && print_usage_and_exit
OUTPUT=$1
SEED=$2
[ -d $OUTPUT ] || mkdir -p $OUTPUT
[ -d $OUTPUT ] || print_usage_and_exit "$OUTPUT is not a valid directory"
[[ $SEED =~ ^[0-9]+$ ]] || print_usage_and_exit "$SEED is not a integer"


ROOT_DIR=`dirname $(readlink -f $0)`
TMP_DIR=$ROOT_DIR/csar_tmp

declare -A TEMPLATES
#fill templates 
for path in $ROOT_DIR/templates/*; do
    [ -d "$path" ] || continue # not directory, skip
    dirname="$(basename "${path}")"
    TEMPLATES[$dirname]=${path}
done
KEYS=(${!TEMPLATES[@]})

key=${KEYS[$(( 10#$SEED % ${#KEYS[@]} ))]}
src=${TEMPLATES[$key]}

rm -rf $TMP_DIR
cp -r -f $src $TMP_DIR
find $TMP_DIR -type f | xargs sed -i "s/##RANDOM_RESOURCE_NAME##/s3p_${SEED}_${key}/g"
cd $TMP_DIR
echo $key
echo $src
zip -r "$ROOT_DIR/s3p_${SEED}_${key}.csar" *
mv -f "$ROOT_DIR/s3p_${SEED}_${key}.csar" $OUTPUT/
cd $ROOT_DIR
rm -rf $TMP_DIR

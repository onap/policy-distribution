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




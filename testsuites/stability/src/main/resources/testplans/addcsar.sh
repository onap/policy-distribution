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
Performance Test for Policy Distribution
## Steps to Run performance test
1. Download Apache JMeter
Download JMeter 5.0 from https://jmeter.apache.org/download_jmeter.cgi, and extracted it locally.

2. Run the setup-components script found within simulator setup in the stability test folder.
This will launch MariaDB, PDPSimulator, PAP, Policy-API and DMaaP Simulator as docker containers.

3. Launch the policy distribution service by running the setup-distribution script found within distributionsetup
in the stability test folder.
If you are running all of the components locally you will need to edit the port that distribution starts on as it is
currently 6969 which conflicts with the policy API port.
This will launch policy-distribution as a docker container and takes 2 arguments, PAP IP and API IP.
If you are running locally these will be 127.0.0.1. If on a VM enter the VM IP.
```
setup-distribution.sh 127.0.0.1 127.0.0.1
```

4. Run the JMeter performance test
```
rm -f performance.log; <jmeter_dir>/bin/jmeter.sh -t performance.jmx -n -Jhost=<pdp service hostname> -Jduration=100 -l performance.log
```
Search for 'get policy failed' in the performance.log file to see if there is any errors found during the performance test.

## JMeter properties
We can configure the following properties when running the JMeter performance test for policy distribution
| Property Name | Default Value | Description |
|---------------|-------------|---------------|
| host | 10.2.0.27 | PAP/API service host name or ip  |
| watchedfolder | /tmp/policy_distribution/distributionmounts | Directory that is mounted to distribution watched folder |
| duration | 259200 | Number of seconds for how long to run the tests |
|PAP_PORT|7000| Port that pap service is exposing|
|API_PORT|6969| Port that API service is exposing|
|DISTRIBUTION_HOST| 127.0.0.1 | Distribution service host name or ip |
|DISTRIBUTION_PORT| 6969 | Distribution service port |


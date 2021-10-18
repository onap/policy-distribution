Stability Test for Policy Distribution
## Steps to Run stability test
1. Download Apache JMeter
Download JMeter 5.0 from https://jmeter.apache.org/download_jmeter.cgi, and extracted it locally.

2. Run the setup-components script found within simulator setup.
This will launch MariaDB, PDPSimulator, PAP, Policy-API and DMaap Simulator as docker containers.

3. Launch the policy distribution service by running the start script found within setup folder.

4. Run the JMeter stability test from testplans folder
```
./run_test.sh
```
Search for 'get policy failed' in the stability.log file to see if there is any errors found during the stability test.

## JMeter properties
We can configure the following properties when running the JMeter stability test for policy distribution 
| Property Name | Default Value | Description |
|---------------|-------------|---------------|
| host | pdp | PDP service host name or ip  |
| csardir | /tmp/policy_distribution/csar | Directory where to store the generated csar files |
| testcsar | {SCRIPT_DIR}/sample_csar_with_apex_policy.csar) | Location of Test CSAR |
| duration | 30 | Number of seconds for how long to run the tests |
| retry | 10 | Number of retry to retrieve the policy of each csar |
| wait | 1000 | Milliseconds to wait between each retry |
|PAP_PORT|7000| Port that pap service is exposing|
|API_PORT|6969| Port that API service is exposing|


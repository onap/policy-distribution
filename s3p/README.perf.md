# Performance Test for Policy Distribution
## Steps to Run performance test
1. Download Apache JMeter
Download JMeter 5.0 from https://jmeter.apache.org/download_jmeter.cgi, and extracted it locally.
 
2. Launch the policy distribution service
Launch the policy distribution service with the configuration from s3pConfig.json, you need to make sure when the service is being launched, the directory configured in s3pConfig.json by receptionHandlerConfigurationParameters.fileConfiguration.parameters.watchPath is a valid local directory(Default directory is /tmp/policy_distribution/csar/).
The policy distribution service will use the FileSystemReceptionHandler plugin to monitor the local directory specified by the 'watchPath' parameter for newly added csar files, parse them to generate policies and forward it to PDP.
```
java -cp "<comma separated directories containing jar files>" org.onap.policy.distribution.main.startstop.Main -c s3pConfig.json
```
  Or if you want to launch it from docker, please do the followings:
```
mkdir -p /tmp/policy_distribution/csar/
docker run -d -e "CONFIG_FILE=/opt/app/policy/distribution/etc/s3pConfig.json" \
           -v /tmp/policy_distribution/csar/:/tmp/policy_distribution/csar/ \
           -p 6969:6969 \
           --name policy-distribution policy-distribution
```
Here we use -e option to "docker run" to pass the config file which the policy distribution service will be launched upon and use -v option to map the local host /tmp/policy_distribution/csar/ directory as the directory of /tmp/policy_distribution/csar/ within the policy-distribution docker.

**NOTED:**
Please make sure when you launch the policy distribution service, the following requirements are met:
a. The policy PDP service is active, and can be reached using the parameters configured by policyForwarderConfigurationParameters.xacmlPdpConfiguration.parameters in the s3pConfig.json file.

b. Make sure you have installed the AAF root CA either in local host or in the docker image(running as root):
```
curl -s https://git.onap.org/dmaap/buscontroller/plain/misc/cert-client-init.sh | bash --
```

3. Get the Jmeter configuration ans scripts
Git clone the policy distribution code, and goto the s3p directory:
```
git clone https://git.onap.org/policy/distribution policy-distribution
cd policy-distribution/s3p
```

4. Run the JMeter stability test
```
rm -f perf.log; <jmeter_dir>/bin/jmeter.sh -t perf.jmx -n -Jhost=<pdp service hostname> -Jtotal=5 -l perf.log
```
In the meantime, you can run various system tools i.e. top, atop, etc. to monitor the cpu/memory usage of the policy-distribution service.

After jmeter finished, search for 'Fail at idx' in the perf.log file to see if there is any errors found during the stability test.

## JMeter properties
We can configure the following properties when running the JMeter stability test for policy distribution 
| Property Name | Default Value | Description |
|---------------|-------------|---------------|
| host | pdp | PDP service host name or ip  |
| csardir | /tmp/policy_distribution/csar | Directory where to store the generated csar files |
| total | 5 | Total number of csar files to be generated in bulk to test performance
| retry | 100 | Number of retry to retrieve the policy of each csar



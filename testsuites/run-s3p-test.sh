#!/bin/bash
# ============LICENSE_START=======================================================
#  Copyright (c) 2023 Nordix Foundation.
# ================================================================================
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
#
# SPDX-License-Identifier: Apache-2.0
# ============LICENSE_END=========================================================

# Check if the correct number of arguments is provided
if [ $# -ne 2 ]; then
    echo "Usage: $0 <type> <duration>"
    exit 1
fi

type="$1"
duration="$2"

# Check if the type is valid
if [ "$type" != "stability" ] && [ "$type" != "performance" ]; then
    echo "Invalid type. Use 'stability' or 'performance'."
    exit 1
fi

sudo apt update

# Check if OpenJDK 17 is installed
if java --version | grep -q "openjdk 17"; then
    echo "OpenJDK 17 is already installed."
else
    echo "OpenJDK 17 is not installed. Installing..."
    sudo apt install -y openjdk-17-jdk
fi

# Check if docker is installed
if docker --version | grep -q "Docker version"; then
    echo "docker is already installed."
else
    echo "docker is not installed. Installing..."
    # Add docker repository
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
    $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

    sudo apt update

    # Install docker
    sudo apt-get install docker-ce docker-ce-cli containerd.io

    sudo chmod 666 /var/run/docker.sock
fi

systemctl status --no-pager docker

docker ps

# Check if docker compose is installed
if docker-compose --version | grep -q "docker-compose version"; then
    echo "docker compose is already installed."
else
    echo "docker compose is not installed. Installing..."
    # Install compose (check if version is still available or update as necessary)
    sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
fi

# Check if install was successful
docker-compose --version

bash ./stability/src/main/resources/setup/versions.sh

bash ./stability/src/main/resources/setup/start.sh

# Install required packages
sudo apt install -y wget unzip

# Install JMeter
mkdir -p ~/jmeter
wget https://dlcdn.apache.org//jmeter/binaries/apache-jmeter-5.5.zip # check if valid version
unzip -q -d ~/jmeter apache-jmeter-5.5.zip
rm apache-jmeter-5.5.zip

# Check if visualvm is installed
if visualvm --help | grep -q "Usage:"; then
    echo "visualvm is already installed."
else
    echo "visualvm is not installed. Installing..."
    # Install compose (check if version is still available or update as necessary)
    sudo apt install visualvm
fi

# Set globally accessable permissions on policy file
sudo chmod 777 /usr/lib/jvm/java-17-openjdk-amd64/bin/visualvm.policy

# Create Java security policy file for VisualVM
file_path="/usr/lib/jvm/java-17-openjdk-amd64/bin/visualvm.policy"

if [ -e "$file_path" ]; then
    echo "File exists already."
else
    echo "File does not exist. Creating now..."
    sudo tee "$file_path" > /dev/null <<EOF
    grant codebase "jrt:/jdk.jstatd" {
       permission java.security.AllPermission;
    };
    grant codebase "jrt:/jdk.internal.jvmstat" {
       permission java.security.AllPermission;
    };
EOF
    sudo chmod 644 "$file_path"  # Adjust file permissions as needed
fi

# check if jstatd is running
process_name="jstatd"

if pgrep "$process_name" > /dev/null; then
    echo "The process '$process_name' is running."
else
    echo "The process '$process_name' is not running. Will run now"
    /usr/lib/jvm/java-17-openjdk-amd64/bin/jstatd -p 1111 -J-Djava.security.policy=/usr/lib/jvm/java-17-openjdk-amd64/bin/visualvm.policy &
fi

visualvm &

tmp_path="/tmp/policydistribution/distributionmount"

if [ -e "$tmp_path" ]; then
    echo "tmp File exists already."
else
    echo "tmp does not exist. Creating now..."
    sudo mkdir -p /tmp/policydistribution/distributionmount
    sudo chmod -R a+trwx /tmp
fi

sleep 240

# Run different tests based on the type
if [ "$type" == "stability" ]; then
    echo "Running stability test for $duration seconds in directory ${PWD}..."
    bash ./stability/src/main/resources/testplans/run_test.sh "$duration"
    echo "Stability test completed."
elif [ "$type" == "performance" ]; then
    echo "Running performance test for $duration seconds in directory ${PWD}..."
    bash ./performance/src/main/resources/testplans/run_test.sh "$duration"
    echo "Performance test completed."
fi
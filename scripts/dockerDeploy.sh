#!/bin/bash

# Deployment script for the Treecreate project
# Validates the env variables, pulls the newest commit from a given branch and deploys via docker using specified name and ports
#Simply cd into the treecreate directory and run ./scripts/dockerDeploy.sh

echo ""
echo "Welcome, this is an automated deployment of the TREECREATE app in a docker container"

echo ""
echo "Pre-run checks:"
echo "Environment variables:"
JDBC_URL=${TREECREATE_JDBC_URL}

checkStatus="true"
if [[ -z $JDBC_URL ]]; then
  echo "! JDBC URL is missing!"
  checkStatus="false"
else
  echo "JDBC URL is okay"
fi

if [[ "$checkStatus" == "true" ]]; then
  echo "Pre-run checks have completed successfully"
else
  echo "! There were failed checks, aborting..."
  exit 1
fi

echo ""
echo "Current branch"
git branch
echo ""

read -p "Would you like to change the branch [no]? " changeBranch
changeBranch="${changeBranch:-no}"

if [[ "$changeBranch" != "no" ]]; then
  read -p "What's the branch name [development]? " branchName
  branchName="${branchName:-development}"
  git checkout $branchName
fi

echo "Pulling the newest version..."
git pull > /dev/null 2>&1
echo ""
read -p "What is the name you would like to deploy under [treecreate-X.X.X]? " dockerName
dockerName="${dockerName:-treecreate-X.X.X}"
echo ""
read -p "What is the port setup you would like to use [4001:5000]? " dockerPort
dockerPort="${dockerPort:-4001:5000}"
echo ""

echo "Checking if the provided setup is available..."
nameOverlap=$(docker ps -a | grep  $dockerName)

if [[ -z $nameOverlap ]]; then
  echo "The name is valid"
else
  echo "! Name is already taken by:"
  echo "$nameOverlap"
  echo "Exiting..."
  exit 1
fi

portOverlap=$(docker ps | grep ${dockerPort/:/->})
if [[ -z $portOverlap ]]; then
  echo "The ports are valid"
else
  echo "! The provided ports are already bound. Use different ports"
  echo "Bound to:"
  echo "$portOverlap"
  echo "Exiting..."
  exit 1
fi

echo "Variable check successful"
echo ""
echo "Building an image $dockerName"
echo ""
docker build -t $dockerName --build-arg TREECREATE_JDBC_URL -f Dockerfile-arm .
echo ""
echo "Building finished"
echo "Running the image $dockerName on ports $dockerPort"
echo ""
docker run --name $dockerName -e TREECREATE_JDBC_URL --restart unless-stopped -dp $dockerPort $dockerName

echo "The image has been run"
echo "You can check its status with"
echo "docker ps"
echo "To access logs, use"
echo "docker logs $dockerName --follow"
echo ""
echo "Thank you for using Hotdeals.dev TM delpoyment system"

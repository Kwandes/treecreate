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
PROD_JDBC_URL=${TREECREATE_PROD_JDBC_URL}
QUICKPAY_SECRET=${TREECREATE_QUICKPAY_SECRET}
MAIL_PASS=${TREECREATE_MAIL_PASS}

checkStatus="true"

if [[ -z $JDBC_URL ]]; then
  echo "! JDBC URL is missing!"
  checkStatus="false"
else
  echo "JDBC URL is okay"
fi

if [[ -z $PROD_JDBC_URL ]]; then
  echo "! PROD JDBC URL is missing!"
  checkStatus="false"
else
  echo "PROD JDBC URL is okay"
fi

if [[ -z $QUICKPAY_SECRET ]]; then
  echo "! QUICKPAY_SECRET is missing!"
  checkStatus="false"
else
  echo "QUICKPAY_SECRET is okay"
fi

if [[ -z $MAIL_PASS ]]; then
  echo "! MAIL_PASS is missing!"
  checkStatus="false"
else
  echo "MAIL_PASS is okay"
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
read -p "What is the name you would like to deploy under [treecreate-x.x.x]? " dockerName
dockerName="${dockerName:-treecreate-x.x.x}"

echo ""

read -p "Would you like to deploy as a test release [yes]? " testRelease
testRelease="${testRelease:-test}"

if [[ "$testRelease" != "test" ]]; then
  testRelease=""
else
  testRelease="-test"
fi

dockerName="$dockerName$testRelease"
echo ""
echo "Name: $dockerName"
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

echo "Checking cpu architecture type to determine dockerfile"
architecture=$(uname -m)

if [[ "$architecture" == "arm"* ]]; then
  dockerfile="Dockerfile-arm"
  echo "CPU is an ARM CPU, Deploying with Dockerfile-arm"
else
  dockerfile="Dockerfile"
  echo "CPU is x86-64 bit, Deploying with Dockerfile"
fi


echo "Variable check successful"
echo ""
echo "Building an image $dockerName"
echo ""
if [[ "$testRelease" != "-test" ]]; then
  echo "Building for production"
  # Even though we are building for prod, the test will occur on the dev DB just in case it causes issues
  docker build -t $dockerName --build-arg TREECREATE_JDBC_URL --build-arg TREECREATE_QUICKPAY_SECRET --build-arg TREECREATE_MAIL_PASS -f $dockerfile .
else
  echo "Building for testing"
  docker build -t $dockerName --build-arg TREECREATE_JDBC_URL --build-arg TREECREATE_QUICKPAY_SECRET --build-arg TREECREATE_MAIL_PASS -f $dockerfile .
fi
echo ""
echo "Building finished"
echo "Running the image $dockerName on ports $dockerPort"
echo ""
if [[ "$testRelease" != "-test" ]]; then
  echo "Deploying to production"
  # The env variable (for the jdbc url) has to match the one inside the applicaiton.properties, so instead we assign a value of the production env variable to it (as opposed to directly assigning it)
  docker run --name $dockerName -e TREECREATE_JDBC_URL=$PROD_JDBC_URL -e TREECREATE_QUICKPAY_SECRET -e TREECREATE_MAIL_PASS -e TREECREATE_ENV=production --restart unless-stopped -dp $dockerPort $dockerName
else
  echo "Deploying to testing"
  docker run --name $dockerName -e TREECREATE_JDBC_URL -e TREECREATE_QUICKPAY_SECRET -e TREECREATE_MAIL_PASS --restart unless-stopped -dp $dockerPort $dockerName
fi

echo "The image has been run"
echo "You can check its status with"
echo "docker ps"
echo "To access logs, use"
echo "docker logs $dockerName --follow"
echo ""
echo "Thank you for using Hotdeals.dev TM deployment system"


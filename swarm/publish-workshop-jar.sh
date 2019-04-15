#!/bin/sh

. variables.sh

eval $(docker-machine env workshop-manager)

DEMO_VERSION=1.0.0

ROOT_PATH=$(pwd)/../flink/com.nextbreakpoint.flinkworkshop

pushd $ROOT_PATH

mvn -Dchannel=github clean package de.jutzig:github-release-plugin:release

popd

#!/bin/sh

. variables.sh

ROOT_PATH=$(pwd)/../flink/com.nextbreakpoint.flinkworkshop

pushd $ROOT_PATH

mvn -Dchannel=github clean package de.jutzig:github-release-plugin:release

popd

#!/bin/sh

docker run -it --rm --net=workshop ${ZOOKEEPER_IMAGE} zkCli.sh -server zookeeper:2181

# Examples
# create /something "value"
# setAcl /something auth:<username>:<password>:crdwa,world:anyone:r
# getAcl /something

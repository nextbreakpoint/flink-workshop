#!/bin/sh

docker run --rm -it --net=demo $KAFKA_IMAGE kafka-console-producer --broker-list kafka:9092 --topic $1 --request-timeout-ms 1000

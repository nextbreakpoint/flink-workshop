#!/bin/sh

docker run --rm -it --net=workshop $KAFKA_IMAGE kafka-topics --delete --zookeeper zookeeper:2181 --topic test-input
docker run --rm -it --net=workshop $KAFKA_IMAGE kafka-topics --delete --zookeeper zookeeper:2181 --topic test-output

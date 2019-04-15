#!/bin/sh

docker run --rm -it --net=workshop $KAFKA_IMAGE kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 8 --config retention.ms=300000 --topic test-input
docker run --rm -it --net=workshop $KAFKA_IMAGE kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 8 --config retention.ms=300000 --topic test-output

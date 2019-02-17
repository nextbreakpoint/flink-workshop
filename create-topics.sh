#!/bin/sh

docker run --rm -it --net=demo $KAFKA_IMAGE /opt/kafka_2.11-$KAFKA_VERSION/bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 8 --config retention.ms=300000 --topic test-input
docker run --rm -it --net=demo $KAFKA_IMAGE /opt/kafka_2.11-$KAFKA_VERSION/bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 8 --config retention.ms=300000 --topic test-output

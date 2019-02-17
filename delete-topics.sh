#!/bin/sh

docker run --rm -it --net=demo $KAFKA_IMAGE /opt/kafka_2.11-$KAFKA_VERSION/bin/kafka-topics.sh --delete --zookeeper zookeeper:2181 --topic test-input
docker run --rm -it --net=demo $KAFKA_IMAGE /opt/kafka_2.11-$KAFKA_VERSION/bin/kafka-topics.sh --delete --zookeeper zookeeper:2181 --topic test-output

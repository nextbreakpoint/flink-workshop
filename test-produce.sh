#!/bin/sh

docker run --rm -it --net=demo $KAFKA_IMAGE /opt/kafka_2.11-$KAFKA_VERSION/bin/kafka-console-producer.sh --broker-list kafka:9092 --topic $1 --request-timeout-ms 1000

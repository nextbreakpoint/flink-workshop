#!/bin/sh

docker run --rm -it --net=demo $KAFKA_IMAGE /opt/kafka_2.11-$KAFKA_VERSION/bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic $1 --from-beginning

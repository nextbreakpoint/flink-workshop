#!/bin/sh

docker run --rm -it --net=workshop $KAFKA_IMAGE kafka-console-consumer --bootstrap-server kafka:9092 --topic $1 --from-beginning

#!/bin/sh

docker build -t workshop/flink:1.9.0 --build-arg flink_version=1.9.0 --build-arg scala_version=2.11 flink

docker build -t workshop/cp-kafka:5.3.1 kafka
docker build -t workshop/cp-schema-registry:5.3.1 schema-registry
docker build -t workshop/zookeeper:3.5.6 zookeeper
docker build -t workshop/prometheus:v2.13.1 prometheus
docker build -t workshop/grafana:6.4.3 grafana

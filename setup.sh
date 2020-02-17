#!/bin/sh

pushd docker
mkdir -p /tmp/workshop
docker build -t workshop/flink:1.9.2 --build-arg flink_version=1.9.2 flink
docker build -t workshop/cp-kafka:5.4.0 kafka
docker build -t workshop/cp-schema-registry:5.4.0 schema-registry
docker build -t workshop/zookeeper:3.5.6 zookeeper
docker build -t workshop/prometheus:v2.13.1 prometheus
docker build -t workshop/grafana:6.4.3 grafana
popd

#!/bin/sh

. variables.sh

eval $(docker-machine env workshop-manager)

ROOT_PATH=$(pwd)/../docker

docker build -t workshop-zookeeper:$ZOOKEEPER_VERSION --build-arg zookeeper_version=$ZOOKEEPER_VERSION $ROOT_PATH/zookeeper
docker build -t workshop-kafka:$KAFKA_VERSION --build-arg cp_kafka_version=$KAFKA_VERSION $ROOT_PATH/kafka
docker build -t workshop-flink:$FLINK_VERSION --build-arg flink_version=$FLINK_VERSION --build-arg scala_version=$SCALA_VERSION $ROOT_PATH/flink
docker build -t workshop-alertmanager:$STACK_VERSION $ROOT_PATH/alertmanager
docker build -t workshop-grafana:$STACK_VERSION $ROOT_PATH/grafana
docker build -t workshop-prometheus:$STACK_VERSION $ROOT_PATH/prometheus
docker build -t workshop-nodeexporter:$STACK_VERSION $ROOT_PATH/nodeexporter

docker tag workshop-zookeeper:$ZOOKEEPER_VERSION $(docker-machine ip workshop-manager):5000/workshop-zookeeper:$ZOOKEEPER_VERSION
docker tag workshop-kafka:$KAFKA_VERSION $(docker-machine ip workshop-manager):5000/workshop-kafka:$KAFKA_VERSION
docker tag workshop-flink:$FLINK_VERSION $(docker-machine ip workshop-manager):5000/workshop-flink:$FLINK_VERSION
docker tag workshop-alertmanager:$STACK_VERSION $(docker-machine ip workshop-manager):5000/workshop-alertmanager:$STACK_VERSION
docker tag workshop-grafana:$STACK_VERSION $(docker-machine ip workshop-manager):5000/workshop-grafana:$STACK_VERSION
docker tag workshop-prometheus:$STACK_VERSION $(docker-machine ip workshop-manager):5000/workshop-prometheus:$STACK_VERSION
docker tag workshop-nodeexporter:$STACK_VERSION $(docker-machine ip workshop-manager):5000/workshop-nodeexporter:$STACK_VERSION

docker push $(docker-machine ip workshop-manager):5000/workshop-zookeeper:$ZOOKEEPER_VERSION
docker push $(docker-machine ip workshop-manager):5000/workshop-kafka:$KAFKA_VERSION
docker push $(docker-machine ip workshop-manager):5000/workshop-flink:$FLINK_VERSION
docker push $(docker-machine ip workshop-manager):5000/workshop-alertmanager:$STACK_VERSION
docker push $(docker-machine ip workshop-manager):5000/workshop-grafana:$STACK_VERSION
docker push $(docker-machine ip workshop-manager):5000/workshop-prometheus:$STACK_VERSION
docker push $(docker-machine ip workshop-manager):5000/workshop-nodeexporter:$STACK_VERSION

eval $(docker-machine env workshop-manager)

docker pull $(docker-machine ip workshop-manager):5000/workshop-zookeeper:$ZOOKEEPER_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-kafka:$KAFKA_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-flink:$FLINK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-alertmanager:$STACK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-grafana:$STACK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-prometheus:$STACK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-nodeexporter:$STACK_VERSION

eval $(docker-machine env workshop-worker1)

docker pull $(docker-machine ip workshop-manager):5000/workshop-zookeeper:$ZOOKEEPER_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-kafka:$KAFKA_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-flink:$FLINK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-alertmanager:$STACK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-grafana:$STACK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-prometheus:$STACK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-nodeexporter:$STACK_VERSION

eval $(docker-machine env workshop-worker2)

docker pull $(docker-machine ip workshop-manager):5000/workshop-zookeeper:$ZOOKEEPER_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-kafka:$KAFKA_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-flink:$FLINK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-alertmanager:$STACK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-grafana:$STACK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-prometheus:$STACK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-nodeexporter:$STACK_VERSION

#!/bin/sh

. variables.sh

eval $(docker-machine env workshop-manager)

ROOT_PATH=$(pwd)/../docker

docker build -t $(docker-machine ip workshop-manager):5000/workshop-zookeeper:$ZOOKEEPER_VERSION --build-arg zookeeper_version=$ZOOKEEPER_VERSION $ROOT_PATH/zookeeper
docker build -t $(docker-machine ip workshop-manager):5000/workshop-kafka:$KAFKA_VERSION --build-arg cp_kafka_version=$KAFKA_VERSION $ROOT_PATH/kafka
docker build -t $(docker-machine ip workshop-manager):5000/workshop-flink:$FLINK_VERSION --build-arg flink_version=$FLINK_VERSION --build-arg scala_version=$SCALA_VERSION $ROOT_PATH/flink
docker build -t $(docker-machine ip workshop-manager):5000/workshop-alertmanager:$ALERTMANAGER_VERSION $ROOT_PATH/alertmanager
docker build -t $(docker-machine ip workshop-manager):5000/workshop-grafana:$GRAFANA_VERSION $ROOT_PATH/grafana
docker build -t $(docker-machine ip workshop-manager):5000/workshop-prometheus:$PROMETHEUS_VERSION $ROOT_PATH/prometheus
docker build -t $(docker-machine ip workshop-manager):5000/workshop-nodeexporter:$NODEEXPORTER_VERSION $ROOT_PATH/nodeexporter

docker push $(docker-machine ip workshop-manager):5000/workshop-zookeeper:$ZOOKEEPER_VERSION
docker push $(docker-machine ip workshop-manager):5000/workshop-kafka:$KAFKA_VERSION
docker push $(docker-machine ip workshop-manager):5000/workshop-flink:$FLINK_VERSION
docker push $(docker-machine ip workshop-manager):5000/workshop-alertmanager:$ALERTMANAGER_VERSION
docker push $(docker-machine ip workshop-manager):5000/workshop-grafana:$GRAFANA_VERSION
docker push $(docker-machine ip workshop-manager):5000/workshop-prometheus:$PROMETHEUS_VERSION
docker push $(docker-machine ip workshop-manager):5000/workshop-nodeexporter:$NODEEXPORTER_VERSION

eval $(docker-machine env workshop-manager)

docker pull $(docker-machine ip workshop-manager):5000/workshop-zookeeper:$ZOOKEEPER_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-kafka:$KAFKA_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-flink:$FLINK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-alertmanager:$ALERTMANAGER_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-grafana:$GRAFANA_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-prometheus:$PROMETHEUS_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-nodeexporter:$NODEEXPORTER_VERSION

eval $(docker-machine env workshop-worker1)

docker pull $(docker-machine ip workshop-manager):5000/workshop-zookeeper:$ZOOKEEPER_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-kafka:$KAFKA_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-flink:$FLINK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-alertmanager:$ALERTMANAGER_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-grafana:$GRAFANA_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-prometheus:$PROMETHEUS_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-nodeexporter:$NODEEXPORTER_VERSION

eval $(docker-machine env workshop-worker2)

docker pull $(docker-machine ip workshop-manager):5000/workshop-zookeeper:$ZOOKEEPER_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-kafka:$KAFKA_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-flink:$FLINK_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-alertmanager:$ALERTMANAGER_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-grafana:$GRAFANA_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-prometheus:$PROMETHEUS_VERSION
docker pull $(docker-machine ip workshop-manager):5000/workshop-nodeexporter:$NODEEXPORTER_VERSION

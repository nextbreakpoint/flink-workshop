#!/bin/sh

. variables.sh

eval $(docker-machine env demo-manager)

docker build -t demo-zookeeper:$ZOOKEEPER_VERSION --build-arg zookeeper_version=$ZOOKEEPER_VERSION docker/zookeeper
docker build -t demo-kafka:$KAFKA_VERSION --build-arg kafka_version=$KAFKA_VERSION --build-arg scala_version=$SCALA_VERSION docker/kafka
docker build -t demo-flink:$FLINK_VERSION --build-arg flink_version=$FLINK_VERSION --build-arg scala_version=$SCALA_VERSION docker/flink
docker build -t demo-alertmanager:$STACK_VERSION docker/alertmanager
docker build -t demo-grafana:$STACK_VERSION docker/grafana
docker build -t demo-prometheus:$STACK_VERSION docker/prometheus
docker build -t demo-nodeexporter:$STACK_VERSION docker/nodeexporter

docker tag demo-zookeeper:$ZOOKEEPER_VERSION $(docker-machine ip demo-manager):5000/demo-zookeeper:$ZOOKEEPER_VERSION
docker tag demo-kafka:$KAFKA_VERSION $(docker-machine ip demo-manager):5000/demo-kafka:$KAFKA_VERSION
docker tag demo-flink:$FLINK_VERSION $(docker-machine ip demo-manager):5000/demo-flink:$FLINK_VERSION
docker tag demo-alertmanager:$STACK_VERSION $(docker-machine ip demo-manager):5000/demo-alertmanager:$STACK_VERSION
docker tag demo-grafana:$STACK_VERSION $(docker-machine ip demo-manager):5000/demo-grafana:$STACK_VERSION
docker tag demo-prometheus:$STACK_VERSION $(docker-machine ip demo-manager):5000/demo-prometheus:$STACK_VERSION
docker tag demo-nodeexporter:$STACK_VERSION $(docker-machine ip demo-manager):5000/demo-nodeexporter:$STACK_VERSION

docker push $(docker-machine ip demo-manager):5000/demo-zookeeper:$ZOOKEEPER_VERSION
docker push $(docker-machine ip demo-manager):5000/demo-kafka:$KAFKA_VERSION
docker push $(docker-machine ip demo-manager):5000/demo-flink:$FLINK_VERSION
docker push $(docker-machine ip demo-manager):5000/demo-alertmanager:$STACK_VERSION
docker push $(docker-machine ip demo-manager):5000/demo-grafana:$STACK_VERSION
docker push $(docker-machine ip demo-manager):5000/demo-prometheus:$STACK_VERSION
docker push $(docker-machine ip demo-manager):5000/demo-nodeexporter:$STACK_VERSION

eval $(docker-machine env demo-manager)

docker pull $(docker-machine ip demo-manager):5000/demo-zookeeper:$ZOOKEEPER_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-kafka:$KAFKA_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-flink:$FLINK_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-alertmanager:$STACK_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-grafana:$STACK_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-prometheus:$STACK_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-nodeexporter:$STACK_VERSION

eval $(docker-machine env demo-worker1)

docker pull $(docker-machine ip demo-manager):5000/demo-zookeeper:$ZOOKEEPER_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-kafka:$KAFKA_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-flink:$FLINK_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-alertmanager:$STACK_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-grafana:$STACK_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-prometheus:$STACK_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-nodeexporter:$STACK_VERSION

eval $(docker-machine env demo-worker2)

docker pull $(docker-machine ip demo-manager):5000/demo-zookeeper:$ZOOKEEPER_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-kafka:$KAFKA_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-flink:$FLINK_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-alertmanager:$STACK_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-grafana:$STACK_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-prometheus:$STACK_VERSION
docker pull $(docker-machine ip demo-manager):5000/demo-nodeexporter:$STACK_VERSION

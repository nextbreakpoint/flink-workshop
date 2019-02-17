#!/bin/sh

export STACK_VERSION=1.0
export ALERTMANAGER_IMAGE=$(docker-machine ip demo-manager):5000/demo-alertmanager:$STACK_VERSION
export NODEEXPORTER_IMAGE=$(docker-machine ip demo-manager):5000/demo-nodeexporter:$STACK_VERSION
export PROMETHEUS_IMAGE=$(docker-machine ip demo-manager):5000/demo-prometheus:$STACK_VERSION
export GRAFANA_IMAGE=$(docker-machine ip demo-manager):5000/demo-grafana:$STACK_VERSION

export SCALA_VERSION=2.11
export KAFKA_VERSION=1.1.1
export KAFKA_IMAGE=$(docker-machine ip demo-manager):5000/demo-kafka:$KAFKA_VERSION
export FLINK_VERSION=1.5.3
export FLINK_IMAGE=$(docker-machine ip demo-manager):5000/demo-flink:$FLINK_VERSION
export ZOOKEEPER_VERSION=3.4.12
export ZOOKEEPER_IMAGE=$(docker-machine ip demo-manager):5000/demo-zookeeper:$ZOOKEEPER_VERSION
export FLINK_JOBS_VERSION=0-SNAPSHOT
export FLINK_JOBS_IMAGE=$(docker-machine ip demo-manager):5000/demo-flink-jobs:$FLINK_JOBS_VERSION

export ENVIRONMENT=demo
export TASK_MANAGER_REPLICAS=1
export GRAPHITE_HOST=graphite
export FLINK_SAVEPOINTS_LOCATION=file:///tmp/savepoints
export FLINK_CHECKPOINTS_LOCATION=file:///tmp/checkpoints
export FLINK_FS_CHECKPOINTS_LOCATION=file:///tmp/fs_checkpoints

export ADMIN_USER=admin
export ADMIN_PASSWORD=admin
export SLACK_URL=https://hooks.slack.com/services/$SLACK_TOKEN
export SLACK_CHANNEL=slack-channel
export SLACK_USER=data-alertmanager

export KAFKA_HOST=$(docker-machine ip demo-worker1)

#!/bin/sh

export STACK_VERSION=1.0
export ALERTMANAGER_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-alertmanager:$STACK_VERSION
export NODEEXPORTER_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-nodeexporter:$STACK_VERSION
export PROMETHEUS_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-prometheus:$STACK_VERSION
export GRAFANA_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-grafana:$STACK_VERSION

export SCALA_VERSION=2.11
export KAFKA_VERSION=5.0.1
export KAFKA_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-kafka:$KAFKA_VERSION
export FLINK_VERSION=1.7.2
export FLINK_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-flink:$FLINK_VERSION
export ZOOKEEPER_VERSION=3.4.12
export ZOOKEEPER_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-zookeeper:$ZOOKEEPER_VERSION
export FLINK_JOBS_VERSION=1.0.1
export FLINK_JOBS_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-flink-jobs:$FLINK_JOBS_VERSION

export ENVIRONMENT=workshop
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

export KAFKA_HOST=$(docker-machine ip workshop-worker1)

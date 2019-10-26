#!/bin/sh

export ALERTMANAGER_VERSION=v0.19.0
export ALERTMANAGER_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-alertmanager:$ALERTMANAGER_VERSION
export NODEEXPORTER_VERSION=v0.18.1
export NODEEXPORTER_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-nodeexporter:$NODEEXPORTER_VERSION
export PROMETHEUS_VERSION=v2.13.1
export PROMETHEUS_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-prometheus:$PROMETHEUS_VERSION
export GRAFANA_VERSION=6.4.3
export GRAFANA_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-grafana:$GRAFANA_VERSION

export SCALA_VERSION=2.11
export KAFKA_VERSION=5.3.1
export KAFKA_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-kafka:$KAFKA_VERSION
export FLINK_VERSION=1.9.0
export FLINK_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-flink:$FLINK_VERSION
export ZOOKEEPER_VERSION=3.5.6
export ZOOKEEPER_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-zookeeper:$ZOOKEEPER_VERSION
export FLINK_JOBS_VERSION=1.1.0
export FLINK_JOBS_IMAGE=$(docker-machine ip workshop-manager):5000/workshop-flink-jobs:$FLINK_JOBS_VERSION

export ENVIRONMENT=workshop
export TASK_MANAGER_REPLICAS=1
export GRAPHITE_HOST=graphite
export FLINK_METRICS_REPORTERS=prometheus,graphite
export FLINK_SAVEPOINTS_LOCATION=file:///tmp/savepoints
export FLINK_CHECKPOINTS_LOCATION=file:///tmp/checkpoints
export FLINK_FS_CHECKPOINTS_LOCATION=file:///tmp/fs_checkpoints

export ADMIN_USER=admin
export ADMIN_PASSWORD=admin
export SLACK_URL=https://hooks.slack.com/services/$SLACK_TOKEN
export SLACK_CHANNEL=slack-channel
export SLACK_USER=data-alertmanager

export KAFKA_HOST=$(docker-machine ip workshop-worker1)

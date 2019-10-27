#!/bin/sh

cat <<EOF >docker-compose.yaml
version: '3.4'
services:
  jobmanager:
    image: workshop/flink:1.9.0
    command: 'jobmanager'
    environment:
      JOB_MANAGER_RPC_ADDRESS: 'jobmanager'
      FLINK_JM_HEAP: 128
      FLINK_CHECKPOINTS_LOCATION: file:///host/checkpoints
      FLINK_SAVEPOINTS_LOCATION: file:///host/savepoints
      FLINK_FS_CHECKPOINTS_LOCATION: file:///host/fs-checkpoints
    volumes:
      - $(pwd)/tmp:/host
    ports:
      - target: 8081
        published: 8081
        protocol: tcp
        mode: ingress
      - target: 9990
        published: 9990
        protocol: tcp
        mode: ingress
    networks:
      - workshop
  taskmanager1:
    image: workshop/flink:1.9.0
    entrypoint:
        - /wait-for-it.sh
        - jobmanager:8081
        - --timeout=300
        - --
        - /docker-entrypoint.sh
    command: 'taskmanager'
    environment:
      JOB_MANAGER_RPC_ADDRESS: 'jobmanager'
      FLINK_TM_HEAP: 2048
      TASK_MANAGER_NUMBER_OF_TASK_SLOTS: 4
      FLINK_CHECKPOINTS_LOCATION: file:///host/checkpoints
      FLINK_SAVEPOINTS_LOCATION: file:///host/savepoints
      FLINK_FS_CHECKPOINTS_LOCATION: file:///host/fs-checkpoints
    networks:
      - workshop
    volumes:
      - $(pwd)/tmp:/host
      - $(pwd)/wait-for-it.sh:/wait-for-it.sh
    ports:
      - target: 9990
        published: 9991
        protocol: tcp
        mode: ingress
  taskmanager2:
    image: workshop/flink:1.9.0
    entrypoint:
        - /wait-for-it.sh
        - jobmanager:8081
        - --timeout=300
        - --
        - /docker-entrypoint.sh
    command: 'taskmanager'
    environment:
      JOB_MANAGER_RPC_ADDRESS: 'jobmanager'
      FLINK_TM_HEAP: 2048
      TASK_MANAGER_NUMBER_OF_TASK_SLOTS: 4
      FLINK_CHECKPOINTS_LOCATION: file:///host/checkpoints
      FLINK_SAVEPOINTS_LOCATION: file:///host/savepoints
      FLINK_FS_CHECKPOINTS_LOCATION: file:///host/fs-checkpoints
    networks:
      - workshop
    volumes:
      - $(pwd)/tmp:/host
      - $(pwd)/wait-for-it.sh:/wait-for-it.sh
    ports:
      - target: 9990
        published: 9992
        protocol: tcp
        mode: ingress
  kafka:
    image: workshop/cp-kafka:5.3.1
    entrypoint:
        - /wait-for-it.sh
        - zookeeper:2181
        - --timeout=300
        - --
        - /etc/confluent/docker/run
    networks:
      - workshop
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,PLAINTEXT_HOST://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:9093
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_NUM_PARTITIONS: 32
      KAFKA_DELETE_TOPICS: "true"
      KAFKA_AUTO_CREATE_TOPICS: "true"
      KAFKA_LOG_RETENTION_HOURS: 24
      KAFKA_TRANSACTION_MAX_TIMEOUT_MS: 20000
      KAFKA_DELETE_TOPIC_ENABLE: "true"
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_LOGS_PATH: /data/kafka-logs
      KAFKA_HEAP_OPTS: "-Xmx800M -Xms800M"
      KAFKA_OPTS: "-javaagent:/opt/jmx-exporter/jmx-exporter.jar=7070:/etc/jmx-exporter/config.yml"
    volumes:
      - kafka-data:/data
      - $(pwd)/wait-for-it.sh:/wait-for-it.sh
    ports:
      - target: 9092
        published: 9092
        protocol: tcp
        mode: ingress
      - target: 9093
        published: 9093
        protocol: tcp
        mode: ingress
      - target: 7070
        published: 7071
        protocol: tcp
        mode: ingress
  schema-registry:
    image: workshop/cp-schema-registry:5.3.1
    networks:
      - workshop
    environment:
      SCHEMA_REGISTRY_HOST_NAME: "schema-registry"
      SCHEMA_REGISTRY_LISTENERS: "http://schema-registry:8082"
      SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL: "zookeeper:2181"
      SCHEMA_REGISTRY_OPTS: "-javaagent:/opt/jmx-exporter/jmx-exporter.jar=7070:/etc/jmx-exporter/config.yml"
    ports:
      - target: 8082
        published: 8082
        protocol: tcp
        mode: ingress
      - target: 7070
        published: 7073
        protocol: tcp
        mode: ingress
  zookeeper:
    image: workshop/zookeeper:3.5.6
    networks:
      - workshop
    environment:
      ZOO_MY_ID: 1
      SERVER_JVMFLAGS: "-javaagent:/opt/jmx-exporter/jmx-exporter.jar=7070:/etc/jmx-exporter/config.yml"
      #SERVER_JVMFLAGS: "-javaagent:/opt/jmx-exporter/jmx-exporter.jar=7070:/etc/jmx-exporter/config.yml -Djava.rmi.server.hostname=localhost -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.rmi.port=9010 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
    volumes:
      - zookeeper-data:/data
      - zookeeper-datalog:/datalog
    ports:
      - target: 2181
        published: 2181
        protocol: tcp
        mode: ingress
      - target: 7070
        published: 7072
        protocol: tcp
        mode: ingress
  grafana:
    image: workshop/grafana:6.4.3
    networks:
      - workshop
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false
    ports:
      - target: 3000
        published: 3000
        protocol: tcp
        mode: ingress
    volumes:
      - grafana-data:/var/lib/grafana
  prometheus:
    image: workshop/prometheus:v2.13.1
    environment:
      - ENVIRONMENT=test
    networks:
      - workshop
    volumes:
      - prometheus-data:/prometheus
    ports:
      - target: 9090
        published: 9090
        protocol: tcp
        mode: ingress
volumes:
    kafka-data:
    zookeeper-data:
    zookeeper-datalog:
    grafana-data:
    prometheus-data:
networks:
  workshop:
    external: true
EOF

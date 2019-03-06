# Data Pipeline Demo

Example of jobs using Apache Kafka and Apache Flink on local Docker Swarm cluster.

## How to deploy

Create Docker machines:

    ./create-machines.sh

Verify machines are running:

    docker-machine ls

    NAME                 ACTIVE   DRIVER       STATE     URL                         SWARM   DOCKER        ERRORS
    workshop-manager     -        virtualbox   Running   tcp://192.168.99.100:2376           v18.06.1-ce   
    workshop-worker1     -        virtualbox   Running   tcp://192.168.99.101:2376           v18.06.1-ce   
    workshop-worker2     -        virtualbox   Running   tcp://192.168.99.102:2376           v18.06.1-ce   

Configure Docker daemon:

    ./configure-docker.sh

Verify daemon is running:

    docker-machine ssh workshop-manager docker ps

Verify daemon config:

    docker-machine ssh workshop-manager cat /etc/docker/daemon.json

    {
            "experimental" : true,
            "log-driver": "json-file",
            "log-opts": {
              "labels": "component"
            },
            "default-ulimits":
            {
                    "nproc": {
                            "Name": "nproc",
                            "Hard": 4096,
                            "Soft": 4096
                    },
                    "nofile": {
                            "Name": "nofile",
                            "Hard": 65536,
                            "Soft": 65536
                    }
            },
            "metrics-addr" : "0.0.0.0:9323",
            "insecure-registries" : [
              "192.168.99.100:5000"
            ]
    }

Create Docker Swarm:

    ./create-swarm.sh

Verify Swarm is running:

    ./swarm-run.sh docker node ls

    ID                            HOSTNAME            STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
    x18uukmtx9ewtytlcf0bmmlhy *   workshop-manager        Ready               Active              Leader              18.06.1-ce
    gykya6sg35gf6uphjtmufjud4     workshop-worker1        Ready               Active                                  18.06.1-ce
    pdraedbqsmacokisq1ayma08f     workshop-worker2        Ready               Active                                  18.06.1-ce

Create local Docker registry:

    ./create-registry.sh

Verify registry is running:

    ./swarm-run.sh docker ps

    CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
    5826d2efbda3        registry:2          "/entrypoint.sh /etc…"   33 seconds ago      Up 32 seconds       0.0.0.0:5000->5000/tcp   registry

Build local Docker images:

    ./build-images.sh

Verify images have been created:

    ./swarm-run.sh docker images

    REPOSITORY                                  TAG                                IMAGE ID            CREATED             SIZE
    192.168.99.100:5000/workshop-nodeexporter   1.0                                0ee3afaea191        4 minutes ago       22.9MB
    workshop-nodeexporter                       1.0                                0ee3afaea191        4 minutes ago       22.9MB
    192.168.99.100:5000/workshop-prometheus     1.0                                d2d46bb58663        5 minutes ago       119MB
    workshop-prometheus                         1.0                                d2d46bb58663        5 minutes ago       119MB
    192.168.99.100:5000/workshop-grafana        1.0                                a2f44c3d7d5c        5 minutes ago       245MB
    workshop-grafana                            1.0                                a2f44c3d7d5c        5 minutes ago       245MB
    192.168.99.100:5000/workshop-alertmanager   1.0                                3cba678ab637        5 minutes ago       36.2MB
    workshop-alertmanager                       1.0                                3cba678ab637        5 minutes ago       36.2MB
    192.168.99.100:5000/workshop-flink          1.5.3                              f8c42019c315        5 minutes ago       601MB
    workshop-flink                              1.5.3                              f8c42019c315        5 minutes ago       601MB
    192.168.99.100:5000/workshop-kafka          1.1.1                              06e1ada255ed        7 minutes ago       485MB
    workshop-kafka                              1.1.1                              06e1ada255ed        7 minutes ago       485MB
    192.168.99.100:5000/workshop-zookeeper      3.4.12                             3a39aa41abfe        9 minutes ago       151MB
    workshop-zookeeper                          3.4.12                             3a39aa41abfe        9 minutes ago       151MB
    grafana/grafana                             5.2.4                              920eb69ade2a        3 days ago          245MB
    flink                                       1.5.3-hadoop28-scala_2.11-alpine   5571c961d2e5        6 days ago          430MB
    prom/alertmanager                           v0.15.2                            a21bf3a63620        4 weeks ago         36.2MB
    prom/prometheus                             v2.3.2                             0915a968017e        2 months ago        119MB
    zookeeper                                   3.4.12                             cfed220ec48b        2 months ago        148MB
    registry                                    2                                  b2b03e9146e1        2 months ago        33.3MB
    prom/node-exporter                          v0.16.0                            188af75e2de0        3 months ago        22.9MB
    java                                        openjdk-8-jre                      e44d62cf8862        20 months ago       311MB

Build jobs Docker image:

    ./build-jobs.sh

Deploy the servers:

    ./swarm-run.sh ./deploy-servers.sh

Create Kafka topics:

    ./swarm-run.sh ./create-topics.sh

Deploy the Flink jobs:

    ./swarm-run.sh ./deploy-flink.sh

Verify the stacks has been created:

    ./swarm-run.sh docker stack ls

    NAME                SERVICES            ORCHESTRATOR
    servers             10                  Swarm
    flink               9                   Swarm

Verify the services are running:

    ./swarm-run.sh docker service ls

Tail the service logs:

    ./swarm-run.sh docker service logs -f flink_workshop-jobmanager

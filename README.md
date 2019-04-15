# Data Pipeline Demo

Example of jobs using Apache Kafka and Apache Flink on local Docker Swarm cluster.

## How to deploy

Open a terminal and change current directory to swarm.

Create Docker machines:

    ./create-machines.sh

Verify machines are running:

    docker-machine ls

    NAME                 ACTIVE   DRIVER       STATE     URL                         SWARM   DOCKER        ERRORS
    workshop-manager     -        virtualbox   Running   tcp://192.168.99.100:2376           v18.09.3-ce   
    workshop-worker1     -        virtualbox   Running   tcp://192.168.99.101:2376           v18.09.3-ce   
    workshop-worker2     -        virtualbox   Running   tcp://192.168.99.102:2376           v18.09.3-ce   

Configure Docker daemon:

    ./configure-docker.sh

Verify daemon is running:

    docker-machine ssh workshop-manager docker info

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

    ./swarm-manager.sh docker node ls

    ID                            HOSTNAME                STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
    x18uukmtx9ewtytlcf0bmmlhy *   workshop-manager        Ready               Active              Leader              18.09.3-ce
    gykya6sg35gf6uphjtmufjud4     workshop-worker1        Ready               Active                                  18.09.3-ce
    pdraedbqsmacokisq1ayma08f     workshop-worker2        Ready               Active                                  18.09.3-ce

Create overlay network:

    ./create-network.sh

Verify workshop network has been created:

    ./swarm-manager.sh docker network ls

    NETWORK ID          NAME                DRIVER              SCOPE
    e5f12f246b86        bridge              bridge              local
    fc9bedb73bf0        docker_gwbridge     bridge              local
    8755236950a0        host                host                local
    jj06e7pxydsr        ingress             overlay             swarm
    aac2389b5947        none                null                local
    2582blnqoz6m        workshop            overlay             swarm

Create local Docker registry:

    ./create-registry.sh

Verify registry is running:

    ./swarm-manager.sh docker ps

    CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
    5826d2efbda3        registry:2          "/entrypoint.sh /etc…"   33 seconds ago      Up 32 seconds       0.0.0.0:5000->5000/tcp   registry

Build Docker images:

    ./build-docker-images.sh

Build workshop Docker image:

    ./build-workshop-image.sh

Verify images have been created:

    ./swarm-manager.sh docker images

    REPOSITORY                                  TAG                                IMAGE ID            CREATED             SIZE
    192.168.99.109:5000/workshop-flink-jobs     0-SNAPSHOT                         eff10741e55b        24 seconds ago      607MB
    workshop-flink                              0-SNAPSHOT                         eff10741e55b        24 seconds ago      607MB
    192.168.99.109:5000/workshop-nodeexporter   1.0                                3a180cf220a3        11 minutes ago      22.9MB
    workshop-nodeexporter                       1.0                                3a180cf220a3        11 minutes ago      22.9MB
    192.168.99.109:5000/workshop-prometheus     1.0                                2c409aa9f37a        11 minutes ago      119MB
    workshop-prometheus                         1.0                                2c409aa9f37a        11 minutes ago      119MB
    192.168.99.109:5000/workshop-grafana        1.0                                02134dd73dcc        11 minutes ago      241MB
    workshop-grafana                            1.0                                02134dd73dcc        11 minutes ago      241MB
    192.168.99.109:5000/workshop-alertmanager   1.0                                9ef9d7440984        11 minutes ago      36.2MB
    workshop-alertmanager                       1.0                                9ef9d7440984        11 minutes ago      36.2MB
    192.168.99.109:5000/workshop-flink          1.7.2                              4ca244d2ae3a        11 minutes ago      603MB
    workshop-flink                              1.7.2                              4ca244d2ae3a        11 minutes ago      603MB
    192.168.99.109:5000/workshop-kafka          5.0.1                              c947594a937f        14 minutes ago      558MB
    workshop-kafka                              5.0.1                              c947594a937f        14 minutes ago      558MB
    192.168.99.109:5000/workshop-zookeeper      3.4.12                             d897a9ddd7d3        15 minutes ago      151MB
    workshop-zookeeper                          3.4.12                             d897a9ddd7d3        15 minutes ago      151MB
    flink                                       1.7.2-hadoop28-scala_2.11-alpine   5b894ae0b905        5 days ago          429MB
    registry                                    2                                  f32a97de94e1        5 weeks ago         25.8MB
    grafana/grafana                             5.4.3                              d0454da13c84        3 months ago        240MB
    confluentinc/cp-kafka                       5.0.1                              5467234daea9        5 months ago        557MB
    prom/alertmanager                           v0.15.2                            a21bf3a63620        8 months ago        36.2MB
    prom/prometheus                             v2.3.2                             0915a968017e        9 months ago        119MB
    zookeeper                                   3.4.12                             cfed220ec48b        9 months ago        148MB
    prom/node-exporter                          v0.16.0                            188af75e2de0        11 months ago       22.9MB

Deploy the servers:

    ./swarm-manager.sh ./deploy-servers.sh

Create Kafka topics:

    ./swarm-manager.sh ./create-topics.sh

Deploy the Flink jobs:

    ./swarm-manager.sh ./deploy-flink.sh

Verify the stacks has been created:

    ./swarm-manager.sh docker stack ls

    NAME                SERVICES            ORCHESTRATOR
    servers             10                  Swarm
    flink               9                   Swarm

Verify the services are running:

    ./swarm-manager.sh docker service ls

    ID                  NAME                          MODE                REPLICAS            IMAGE                                                PORTS
    yk73ly7p3d9j        flink_aggregate-cli           replicated          1/1                 192.168.99.109:5000/workshop-flink-jobs:0-SNAPSHOT   
    xqa97ok6gvf5        flink_aggregate-jobmanager    replicated          1/1                 192.168.99.109:5000/workshop-flink:1.7.2             *:28081->8081/tcp
    wh6c56a55j0t        flink_aggregate-taskmanager   replicated          1/1                 192.168.99.109:5000/workshop-flink:1.7.2             
    vt61a43qmwpq        flink_generate-cli            replicated          1/1                 192.168.99.109:5000/workshop-flink-jobs:0-SNAPSHOT   
    qvu1f3896gs4        flink_generate-jobmanager     replicated          1/1                 192.168.99.109:5000/workshop-flink:1.7.2             *:18081->8081/tcp
    8kcpid7mwk5m        flink_generate-taskmanager    replicated          1/1                 192.168.99.109:5000/workshop-flink:1.7.2             
    ottxwvigtvfv        flink_print-cli               replicated          1/1                 192.168.99.109:5000/workshop-flink-jobs:0-SNAPSHOT   
    34nk9k658fka        flink_print-jobmanager        replicated          1/1                 192.168.99.109:5000/workshop-flink:1.7.2             *:38081->8081/tcp
    e4uux5nnbomt        flink_print-taskmanager       replicated          1/1                 192.168.99.109:5000/workshop-flink:1.7.2             
    svbmw3i1rr0v        servers_alertmanager          replicated          1/1                 192.168.99.109:5000/workshop-alertmanager:1.0        *:9093->9093/tcp
    hco41sxqhcc1        servers_cadvisor              global              3/3                 google/cadvisor:latest                               
    3zvnubeifovv        servers_dockerd-exporter      global              3/3                 stefanprodan/caddy:latest                            
    t8dastcax40r        servers_grafana               replicated          1/1                 192.168.99.109:5000/workshop-grafana:1.0             *:8081->3000/tcp
    lgpc9vjaqdow        servers_graphite              replicated          1/1                 graphiteapp/graphite-statsd:latest                   *:2003->2003/tcp, *:8080->80/tcp
    z9aulmzyf0h4        servers_kafka                 replicated          1/1                 192.168.99.109:5000/workshop-kafka:5.0.1             *:9092->9092/tcp
    h1k5lsqtw8pz        servers_node-exporter         global              3/3                 192.168.99.109:5000/workshop-nodeexporter:1.0        
    gdv8zewlx6xf        servers_prometheus            replicated          1/1                 192.168.99.109:5000/workshop-prometheus:1.0          *:9090->9090/tcp
    vncbw3xx4h2n        servers_unsee                 replicated          1/1                 cloudflare/unsee:v0.8.0                              
    mxaxxtvmw3p6        servers_zookeeper             replicated          1/1                 192.168.99.109:5000/workshop-zookeeper:3.4.12        *:2181->2181/tcp
    
Tail the logs of Generate job:

    ./swarm-manager.sh docker service logs -f flink_generate-jobmanager
    ./swarm-manager.sh docker service logs -f flink_generate-taskmanager

Tail the logs of Aggregate job:

    ./swarm-manager.sh docker service logs -f flink_aggregate-jobmanager
    ./swarm-manager.sh docker service logs -f flink_aggregate-taskmanager

Tail the logs of Print job:

    ./swarm-manager.sh docker service logs -f flink_print-jobmanager
    ./swarm-manager.sh docker service logs -f flink_print-taskmanager

Consumer messages from input topic:

    docker run --rm -it confluentinc/cp-kafka:5.0.1 kafka-console-consumer --bootstrap-server 192.168.99.100:9092 --topic test-input --from-beginning

Consumer messages from output topic:

    docker run --rm -it confluentinc/cp-kafka:5.0.1 kafka-console-consumer --bootstrap-server 192.168.99.100:9092 --topic test-output --from-beginning

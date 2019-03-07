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

Verify demo network has been created:

    ./swarm-manager.sh docker network ls

    NETWORK ID          NAME                DRIVER              SCOPE
    e5f12f246b86        bridge              bridge              local
    2582blnqoz6m        demo                overlay             swarm
    fc9bedb73bf0        docker_gwbridge     bridge              local
    8755236950a0        host                host                local
    jj06e7pxydsr        ingress             overlay             swarm
    aac2389b5947        none                null                local

Create local Docker registry:

    ./create-registry.sh

Verify registry is running:

    ./swarm-manager.sh docker ps

    CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
    5826d2efbda3        registry:2          "/entrypoint.sh /etc…"   33 seconds ago      Up 32 seconds       0.0.0.0:5000->5000/tcp   registry

Build servers Docker images:

    ./build-images.sh

Verify images have been created:

    ./swarm-manager.sh docker images

    REPOSITORY                                  TAG                                IMAGE ID            CREATED             SIZE
    192.168.99.108:5000/workshop-alertmanager   1.0                                deebb6bbaf3b        2 minutes ago       36.2MB
    workshop-alertmanager                       1.0                                deebb6bbaf3b        2 minutes ago       36.2MB
    192.168.99.108:5000/workshop-kafka          5.0.1                              b57cddac18a7        2 minutes ago       558MB
    workshop-kafka                              5.0.1                              b57cddac18a7        2 minutes ago       558MB
    192.168.99.108:5000/workshop-nodeexporter   1.0                                5e57de9560a1        6 minutes ago       22.9MB
    workshop-nodeexporter                       1.0                                5e57de9560a1        6 minutes ago       22.9MB
    192.168.99.108:5000/workshop-prometheus     1.0                                16e0054d3584        6 minutes ago       119MB
    workshop-prometheus                         1.0                                16e0054d3584        6 minutes ago       119MB
    192.168.99.108:5000/workshop-grafana        1.0                                6fe55e5ec7ca        7 minutes ago       245MB
    workshop-grafana                            1.0                                6fe55e5ec7ca        7 minutes ago       245MB
    192.168.99.108:5000/workshop-flink          1.7.2                              abe2fbcd22f0        7 minutes ago       603MB
    workshop-flink                              1.7.2                              abe2fbcd22f0        7 minutes ago       603MB
    192.168.99.108:5000/workshop-zookeeper      3.4.12                             16e0f10e009c        8 minutes ago       151MB
    workshop-zookeeper                          3.4.12                             16e0f10e009c        8 minutes ago       151MB
    flink                                       1.7.2-hadoop28-scala_2.11-alpine   d4ef8891f2cd        2 weeks ago         429MB
    registry                                    2                                  d0eed8dad114        4 weeks ago         25.8MB
    confluentinc/cp-kafka                       5.0.1                              5467234daea9        4 months ago        557MB
    grafana/grafana                             5.2.4                              920eb69ade2a        6 months ago        245MB
    prom/alertmanager                           v0.15.2                            a21bf3a63620        6 months ago        36.2MB
    prom/prometheus                             v2.3.2                             0915a968017e        7 months ago        119MB
    zookeeper                                   3.4.12                             cfed220ec48b        7 months ago        148MB
    prom/node-exporter                          v0.16.0                            188af75e2de0        9 months ago        22.9MB

Build Flink demo Docker image:

    ./build-demo.sh

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
    i7vff0fwyk6k        flink_aggregate-cli           replicated          1/1                 192.168.99.108:5000/workshop-flink-jobs:0-SNAPSHOT   
    t2zem4a05l9q        flink_aggregate-jobmanager    replicated          1/1                 192.168.99.108:5000/workshop-flink:1.7.2             
    5oka2zt7k0xk        flink_aggregate-taskmanager   replicated          1/1                 192.168.99.108:5000/workshop-flink:1.7.2             
    l70tlus1ncc4        flink_generate-cli            replicated          1/1                 192.168.99.108:5000/workshop-flink-jobs:0-SNAPSHOT   
    s2mdv0lk7pke        flink_generate-jobmanager     replicated          1/1                 192.168.99.108:5000/workshop-flink:1.7.2             
    p9r8o40t393v        flink_generate-taskmanager    replicated          1/1                 192.168.99.108:5000/workshop-flink:1.7.2             
    2u8sxr0y9uzn        servers_alertmanager          replicated          1/1                 192.168.99.108:5000/workshop-alertmanager:1.0        *:9093->9093/tcp
    ja0sadap189u        servers_cadvisor              global              3/3                 google/cadvisor:latest                               
    c0gf7ybhl1a9        servers_dockerd-exporter      global              3/3                 stefanprodan/caddy:latest                            
    p7es593xmqll        servers_grafana               replicated          1/1                 192.168.99.108:5000/workshop-grafana:1.0             *:8081->3000/tcp
    t2uimqbfkdu1        servers_graphite              replicated          1/1                 hopsoft/graphite-statsd:latest                       *:80->80/tcp, *:2003->2003/tcp
    lum3b9nt1xp1        servers_kafka                 replicated          1/1                 192.168.99.108:5000/workshop-kafka:5.0.1             *:9092->9092/tcp
    i095cbcg7p14        servers_node-exporter         global              3/3                 192.168.99.108:5000/workshop-nodeexporter:1.0        
    86zcshobo9eo        servers_prometheus            replicated          1/1                 192.168.99.108:5000/workshop-prometheus:1.0          *:9090->9090/tcp
    rrorrrblzjye        servers_unsee                 replicated          1/1                 cloudflare/unsee:v0.8.0                              
    xveyaw82fkus        servers_zookeeper             replicated          1/1                 192.168.99.108:5000/workshop-zookeeper:3.4.12        *:2181->2181/tcp

Tail the logs of Generate job:

    ./swarm-manager.sh docker service logs -f flink_generate-jobmanager
    ./swarm-manager.sh docker service logs -f flink_generate-taskmanager

Tail the logs of Aggregate job:

    ./swarm-manager.sh docker service logs -f flink_aggregate-jobmanager
    ./swarm-manager.sh docker service logs -f flink_aggregate-taskmanager

Tail the logs of Output job:

    ./swarm-manager.sh docker service logs -f flink_output-jobmanager
    ./swarm-manager.sh docker service logs -f flink_output-taskmanager

Consumer messages from input topic:

    docker run --rm -it confluentinc/cp-kafka:5.0.1 kafka-console-consumer --bootstrap-server 192.168.99.108:9092 --topic test-input --from-beginning

Consumer messages from output topic:

    docker run --rm -it confluentinc/cp-kafka:5.0.1 kafka-console-consumer --bootstrap-server 192.168.99.108:9092 --topic test-output --from-beginning

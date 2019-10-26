# flink-workshop

Example of jobs using Apache Kafka and Apache Flink on local Docker Swarm cluster.

## License

The project is distributed under the terms of BSD 3-Clause License.

    Copyright (c) 2019, Andrea Medeghini
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this
      list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.

    * Neither the name of the project nor the names of its
      contributors may be used to endorse or promote products derived from
      this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
    FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
    SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
    CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
    OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
    OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

## Documentation

Install Docker CE 2.1.x and VirtualBox 6.0.x.

Open a terminal and change directory:

    cd swarm

Create Docker machines:

    ./create-machines.sh

Check machines are running:

    docker-machine ls

    NAME               ACTIVE   DRIVER       STATE     URL                         SWARM   DOCKER     ERRORS
    workshop-manager   -        virtualbox   Running   tcp://192.168.99.100:2376           v19.03.4
    workshop-worker1   -        virtualbox   Running   tcp://192.168.99.101:2376           v19.03.4
    workshop-worker2   -        virtualbox   Running   tcp://192.168.99.102:2376           v19.03.4

Configure Docker daemon:

    ./configure-docker.sh

Check daemon is running:

    docker-machine ssh workshop-manager docker info

    Client:
     Debug Mode: false

    Server:
     Containers: 0
      Running: 0
      Paused: 0
      Stopped: 0
     Images: 0
     Server Version: 19.03.4
     Storage Driver: overlay2
      Backing Filesystem: extfs
      Supports d_type: true
      Native Overlay Diff: true
     Logging Driver: json-file
     Cgroup Driver: cgroupfs
     Plugins:
      Volume: local
      Network: bridge host ipvlan macvlan null overlay
      Log: awslogs fluentd gcplogs gelf journald json-file local logentries splunk syslog
     Swarm: inactive
     Runtimes: runc
     Default Runtime: runc
     Init Binary: docker-init
     containerd version: b34a5c8af56e510852c35414db4c1f4fa6172339
     runc version: 3e425f80a8c931f88e6d94a8c831b9d5aa481657
     init version: fec3683
     Security Options:
      seccomp
       Profile: default
     Kernel Version: 4.14.150-boot2docker
     Operating System: Boot2Docker 19.03.4 (TCL 10.1)
     OSType: linux
     Architecture: x86_64
     CPUs: 1
     Total Memory: 1.951GiB
     Name: workshop-manager
     ID: TEF5:IKFL:FMOY:GMCP:6GNP:GCQU:L2WI:CNYM:JP5F:JJ47:OYTP:VPOZ
     Docker Root Dir: /mnt/sda1/var/lib/docker
     Debug Mode: false
     Registry: https://index.docker.io/v1/
     Labels:
      provider=virtualbox
     Experimental: true
     Insecure Registries:
      192.168.99.100:5000
      127.0.0.0/8
     Live Restore Enabled: false
     Product License: Community Engine

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

Check Swarm is running:

    ./swarm-manager.sh docker node ls

    ID                            HOSTNAME            STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
    xy6u4ypqiw1vrwt229iyaxbhz *   workshop-manager    Ready               Active              Leader              19.03.4
    x473aw0q6153b61xspdu5vu7y     workshop-worker1    Ready               Active                                  19.03.4
    7wltmmyi6qbshcl5t8khcd6dx     workshop-worker2    Ready               Active                                  19.03.4

Create overlay network:

    ./create-network.sh

Check workshop network has been created:

    ./swarm-manager.sh docker network ls

    NETWORK ID          NAME                DRIVER              SCOPE
    4eeab94637e1        bridge              bridge              local
    64c438d23485        docker_gwbridge     bridge              local
    61988f755457        host                host                local
    e7p527ehbkzw        ingress             overlay             swarm
    f6fc3ac8b647        none                null                local
    t0zjjetg1jh5        workshop            overlay             swarm

Create local Docker registry:

    ./create-registry.sh

Check registry is running:

    ./swarm-manager.sh docker ps

    CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
    6a471e315ce2        registry:2          "/entrypoint.sh /etc…"   5 seconds ago       Up 5 seconds        0.0.0.0:5000->5000/tcp   registry

Build Docker images:

    ./build-docker-images.sh

Build workshop Docker image:

    ./build-workshop-image.sh

Check images have been created:

    ./swarm-manager.sh docker images

    REPOSITORY                                  TAG                 IMAGE ID            CREATED             SIZE
    192.168.99.100:5000/workshop-flink-jobs     1.1.0               bc332384f631        About a minute ago   554MB
    192.168.99.100:5000/workshop-kafka          5.3.1               327bbddc08e2        20 minutes ago       590MB
    192.168.99.100:5000/workshop-zookeeper      3.5.6               0a00c2f647ac        20 minutes ago       241MB
    192.168.99.100:5000/workshop-nodeexporter   v0.18.1             146ec2fb7ed9        21 minutes ago       22.9MB
    192.168.99.100:5000/workshop-prometheus     v2.13.1             160b91a27b5f        21 minutes ago       129MB
    192.168.99.100:5000/workshop-grafana        6.4.3               a4d9f00a263d        21 minutes ago       206MB
    192.168.99.100:5000/workshop-alertmanager   v0.19.0             4b73369112e5        22 minutes ago       53.2MB
    192.168.99.100:5000/workshop-flink          1.9.0               ae32e42089a2        22 minutes ago       741MB
    zookeeper                                   3.5.6               e41846a619f5        5 days ago           224MB
    flink                                       1.9.0-scala_2.11    8fde30155334        7 days ago           544MB
    maven                                       3.6.2-jdk-8         99eb11a22d10        7 days ago           499MB
    prom/prometheus                             v2.13.1             2c8e464e47f4        9 days ago           129MB
    grafana/grafana                             6.4.3               a532fe3b344a        10 days ago          206MB
    confluentinc/cp-kafka                       5.3.1               be6286eb3417        7 weeks ago          590MB
    prom/alertmanager                           v0.19.0             30594e96cbe8        7 weeks ago          53.2MB
    prom/node-exporter                          v0.18.1             e5a616e4b9cf        4 months ago         22.9MB
    registry                                    2                   f32a97de94e1        7 months ago         25.8MB

Deploy the servers:

    ./swarm-manager.sh ./deploy-servers.sh

Create Kafka topics:

    ./swarm-manager.sh ./create-topics.sh

Deploy the Flink jobs:

    ./swarm-manager.sh ./deploy-flink.sh

Check stack has been created:

    ./swarm-manager.sh docker stack ls

    NAME                SERVICES            ORCHESTRATOR
    servers             10                  Swarm
    flink               9                   Swarm

Check services are running:

    ./swarm-manager.sh docker service ls

    ID                  NAME                          MODE                REPLICAS            IMAGE                                                PORTS
    yk73ly7p3d9j        flink_aggregate-cli           replicated          1/1                 192.168.99.100:5000/workshop-flink-jobs:1.1.0   
    xqa97ok6gvf5        flink_aggregate-jobmanager    replicated          1/1                 192.168.99.100:5000/workshop-flink:1.9.0             *:28081->8081/tcp
    wh6c56a55j0t        flink_aggregate-taskmanager   replicated          1/1                 192.168.99.100:5000/workshop-flink:1.9.0             
    vt61a43qmwpq        flink_generate-cli            replicated          1/1                 192.168.99.100:5000/workshop-flink-jobs:1.1.0   
    qvu1f3896gs4        flink_generate-jobmanager     replicated          1/1                 192.168.99.100:5000/workshop-flink:1.9.0             *:18081->8081/tcp
    8kcpid7mwk5m        flink_generate-taskmanager    replicated          1/1                 192.168.99.100:5000/workshop-flink:1.9.0             
    ottxwvigtvfv        flink_print-cli               replicated          1/1                 192.168.99.100:5000/workshop-flink-jobs:1.1.0   
    34nk9k658fka        flink_print-jobmanager        replicated          1/1                 192.168.99.100:5000/workshop-flink:1.9.0             *:38081->8081/tcp
    e4uux5nnbomt        flink_print-taskmanager       replicated          1/1                 192.168.99.100:5000/workshop-flink:1.9.0             
    owm1pgp26324        servers_alertmanager          replicated          1/1                 192.168.99.100:5000/workshop-alertmanager:v0.19.0    *:9093->9093/tcp
    oer7uyx11uth        servers_cadvisor              global              3/3                 google/cadvisor:latest
    5fydyjn550u9        servers_dockerd-exporter      global              3/3                 stefanprodan/caddy:latest
    xfcs5vgrsl4i        servers_grafana               replicated          1/1                 192.168.99.100:5000/workshop-grafana:6.4.3           *:8081->3000/tcp
    jput70dfcn9w        servers_graphite              replicated          1/1                 graphiteapp/graphite-statsd:latest                   *:2003->2003/tcp, *:8080->80/tcp
    jgbeumfauag4        servers_kafka                 replicated          1/1                 192.168.99.100:5000/workshop-kafka:5.3.1             *:9092->9092/tcp
    unejdnofjedp        servers_node-exporter         global              3/3                 192.168.99.100:5000/workshop-nodeexporter:v0.18.1
    x0s61ya0jvae        servers_prometheus            replicated          1/1                 192.168.99.100:5000/workshop-prometheus:v2.13.1      *:9090->9090/tcp
    s9epz2ciupt3        servers_unsee                 replicated          1/1                 cloudflare/unsee:v0.9.2
    c35jrh8kqf3o        servers_zookeeper             replicated          1/1                 192.168.99.100:5000/workshop-zookeeper:3.5.6         *:2181->2181/tcp

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

    docker run --rm -it confluentinc/cp-kafka:5.3.1 kafka-console-consumer --bootstrap-server 192.168.99.100:9092 --topic test-input --from-beginning

Consumer messages from output topic:

    docker run --rm -it confluentinc/cp-kafka:5.3.1 kafka-console-consumer --bootstrap-server 192.168.99.100:9092 --topic test-output --from-beginning

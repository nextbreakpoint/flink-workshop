#!/bin/sh

. variables.sh

eval $(docker-machine env demo-manager)

DEMO_VERSION=0-SNAPSHOT

cd demo && mvn clean package

docker tag demo-flink:${DEMO_VERSION} $(docker-machine ip demo-manager):5000/demo-flink-jobs:${DEMO_VERSION}

docker push $(docker-machine ip demo-manager):5000/demo-flink-jobs:${DEMO_VERSION}

eval $(docker-machine env demo-manager)

docker pull $(docker-machine ip demo-manager):5000/demo-flink-jobs:${DEMO_VERSION}

eval $(docker-machine env demo-worker1)

docker pull $(docker-machine ip demo-manager):5000/demo-flink-jobs:${DEMO_VERSION}

eval $(docker-machine env demo-worker2)

docker pull $(docker-machine ip demo-manager):5000/demo-flink-jobs:${DEMO_VERSION}

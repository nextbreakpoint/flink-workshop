#!/bin/sh

. variables.sh

eval $(docker-machine env workshop-manager)

DEMO_VERSION=0-SNAPSHOT

cd demo && mvn clean package

docker tag workshop-flink:${DEMO_VERSION} $(docker-machine ip workshop-manager):5000/workshop-flink-jobs:${DEMO_VERSION}

docker push $(docker-machine ip workshop-manager):5000/workshop-flink-jobs:${DEMO_VERSION}

eval $(docker-machine env workshop-manager)

docker pull $(docker-machine ip workshop-manager):5000/workshop-flink-jobs:${DEMO_VERSION}

eval $(docker-machine env workshop-worker1)

docker pull $(docker-machine ip workshop-manager):5000/workshop-flink-jobs:${DEMO_VERSION}

eval $(docker-machine env workshop-worker2)

docker pull $(docker-machine ip workshop-manager):5000/workshop-flink-jobs:${DEMO_VERSION}

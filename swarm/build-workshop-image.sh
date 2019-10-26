#!/bin/sh

. variables.sh

eval $(docker-machine env workshop-manager)

ROOT_PATH=$(pwd)/../flink/com.nextbreakpoint.flinkworkshop

pushd $ROOT_PATH

docker build -t $(docker-machine ip workshop-manager):5000/workshop-flink-jobs:${FLINK_JOBS_VERSION} --build-arg flink_version=${FLINK_VERSION} --build-arg scala_version=${SCALA_VERSION} .

popd

docker push $(docker-machine ip workshop-manager):5000/workshop-flink-jobs:${FLINK_JOBS_VERSION}

eval $(docker-machine env workshop-manager)

docker pull $(docker-machine ip workshop-manager):5000/workshop-flink-jobs:${FLINK_JOBS_VERSION}

eval $(docker-machine env workshop-worker1)

docker pull $(docker-machine ip workshop-manager):5000/workshop-flink-jobs:${FLINK_JOBS_VERSION}

eval $(docker-machine env workshop-worker2)

docker pull $(docker-machine ip workshop-manager):5000/workshop-flink-jobs:${FLINK_JOBS_VERSION}

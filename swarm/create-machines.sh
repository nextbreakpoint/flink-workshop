#!/bin/sh

docker-machine create -d virtualbox --virtualbox-memory "2048" workshop-manager
docker-machine create -d virtualbox --virtualbox-memory "3072" workshop-worker1
docker-machine create -d virtualbox --virtualbox-memory "3072" workshop-worker2

#!/bin/sh

docker-machine create -d virtualbox --virtualbox-memory "2048" demo-manager
docker-machine create -d virtualbox --virtualbox-memory "3072" demo-worker1
docker-machine create -d virtualbox --virtualbox-memory "3072" demo-worker2

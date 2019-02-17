#!/bin/sh

eval $(docker-machine env demo-manager)
docker swarm init --advertise-addr $(docker-machine ip demo-manager)

eval $(docker-machine env demo-manager)
export TOKEN=$(docker swarm join-token worker -q)

eval $(docker-machine env demo-worker1)
docker swarm join --token $TOKEN $(docker-machine ip demo-manager):2377

eval $(docker-machine env demo-worker2)
docker swarm join --token $TOKEN $(docker-machine ip demo-manager):2377

eval $(docker-machine env demo-manager)
export NODE=$(docker node ls -q --filter "name=demo-worker1")
docker node update --label-add zone=a $NODE
export NODE=$(docker node ls -q --filter "name=demo-worker2")
docker node update --label-add zone=b $NODE

eval $(docker-machine env demo-manager)
docker network create demo -d overlay --subnet 192.168.10.0/24 --attachable

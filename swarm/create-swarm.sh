#!/bin/sh

eval $(docker-machine env workshop-manager)
docker swarm init --advertise-addr $(docker-machine ip workshop-manager)

eval $(docker-machine env workshop-manager)
export TOKEN=$(docker swarm join-token worker -q)

eval $(docker-machine env workshop-worker1)
docker swarm join --token $TOKEN $(docker-machine ip workshop-manager):2377

eval $(docker-machine env workshop-worker2)
docker swarm join --token $TOKEN $(docker-machine ip workshop-manager):2377

eval $(docker-machine env workshop-manager)

export NODE=$(docker node ls -q --filter "name=workshop-worker1")
docker node update --label-add zone=a $NODE

export NODE=$(docker node ls -q --filter "name=workshop-worker2")
docker node update --label-add zone=b $NODE

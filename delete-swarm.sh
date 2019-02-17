#!/bin/sh

eval $(docker-machine env demo-manager)
docker swarm leave -f

eval $(docker-machine env demo-worker1)
docker swarm leave -f

eval $(docker-machine env demo-worker2)
docker swarm leave -f

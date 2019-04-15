#!/bin/sh

eval $(docker-machine env workshop-manager)
docker swarm leave -f

eval $(docker-machine env workshop-worker1)
docker swarm leave -f

eval $(docker-machine env workshop-worker2)
docker swarm leave -f

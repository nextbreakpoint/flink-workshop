#!/bin/sh

eval $(docker-machine env workshop-manager)

docker network create demo -d overlay --subnet 192.168.10.0/24 --attachable

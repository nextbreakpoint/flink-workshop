#!/bin/sh

eval $(docker-machine env workshop-manager)

docker network create workshop -d overlay --subnet 192.168.10.0/24 --attachable

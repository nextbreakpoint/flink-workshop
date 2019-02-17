#!/bin/sh

eval $(docker-machine env demo-manager)
docker network create demo -d overlay --subnet 192.168.10.0/24 --attachable

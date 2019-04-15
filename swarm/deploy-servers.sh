#!/bin/sh

docker stack deploy -c workshop-servers.yaml servers --with-registry-auth

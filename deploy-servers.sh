#!/bin/sh

docker stack deploy -c demo-servers.yaml servers --with-registry-auth

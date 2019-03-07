#!/bin/sh

docker stack deploy -c workshop-flink.yaml flink --with-registry-auth

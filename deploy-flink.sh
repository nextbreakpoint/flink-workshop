#!/bin/sh

docker stack deploy -c demo-flink.yaml flink --with-registry-auth

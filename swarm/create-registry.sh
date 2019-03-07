#!/bin/sh

eval $(docker-machine env workshop-manager)

docker run -d -p 5000:5000 --memory=200M --cpus=0.2 --restart=always --name registry registry:2

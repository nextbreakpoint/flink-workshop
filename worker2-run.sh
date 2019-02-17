#!/bin/sh

. variables.sh

eval $(docker-machine env demo-worker2)

$@

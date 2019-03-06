#!/bin/sh

. variables.sh

eval $(docker-machine env workshop-manager)

$@

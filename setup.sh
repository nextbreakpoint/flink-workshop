#!/bin/sh

pushd docker
mkdir -p tmp
./build_images.sh
./make_compose.sh
popd

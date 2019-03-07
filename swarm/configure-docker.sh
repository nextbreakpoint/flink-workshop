#!/bin/sh

docker-machine ssh workshop-manager "sudo cat <<EOF >daemon.json
{
        \"experimental\" : true,
        \"log-driver\": \"json-file\",
        \"log-opts\": {
          \"labels\": \"component\"
        },
        \"default-ulimits\":
        {
                \"nproc\": {
                        \"Name\": \"nproc\",
                        \"Hard\": 4096,
                        \"Soft\": 4096
                },
                \"nofile\": {
                        \"Name\": \"nofile\",
                        \"Hard\": 65536,
                        \"Soft\": 65536
                }
        },
        \"metrics-addr\" : \"0.0.0.0:9323\",
        \"insecure-registries\" : [
          \"$(docker-machine ip workshop-manager):5000\"
        ]
}
EOF"

docker-machine ssh workshop-worker1 "sudo cat <<EOF >daemon.json
{
        \"experimental\" : true,
        \"log-driver\": \"json-file\",
        \"log-opts\": {
          \"labels\": \"component\"
        },
        \"default-ulimits\":
        {
                \"nproc\": {
                        \"Name\": \"nproc\",
                        \"Hard\": 4096,
                        \"Soft\": 4096
                },
                \"nofile\": {
                        \"Name\": \"nofile\",
                        \"Hard\": 65536,
                        \"Soft\": 65536
                }
        },
        \"metrics-addr\" : \"0.0.0.0:9323\",
        \"insecure-registries\" : [
          \"$(docker-machine ip workshop-manager):5000\"
        ]
}
EOF"

docker-machine ssh workshop-worker2 "sudo cat <<EOF >daemon.json
{
        \"experimental\" : true,
        \"log-driver\": \"json-file\",
        \"log-opts\": {
          \"labels\": \"component\"
        },
        \"default-ulimits\":
        {
                \"nproc\": {
                        \"Name\": \"nproc\",
                        \"Hard\": 4096,
                        \"Soft\": 4096
                },
                \"nofile\": {
                        \"Name\": \"nofile\",
                        \"Hard\": 65536,
                        \"Soft\": 65536
                }
        },
        \"metrics-addr\" : \"0.0.0.0:9323\",
        \"insecure-registries\" : [
          \"$(docker-machine ip workshop-manager):5000\"
        ]
}
EOF"

docker-machine ssh workshop-manager sudo mv daemon.json /etc/docker/daemon.json
docker-machine ssh workshop-worker1 sudo mv daemon.json /etc/docker/daemon.json
docker-machine ssh workshop-worker2 sudo mv daemon.json /etc/docker/daemon.json

docker-machine ssh workshop-manager sudo cat /etc/docker/daemon.json
docker-machine ssh workshop-worker1 sudo cat /etc/docker/daemon.json
docker-machine ssh workshop-worker2 sudo cat /etc/docker/daemon.json

docker-machine ssh workshop-manager sudo /etc/init.d/docker restart
docker-machine ssh workshop-worker1 sudo /etc/init.d/docker restart
docker-machine ssh workshop-worker2 sudo /etc/init.d/docker restart

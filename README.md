# flink-workshop (work in progress)

Example of Flink jobs for workshop. It contains code of Flink jobs and scripts for creating Docker containers.

## License

The project is distributed under the terms of BSD 3-Clause License.

    Copyright (c) 2019, Andrea Medeghini
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this
      list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.

    * Neither the name of the project nor the names of its
      contributors may be used to endorse or promote products derived from
      this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
    FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
    SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
    CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
    OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
    OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

## Package Flink jobs

Create JAR file with command:

    mvn clean package

## Create Docker network

    docker network create workshop

## Build Docker images

Create Docker images with command:

    ./setup.sh

## Start containers

Start Docker containers with command:

    ./start.sh

## Start Flink jobs

Start Flink jobs with command:

    ./start_jobs.sh

## Cancel Flink jobs (with savepoint)

Cancel Flink jobs with command:

    ./cancel_jobs.sh

## Stop Flink jobs (when finished)

Stop Flink jobs with command:

    ./stop_jobs.sh

## Stop containers (when finished)

Stop Docker containers with command:

    ./stop.sh

## Monitor Flink jobs

Access Grafana dashboard at http://localhost:3000 (user: admin, password: admin)

Access Prometheus at http://localhost:9090

Access Flink UI at http://localhost:8081

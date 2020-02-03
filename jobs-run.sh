#!/usr/bin/env sh

curl -X POST -H "Expect:" -F "jarfile=@target/com.nextbreakpoint.flinkworkshop-1.1.0.jar" http://localhost:48081/jars/upload

JARID=$(curl -s http://localhost:48081/jars | jq -r '.files[0].id')

curl -X POST -d "{\"entryClass\":\"com.nextbreakpoint.flink.jobs.GenerateJob\",\"parallelism\":\"1\",\"programArgs\":\"--BOOTSTRAP_SERVERS kafka:9092\"}" http://localhost:48081/jars/${JARID}/run
curl -X POST -d "{\"entryClass\":\"com.nextbreakpoint.flink.jobs.AggregateJob\",\"parallelism\":\"2\",\"programArgs\":\"--BOOTSTRAP_SERVERS kafka:9092\"}" http://localhost:48081/jars/${JARID}/run
curl -X POST -d "{\"entryClass\":\"com.nextbreakpoint.flink.jobs.PrintJob\",\"parallelism\":\"1\",\"programArgs\":\"--BOOTSTRAP_SERVERS kafka:9092\"}" http://localhost:48081/jars/${JARID}/run

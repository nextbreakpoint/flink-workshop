#!/usr/bin/env sh

JOBS=$(curl -s http://localhost:8081/jobs | jq -r '.jobs[] | select(.status=="RUNNING") | .id')

for JOB in $JOBS
do
  curl -X POST -d "{\"cancel-job\":\"true\"}" http://localhost:8081/jobs/${JOB}/savepoints
done


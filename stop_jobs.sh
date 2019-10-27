#!/usr/bin/env sh

JOBS=$(curl -s http://localhost:8081/jobs | jq -r '.jobs[] | select(.status!="CANCELED") | .id')

for JOB in $JOBS
do
  curl -X PATCH http://localhost:8081/jobs/$JOB
done

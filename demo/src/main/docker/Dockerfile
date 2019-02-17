ARG flink_version

FROM demo-flink:${flink.version}

USER root

COPY maven/ /maven

COPY cli_entrypoint.sh /cli_entrypoint.sh

USER root

RUN chmod u+x /cli_entrypoint.sh

ENV SERVICE_JAR ${service.jar}

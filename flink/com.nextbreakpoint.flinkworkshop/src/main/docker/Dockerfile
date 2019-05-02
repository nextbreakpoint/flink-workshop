ARG flink_version

FROM workshop-flink:${flink.version}

USER flink

COPY maven/ /maven

COPY cli_entrypoint.sh /cli_entrypoint.sh

USER root

RUN chmod -R u+rx /maven
RUN chmod -R u+rx /cli_entrypoint.sh

RUN chown -R flink:flink /maven
RUN chown -R flink:flink /cli_entrypoint.sh

ENV SERVICE_JAR ${service.jar}

USER flink

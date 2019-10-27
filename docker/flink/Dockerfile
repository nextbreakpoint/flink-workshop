ARG flink_version
ARG scala_version

FROM flink:${flink_version}-scala_${scala_version}

ARG flink_version
ARG scala_version

ENV FLINK_VERSION $flink_version
ENV SCALA_VERSION $scala_version

USER root

RUN apt-get update -y && apt install -y wget

RUN wget --max-redirect=2 -O $FLINK_HOME/lib/flink-metrics-prometheus_${SCALA_VERSION}-${FLINK_VERSION}.jar https://repo1.maven.org/maven2/org/apache/flink/flink-metrics-prometheus_${SCALA_VERSION}/${FLINK_VERSION}/flink-metrics-prometheus_${SCALA_VERSION}-${FLINK_VERSION}.jar

RUN chown -R flink.flink $FLINK_HOME/lib

COPY docker-entrypoint.sh /docker-entrypoint.sh

RUN chmod a+x /docker-entrypoint.sh

COPY flink-conf.yaml /opt/flink/conf/flink-conf.yaml

RUN chmod a+w /opt/flink/conf/flink-conf.yaml


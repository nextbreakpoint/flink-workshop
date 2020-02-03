ARG flink_version

FROM flink:${flink_version}

ARG flink_version

ENV FLINK_VERSION $flink_version

RUN cp $FLINK_HOME/opt/flink-s3-fs-hadoop-${FLINK_VERSION}.jar $FLINK_HOME/lib/flink-s3-fs-hadoop-${FLINK_VERSION}.jar
#RUN cp $FLINK_HOME/opt/flink-s3-fs-presto-${FLINK_VERSION}.jar $FLINK_HOME/lib/flink-s3-fs-presto-${FLINK_VERSION}.jar
RUN cp $FLINK_HOME/opt/flink-metrics-graphite-${FLINK_VERSION}.jar $FLINK_HOME/lib/flink-metrics-graphite-${FLINK_VERSION}.jar
RUN cp $FLINK_HOME/opt/flink-metrics-prometheus-${FLINK_VERSION}.jar $FLINK_HOME/lib/flink-metrics-prometheus-${FLINK_VERSION}.jar

COPY wait-for-it.sh /wait-for-it.sh
COPY docker-entrypoint.sh /docker-entrypoint.sh
COPY flink-conf.yaml /opt/flink/conf/flink-conf.yaml

USER root

RUN chown flink.flink /wait-for-it.sh && chmod u+x /wait-for-it.sh
RUN chown flink.flink /docker-entrypoint.sh && chmod u+x /docker-entrypoint.sh
RUN chown flink.flink /opt/flink/conf/flink-conf.yaml && chmod u+rw /opt/flink/conf/flink-conf.yaml

USER flink

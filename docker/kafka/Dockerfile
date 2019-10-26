ARG cp_kafka_version

FROM confluentinc/cp-kafka:${cp_kafka_version}

#RUN apt update && apt install -y wget

RUN mkdir -p /opt/jmx-exporter && \
    mkdir -p /etc/jmx-exporter && \
    wget -O /opt/jmx-exporter/jmx-exporter.jar https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.12.0/jmx_prometheus_javaagent-0.12.0.jar

COPY custom_entrypoint.sh /custom_entrypoint.sh

COPY wait-for-it.sh /wait-for-it.sh

COPY jmx-exporter.yml /etc/jmx-exporter/kafka.yml

RUN chmod a+x /custom_entrypoint.sh

RUN chmod a+x /wait-for-it.sh

ENTRYPOINT ["/custom_entrypoint.sh"]

CMD ["exec", "/etc/confluent/docker/run"]

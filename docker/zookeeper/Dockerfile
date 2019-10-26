ARG zookeeper_version

FROM zookeeper:${zookeeper_version}

RUN apt update -y && apt-get install -y wget

RUN mkdir -p /opt/jmx-exporter && \
    mkdir -p /etc/jmx-exporter && \
    wget -O /opt/jmx-exporter/jmx-exporter.jar https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.12.0/jmx_prometheus_javaagent-0.12.0.jar

COPY custom_entrypoint.sh /custom_entrypoint.sh

COPY jmx-exporter.yml /etc/jmx-exporter/zookeeper.yml

RUN chmod a+x /custom_entrypoint.sh

ENTRYPOINT ["/custom_entrypoint.sh"]

CMD ["zkServer.sh", "start-foreground"]

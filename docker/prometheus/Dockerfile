FROM prom/prometheus:v2.13.1

ENV ENVIRONMENT=dev

USER root

COPY docker-entrypoint.sh /etc/prometheus/docker-entrypoint.sh
COPY prometheus.yml /etc/prometheus/prometheus.yml

RUN chmod a+x /etc/prometheus/docker-entrypoint.sh

ENTRYPOINT [ "/etc/prometheus/docker-entrypoint.sh" ]

CMD [ "--config.file=/etc/prometheus/prometheus.yml", "--storage.tsdb.path=/prometheus", "--web.console.libraries=/etc/prometheus/console_libraries", "--web.console.templates=/etc/prometheus/consoles", "--storage.tsdb.retention=24h" ]

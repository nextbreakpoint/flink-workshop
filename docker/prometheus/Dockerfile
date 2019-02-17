FROM prom/prometheus:v2.3.2
# https://hub.docker.com/r/prom/prometheus/tags/

ENV WEAVE_TOKEN=none
ENV ENVIRONMENT=dev

USER root

COPY conf /etc/prometheus/
COPY rules /etc/prometheus/

RUN chmod a+x /etc/prometheus/docker-entrypoint.sh

ENTRYPOINT [ "/etc/prometheus/docker-entrypoint.sh" ]
CMD        [ "--config.file=/etc/prometheus/prometheus.yml", \
             "--storage.tsdb.path=/prometheus", \
             "--web.console.libraries=/etc/prometheus/console_libraries", \
             "--web.console.templates=/etc/prometheus/consoles" ]

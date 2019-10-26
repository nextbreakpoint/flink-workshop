FROM prom/node-exporter:v0.18.1

ENV NODE_ID=none

USER root

COPY docker-entrypoint.sh /etc/node-exporter/docker-entrypoint.sh
RUN chmod a+x /etc/node-exporter/docker-entrypoint.sh

ENTRYPOINT [ "/etc/node-exporter/docker-entrypoint.sh" ]
CMD [ "/bin/node_exporter" ]

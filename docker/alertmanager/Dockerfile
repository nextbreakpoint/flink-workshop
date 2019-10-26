FROM prom/alertmanager:v0.19.0

COPY alertmanager.yml /etc/alertmanager/alertmanager.yml
COPY docker-entrypoint.sh /etc/alertmanager/docker-entrypoint.sh

USER root

RUN chmod a+x /etc/alertmanager/docker-entrypoint.sh

ENTRYPOINT [ "/etc/alertmanager/docker-entrypoint.sh" ]
CMD [ "--config.file=/etc/alertmanager/alertmanager.yml", "--storage.path=/alertmanager" ]

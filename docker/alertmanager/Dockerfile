FROM prom/alertmanager:v0.15.2

COPY conf /etc/alertmanager/
RUN chmod a+x /etc/alertmanager/docker-entrypoint.sh

ENTRYPOINT  [ "/etc/alertmanager/docker-entrypoint.sh" ]
CMD        [ "--config.file=/etc/alertmanager/alertmanager.yml", \
             "--storage.path=/alertmanager" ]

#!/bin/sh

cd /

mkdir -p /secrets

if [ -z "$KEYSTORE_LOCATION" ]; then
  if [ -n "$KEYSTORE_CONTENT" ]; then
    echo $KEYSTORE_CONTENT | base64 -d > /secrets/keystore.jks
    export KEYSTORE_LOCATION=/secrets/keystore.jks
  fi
fi

if [ -z "$TRUSTSTORE_LOCATION" ]; then
  if [ -n "$TRUSTSTORE_CONTENT" ]; then
    echo $TRUSTSTORE_CONTENT | base64 -d > /secrets/truststore.jks
    export TRUSTSTORE_LOCATION=/secrets/truststore.jks
  fi
fi

export KAFKA_OPTS="-javaagent:/opt/jmx-exporter/jmx-exporter.jar=7070:/etc/jmx-exporter/kafka.yml"

if [ -z "$JAAS_CONFIG_LOCATION" ]; then
  if [ -n "$JAAS_CONFIG_CONTENT" ]; then
    echo $JAAS_CONFIG_CONTENT | base64 -d > /secrets/kafka_jaas.conf
    export KAFKA_OPTS=$KAFKA_OPTS" -Djava.security.auth.login.config=/secrets/kafka_jaas.conf"
  fi
fi

if [ -n "$KEYSTORE_LOCATION" ]; then
  if [ -n "$KEYSTORE_PASSWORD" ]; then
    if [ -n "$TRUSTSTORE_LOCATION" ]; then
      if [ -n "$TRUSTSTORE_PASSWORD" ]; then
cat <<EOF > /secrets/client-ssl.properties
security.protocol=SSL
ssl.truststore.location=${TRUSTSTORE_LOCATION}
ssl.truststore.password=${TRUSTSTORE_PASSWORD}
ssl.keystore.location=${KEYSTORE_LOCATION}
ssl.keystore.password=${KEYSTORE_PASSWORD}
EOF
      fi
    fi
  fi
fi

exec "$@"

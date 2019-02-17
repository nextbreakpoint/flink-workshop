FROM java:openjdk-8-jre

ARG scala_version
ARG kafka_version

ENV SCALA_VERSION $scala_version
ENV KAFKA_VERSION $kafka_version
ENV KAFKA_HOME /opt/kafka_${SCALA_VERSION}-${KAFKA_VERSION}

RUN apt-get update && \
    apt-get install -y wget dnsutils && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get clean

RUN wget -q http://mirrors.ukfast.co.uk/sites/ftp.apache.org/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz -O kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz && \
    wget -q http://www-eu.apache.org/dist/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz.asc -O kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz.asc && \
    wget -q http://www-eu.apache.org/dist/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz.sha1 -O kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz.sha1 && \
    gpg --print-md SHA1 kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz > kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz.sha1.computed && \
    diff kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz.sha1.computed kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz.sha1

RUN wget -q http://www-eu.apache.org/dist/kafka/KEYS -O KEYS && \
    wget -q https://home.apache.org/keys/group/kafka.asc -O kafka.asc && \
    gpg --import KEYS && \
    gpg --import kafka.asc && \
    gpg --fingerprint B9F77D0E | grep "AFAC DB09 E6F0 FF28 C93D  64BC BEED 4F6C B9F7 7D0E" && \
    gpg --verify kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz.asc kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz && \
    tar xfz kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz -C /opt && \
    rm kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz

RUN curl -LO "https://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip" -H 'Cookie: oraclelicense=accept-securebackup-cookie' && \
    unzip jce_policy-8.zip && \
    rm jce_policy-8.zip && \
    cp -v UnlimitedJCEPolicyJDK8/*.jar /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security

RUN mkdir -p /opt/jmx-exporter && \
    mkdir -p /etc/jmx-exporter && \
    wget -O /opt/jmx-exporter/jmx-exporter.jar https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.3.1/jmx_prometheus_javaagent-0.3.1.jar

COPY start-kafka.sh /usr/bin/start-kafka.sh

COPY custom_entrypoint.sh /custom_entrypoint.sh

COPY jmx-exporter.yml /etc/jmx-exporter/kafka.yml

# This script is required to coordinate containers and force an execution order
COPY wait-for-it.sh /wait-for-it.sh

RUN chmod a+x /custom_entrypoint.sh

RUN chmod a+x /usr/bin/start-kafka.sh

RUN chmod a+rx /wait-for-it.sh

EXPOSE 9092

ENTRYPOINT ["/custom_entrypoint.sh"]

CMD ["/usr/bin/start-kafka.sh"]

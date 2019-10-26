ARG flink_version
ARG scala_version

FROM flink:${flink_version}-scala_${scala_version}

ARG flink_version
ARG scala_version

ENV FLINK_VERSION $flink_version
ENV SCALA_VERSION $scala_version

USER root

RUN apt update -y && apt-get install -y wget

USER flink

RUN wget --max-redirect=2 -O $FLINK_HOME/lib/flink-metrics-graphite-$FLINK_VERSION.jar https://repo1.maven.org/maven2/org/apache/flink/flink-metrics-graphite/$FLINK_VERSION/flink-metrics-graphite-$FLINK_VERSION.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/flink-metrics-prometheus_${SCALA_VERSION}-${FLINK_VERSION}.jar https://repo1.maven.org/maven2/org/apache/flink/flink-metrics-prometheus_${SCALA_VERSION}/${FLINK_VERSION}/flink-metrics-prometheus_${SCALA_VERSION}-${FLINK_VERSION}.jar

RUN wget --max-redirect=2 -O $FLINK_HOME/lib/metrics-core-4.1.1.jar https://repo1.maven.org/maven2/io/dropwizard/metrics/metrics-core/4.1.1/metrics-core-4.1.1.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/kafka-clients-2.3.0.jar https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/2.3.0/kafka-clients-2.3.0.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/jackson-annotations-2.10.0.jar https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.10.0/jackson-annotations-2.10.0.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/jackson-databind-2.10.0.jar https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.10.0/jackson-databind-2.10.0.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/jackson-core-2.10.0.jar https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.10.0/jackson-core-2.10.0.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/jackson-dataformat-cbor-2.10.0.jar https://repo1.maven.org/maven2/com/fasterxml/jackson/dataformat/jackson-dataformat-cbor/2.10.0/jackson-dataformat-cbor-2.10.0.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/jackson-datatype-joda-2.10.0.jar https://repo1.maven.org/maven2/com/fasterxml/jackson/datatype/jackson-datatype-joda/2.10.0/jackson-datatype-joda-2.10.0.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/joda-time-2.10.4.jar https://repo1.maven.org/maven2/joda-time/joda-time/2.10.4/joda-time-2.10.4.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/guava-28.1-jre.jar https://repo1.maven.org/maven2/com/google/guava/guava/28.1-jre/guava-28.1-jre.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/hadoop-aws-2.8.5.jar https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/2.8.5/hadoop-aws-2.8.5.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/hadoop-auth-2.8.5.jar https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-auth/2.8.5/hadoop-auth-2.8.5.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/hadoop-hdfs-2.8.5.jar https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-hdfs/2.8.5/hadoop-hdfs-2.8.5.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/hadoop-common-2.8.5.jar https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-common/2.8.5/hadoop-common-2.8.5.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/hadoop-annotations-2.8.5.jar https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-annotations/2.8.5/hadoop-annotations-2.8.5.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/aws-java-sdk-core-1.11.658.jar https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-core/1.11.658/aws-java-sdk-core-1.11.658.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/aws-java-sdk-kms-1.11.658.jar https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-kms/1.11.658/aws-java-sdk-kms-1.11.658.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/aws-java-sdk-s3-1.11.658.jar https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-s3/1.11.658/aws-java-sdk-s3-1.11.658.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/httpcore-4.4.12.jar https://repo1.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.12/httpcore-4.4.12.jar
RUN wget --max-redirect=2 -O $FLINK_HOME/lib/httpclient-4.5.10.jar https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.10/httpclient-4.5.10.jar

RUN chown -R flink.flink $FLINK_HOME/lib

COPY custom_entrypoint.sh /custom_entrypoint.sh
COPY core-site.xml /hadoop/etc/core-site.xml
COPY wait-for-it.sh /wait-for-it.sh

USER root

RUN chmod a+rx /custom_entrypoint.sh
RUN chmod a+rx /wait-for-it.sh

USER flink

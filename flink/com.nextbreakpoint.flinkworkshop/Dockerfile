ARG flink_version
ARG scala_version

FROM maven:3.6.2-jdk-8 AS build
ADD . /src
WORKDIR /src
COPY toolchains.xml /src/toolchains.xml
RUN mvn -t toolchains.xml -B clean package

FROM flink:${flink_version}-scala_${scala_version}
COPY --from=build /src/target/com.nextbreakpoint.flinkworkshop-*.jar /flinkworkshop.jar
WORKDIR /
USER root
COPY cli_entrypoint.sh /cli_entrypoint.sh
RUN chmod -R u+rx /flinkworkshop.jar
RUN chmod -R u+rx /cli_entrypoint.sh
RUN chown -R flink:flink /flinkworkshop.jar
RUN chown -R flink:flink /cli_entrypoint.sh
ENV SERVICE_JAR flinkworkshop.jar
USER flink

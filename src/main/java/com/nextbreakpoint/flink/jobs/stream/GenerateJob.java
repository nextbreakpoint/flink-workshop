package com.nextbreakpoint.flink.jobs.stream;

import com.nextbreakpoint.flink.common.Environment;
import com.nextbreakpoint.flink.common.Kafka;
import com.nextbreakpoint.flink.common.Sinks;
import com.nextbreakpoint.flink.sensor.RandomSensorDataSource;
import com.nextbreakpoint.flink.sensor.SensorEvent;
import com.nextbreakpoint.flink.sensor.SensorEventSerializationSchema;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.Properties;

import static com.nextbreakpoint.flink.common.Arguments.*;
import static com.nextbreakpoint.flink.common.Parameters.getOptionalParam;

public class GenerateJob {
    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final boolean localEnvironment = Boolean.parseBoolean(getOptionalParam(parameters, LOCAL_ENVIRONMENT, "false"));
        final boolean enabledWebui = Boolean.parseBoolean(getOptionalParam(parameters, ENABLE_WEBUI, "false"));
        final int restPort = Integer.parseInt(getOptionalParam(parameters, REST_PORT, "8081"));
        final int parallelism = Integer.parseInt(getOptionalParam(parameters, PARALLELISM, "0"));
        final boolean consoleOutput = Boolean.parseBoolean(getOptionalParam(parameters, CONSOLE_OUTPUT, "false"));
        final String bootstrapServers = getOptionalParam(parameters, BOOTSTRAP_SERVERS, "localhost:9093");
        final String outputTopicName = getOptionalParam(parameters, OUTPUT_TOPIC_NAME, "workshop-sensor-events");
        final String producerClientId = getOptionalParam(parameters, PRODUCER_CLIENT_ID, "workshop-generate-job");

        final StreamExecutionEnvironment environment = Environment.getStreamExecutionEnvironment(localEnvironment, enabledWebui, restPort);

        environment.setParallelism(parallelism > 0 ? parallelism : environment.getParallelism());

        environment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        environment.setRestartStrategy(RestartStrategies.noRestart());

        environment.getConfig().setAutoWatermarkInterval(100);

        final Properties producerProperties = Kafka.defaultProducerProperties(bootstrapServers, producerClientId);

        final SinkFunction<SensorEvent> sink = Sinks.createKafkaSink(outputTopicName, new SensorEventSerializationSchema(outputTopicName), producerProperties);

        final DataStream<SensorEvent> pipeline = environment.addSource(new RandomSensorDataSource())
                .uid("fake-source")
                .name("source");

        pipeline.addSink(sink)
                .uid("kafka-sink")
                .name("kafka");

        if (consoleOutput) {
            pipeline.addSink(new PrintSinkFunction<>("event", false))
                    .uid("console-sink")
                    .name("console");
        }

        environment.execute("workshop-generate-job");
    }
}

package com.nextbreakpoint.flink.jobs.stream;

import com.nextbreakpoint.flink.common.Environment;
import com.nextbreakpoint.flink.common.Kafka;
import com.nextbreakpoint.flink.common.Sinks;
import com.nextbreakpoint.flink.common.Sources;
import com.nextbreakpoint.flink.sensor.SensorEvent;
import com.nextbreakpoint.flink.sensor.SensorEventDeserializationSchema;
import com.nextbreakpoint.flink.sensor.SensorEventTimestampExtractor;
import com.nextbreakpoint.flink.sensor.SensorEventToStringFunctiom;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;

import java.util.Properties;

import static com.nextbreakpoint.flink.common.Arguments.*;
import static com.nextbreakpoint.flink.common.Parameters.getOptionalParam;

public class ArchiveJob {
    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final boolean localEnvironment = Boolean.parseBoolean(getOptionalParam(parameters, LOCAL_ENVIRONMENT, "false"));
        final boolean enabledWebui = Boolean.parseBoolean(getOptionalParam(parameters, ENABLE_WEBUI, "false"));
        final int restPort = Integer.parseInt(getOptionalParam(parameters, REST_PORT, "8081"));
        final int parallelism = Integer.parseInt(getOptionalParam(parameters, PARALLELISM, "0"));
        final String bootstrapServers = getOptionalParam(parameters, BOOTSTRAP_SERVERS, "localhost:9093");
        final String sourceTopicName = getOptionalParam(parameters, SOURCE_TOPIC_NAME, "sensor-events");
        final String outputBucketName = getOptionalParam(parameters, OUTPUT_BUCKET_NAME, "file:///tmp/flink/sensor-events/source");
        final String consumerGroupName = getOptionalParam(parameters, CONSUMER_GROUP_NAME, "sensor-event-archive-job");
        final String autoOffsetReset = getOptionalParam(parameters, AUTO_OFFSET_RESET, "earliest");
        final boolean consoleOutput = Boolean.parseBoolean(getOptionalParam(parameters, CONSOLE_OUTPUT, "false"));

        final StreamExecutionEnvironment environment = Environment.getStreamExecutionEnvironment(localEnvironment, enabledWebui, restPort);

        environment.setParallelism(parallelism > 0 ? parallelism : environment.getParallelism());

        environment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        environment.setRestartStrategy(RestartStrategies.noRestart());

        Environment.configureCheckpointing(environment, 60000, 20000);

        final KafkaDeserializationSchema<SensorEvent> deserializationSchema = new SensorEventDeserializationSchema();

        final Properties consumerProperties = Kafka.defaultConsumerProperties(bootstrapServers, consumerGroupName, autoOffsetReset);

        final SourceFunction<SensorEvent> kafkaSource = Sources.createKafkaSource(sourceTopicName, deserializationSchema, consumerProperties);

        final DataStream<String> pipeline = environment.addSource(kafkaSource)
                .assignTimestampsAndWatermarks(new SensorEventTimestampExtractor(Time.milliseconds(500)))
                .uid("kafka-source")
                .name("kafka")
                .map(new SensorEventToStringFunctiom())
                .uid("csv")
                .name("csv");

        pipeline.addSink(Sinks.createTextFileSink(new Path(outputBucketName), 60000));

        if (consoleOutput) {
            pipeline.addSink(new PrintSinkFunction<>("event", false))
                    .uid("console-sink")
                    .name("console");
        }

        environment.execute("sensor-event-archive-job");
    }
}

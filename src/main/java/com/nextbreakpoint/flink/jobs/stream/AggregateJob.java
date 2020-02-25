package com.nextbreakpoint.flink.jobs.stream;

import com.nextbreakpoint.flink.common.Environment;
import com.nextbreakpoint.flink.common.Kafka;
import com.nextbreakpoint.flink.common.Sinks;
import com.nextbreakpoint.flink.common.Sources;
import com.nextbreakpoint.flink.sensor.*;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;

import java.util.Properties;

import static com.nextbreakpoint.flink.common.Arguments.*;
import static com.nextbreakpoint.flink.common.Parameters.getOptionalParam;

public class AggregateJob {
    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final boolean localEnvironment = Boolean.parseBoolean(getOptionalParam(parameters, LOCAL_ENVIRONMENT, "false"));
        final boolean enabledWebui = Boolean.parseBoolean(getOptionalParam(parameters, ENABLE_WEBUI, "false"));
        final int restPort = Integer.parseInt(getOptionalParam(parameters, REST_PORT, "8081"));
        final int parallelism = Integer.parseInt(getOptionalParam(parameters, PARALLELISM, "0"));
        final boolean consoleOutput = Boolean.parseBoolean(getOptionalParam(parameters, CONSOLE_OUTPUT, "false"));
        final String bootstrapServers = getOptionalParam(parameters, BOOTSTRAP_SERVERS, "localhost:9093");
        final String sourceTopicName = getOptionalParam(parameters, SOURCE_TOPIC_NAME, "sensor-events");
        final String outputTopicName = getOptionalParam(parameters, OUTPUT_TOPIC_NAME, "max-temperature-events");
        final String consumerGroupName = getOptionalParam(parameters, CONSUMER_GROUP_NAME, "sensor-event-aggregate-job");
        final String producerClientId = getOptionalParam(parameters, PRODUCER_CLIENT_ID, "sensor-event-aggregate-job");
        final String autoOffsetReset = getOptionalParam(parameters, AUTO_OFFSET_RESET, "earliest");

        final StreamExecutionEnvironment environment = Environment.getStreamExecutionEnvironment(localEnvironment, enabledWebui, restPort);

        environment.setParallelism(parallelism > 0 ? parallelism : environment.getParallelism());

        environment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        environment.setRestartStrategy(RestartStrategies.noRestart());

        environment.getConfig().setAutoWatermarkInterval(100);

        environment.enableCheckpointing(60000);

        final Properties consumerProperties = Kafka.defaultConsumerProperties(bootstrapServers, consumerGroupName, autoOffsetReset);

        final Properties producerProperties = Kafka.defaultProducerProperties(bootstrapServers, producerClientId);

        final KafkaDeserializationSchema<SensorEvent> deserializationSchema = new SensorEventDeserializationSchema();

        final SourceFunction<SensorEvent> kafkaSource = Sources.createKafkaSource(sourceTopicName, deserializationSchema, consumerProperties);

        final KafkaSerializationSchema<MaxTemperatureEvent> serializationSchema = new MaxTemperatureEventSerializationSchema(outputTopicName);

        final SinkFunction<MaxTemperatureEvent> kafkaSink = Sinks.createKafkaSink(outputTopicName, serializationSchema, producerProperties);

        final DataStream<MaxTemperatureEvent> pipeline = environment.addSource(kafkaSource)
                .assignTimestampsAndWatermarks(new SensorEventTimestampExtractor(Time.milliseconds(500)))
                .uid("kafka-source")
                .name("source")
                .keyBy(new SensorEventKeySelector())
                .window(SlidingEventTimeWindows.of(Time.seconds(300), Time.seconds(10)))
                .aggregate(new MaxTemperatureAggregateFunction(), new MaxTemperatureWindowFunction())
                .uid("max-temperature-window")
                .name("max-temperature");

        pipeline.addSink(kafkaSink)
                .uid("kafka-sink")
                .name("kafka");

        if (consoleOutput) {
            pipeline.addSink(new PrintSinkFunction<>("event", false))
                    .uid("console-sink")
                    .name("console");
        }

        environment.execute("sensor-event-aggregate-job");
    }
}

package com.nextbreakpoint.flink.jobs;

import com.nextbreakpoint.flink.common.Environment;
import com.nextbreakpoint.flink.common.Parameters;
import com.nextbreakpoint.flink.sensor.*;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.Properties;

import static com.nextbreakpoint.flink.common.Arguments.*;

public class AggregateJob {

    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final String developMode = Parameters.getOptionalParam(parameters, DEVELOP_MODE, "disabled");
        final int parallelism = Integer.parseInt(Parameters.getOptionalParam(parameters, PARALLELISM, "0"));
        final String bootstrapServers = Parameters.getOptionalParam(parameters, BOOTSTRAP_SERVERS, "localhost:9093");
        final String sourceTopicName = Parameters.getOptionalParam(parameters, SOURCE_TOPIC_NAME, "test-input");
        final String outputTopicName = Parameters.getOptionalParam(parameters, OUTPUT_TOPIC_NAME, "test-output");
        final String consumerGroupName = Parameters.getOptionalParam(parameters, CONSUMER_GROUP_NAME, "aggregate-job");
        final String producerClientId = Parameters.getOptionalParam(parameters, PRODUCER_CLIENT_ID, "aggregate-job");
        final String autoOffsetReset = Parameters.getOptionalParam(parameters, AUTO_OFFSET_RESET, "earliest");

        final StreamExecutionEnvironment env = Environment.getExecutionEnvironment(developMode);

        env.setParallelism(parallelism > 0 ? parallelism : env.getParallelism());

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        env.setRestartStrategy(RestartStrategies.noRestart());

        env.getConfig().setAutoWatermarkInterval(100);

        final Properties consumerProperties = Environment.defaultKafkaConsumerProperties(bootstrapServers, consumerGroupName, autoOffsetReset);

        final Properties producerProperties = Environment.defaultKafkaProducerProperties(bootstrapServers, producerClientId);

        final SourceFunction<SensorData> source = Environment.createKafkaSource(sourceTopicName, new SensorDeserializationSchema(), consumerProperties);

        final SinkFunction<SensorData> sink = Environment.createKafkaSink(outputTopicName, new SensorSerializationSchema(outputTopicName), producerProperties);

        env.addSource(source)
                .assignTimestampsAndWatermarks(new SensorTimestampExtractor(Time.milliseconds(500)))
                .uid("source")
                .name("Source of data from sensors")
                .keyBy(SensorData::getId)
                .window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(1)))
                .aggregate(new SensorAggregateFunction(), new SensorRichWindowFunction())
                .uid("window")
                .name("Find max value in window")
                .addSink(sink)
                .uid("publish")
                .name("Publish data from sensors");

        env.execute("Aggregate data from sensors");
    }
}

package com.nextbreakpoint.flink.jobs;

import com.nextbreakpoint.flink.common.Environment;
import com.nextbreakpoint.flink.common.Parameters;
import com.nextbreakpoint.flink.sensor.SensorData;
import com.nextbreakpoint.flink.sensor.SensorDeserializationSchema;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Properties;

import static com.nextbreakpoint.flink.common.Arguments.*;

public class PrintJob {

    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final String developMode = Parameters.getOptionalParam(parameters, DEVELOP_MODE, "disabled");
        final int parallelism = Integer.parseInt(Parameters.getOptionalParam(parameters, PARALLELISM, "0"));
        final String bootstrapServers = Parameters.getOptionalParam(parameters, BOOTSTRAP_SERVERS, "localhost:9093");
        final String sourceTopicName = Parameters.getOptionalParam(parameters, SOURCE_TOPIC_NAME, "test-output");
        final String consumerGroupName = Parameters.getOptionalParam(parameters, CONSUMER_GROUP_NAME, "print-job");
        final String autoOffsetReset = Parameters.getOptionalParam(parameters, AUTO_OFFSET_RESET, "latest");

        final StreamExecutionEnvironment env = Environment.getExecutionEnvironment(developMode);

        env.setParallelism(parallelism > 0 ? parallelism : env.getParallelism());

        env.setRestartStrategy(RestartStrategies.noRestart());

        final Properties properties = Environment.defaultKafkaConsumerProperties(bootstrapServers, consumerGroupName, autoOffsetReset);

        final SourceFunction<SensorData> source = Environment.createKafkaSource(sourceTopicName, new SensorDeserializationSchema(), properties);

        env.addSource(source)
                .uid("source")
                .name("Source of data from sensors")
                .addSink(new PrintSinkFunction<>("sensor", false))
                .uid("print")
                .name("Print data from sensors");

        env.execute("Print data from sensors");
    }
}

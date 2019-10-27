package com.nextbreakpoint.flink.jobs;

import com.nextbreakpoint.flink.common.Environment;
import com.nextbreakpoint.flink.common.Parameters;
import com.nextbreakpoint.flink.sensor.RandomSensorDataSource;
import com.nextbreakpoint.flink.sensor.SensorData;
import com.nextbreakpoint.flink.sensor.SensorSerializationSchema;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.Properties;

import static com.nextbreakpoint.flink.common.Arguments.*;

public class GenerateJob {

    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final String developMode = Parameters.getOptionalParam(parameters, DEVELOP_MODE, "disabled");
        final int parallelism = Integer.parseInt(Parameters.getOptionalParam(parameters, PARALLELISM, "0"));
        final String bootstrapServers = Parameters.getOptionalParam(parameters, BOOTSTRAP_SERVERS, "localhost:9093");
        final String outputTopicName = Parameters.getOptionalParam(parameters, OUTPUT_TOPIC_NAME, "test-input");
        final String producerClientId = Parameters.getOptionalParam(parameters, PRODUCER_CLIENT_ID, "generate-job-client");

        final StreamExecutionEnvironment env = Environment.getExecutionEnvironment(developMode);

        env.setParallelism(parallelism > 0 ? parallelism : env.getParallelism());

        env.setRestartStrategy(RestartStrategies.noRestart());

        final Properties properties = Environment.defaultKafkaProducerProperties(bootstrapServers, producerClientId);

        final SinkFunction<SensorData> sink = Environment.createKafkaSink(outputTopicName, new SensorSerializationSchema(outputTopicName), properties);

        env.addSource(new RandomSensorDataSource())
                .uid("source")
                .name("Source of data from sensors")
                .addSink(sink)
                .uid("publish")
                .name("Publish data from sensors");

        env.execute("Generate data from sensors");
    }
}

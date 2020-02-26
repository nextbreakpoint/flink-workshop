package com.nextbreakpoint.flink.jobs.stream;

import com.nextbreakpoint.flink.common.Environment;
import com.nextbreakpoint.flink.sensor.RandomSensorDataSource;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;

import static com.nextbreakpoint.flink.common.Arguments.*;
import static com.nextbreakpoint.flink.common.Parameters.getOptionalParam;

public class TestJob {

    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final boolean localEnvironment = Boolean.parseBoolean(getOptionalParam(parameters, LOCAL_ENVIRONMENT, "false"));
        final boolean enabledWebui = Boolean.parseBoolean(getOptionalParam(parameters, ENABLE_WEBUI, "false"));
        final int restPort = Integer.parseInt(getOptionalParam(parameters, REST_PORT, "8081"));
        final int parallelism = Integer.parseInt(getOptionalParam(parameters, PARALLELISM, "0"));

        final StreamExecutionEnvironment environment = Environment.getStreamExecutionEnvironment(localEnvironment, enabledWebui, restPort);

        environment.setParallelism(parallelism > 0 ? parallelism : environment.getParallelism());

        environment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        environment.setRestartStrategy(RestartStrategies.noRestart());

        environment.getConfig().setAutoWatermarkInterval(100);

        environment.enableCheckpointing(60000);

        environment.addSource(new RandomSensorDataSource())
                .uid("fake-source")
                .name("source")
                .addSink(new PrintSinkFunction<>("event", false))
                .uid("console-sink")
                .name("console");

        environment.execute("workshop-test-job");
    }
}

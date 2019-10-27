package com.nextbreakpoint.flink.jobs;

import com.nextbreakpoint.flink.common.Environment;
import com.nextbreakpoint.flink.common.Parameters;
import com.nextbreakpoint.flink.sensor.RandomSensorDataSource;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;

import static com.nextbreakpoint.flink.common.Arguments.DEVELOP_MODE;
import static com.nextbreakpoint.flink.common.Arguments.PARALLELISM;

public class TestJob {

    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final String developMode = Parameters.getOptionalParam(parameters, DEVELOP_MODE, "disabled");
        final int parallelism = Integer.parseInt(Parameters.getOptionalParam(parameters, PARALLELISM, "0"));

        final StreamExecutionEnvironment env = Environment.getExecutionEnvironment(developMode);

        env.setParallelism(parallelism > 0 ? parallelism : env.getParallelism());

        env.setRestartStrategy(RestartStrategies.noRestart());

        env.addSource(new RandomSensorDataSource())
                .uid("source")
                .name("Source of data from sensors")
                .addSink(new PrintSinkFunction<>("sensor", false))
                .uid("print")
                .name("Print data from sensors");

        env.execute("Test data from sensors");
    }
}

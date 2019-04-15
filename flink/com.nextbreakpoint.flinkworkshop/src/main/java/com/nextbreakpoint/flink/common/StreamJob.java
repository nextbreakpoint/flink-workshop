package com.nextbreakpoint.flink.common;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StreamJob {
    private static final Logger log = LoggerFactory.getLogger(StreamJob.class);

    private static final int RESTART_ATTEMPTS = 60;
    private static final int DELAY_BETWEEN_ATTEMPTS = 10_000;

    private final StreamExecutionEnvironment env;

    private final String basePath;

    public StreamJob(StreamExecutionEnvironment env, String basePath) {
        this.env = env;
        this.basePath = basePath;

        log.info("Configure restart strategy");
        this.env.setRestartStrategy(RestartStrategies.fixedDelayRestart(RESTART_ATTEMPTS, DELAY_BETWEEN_ATTEMPTS));

        log.info("Enable event-time");
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    }

    public StreamJob enableCheckpointing(int checkpointInterval) {
        if (basePath == null || basePath.trim().isEmpty()) {
            throw new IllegalArgumentException("basePath cannot be null or empty when using checkpointing with FsStateBackend");
        }

        final CheckpointConfig config = env.getCheckpointConfig();
        config.setMinPauseBetweenCheckpoints(60000);
        config.setCheckpointInterval(checkpointInterval);
        config.setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);

        log.info("Enable FS state backend");
        final boolean asyncSnapshots = false;
        env.setStateBackend((StateBackend)new FsStateBackend(basePath + "/state", asyncSnapshots));

        return this;
    }

    public StreamJob initMemoryStateBackend() {
        log.info("Enable memory backend");
        final boolean asyncSnapshots = false;
        env.setStateBackend((StateBackend)new MemoryStateBackend(asyncSnapshots));

        return this;
    }

    public StreamJob disableRestart() {
        log.info("Disable restart strategy");
        env.setRestartStrategy(RestartStrategies.noRestart());

        return this;
    }

    public abstract StreamJob transform();

    public void run() throws Exception {
        env.execute();
    }

    protected static String getNonNullableParam(ParameterTool parameters, String paramName) {
        String param = parameters.get(paramName, System.getProperty(paramName, System.getenv(paramName)));
        if (param == null || param.trim().isEmpty()) {
            throw new RuntimeException(paramName + " parameter can not be empty");
        } else {
            return param;
        }
    }

    protected static String getNullableParam(ParameterTool parameters, String paramName) {
        final String s = parameters.get(paramName, System.getProperty(paramName, System.getenv(paramName)));
        if(s == null) {
            log.info("Parameter '{}' is null (not set), using default or whatever...", paramName);
        }
        return s;
    }

    protected static String getNullableParam(ParameterTool parameters, String paramName, String defaultValue) {
        final String s = parameters.get(paramName, System.getProperty(paramName, System.getenv(paramName)));
        if(s == null) {
            log.info("Parameter '{}' is null (not set), using default '{}'", paramName, defaultValue);
        }
        return s != null ? s : defaultValue;
    }
}

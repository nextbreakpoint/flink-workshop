package com.nextbreakpoint.flink.common;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Environment {
    private Environment() {}

    public static StreamExecutionEnvironment getStreamExecutionEnvironment(boolean localEnvironment, boolean webui, int port) {
        if (localEnvironment) {
            if (webui) {
                Configuration configuration = new Configuration();
                configuration.setString("rest.port", String.valueOf(port));
                return StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
            } else {
                return StreamExecutionEnvironment.createLocalEnvironment();
            }
        } else {
            return StreamExecutionEnvironment.getExecutionEnvironment();
        }
    }

    public static ExecutionEnvironment getExecutionEnvironment(boolean localEnvironment, boolean webui, int port) {
        if (localEnvironment) {
            if (webui) {
                Configuration configuration = new Configuration();
                configuration.setString("rest.port", String.valueOf(port));
                return ExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
            } else {
                return ExecutionEnvironment.createLocalEnvironment();
            }
        } else {
            return ExecutionEnvironment.getExecutionEnvironment();
        }
    }

    public static void configureFileStateBackend(StreamExecutionEnvironment env, String basePath) {
        env.setStateBackend((StateBackend) new FsStateBackend(basePath, true));
    }

    public static void configureInMemoryBackend(StreamExecutionEnvironment env) {
        env.setStateBackend((StateBackend) new MemoryStateBackend(true));
    }

    public static void configureRestartStrategy(StreamExecutionEnvironment env) {
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(5, 60000));
    }

    public static void configureRestartStrategy(ExecutionEnvironment env) {
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(5, 60000));
    }

    public static void configureCheckpointing(StreamExecutionEnvironment env, int checkpointInterval, int pauseBetweenCheckpoints) {
        final CheckpointConfig config = env.getCheckpointConfig();
        config.setMinPauseBetweenCheckpoints(pauseBetweenCheckpoints);
        config.setCheckpointInterval(checkpointInterval);
        config.setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
    }
}

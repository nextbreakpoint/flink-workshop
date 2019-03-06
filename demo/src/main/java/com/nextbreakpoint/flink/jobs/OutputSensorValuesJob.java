package com.nextbreakpoint.flink.jobs;

import com.nextbreakpoint.flink.sensor.SensorMessage;
import com.nextbreakpoint.flink.sensor.SensorMessageDeserializationSchema;
import com.nextbreakpoint.flink.common.StreamJob;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import static com.nextbreakpoint.flink.common.Constants.BUCKET_BASE_PATH;
import static com.nextbreakpoint.flink.common.Constants.CONSUMER_GROUP_NAME;
import static com.nextbreakpoint.flink.common.Constants.JOB_PARALLELISM;
import static com.nextbreakpoint.flink.common.Constants.SOURCE_TOPIC_NAME;
import static com.nextbreakpoint.flink.common.FlinkUtil.createKafkaSource;
import static java.lang.Integer.valueOf;

public class OutputSensorValuesJob extends StreamJob {
    private final DataStream<SensorMessage> source;
    private final SinkFunction<SensorMessage> sink;

    public OutputSensorValuesJob(
            StreamExecutionEnvironment env,
            String basePath,
            DataStream<SensorMessage> source,
            SinkFunction<SensorMessage> sink) {
        super(env, basePath);
        this.source = source;
        this.sink = sink;
    }

    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final String bucketBasePath = getNonNullableParam(parameters, BUCKET_BASE_PATH);

        final String sourceTopicName = getNonNullableParam(parameters, SOURCE_TOPIC_NAME);

        final String consumerGroupName = getNonNullableParam(parameters, CONSUMER_GROUP_NAME);

        final int parallelism = Integer.valueOf(getNullableParam(parameters, JOB_PARALLELISM, "1"));

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(valueOf(parallelism));

        final DataStreamSource<SensorMessage> source = env.addSource(createKafkaSource(parameters, sourceTopicName, new SensorMessageDeserializationSchema(), consumerGroupName, "earliest"));

        final OutputSensorValuesJob job = new OutputSensorValuesJob(env, bucketBasePath, source, new PrintSinkFunction<>());

        job.enableCheckpointing(600000);

        job.disableRestart();

        job.transform().run();
    }

    @Override
    public StreamJob transform() {
        source.addSink(sink);

        return this;
    }
}
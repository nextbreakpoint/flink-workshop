package com.nextbreakpoint.flink.jobs;

import com.nextbreakpoint.flink.sensor.SensorMessage;
import com.nextbreakpoint.flink.sensor.SensorMessageSerializationSchema;
import com.nextbreakpoint.flink.common.FlinkUtil;
import com.nextbreakpoint.flink.common.StreamJob;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.nextbreakpoint.flink.common.Constants.BUCKET_BASE_PATH;
import static com.nextbreakpoint.flink.common.Constants.JOB_PARALLELISM;
import static com.nextbreakpoint.flink.common.Constants.TARGET_TOPIC_NAME;
import static java.lang.Integer.valueOf;

public class GenerateSensorValuesJob extends StreamJob {
    private final DataStream<SensorMessage> source;
    private final SinkFunction<SensorMessage> sink;

    public GenerateSensorValuesJob(
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

        final String targetTopicName = getNonNullableParam(parameters, TARGET_TOPIC_NAME);

        final int parallelism = Integer.valueOf(getNullableParam(parameters, JOB_PARALLELISM, "1"));

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(valueOf(parallelism));

        final SinkFunction<SensorMessage> sink = FlinkUtil.createKafkaSink(parameters, targetTopicName, new SensorMessageSerializationSchema(targetTopicName));

        final DataStreamSource<SensorMessage> source = env.addSource(new RandomSensorValuesSource());

        final GenerateSensorValuesJob job = new GenerateSensorValuesJob(env, bucketBasePath, source, sink);

        job.enableCheckpointing(600000);

        job.disableRestart();

        job.transform().run();
    }

    @Override
    public StreamJob transform() {
        source.addSink(sink);

        return this;
    }

    private static class RandomSensorValuesSource implements SourceFunction<SensorMessage> {
        private static final List<UUID> sensors = IntStream.range(0, 10)
                .mapToObj(i -> UUID.randomUUID()).collect(Collectors.toList());

        private volatile boolean running = true;

        @Override
        public void run(SourceContext<SensorMessage> sourceContext) {
            running = true;

            for (;;) {
                final DateTime instant = new DateTime();

                final List<SensorMessage> messages = sensors.stream()
                        .map(sensorId -> new SensorMessage(sensorId.toString(), Math.random(), ISODateTimeFormat.dateTime().print(instant)))
                        .collect(Collectors.toList());

                messages.stream().forEach(message -> sourceContext.collectWithTimestamp(message, instant.getMillis()));

                sourceContext.emitWatermark(new Watermark(instant.getMillis()));

                sourceContext.markAsTemporarilyIdle();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }

                if (!running) {
                    break;
                }
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }
}

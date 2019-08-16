package com.nextbreakpoint.flink.jobs;

import com.nextbreakpoint.flink.common.FlinkUtil;
import com.nextbreakpoint.flink.common.StreamJob;
import com.nextbreakpoint.flink.sensor.SensorData;
import com.nextbreakpoint.flink.sensor.SensorSerializationSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.nextbreakpoint.flink.common.Constants.BUCKET_BASE_PATH;
import static com.nextbreakpoint.flink.common.Constants.TARGET_TOPIC_NAME;

public class GenerateJob extends StreamJob {
    private final DataStream<SensorData> source;
    private final SinkFunction<SensorData> sink;

    public GenerateJob(
            StreamExecutionEnvironment env,
            String basePath,
            DataStream<SensorData> source,
            SinkFunction<SensorData> sink) {
        super(env, basePath);
        this.source = source;
        this.sink = sink;
    }

    public static void main(String[] args) throws Exception {
        final ParameterTool parameters = ParameterTool.fromArgs(args);

        final String bucketBasePath = getNonNullableParam(parameters, BUCKET_BASE_PATH);

        final String targetTopicName = getNonNullableParam(parameters, TARGET_TOPIC_NAME);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        final SinkFunction<SensorData> sink = FlinkUtil.createKafkaSink(parameters, targetTopicName, new SensorSerializationSchema(targetTopicName));

        final DataStreamSource<SensorData> source = env.addSource(new RandomSensorDataSource());

        final GenerateJob job = new GenerateJob(env, bucketBasePath, source, sink);

        job.enableCheckpointing(3600000);

        job.disableRestart();

        job.transform().run();
    }

    @Override
    public StreamJob transform() {
        source.addSink(sink);

        return this;
    }

    private static class RandomSensorDataSource implements SourceFunction<SensorData> {
        private static final List<Tuple2<Integer, UUID>> sensors = IntStream.range(0, 10)
                .mapToObj(i -> new Tuple2<>(i, UUID.randomUUID())).collect(Collectors.toList());

        private volatile boolean running = true;

        @Override
        public void run(SourceContext<SensorData> sourceContext) {
            running = true;

            double time = 0;

            for (;;) {
                final long millis = System.currentTimeMillis();

                final double currentTime = time;

                final List<SensorData> messages = sensors.stream()
                        .map(tuple -> new SensorData(tuple.f1.toString(), Math.sin(2 * Math.PI * (currentTime + tuple.f0)) + Math.random() / 100.0, LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                        .collect(Collectors.toList());

                messages.forEach(message -> sourceContext.collectWithTimestamp(message, millis));

                sourceContext.emitWatermark(new Watermark(millis));

                sourceContext.markAsTemporarilyIdle();

                time += 1;

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

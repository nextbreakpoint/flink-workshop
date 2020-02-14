package com.nextbreakpoint.flink.sensor;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomSensorDataSource implements ParallelSourceFunction<SensorEvent> {
    private volatile boolean running;

    @Override
    public void run(SourceContext<SensorEvent> sourceContext) {
        running = true;
        final List<Tuple2<Double, UUID>> tuples = IntStream.range(0, 10)
                .mapToObj(i -> new Tuple2<>(Math.random() * 100, UUID.randomUUID()))
                .collect(Collectors.toList());

        while (running) {
            final long timestamp = System.currentTimeMillis();

            tuples.stream()
                .map(tuple -> createSensorEvent(timestamp, tuple))
                .forEach(message -> sourceContext.collectWithTimestamp(message, timestamp));

            sourceContext.emitWatermark(new Watermark(timestamp));

            sourceContext.markAsTemporarilyIdle();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @Override
    public void cancel() {
        running = false;
    }

    private SensorEvent createSensorEvent(long timestamp, Tuple2<Double, UUID> tuple) {
        return new SensorEvent(
                UUID.randomUUID().toString(),
                tuple.f1.toString(),
                nextRandomValue(tuple.f0, timestamp),
                formatTimestamp(timestamp)
        );
    }

    private double nextRandomValue(Double offset, long timestamp) {
        return Math.sin(2 * Math.PI * (timestamp / 1000.0)) * offset / 10.0 + (Math.random() * offset / 20.0) + offset;
    }

    private String formatTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.of("UTC")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}

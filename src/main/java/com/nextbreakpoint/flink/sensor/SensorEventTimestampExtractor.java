package com.nextbreakpoint.flink.sensor;

import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SensorEventTimestampExtractor extends BoundedOutOfOrdernessTimestampExtractor<SensorEvent> {
    public SensorEventTimestampExtractor(Time maxOutOfOrderness) {
        super(maxOutOfOrderness);
    }

    @Override
    public long extractTimestamp(SensorEvent message) {
        return LocalDateTime.parse(message.getTimestamp()).toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}

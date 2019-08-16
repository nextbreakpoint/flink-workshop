package com.nextbreakpoint.flink.sensor;

import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class SensorTimestampExtractor extends BoundedOutOfOrdernessTimestampExtractor<SensorData> {
    public SensorTimestampExtractor(Time maxOutOfOrderness) {
        super(maxOutOfOrderness);
    }

    @Override
    public long extractTimestamp(SensorData message) {
        return LocalDateTime.parse(message.getTimestamp()).toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}

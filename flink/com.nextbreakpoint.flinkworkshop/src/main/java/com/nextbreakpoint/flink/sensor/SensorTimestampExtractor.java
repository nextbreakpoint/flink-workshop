package com.nextbreakpoint.flink.sensor;

import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.joda.time.DateTime;

public class SensorTimestampExtractor extends BoundedOutOfOrdernessTimestampExtractor<SensorData> {
    public SensorTimestampExtractor(Time maxOutOfOrderness) {
        super(maxOutOfOrderness);
    }

    @Override
    public long extractTimestamp(SensorData message) {
        return DateTime.parse(message.getTimestamp()).toDate().toInstant().toEpochMilli();
    }
}

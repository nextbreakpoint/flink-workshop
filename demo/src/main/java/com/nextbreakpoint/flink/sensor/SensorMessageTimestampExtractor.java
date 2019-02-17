package com.nextbreakpoint.flink.sensor;

import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.joda.time.DateTime;

public class SensorMessageTimestampExtractor extends BoundedOutOfOrdernessTimestampExtractor<SensorMessage> {

    public SensorMessageTimestampExtractor(Time maxOutOfOrderness) {
        super(maxOutOfOrderness);
    }

    @Override
    public long extractTimestamp(SensorMessage message) {
        return DateTime.parse(message.getTimestamp()).toDate().toInstant().toEpochMilli();
    }

}

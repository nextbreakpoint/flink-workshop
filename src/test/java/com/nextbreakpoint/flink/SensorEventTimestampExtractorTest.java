package com.nextbreakpoint.flink;

import com.nextbreakpoint.flink.sensor.SensorEvent;
import com.nextbreakpoint.flink.sensor.SensorEventTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SensorEventTimestampExtractorTest {
    @Test
    public void shouldParseDate() {
        SensorEventTimestampExtractor extractor = new SensorEventTimestampExtractor(Time.seconds(10));
        SensorEvent sensorEvent = new SensorEvent();
        sensorEvent.setTimestamp("2019-08-16T12:58:30.032");
        long actualTimestamp = extractor.extractTimestamp(sensorEvent);
        assertThat(actualTimestamp).isEqualTo(1565960310032L);
    }
}

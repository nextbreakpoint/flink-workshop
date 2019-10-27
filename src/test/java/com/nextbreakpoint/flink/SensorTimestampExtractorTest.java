package com.nextbreakpoint.flink;

import com.nextbreakpoint.flink.sensor.SensorData;
import com.nextbreakpoint.flink.sensor.SensorTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SensorTimestampExtractorTest {
    @Test
    public void shouldParseDate() {
        SensorTimestampExtractor extractor = new SensorTimestampExtractor(Time.seconds(10));
        SensorData sensorData = new SensorData();
        sensorData.setTimestamp("2019-08-16T12:58:30.032");
        long actualTimestamp = extractor.extractTimestamp(sensorData);
        assertThat(actualTimestamp).isEqualTo(1565960310032L);
    }
}

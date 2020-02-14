package com.nextbreakpoint.flink.sensor;

import org.apache.flink.api.java.functions.KeySelector;

public class SensorEventKeySelector implements KeySelector<SensorEvent, String> {
    @Override
    public String getKey(SensorEvent event) throws Exception {
        return event.getSensorId();
    }
}

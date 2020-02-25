package com.nextbreakpoint.flink.sensor;

import org.apache.flink.api.common.functions.MapFunction;

public class SensorEventToStringFunctiom implements MapFunction<SensorEvent, String> {
    @Override
    public String map(SensorEvent event) {
        final StringBuilder builder = new StringBuilder();
        builder.append(event.getEventId());
        builder.append(",");
        builder.append(event.getSensorId());
        builder.append(",");
        builder.append(event.getTemperature());
        builder.append(",");
        builder.append(event.getTimestamp());
        return builder.toString();
    }
}

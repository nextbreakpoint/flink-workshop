package com.nextbreakpoint.flink.sensor;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.types.Row;

public class SensorEventToRowFunctiom implements MapFunction<SensorEvent, Row> {
    @Override
    public Row map(SensorEvent event) {
        final Row row = new Row(4);
        row.setField(0, event.getEventId());
        row.setField(1, event.getSensorId());
        row.setField(2, event.getTemperature());
        row.setField(3, event.getTimestamp());
        return row;
    }
}

package com.nextbreakpoint.flink.sensor;

import org.apache.flink.api.common.functions.AggregateFunction;

public class MaxTemperatureAggregateFunction implements AggregateFunction<SensorEvent, MaxTemperatureEvent, MaxTemperatureEvent> {
    @Override
    public MaxTemperatureEvent createAccumulator() {
        MaxTemperatureEvent event = new MaxTemperatureEvent();
        event.setTemperature(Double.MIN_VALUE);
        return event;
    }

    @Override
    public MaxTemperatureEvent add(SensorEvent event, MaxTemperatureEvent accumulator) {
        accumulator.setSensorId(event.getSensorId());
        accumulator.setTimestamp(event.getTimestamp());
        if (accumulator.getTemperature() < event.getTemperature()) {
            accumulator.setTemperature(event.getTemperature());
        }
        return accumulator;
    }

    @Override
    public MaxTemperatureEvent getResult(MaxTemperatureEvent accumulator) {
        return accumulator;
    }

    @Override
    public MaxTemperatureEvent merge(MaxTemperatureEvent accu1, MaxTemperatureEvent accu2) {
        return accu1.getTemperature() < accu2.getTemperature() ? accu2 : accu1;
    }
}

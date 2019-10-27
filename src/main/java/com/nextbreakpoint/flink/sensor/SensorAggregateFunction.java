package com.nextbreakpoint.flink.sensor;

import org.apache.flink.api.common.functions.AggregateFunction;

public class SensorAggregateFunction implements AggregateFunction<SensorData, Double, Double> {
    @Override
    public Double createAccumulator() {
        return Double.MIN_VALUE;
    }

    @Override
    public Double add(SensorData data, Double aDouble) {
        return aDouble < data.getValue() ? data.getValue() : aDouble;
    }

    @Override
    public Double getResult(Double aDouble) {
        return aDouble;
    }

    @Override
    public Double merge(Double aDouble0, Double aDouble1) {
        return aDouble0 < aDouble1 ? aDouble1 : aDouble0;
    }
}

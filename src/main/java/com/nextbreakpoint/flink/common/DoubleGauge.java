package com.nextbreakpoint.flink.common;

import org.apache.flink.metrics.Gauge;

import java.io.Serializable;

public class DoubleGauge implements Gauge<Double>, Serializable {
    private Double value;

    @Override
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}

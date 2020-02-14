package com.nextbreakpoint.flink.sensor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MaxTemperatureEvent implements Comparable<MaxTemperatureEvent> {
    @JsonProperty("sensor_id")
    private String sensorId;
    @JsonProperty("temperature")
    private Double temperature;
    @JsonProperty("timestamp")
    private String timestamp;

    public MaxTemperatureEvent() {
    }

    @JsonCreator
    public MaxTemperatureEvent(
        @JsonProperty("sensor_id") String sensorId,
        @JsonProperty("temperature") Double temperature,
        @JsonProperty("timestamp") String timestamp
    ) {
        this.sensorId = sensorId;
        this.temperature = temperature;
        this.timestamp = timestamp;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSensorId() {
        return sensorId;
    }

    public Double getTemperature() {
        return temperature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int compareTo(MaxTemperatureEvent o) {
        return this.getTemperature().compareTo(o.getTemperature());
    }

    @Override
    public String toString() {
        return "SensorEvent{" +
                "sensorId='" + sensorId + '\'' +
                ", temperature=" + temperature +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}

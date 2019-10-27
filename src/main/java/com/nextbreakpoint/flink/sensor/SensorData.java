package com.nextbreakpoint.flink.sensor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SensorData implements Comparable<SensorData> {
    @JsonProperty("sensor_id")
    private String id;
    @JsonProperty("sensor_value")
    private Double value;
    @JsonProperty("sensor_timestamp")
    private String timestamp;

    public SensorData() {
    }

    @JsonCreator
    public SensorData(
        @JsonProperty("sensor_id") String id,
        @JsonProperty("sensor_value") Double value,
        @JsonProperty("sensor_timestamp") String timestamp
    ) {
        this.id = id;
        this.value = value;
        this.timestamp = timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public Double getValue() {
        return value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(SensorData o) {
        return this.getValue().compareTo(o.getValue());
    }

    @Override
    public String toString() {
        return "SensorData {" +
                "id='" + id + '\'' +
                ", value=" + value +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}

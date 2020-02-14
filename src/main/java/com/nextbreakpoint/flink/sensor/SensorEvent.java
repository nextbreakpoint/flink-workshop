package com.nextbreakpoint.flink.sensor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SensorEvent implements Comparable<SensorEvent> {
    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("sensor_id")
    private String sensorId;
    @JsonProperty("temperature")
    private Double temperature;
    @JsonProperty("timestamp")
    private String timestamp;

    public SensorEvent() {
    }

    @JsonCreator
    public SensorEvent(
        @JsonProperty("event_id") String eventId,
        @JsonProperty("sensor_id") String sensorId,
        @JsonProperty("temperature") Double temperature,
        @JsonProperty("timestamp") String timestamp
    ) {
        this.eventId = eventId;
        this.sensorId = sensorId;
        this.temperature = temperature;
        this.timestamp = timestamp;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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

    public String getEventId() {
        return eventId;
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

    @Override
    public int compareTo(SensorEvent o) {
        return this.getTemperature().compareTo(o.getTemperature());
    }

    @Override
    public String toString() {
        return "SensorEvent{" +
                "eventId='" + eventId + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", temperature=" + temperature +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}

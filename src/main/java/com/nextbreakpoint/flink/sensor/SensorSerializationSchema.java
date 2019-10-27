package com.nextbreakpoint.flink.sensor;

import com.nextbreakpoint.flink.common.Json;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

public class SensorSerializationSchema implements KafkaSerializationSchema<SensorData> {
    private final String topic;

    public SensorSerializationSchema(String topic) {
        this.topic = topic;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(SensorData sensorData, @Nullable Long timestamp) {
        try {
            return new ProducerRecord<>(topic, sensorData.getId().getBytes(StandardCharsets.UTF_8), Json.toJson(sensorData).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

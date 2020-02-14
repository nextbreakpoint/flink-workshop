package com.nextbreakpoint.flink.sensor;

import com.nextbreakpoint.flink.common.Json;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;

public class MaxTemperatureEventSerializationSchema implements KafkaSerializationSchema<MaxTemperatureEvent> {
    private final String topic;

    public MaxTemperatureEventSerializationSchema(String topic) {
        this.topic = topic;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(MaxTemperatureEvent event, @Nullable Long timestamp) {
        try {
            return new ProducerRecord<>(topic, event.getSensorId().getBytes(StandardCharsets.UTF_8), Json.toJson(event).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

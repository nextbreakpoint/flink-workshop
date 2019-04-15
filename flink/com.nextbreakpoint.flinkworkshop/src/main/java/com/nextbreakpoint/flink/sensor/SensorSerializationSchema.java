package com.nextbreakpoint.flink.sensor;

import com.nextbreakpoint.flink.common.JsonUtil;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;

import java.nio.charset.StandardCharsets;

public class SensorSerializationSchema implements KeyedSerializationSchema<SensorData> {
    private final String topic;

    public SensorSerializationSchema(String topic) {
        this.topic = topic;
    }

    @Override
    public byte[] serializeKey(SensorData message) {
        try {
            return message.getId().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] serializeValue(SensorData message) {
        try {
            return JsonUtil.toJson(message).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTargetTopic(SensorData message) {
        return topic;
    }
}

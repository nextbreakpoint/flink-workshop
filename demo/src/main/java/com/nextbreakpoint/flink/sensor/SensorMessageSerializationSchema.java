package com.nextbreakpoint.flink.sensor;

import com.nextbreakpoint.flink.common.JsonUtil;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;

public class SensorMessageSerializationSchema implements KeyedSerializationSchema<SensorMessage> {
    private final String topic;

    public SensorMessageSerializationSchema(String topic) {
        this.topic = topic;
    }

    @Override
    public byte[] serializeKey(SensorMessage message) {
        try {
            return message.getId().getBytes("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] serializeValue(SensorMessage message) {
        try {
            return JsonUtil.toJson(message).getBytes("UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTargetTopic(SensorMessage message) {
        return topic;
    }
}

package com.nextbreakpoint.flink.sensor;

import com.nextbreakpoint.flink.common.JsonUtil;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema;

import java.nio.charset.StandardCharsets;

public class SensorDeserializationSchema implements KeyedDeserializationSchema<SensorData> {
    @Override
    public SensorData deserialize(byte[] messageKey, byte[] message, String topic, int partition, long offset) {
        return JsonUtil.fromJson(new String(message, StandardCharsets.UTF_8), SensorData.class);
    }

    @Override
    public boolean isEndOfStream(SensorData sensorData) {
        return false;
    }

    @Override
    public TypeInformation<SensorData> getProducedType() {
        return TypeInformation.of(SensorData.class);
    }
}

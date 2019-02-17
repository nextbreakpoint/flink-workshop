package com.nextbreakpoint.flink.sensor;

import com.nextbreakpoint.flink.common.JsonUtil;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema;

import java.io.IOException;

public class SensorMessageDeserializationSchema implements KeyedDeserializationSchema<SensorMessage> {
    @Override
    public SensorMessage deserialize(byte[] messageKey, byte[] message, String topic, int partition, long offset) throws IOException {
        return JsonUtil.fromJson(new String(message, "UTF-8"), SensorMessage.class);
    }

    @Override
    public boolean isEndOfStream(SensorMessage sensorMessage) {
        return false;
    }

    @Override
    public TypeInformation<SensorMessage> getProducedType() {
        return TypeInformation.of(SensorMessage.class);
    }
}

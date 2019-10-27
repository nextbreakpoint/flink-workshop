package com.nextbreakpoint.flink.sensor;

import com.nextbreakpoint.flink.common.Json;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.nio.charset.StandardCharsets;

public class SensorDeserializationSchema implements KafkaDeserializationSchema<SensorData> {
    @Override
    public boolean isEndOfStream(SensorData sensorData) {
        return false;
    }

    @Override
    public SensorData deserialize(ConsumerRecord<byte[], byte[]> consumerRecord) throws Exception {
        return Json.fromJson(new String(consumerRecord.value(), StandardCharsets.UTF_8), SensorData.class);
    }

    @Override
    public TypeInformation<SensorData> getProducedType() {
        return TypeInformation.of(SensorData.class);
    }
}

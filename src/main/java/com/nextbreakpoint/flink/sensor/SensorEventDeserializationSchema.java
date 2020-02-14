package com.nextbreakpoint.flink.sensor;

import com.nextbreakpoint.flink.common.Json;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.nio.charset.StandardCharsets;

public class SensorEventDeserializationSchema implements KafkaDeserializationSchema<SensorEvent> {
    @Override
    public boolean isEndOfStream(SensorEvent sensorEvent) {
        return false;
    }

    @Override
    public SensorEvent deserialize(ConsumerRecord<byte[], byte[]> record) throws Exception {
        return Json.fromJson(new String(record.value(), StandardCharsets.UTF_8), SensorEvent.class);
    }

    @Override
    public TypeInformation<SensorEvent> getProducedType() {
        return TypeInformation.of(SensorEvent.class);
    }
}

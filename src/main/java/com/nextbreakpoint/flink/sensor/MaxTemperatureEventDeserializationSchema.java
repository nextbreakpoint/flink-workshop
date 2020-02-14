package com.nextbreakpoint.flink.sensor;

import com.nextbreakpoint.flink.common.Json;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.nio.charset.StandardCharsets;

public class MaxTemperatureEventDeserializationSchema implements KafkaDeserializationSchema<MaxTemperatureEvent> {
    @Override
    public boolean isEndOfStream(MaxTemperatureEvent sensorEvent) {
        return false;
    }

    @Override
    public MaxTemperatureEvent deserialize(ConsumerRecord<byte[], byte[]> record) throws Exception {
        return Json.fromJson(new String(record.value(), StandardCharsets.UTF_8), MaxTemperatureEvent.class);
    }

    @Override
    public TypeInformation<MaxTemperatureEvent> getProducedType() {
        return TypeInformation.of(MaxTemperatureEvent.class);
    }
}

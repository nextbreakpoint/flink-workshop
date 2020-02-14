package com.nextbreakpoint.flink.common;

import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;

import java.util.Properties;

public class Sources {
    private Sources() {}

    public static <T> SourceFunction<T> createKafkaSource(String topicName, KafkaDeserializationSchema<T> schema, Properties properties) {
        final FlinkKafkaConsumer<T> kafkaConsumer = new FlinkKafkaConsumer<>(topicName, schema, properties);
        kafkaConsumer.setCommitOffsetsOnCheckpoints(false);
        return kafkaConsumer;
    }
}

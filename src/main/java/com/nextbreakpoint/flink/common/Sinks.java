package com.nextbreakpoint.flink.common;

import org.apache.flink.api.common.serialization.BulkWriter;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;

import java.util.Properties;

public class Sinks {
    private Sinks() {}

    public static <T> SinkFunction<T> createKafkaSink(String topicName, KafkaSerializationSchema<T> schema, Properties properties) {
        final FlinkKafkaProducer.Semantic semantic = FlinkKafkaProducer.Semantic.AT_LEAST_ONCE;
        final FlinkKafkaProducer<T> kafkaProducer = new FlinkKafkaProducer<>(topicName, schema, properties, semantic);
        kafkaProducer.setWriteTimestampToKafka(true);
        return kafkaProducer;
    }

    public static <T> StreamingFileSink<T> createTextFileSink(Path basePath, int interval) {
        return StreamingFileSink.forRowFormat(basePath, new SimpleStringEncoder<T>())
                .withBucketAssigner(new DateTimeBucketAssigner<>())
                .withBucketCheckInterval(interval)
                .build();
    }

    public static <T> StreamingFileSink<T> createBulkFileSink(Path basePath, int interval, BulkWriter.Factory<T> factory) {
        return StreamingFileSink.forBulkFormat(basePath, factory)
                .withBucketAssigner(new DateTimeBucketAssigner<>())
                .withBucketCheckInterval(interval)
                .build();
    }
}

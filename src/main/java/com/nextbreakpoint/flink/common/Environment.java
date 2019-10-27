package com.nextbreakpoint.flink.common;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.io.File;
import java.util.Properties;

public class Environment {
    private Environment() {}

    public static <T> SourceFunction<T> createKafkaSource(String topicName, KafkaDeserializationSchema<T> schema, Properties properties) {
        final FlinkKafkaConsumer<T> kafkaConsumer = new FlinkKafkaConsumer<>(topicName, schema, properties);
        kafkaConsumer.setCommitOffsetsOnCheckpoints(false);
        return kafkaConsumer;
    }

    public static <T> SinkFunction<T> createKafkaSink(String topicName, KafkaSerializationSchema<T> schema, Properties properties) {
        final FlinkKafkaProducer.Semantic semantic = FlinkKafkaProducer.Semantic.AT_LEAST_ONCE;
        final FlinkKafkaProducer<T> kafkaProducer = new FlinkKafkaProducer<>(topicName, schema, properties, semantic);
        kafkaProducer.setWriteTimestampToKafka(true);
        return kafkaProducer;
    }

    public static Properties defaultKafkaConsumerProperties(String bootstrapServers, String groupId, String autoOffsetReset) {
        final Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerConfig.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return consumerConfig;
    }

    public static Properties defaultKafkaProducerProperties(String bootstrapServers, Object clientId) {
        final Properties producerConfig = new Properties();
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
        producerConfig.put(ProducerConfig.RETRIES_CONFIG, 5);
        producerConfig.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        producerConfig.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        producerConfig.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        producerConfig.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 60000);
        return producerConfig;
    }

//    consumerConfig.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
//    if (isDefined(keystoreLocation)) {
//        log.info("Found keystoreLocation: " + keystoreLocation);
//        consumerConfig.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
//    }
//            if (isDefined(keystorePassword)) {
//        log.info("Found keystorePassword");
//        consumerConfig.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);
//    }
//            if (isDefined(truststoreLocation)) {
//        log.info("Found truststoreLocation: " + truststoreLocation);
//        consumerConfig.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
//    }
//            if (isDefined(truststorePassword)) {
//        log.info("Found truststorePassword");
//        consumerConfig.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);
//    }

    public static <T> StreamingFileSink<T> createFileSink(String path) {
        return StreamingFileSink.forRowFormat(Path.fromLocalFile(new File(path)), new SimpleStringEncoder<T>())
                .withBucketAssigner(new DateTimeBucketAssigner<>())
                .withBucketCheckInterval(60000)
                .build();
    }

    public static StreamExecutionEnvironment getExecutionEnvironment(String developMode) {
        if (developMode.equals("enabled_with_ui")) {
            return StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        } else if (developMode.equals("enabled")) {
            return StreamExecutionEnvironment.createLocalEnvironment();
        } else {
            return StreamExecutionEnvironment.getExecutionEnvironment();
        }
    }

    public static void configureFileStateBackend(StreamExecutionEnvironment env, String basePath) {
        env.setStateBackend((StateBackend) new FsStateBackend(basePath, true));
    }

    public static void configureInMemoryBackend(StreamExecutionEnvironment env) {
        env.setStateBackend((StateBackend) new MemoryStateBackend(true));
    }

    public static void configureRestartStrategy(StreamExecutionEnvironment env) {
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(5, 60000));
    }

    public static void configureCheckpointing(StreamExecutionEnvironment env, int checkpointInterval, int pauseBetweenCheckpoints) {
        final CheckpointConfig config = env.getCheckpointConfig();
        config.setMinPauseBetweenCheckpoints(pauseBetweenCheckpoints);
        config.setCheckpointInterval(checkpointInterval);
        config.setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
    }
}

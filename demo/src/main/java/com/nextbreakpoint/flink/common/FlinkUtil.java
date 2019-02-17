package com.nextbreakpoint.flink.common;

import com.nextbreakpoint.flink.sensor.SensorMessage;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.fs.bucketing.BucketingSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Properties;

public class FlinkUtil {
    private static final Logger log = LoggerFactory.getLogger(FlinkUtil.class);

    private FlinkUtil() {}

    public static SourceFunction<SensorMessage> createKafkaSource(ParameterTool parameters, String topicName, KeyedDeserializationSchema schema, String jobGroupId, String autoOffsetReset) {
        final String bootstrapServers = StreamJob.getNonNullableParam(parameters, Constants.BOOTSTRAP_SERVERS);
        final String keystoreLocation = StreamJob.getNullableParam(parameters, Constants.KEYSTORE_LOCATION);
        final String keystorePassword = StreamJob.getNullableParam(parameters, Constants.KEYSTORE_PASSWORD);
        final String truststoreLocation = StreamJob.getNullableParam(parameters, Constants.TRUSTSTORE_LOCATION);
        final String truststorePassword = StreamJob.getNullableParam(parameters, Constants.TRUSTSTORE_PASSWORD);
        log.info("Source topic: '{}'", topicName);
        final Properties consumerProperties = createKafkaConsumerProperties(bootstrapServers, keystoreLocation, keystorePassword, truststoreLocation, truststorePassword, jobGroupId, autoOffsetReset);
        final FlinkKafkaConsumer011<SensorMessage> kafkaConsumer011 = new FlinkKafkaConsumer011<>(topicName, schema, consumerProperties);
        kafkaConsumer011.setCommitOffsetsOnCheckpoints(false);
        kafkaConsumer011.setStartFromEarliest();
        return kafkaConsumer011;
    }

    public static <T> SinkFunction<T> createKafkaSink(ParameterTool parameters, String topicName, KeyedSerializationSchema schema) {
        final String bootstrapServers = StreamJob.getNonNullableParam(parameters, Constants.BOOTSTRAP_SERVERS);
        final String keystoreLocation = StreamJob.getNullableParam(parameters, Constants.KEYSTORE_LOCATION);
        final String keystorePassword = StreamJob.getNullableParam(parameters, Constants.KEYSTORE_PASSWORD);
        final String truststoreLocation = StreamJob.getNullableParam(parameters, Constants.TRUSTSTORE_LOCATION);
        final String truststorePassword = StreamJob.getNullableParam(parameters, Constants.TRUSTSTORE_PASSWORD);
        log.info("Target topic: '{}'", topicName);
        final Properties producerProperties = createKafkaProducerProperties(bootstrapServers, keystoreLocation, keystorePassword, truststoreLocation, truststorePassword);
        final FlinkKafkaProducer011.Semantic semantic = FlinkKafkaProducer011.Semantic.AT_LEAST_ONCE;
        final FlinkKafkaProducer011<T> kafkaProducer011 = new FlinkKafkaProducer011<>(topicName, schema, producerProperties, Optional.empty(), semantic, 10);
        kafkaProducer011.setWriteTimestampToKafka(true);
        return kafkaProducer011;
    }

    private static Properties createKafkaConsumerProperties(String bootstrapServers, String keystoreLocation, String keystorePassword, String truststoreLocation, String truststorePassword, String groupId, String autoOffsetReset) {
        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerConfig.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        if (isDefined(keystoreLocation) && isDefined(truststoreLocation)) {
            consumerConfig.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            if (isDefined(keystoreLocation)) {
                log.info("Found keystoreLocation: " + keystoreLocation);
                consumerConfig.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
            }
            if (isDefined(keystorePassword)) {
                log.info("Found keystorePassword");
                consumerConfig.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);
            }
            if (isDefined(truststoreLocation)) {
                log.info("Found truststoreLocation: " + truststoreLocation);
                consumerConfig.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
            }
            if (isDefined(truststorePassword)) {
                log.info("Found truststorePassword");
                consumerConfig.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);
            }
        }
        return consumerConfig;
    }

    private static Properties createKafkaProducerProperties(String bootstrapServers, String keystoreLocation, String keystorePassword, String truststoreLocation, String truststorePassword) {
        Properties producerConfig = new Properties();
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerConfig.put(ProducerConfig.ACKS_CONFIG, "1");
        producerConfig.put(ProducerConfig.RETRIES_CONFIG, 5);
        producerConfig.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        producerConfig.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        producerConfig.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        producerConfig.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 1800000);
        if (isDefined(keystoreLocation) && isDefined(truststoreLocation)) {
            producerConfig.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            if (isDefined(keystoreLocation)) {
                log.info("Found keystoreLocation: " + keystoreLocation);
                producerConfig.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
            }
            if (isDefined(keystorePassword)) {
                log.info("Found keystorePassword");
                producerConfig.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);
            }
            if (isDefined(truststoreLocation)) {
                log.info("Found truststoreLocation: " + truststoreLocation);
                producerConfig.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
            }
            if (isDefined(truststorePassword)) {
                log.info("Found truststorePassword");
                producerConfig.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);
            }
        }
        return producerConfig;
    }

    private static boolean isDefined(String s) {
        return s != null && !s.isEmpty();
    }

    public static <T> BucketingSink<T> createBucketingSink(String path, int bucketThresholdMillis) {
        final BucketingSink<T> bucketingSink = new BucketingSink<>(path);
        bucketingSink.setInactiveBucketThreshold(bucketThresholdMillis);
        bucketingSink.setBatchSize(102400);
        return bucketingSink;
    }
}

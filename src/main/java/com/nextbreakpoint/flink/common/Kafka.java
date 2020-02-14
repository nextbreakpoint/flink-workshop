package com.nextbreakpoint.flink.common;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.plain.internals.PlainSaslServer;

import java.util.Map;
import java.util.Properties;

public class Kafka {
    private Kafka() {}

    public static Properties createProducerProperties(Map<String, String> config, String defaultBootstrapServers, String defaultProducerClientId) {
        final String bootstrapServers = config.getOrDefault("kafka_bootstrap_servers", defaultBootstrapServers);
        final String clientId = config.getOrDefault("kafka_client_id", defaultProducerClientId);
        final String acks = config.getOrDefault("kafka_acks", "all");
        final String retries = config.getOrDefault("kafka_retries", "2");
        final String securityProtocol = config.getOrDefault("kafka_security_protocol", "PLAINTEXT");

        final String confluentApiKey = config.get("kafka_confluent_api_key");
        final String confluentSecret = config.get("kafka_confluent_secret");

        final Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        properties.put(ProducerConfig.ACKS_CONFIG, acks);
        properties.put(ProducerConfig.RETRIES_CONFIG, retries);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        properties.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 60000);

        if (confluentApiKey != null && confluentSecret != null) {
            properties.put(SaslConfigs.SASL_MECHANISM, PlainSaslServer.PLAIN_MECHANISM);
            properties.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "https");
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
            properties.put(SaslConfigs.SASL_JAAS_CONFIG, getJaasConfig(confluentApiKey, confluentSecret));
        }

        return properties;
    }

    public static Properties createConsumerProperties(Map<String, String> config, String defaultBootstrapServers, String defaultGroupId, String defaultAutoOffsetReset) {
        final String bootstrapServers = config.getOrDefault("kafka_bootstrap_servers", defaultBootstrapServers);
        final String groupId = config.getOrDefault("kafka_group_id", defaultGroupId);
        final String autoOffsetReset = config.getOrDefault("kafka_auto_offset_reset", defaultAutoOffsetReset);
        final String enableAutoCommit = config.getOrDefault("kafka_enable_auto_commit", "false");
        final String securityProtocol = config.getOrDefault("kafka_security_protocol", "PLAINTEXT");
        final String isolationLevel = config.getOrDefault("kafka_isolation_level", "read_committed");

        final String confluentApiKey = config.get("kafka_confluent_api_key");
        final String confluentSecret = config.get("kafka_confluent_secret");

        final Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolationLevel);

        if (confluentApiKey != null && confluentSecret != null) {
            properties.put(SaslConfigs.SASL_MECHANISM, PlainSaslServer.PLAIN_MECHANISM);
            properties.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "https");
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
            properties.put(SaslConfigs.SASL_JAAS_CONFIG, getJaasConfig(confluentApiKey, confluentSecret));
        }

        return properties;
    }

    public static Properties defaultConsumerProperties(String bootstrapServers, String groupId, String autoOffsetReset) {
        final Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return properties;
    }

    public static Properties defaultProducerProperties(String bootstrapServers, Object clientId) {
        final Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 5);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        properties.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 60000);
        return properties;
    }

    private static String getJaasConfig(String confluentApiKey, String confluentSecret) {
        return String.format("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\" ;", confluentApiKey, confluentSecret);
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
}
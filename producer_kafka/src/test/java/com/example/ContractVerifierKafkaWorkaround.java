package com.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.cloud.contract.verifier.messaging.kafka.KafkaStubMessagesInitializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.HashMap;
import java.util.Map;

import static java.util.UUID.randomUUID;

class ContractVerifierKafkaWorkaround implements KafkaStubMessagesInitializer {
    private static final Log log = LogFactory.getLog(ContractVerifierKafkaWorkaround.class);

    @Override
    public Map<String, Consumer> initialize(EmbeddedKafkaBroker broker, KafkaProperties kafkaProperties) {
        Map<String, Consumer> map = new HashMap<>();
        for (String topic : broker.getTopics()) {
            map.put(topic, prepareListener(broker, topic));
        }
        return map;
    }

    private Consumer prepareListener(EmbeddedKafkaBroker broker, String destination) {
		Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps(randomUUID().toString(), "false", broker);
		consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(
				consumerProperties);
		Consumer<String, String> consumer = consumerFactory.createConsumer();
		broker.consumeFromAnEmbeddedTopic(consumer, destination);
		if (log.isDebugEnabled()) {
			log.debug("Prepared consumer for destination [" + destination + "]");
		}
		return consumer;
    }
}

package com.example;

import org.springframework.cloud.contract.verifier.messaging.kafka.KafkaStubMessagesInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class KafkaTestConfiguration {

	@Bean
	KafkaStubMessagesInitializer kafkaStubMessagesInitializer() {
		return new ContractVerifierKafkaWorkaround();
	}
}

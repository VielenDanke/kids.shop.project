package kz.danke.user.service.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${purchase.event.topic}")
    private String purchaseEventTopic;

    @Bean
    public NewTopic purchaseTopic() {
        return TopicBuilder
                .name(purchaseEventTopic)
                .replicas(1)
                .partitions(1)
                .compact()
                .build();
    }
}

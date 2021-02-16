package kz.danke.shipment.service.configuration;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory
    ) {
        final int amountOfThreadForConcurrency = 5;

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

        configurer.configure(factory, kafkaConsumerFactory);

        factory.setConcurrency(amountOfThreadForConcurrency);
//        factory.setErrorHandler(((thrownException, data) -> {
//            log.info("Exception in consumer is {} and the record is {}", thrownException.getMessage(), data);
//        }));
//        factory.setRetryTemplate(retryTemplate());
//        factory.setRecoveryCallback(context -> {
//            boolean isContains = context.getLastThrowable().toString().contains(RecoverableDataAccessException.class.getSimpleName());
//
//            if (isContains) {
//                String record = "record";
//
//                ConsumerRecord<Integer, String> consumerRecord = (ConsumerRecord<Integer, String>) context.getAttribute(record);
//
//                libraryEventService.handleRecovery(consumerRecord);
//
//                log.info("Inside the recoverable logic");
//            } else {
//                log.info("Inside the non recoverable logic");
//                throw new RuntimeException(context.getLastThrowable().getMessage());
//            }
//            return null;
//        });

//        ContainerProperties containerProperties = factory.getContainerProperties();
//
//        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

//    private RetryTemplate retryTemplate() {
//        RetryTemplate retryTemplate = new RetryTemplate();
//
//        retryTemplate.setRetryPolicy(simpleRetryPolicy());
//        retryTemplate.setBackOffPolicy(getFixedBackOffPolicy());
//
//        return retryTemplate;
//    }
//
//    private FixedBackOffPolicy getFixedBackOffPolicy() {
//        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
//
//        fixedBackOffPolicy.setBackOffPeriod(1000);
//
//        return fixedBackOffPolicy;
//    }
//
//    private RetryPolicy simpleRetryPolicy() {
////        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
////
////        retryPolicy.setMaxAttempts(3);
//        Map<Class<? extends Throwable>, Boolean> exceptionMap = new HashMap<>();
//
//        exceptionMap.put(IllegalArgumentException.class, false);
//        exceptionMap.put(RecoverableDataAccessException.class, true);
//
//        return new SimpleRetryPolicy(
//                3,
//                exceptionMap,
//                true
//        );
//    }
}

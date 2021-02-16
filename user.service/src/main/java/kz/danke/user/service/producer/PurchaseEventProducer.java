package kz.danke.user.service.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.danke.user.service.event.PurchaseKafkaEvent;
import kz.danke.user.service.service.JsonObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class PurchaseEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonObjectMapper jsonObjectMapper;

    public CompletableFuture<SendResult<String, String>> sendPurchaseEventRecord(PurchaseKafkaEvent event) throws JsonProcessingException {
        RecordHeader header = new RecordHeader("event-source", "scanner".getBytes());

        List<Header> recordHeaders = List.of(header);

        ProducerRecord<String, String> record = createProducerRecord(
                event, recordHeaders
        );
        return kafkaTemplate.send(record)
                .completable();
    }

    private ProducerRecord<String, String> createProducerRecord(PurchaseKafkaEvent event, List<Header> headers) throws JsonProcessingException {
        String purchaseEvent = jsonObjectMapper.serializeObject(event);

        return new ProducerRecord<>(
                "purchase_event_topic",
                null,
                UUID.randomUUID().toString(),
                purchaseEvent,
                headers
        );
    }
}

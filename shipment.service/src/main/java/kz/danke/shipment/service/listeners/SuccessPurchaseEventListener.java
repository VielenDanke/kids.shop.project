package kz.danke.shipment.service.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.danke.shipment.service.event.PurchaseKafkaEvent;
import kz.danke.shipment.service.mapper.PurchaseMapper;
import kz.danke.shipment.service.model.Purchase;
import kz.danke.shipment.service.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuccessPurchaseEventListener {

    private final ObjectMapper objectMapper;
    private final PurchaseService purchaseService;

    @KafkaListener(
            topics = {"purchase_event_topic"}
    )
    public void onEvent(ConsumerRecord<String, String> purchaseEventConsumer) throws JsonProcessingException {
        String value = purchaseEventConsumer.value();
        PurchaseKafkaEvent purchaseKafkaEvent = objectMapper.readValue(value, PurchaseKafkaEvent.class);
        Purchase purchase = PurchaseMapper.mapToPurchase(purchaseKafkaEvent);
        purchaseService.save(purchase);
    }
}

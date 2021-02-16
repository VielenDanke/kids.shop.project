package kz.danke.shipment.service.mapper;

import kz.danke.shipment.service.event.PurchaseKafkaEvent;
import kz.danke.shipment.service.model.Purchase;

public class PurchaseMapper {

    public static Purchase mapToPurchase(PurchaseKafkaEvent event) {
        return Purchase.builder()
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .city(event.getCity())
                .address(event.getAddress())
                .phoneNumber(event.getPhoneNumber())
                .email(event.getEmail())
                .build();
    }
}

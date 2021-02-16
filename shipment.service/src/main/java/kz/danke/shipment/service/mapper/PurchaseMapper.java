package kz.danke.shipment.service.mapper;

import kz.danke.shipment.service.event.PurchaseKafkaEvent;
import kz.danke.shipment.service.model.Purchase;

public class PurchaseMapper {

    public static Purchase mapToPurchase(PurchaseKafkaEvent event) {
        return new Purchase(
                null, event.getFirstName(), event.getLastName(), event.getCity(), event.getAddress(),
                event.getPhoneNumber(), event.getEmail()
        );
    }
}

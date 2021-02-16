package kz.danke.user.service.mapper;

import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.event.PurchaseKafkaEvent;

public class PurchaseEventMapper {

    public static PurchaseKafkaEvent mapToPurchaseEvent(ChargeRequest chargeRequest) {
        return new PurchaseKafkaEvent(
                chargeRequest.getFirstName(), chargeRequest.getLastName(), chargeRequest.getCity(),
                chargeRequest.getAddress(), chargeRequest.getPhoneNumber(), chargeRequest.getEmail()
        );
    }
}

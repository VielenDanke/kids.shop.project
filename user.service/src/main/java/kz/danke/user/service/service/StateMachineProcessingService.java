package kz.danke.user.service.service;

import kz.danke.user.service.document.Cart;
import kz.danke.user.service.dto.request.ChargeRequest;

public interface StateMachineProcessingService {

    void processReserve(Cart cart, String stateMachineID);

    Cart restoreCartFromStateMachine(String stateMachineID);

    void processChargeEvent(ChargeRequest chargeRequest, String stateMachineID);

    void processReserveDecline(String stateMachineID);
}

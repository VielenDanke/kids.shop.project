package kz.danke.user.service.service.impl;

import kz.danke.user.service.config.state.machine.PurchaseEvent;
import kz.danke.user.service.config.state.machine.PurchaseState;
import kz.danke.user.service.document.Cart;
import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.exception.StateMachinePersistingException;
import kz.danke.user.service.service.JsonObjectMapper;
import kz.danke.user.service.service.StateMachineProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static kz.danke.user.service.config.state.machine.StateMachineConfig.CLOTH_CART_KEY;
import static kz.danke.user.service.config.state.machine.actions.PurchaseAction.USER_DATA_KEY;

@Service
public class StateMachineProcessingServiceImpl implements StateMachineProcessingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateMachineProcessingServiceImpl.class);

    private final StateMachineFactory<PurchaseState, PurchaseEvent> stateMachineFactory;
    private final StateMachinePersister<PurchaseState, PurchaseEvent, String> stateMachinePersister;
    private final JsonObjectMapper jsonObjectMapper;

    @Autowired
    public StateMachineProcessingServiceImpl(StateMachineFactory<PurchaseState, PurchaseEvent> stateMachineFactory,
                                             StateMachinePersister<PurchaseState, PurchaseEvent, String> stateMachinePersister,
                                             JsonObjectMapper jsonObjectMapper) {
        this.stateMachineFactory = stateMachineFactory;
        this.stateMachinePersister = stateMachinePersister;
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Override
    public void processReserve(Cart cart, String stateMachineID) {
        StateMachine<PurchaseState, PurchaseEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.getExtendedState().getVariables().put(CLOTH_CART_KEY, jsonObjectMapper.serializeObject(cart));
        stateMachine.sendEvent(PurchaseEvent.RESERVE);
        try {
            stateMachinePersister.persist(stateMachine, stateMachineID);
        } catch (Exception e) {
            processError(stateMachineID, PurchaseEvent.RESERVE.name(), e.getLocalizedMessage());
        }
    }

    @Override
    public Cart restoreCartFromStateMachine(String stateMachineID) {
        StateMachine<PurchaseState, PurchaseEvent> restoredStateMachine = stateMachineFactory.getStateMachine();
        try {
            stateMachinePersister
                    .restore(restoredStateMachine, stateMachineID);
        } catch (Exception e) {
            processError(stateMachineID, "Restore cart", e.getLocalizedMessage());
        }
        return jsonObjectMapper.deserializeJson(
                (String) restoredStateMachine.getExtendedState().getVariables().get(CLOTH_CART_KEY),
                Cart.class
        );
    }

    @Override
    public void processChargeEvent(ChargeRequest chargeRequest, String stateMachineID) {
        StateMachine<PurchaseState, PurchaseEvent> stateMachine = stateMachineFactory.getStateMachine();
        try {
            stateMachinePersister.restore(stateMachine, stateMachineID);
            stateMachine.getExtendedState().getVariables().put(
                    USER_DATA_KEY, jsonObjectMapper.serializeObject(chargeRequest)
            );
            stateMachine.sendEvent(PurchaseEvent.BUY);
        } catch (Exception e) {
            processError(stateMachineID, PurchaseEvent.BUY.name(), e.getLocalizedMessage());
        }
    }

    @Override
    public void processReserveDecline(String stateMachineID) {
        StateMachine<PurchaseState, PurchaseEvent> stateMachine = stateMachineFactory.getStateMachine();
        try {
            stateMachinePersister.restore(stateMachine, stateMachineID);
        } catch (Exception e) {
            processError(stateMachineID, PurchaseEvent.RESERVE_DECLINE.name(), e.getLocalizedMessage());
        }
        stateMachine.sendEvent(PurchaseEvent.RESERVE_DECLINE);
    }

    private void processError(String stateMachineID, String reserveDecline, String localizedMessage) {
        LOGGER.error("Error during state machine persisting event {} occurred {}",
                reserveDecline, localizedMessage);
        throw new StateMachinePersistingException(
                String.format("State machine persisting exception. ID: %s", stateMachineID)
        );
    }
}

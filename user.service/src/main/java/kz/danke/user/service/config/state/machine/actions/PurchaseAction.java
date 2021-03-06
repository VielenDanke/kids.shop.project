package kz.danke.user.service.config.state.machine.actions;

import kz.danke.user.service.config.state.machine.PurchaseEvent;
import kz.danke.user.service.config.state.machine.PurchaseState;
import kz.danke.user.service.document.Cart;
import kz.danke.user.service.document.ClothCart;
import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.event.PurchaseKafkaEvent;
import kz.danke.user.service.mapper.PurchaseEventMapper;
import kz.danke.user.service.producer.PurchaseEventProducer;
import kz.danke.user.service.service.JsonObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.List;

import static kz.danke.user.service.config.state.machine.StateMachineConfig.CLOTH_CART_KEY;

@Slf4j
public class PurchaseAction implements Action<PurchaseState, PurchaseEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseAction.class);

    public static final String USER_DATA_KEY = "USER_DATA";

    private final JsonObjectMapper jsonObjectMapper;
    private final PurchaseEventProducer producer;

    public PurchaseAction(JsonObjectMapper jsonObjectMapper, PurchaseEventProducer producer) {
        this.jsonObjectMapper = jsonObjectMapper;
        this.producer = producer;
    }

    @SneakyThrows
    @Override
    public void execute(StateContext<PurchaseState, PurchaseEvent> stateContext) {
        String clothCartList = stateContext.getExtendedState().get(CLOTH_CART_KEY, String.class);
        String userData = stateContext.getExtendedState().get(USER_DATA_KEY, String.class);

        Cart clothCart = jsonObjectMapper.deserializeJson(clothCartList, Cart.class);
        ChargeRequest chargeRequest = jsonObjectMapper.deserializeJson(userData, ChargeRequest.class);

        List<ClothCart> clothes = clothCart.getClothCartList();

        LOGGER.info("Purchase accepted {}", clothes.stream().map(ClothCart::getPrice).reduce(0, Integer::sum));
        LOGGER.info("User data processed {}", chargeRequest.toString());

        PurchaseKafkaEvent purchaseKafkaEvent = PurchaseEventMapper.mapToPurchaseEvent(chargeRequest);

        producer.sendPurchaseEventRecord(purchaseKafkaEvent)
                .thenAccept(event -> {
                    log.info(event.getProducerRecord().value());
                });
    }
}

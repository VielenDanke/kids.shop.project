package kz.danke.user.service.config.state.machine.actions;

import kz.danke.user.service.config.state.machine.PurchaseEvent;
import kz.danke.user.service.config.state.machine.PurchaseState;
import kz.danke.user.service.document.Cart;
import kz.danke.user.service.service.JsonObjectMapper;
import kz.danke.user.service.service.UserService;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import static kz.danke.user.service.config.state.machine.StateMachineConfig.CLOTH_CART_KEY;

public class ReserveAction implements Action<PurchaseState, PurchaseEvent> {

    private final JsonObjectMapper jsonObjectMapper;
    private final UserService userService;

    public ReserveAction(JsonObjectMapper jsonObjectMapper, UserService userService) {
        this.jsonObjectMapper = jsonObjectMapper;
        this.userService = userService;
    }

    @Override
    public void execute(StateContext<PurchaseState, PurchaseEvent> stateContext) {
        String clothCartList = stateContext.getExtendedState().get(CLOTH_CART_KEY, String.class);

        Cart clothCart = jsonObjectMapper.deserializeJson(clothCartList, Cart.class);

        userService.reserveCartShop(clothCart)
                .doOnNext(cart ->
                        stateContext.getExtendedState().getVariables().put(
                                CLOTH_CART_KEY, jsonObjectMapper.serializeObject(cart)
                        )
                ).subscribe();
    }
}

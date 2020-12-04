package kz.danke.user.service.config.state.machine.actions;

import kz.danke.user.service.config.state.machine.PurchaseEvent;
import kz.danke.user.service.config.state.machine.PurchaseState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class ErrorAction implements Action<PurchaseState, PurchaseEvent> {

    @Override
    public void execute(StateContext<PurchaseState, PurchaseEvent> stateContext) {
        Exception exception = stateContext.getException();
        String localizedMessage = exception.getLocalizedMessage();

        System.out.println(localizedMessage);
    }
}

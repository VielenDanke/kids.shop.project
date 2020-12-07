package kz.danke.user.service.config.state.machine.persister;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.danke.user.service.config.state.machine.PurchaseEvent;
import kz.danke.user.service.config.state.machine.PurchaseState;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;

import java.util.HashMap;

public class InMemoryStateMachinePersist implements StateMachinePersist<PurchaseState, PurchaseEvent, String> {

    private final HashMap<String, StateMachineContext<PurchaseState, PurchaseEvent>> contextMap = new HashMap<>();

    @Override
    public void write(StateMachineContext<PurchaseState, PurchaseEvent> stateMachineContext, String contextObj) throws Exception {
        contextMap.put(contextObj, stateMachineContext);
    }

    @Override
    public StateMachineContext<PurchaseState, PurchaseEvent> read(String contextObj) throws Exception {
        return contextMap.get(contextObj);
    }
}

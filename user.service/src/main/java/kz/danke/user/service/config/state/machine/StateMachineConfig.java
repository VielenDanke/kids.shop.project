package kz.danke.user.service.config.state.machine;

import kz.danke.user.service.config.state.machine.actions.*;
import kz.danke.user.service.config.state.machine.listener.PurchaseStateMachineEventListener;
import kz.danke.user.service.config.state.machine.persister.InMemoryStateMachinePersist;
import kz.danke.user.service.service.JsonObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<PurchaseState, PurchaseEvent> {

    public static final String CLOTH_CART_KEY = "CLOTH_CART";

    private final JsonObjectMapper jsonObjectMapper;

    @Autowired
    public StateMachineConfig(JsonObjectMapper jsonObjectMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PurchaseState, PurchaseEvent> config) throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(new PurchaseStateMachineEventListener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<PurchaseState, PurchaseEvent> states) throws Exception {
        states
                .withStates()
                .initial(PurchaseState.NEW)
                .states(EnumSet.allOf(PurchaseState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PurchaseState, PurchaseEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PurchaseState.NEW)
                .target(PurchaseState.RESERVED)
                .event(PurchaseEvent.RESERVE)
                .action(reserveAction(), errorAction())
                .and()
                .withExternal()
                .source(PurchaseState.RESERVED)
                .target(PurchaseState.CANCEL_RESERVED)
                .event(PurchaseEvent.RESERVE_DECLINE)
                .action(declineAction(), errorAction())
                .and()
                .withExternal()
                .source(PurchaseState.RESERVED)
                .target(PurchaseState.PURCHASE_COMPLETE)
                .event(PurchaseEvent.BUY)
                .action(purchaseAction(), errorAction())
                .and()
                .withExternal()
                .source(PurchaseState.CANCEL_RESERVED)
                .target(PurchaseState.RESERVED)
                .event(PurchaseEvent.RESTORE_RESERVE)
                .action(restoreAction(), errorAction());
    }

    @Bean
    public Action<PurchaseState, PurchaseEvent> reserveAction() {
        return new ReserveAction(jsonObjectMapper);
    }

    @Bean
    public Action<PurchaseState, PurchaseEvent> declineAction() {
        return new DeclineAction(jsonObjectMapper);
    }

    @Bean
    public Action<PurchaseState, PurchaseEvent> purchaseAction() {
        return new PurchaseAction(jsonObjectMapper);
    }

    @Bean
    public Action<PurchaseState, PurchaseEvent> restoreAction() {
        return new RestoreReserve(jsonObjectMapper);
    }

    @Bean
    public Action<PurchaseState, PurchaseEvent> errorAction() {
        return new ErrorAction();
    }

    @Bean
    public StateMachinePersister<PurchaseState, PurchaseEvent, String> inMemoryStateMachinePersister() {
        return new DefaultStateMachinePersister<>(new InMemoryStateMachinePersist());
    }
}

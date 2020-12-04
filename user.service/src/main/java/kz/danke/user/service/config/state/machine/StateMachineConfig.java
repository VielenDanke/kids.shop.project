package kz.danke.user.service.config.state.machine;

import kz.danke.user.service.config.state.machine.listener.PurchaseStateMachineEventListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachine
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<PurchaseState, PurchaseEvent> {

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
        
    }
}

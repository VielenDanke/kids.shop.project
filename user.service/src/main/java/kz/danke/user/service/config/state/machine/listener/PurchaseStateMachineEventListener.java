package kz.danke.user.service.config.state.machine.listener;

import kz.danke.user.service.config.state.machine.PurchaseEvent;
import kz.danke.user.service.config.state.machine.PurchaseState;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

public class PurchaseStateMachineEventListener extends StateMachineListenerAdapter<PurchaseState, PurchaseEvent> {

    @Override
    public void stateChanged(State<PurchaseState, PurchaseEvent> from, State<PurchaseState, PurchaseEvent> to) {
    }

    @Override
    public void stateEntered(State<PurchaseState, PurchaseEvent> state) {
    }

    @Override
    public void stateExited(State<PurchaseState, PurchaseEvent> state) {
    }

    @Override
    public void eventNotAccepted(Message<PurchaseEvent> event) {
    }

    @Override
    public void transition(Transition<PurchaseState, PurchaseEvent> transition) {
    }

    @Override
    public void transitionStarted(Transition<PurchaseState, PurchaseEvent> transition) {
    }

    @Override
    public void transitionEnded(Transition<PurchaseState, PurchaseEvent> transition) {
    }

    @Override
    public void stateMachineStarted(StateMachine<PurchaseState, PurchaseEvent> stateMachine) {
    }

    @Override
    public void stateMachineStopped(StateMachine<PurchaseState, PurchaseEvent> stateMachine) {
    }

    @Override
    public void stateMachineError(StateMachine<PurchaseState, PurchaseEvent> stateMachine, Exception exception) {
    }

    @Override
    public void extendedStateChanged(Object key, Object value) {
    }

    @Override
    public void stateContext(StateContext<PurchaseState, PurchaseEvent> stateContext) {
    }
}

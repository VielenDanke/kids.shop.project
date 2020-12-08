package kz.danke.user.service.config;

import kz.danke.user.service.config.state.machine.PurchaseEvent;
import kz.danke.user.service.config.state.machine.PurchaseState;
import kz.danke.user.service.document.Cart;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.dto.request.RegistrationRequest;
import kz.danke.user.service.dto.response.ChargeResponse;
import kz.danke.user.service.dto.response.RegistrationResponse;
import kz.danke.user.service.dto.response.UserCabinetResponse;
import kz.danke.user.service.exception.*;
import kz.danke.user.service.service.JsonObjectMapper;
import kz.danke.user.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static kz.danke.user.service.config.state.machine.StateMachineConfig.CLOTH_CART_KEY;

@Component
public class UserHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserHandler.class);

    private final UserService userService;
    private final StateMachineFactory<PurchaseState, PurchaseEvent> stateMachineFactory;
    private final JsonObjectMapper jsonObjectMapper;
    private final StateMachinePersister<PurchaseState, PurchaseEvent, String> stateMachinePersister;

    @Autowired
    public UserHandler(UserService userService,
                       StateMachineFactory<PurchaseState, PurchaseEvent> stateMachineFactory,
                       JsonObjectMapper jsonObjectMapper,
                       StateMachinePersister<PurchaseState, PurchaseEvent, String> stateMachinePersister) {
        this.userService = userService;
        this.stateMachineFactory = stateMachineFactory;
        this.jsonObjectMapper = jsonObjectMapper;
        this.stateMachinePersister = stateMachinePersister;
    }

    public Mono<ServerResponse> handleCartProcess(ServerRequest serverRequest) {
        final String stateMachineID = UUID.randomUUID().toString();

        return serverRequest.bodyToMono(Cart.class)
                .doOnNext(cart -> {
                    StateMachine<PurchaseState, PurchaseEvent> stateMachine = stateMachineFactory.getStateMachine();
                    stateMachine.getExtendedState().getVariables().put(CLOTH_CART_KEY, jsonObjectMapper.serializeObject(cart));
                    stateMachine.sendEvent(PurchaseEvent.RESERVE);
                    try {
                        stateMachinePersister.persist(stateMachine, stateMachineID);
                    } catch (Exception e) {
                        String localizedMessage = e.getLocalizedMessage();
                        LOGGER.error("Error during state persisting occurred {}", localizedMessage);
                        Mono.defer(() -> Mono.error(new StateMachinePersistingException(localizedMessage)));
                    }
                })
                .flatMap(cart -> {
                    StateMachine<PurchaseState, PurchaseEvent> restoredStateMachine = stateMachineFactory.getStateMachine();
                    try {
                        stateMachinePersister
                                .restore(restoredStateMachine, stateMachineID);
                    } catch (Exception e) {
                        Mono.defer(() -> Mono.error(new StateMachinePersistingException(e.getLocalizedMessage())));
                    }
                    return ServerResponse.ok()
                            .header("STATE_ID", stateMachineID)
                            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "STATE_ID")
                            .body(Mono.just(
                                    jsonObjectMapper.deserializeJson(
                                            (String) restoredStateMachine.getExtendedState().getVariables().get(CLOTH_CART_KEY),
                                            Cart.class
                                    )
                            ), Cart.class);
                })
                .onErrorResume(ClothCartNotFoundException.class, ex ->
                        createServerResponse(ex, ex.getResponseStatus(), serverRequest)
                )
                .onErrorResume(UserNotAuthorizedException.class, ex -> createServerResponse(ex, 401, serverRequest))
                .onErrorResume(UserNotFoundException.class, ex -> createServerResponse(ex, 400, serverRequest));
    }

    public Mono<ServerResponse> handleChargeProcess(ServerRequest serverRequest) {
        final String stateID = serverRequest.headers().firstHeader("STATE_ID");

        return Mono.justOrEmpty(stateID)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new HeaderNotFoundException("STATE_ID header not found"))))
                .doOnNext(stID -> {
                    StateMachine<PurchaseState, PurchaseEvent> stateMachine = stateMachineFactory.getStateMachine();
                    try {
                        stateMachinePersister.restore(stateMachine, stID);
                        stateMachine.sendEvent(PurchaseEvent.BUY);
                    } catch (Exception e) {
                        Mono.defer(() -> Mono.error(new StateMachinePersistingException("State machine exception")));
                    }
                })
                .flatMap(stID -> ServerResponse.ok().body(Mono.just(
                        String.format("Charge successfully processed with STATE_ID: %s", stID)
                ), String.class))
                .onErrorResume(UserNotAuthorizedException.class, ex -> createServerResponse(ex, 401, serverRequest))
                .onErrorResume(UserNotFoundException.class, ex -> createServerResponse(ex, 400, serverRequest));
    }

    private Mono<ServerResponse> createServerResponse(Exception ex, Integer status, ServerRequest request) {
        return ServerResponse
                .status(status)
                .body(Mono.just(new ResponseFailed(
                        ex.toString(),
                        ex.getLocalizedMessage(),
                        request.path())
                ), ResponseFailed.class);
    }

    public Mono<ServerResponse> getUserCabinet(ServerRequest serverRequest) {
        return userService.getUserInSession()
                .map(UserCabinetResponse::toUserCabinetResponse)
                .flatMap(user -> ServerResponse.ok().body(Mono.just(user), UserCabinetResponse.class))
                .onErrorResume(UserNotAuthorizedException.class, ex -> createServerResponse(ex, 401, serverRequest))
                .onErrorResume(UserNotFoundException.class, ex -> createServerResponse(ex, 400, serverRequest));
    }

    public Mono<ServerResponse> saveNewUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegistrationRequest.class)
                .map(regReq -> User.builder().username(regReq.getUsername()).password(regReq.getPassword()).build())
                .flatMap(userService::saveNewUser)
                .map(user -> new RegistrationResponse(user.getId(), user.getUsername()))
                .flatMap(regResp -> ServerResponse.ok().body(Mono.just(regResp), RegistrationResponse.class))
                .onErrorResume(Exception.class, ex -> ServerResponse.status(500).body(
                        Mono.just(new ResponseFailed(ex.getLocalizedMessage(), ex.toString(), serverRequest.path())),
                        ResponseFailed.class
                ));
    }
}

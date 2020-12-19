package kz.danke.user.service.config;

import kz.danke.user.service.document.Cart;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.request.ChargeRequest;
import kz.danke.user.service.dto.request.RegistrationRequest;
import kz.danke.user.service.dto.request.UserUpdateRequest;
import kz.danke.user.service.dto.response.RegistrationResponse;
import kz.danke.user.service.dto.response.UserCabinetResponse;
import kz.danke.user.service.exception.*;
import kz.danke.user.service.service.JsonObjectMapper;
import kz.danke.user.service.service.StateMachineProcessingService;
import kz.danke.user.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class UserHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserHandler.class);

    private final UserService userService;
    private final JsonObjectMapper jsonObjectMapper;
    private final StateMachineProcessingService stateMachineProcessingService;

    @Autowired
    public UserHandler(UserService userService,
                       JsonObjectMapper jsonObjectMapper,
                       StateMachineProcessingService stateMachineProcessingService) {
        this.userService = userService;
        this.jsonObjectMapper = jsonObjectMapper;
        this.stateMachineProcessingService = stateMachineProcessingService;
    }

    public Mono<ServerResponse> handleCartProcess(ServerRequest serverRequest) {
        final String stateMachineID = UUID.randomUUID().toString();

        return serverRequest.bodyToMono(Cart.class)
                .doOnNext(cart -> stateMachineProcessingService.processReserve(cart, stateMachineID))
                .then(ServerResponse.ok()
                        .header("STATE_ID", stateMachineID)
                        .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "STATE_ID")
                        .body(Mono.just("Reserve successfully processed"), String.class))
                .onErrorResume(ClothCartNotFoundException.class, ex ->
                        createServerResponse(ex, ex.getResponseStatus(), serverRequest)
                )
                .onErrorResume(StateMachinePersistingException.class, ex -> createServerResponse(ex, 500, serverRequest));
    }

    public Mono<ServerResponse> handleCartRetrieve(ServerRequest serverRequest) {
        final String stateMachineID = serverRequest.headers().firstHeader("STATE_ID");

        return Mono.justOrEmpty(stateMachineID)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new HeaderNotFoundException("STATE_ID header not found"))))
                .map(stateMachineProcessingService::restoreCartFromStateMachine)
                .flatMap(cart -> ServerResponse.ok().body(Mono.just(cart), Cart.class))
                .onErrorResume(HeaderNotFoundException.class, ex -> createServerResponse(ex, 400, serverRequest));
    }

    public Mono<ServerResponse> handleChargeProcess(ServerRequest serverRequest) {
        final String stateID = serverRequest.headers().firstHeader("STATE_ID");

        return serverRequest.bodyToMono(ChargeRequest.class)
                .doOnNext(chargeRequest -> stateMachineProcessingService.processChargeEvent(chargeRequest, stateID))
                .then(ServerResponse.ok().body(Mono.just(
                        String.format("Charge successfully processed with STATE_ID: %s", stateID)
                ), String.class))
                .onErrorResume(StateMachinePersistingException.class, ex -> createServerResponse(ex, 500, serverRequest));
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

    public Mono<ServerResponse> handleCartReserveDecline(ServerRequest serverRequest) {
        final String stateID = serverRequest.headers().firstHeader("STATE_ID");

        return Mono.justOrEmpty(stateID)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new HeaderNotFoundException("STATE_ID header not found"))))
                .doOnNext(stID -> stateMachineProcessingService.processReserveDecline(stateID))
                .then(ServerResponse.ok().body(Mono.just(
                        String.format("Decline successfully processed, STATE_ID: %s", stateID)
                ), String.class))
                .onErrorResume(StateMachinePersistingException.class, ex -> createServerResponse(ex, 500, serverRequest));
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

    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        return null;
    }
}

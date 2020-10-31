package kz.danke.edge.service.configuration;

import kz.danke.edge.service.document.User;
import kz.danke.edge.service.dto.request.RegistrationRequest;
import kz.danke.edge.service.dto.response.RegistrationResponse;
import kz.danke.edge.service.exception.ResponseFailed;
import kz.danke.edge.service.exception.UserAlreadyExistsException;
import kz.danke.edge.service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class UserHandler {

    private final UserService userService;

    @Autowired
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> handleRegistration(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegistrationRequest.class)
                .map(registrationRequest -> User.builder()
                        .username(registrationRequest.getUsername())
                        .password(registrationRequest.getPassword())
                        .build()
                )
                .flatMap(userService::save)
                .map(user -> RegistrationResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .build()
                )
                .flatMap(registrationResponse -> ServerResponse.ok().body(
                        Mono.just(registrationResponse),
                        RegistrationResponse.class)
                )
                .onErrorContinue(Exception.class, (ex, obj) -> ServerResponse.badRequest().build())
                .onErrorResume(UserAlreadyExistsException.class, (ex) -> {
                    ResponseFailed responseFailed = new ResponseFailed(ex.toString(), ex.getLocalizedMessage());
                    return ServerResponse.badRequest().body(Mono.just(responseFailed), ResponseFailed.class);
                })
                .switchIfEmpty(ServerResponse.notFound().build());

    }
}

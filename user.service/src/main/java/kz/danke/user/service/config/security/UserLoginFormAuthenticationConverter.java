package kz.danke.user.service.config.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class UserLoginFormAuthenticationConverter implements ServerAuthenticationConverter {

    private String usernameFieldName = "username";
    private String passwordFieldName = "password";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange
                .getFormData()
                .map(map -> {
                    String username = map.getFirst(usernameFieldName);
                    String password = map.getFirst(passwordFieldName);

                    return new UsernamePasswordAuthenticationToken(
                            username, password
                    );
                });
    }

    public void setUsernameFieldName(String usernameFieldName) {
        this.usernameFieldName = usernameFieldName;
    }

    public void setPasswordFieldName(String passwordFieldName) {
        this.passwordFieldName = passwordFieldName;
    }
}

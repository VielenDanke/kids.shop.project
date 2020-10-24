package kz.danke.user.service.config.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class UserLoginFormAuthenticationConverter implements ServerAuthenticationConverter {

    private String usernameFieldName = "username";
    private String passwordFieldName = "password";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange
                .getFormData()
                .map(this::createAuthentication);
    }

    private Authentication createAuthentication(MultiValueMap<String, String> stringStringMultiValueMap) {
        return new UsernamePasswordAuthenticationToken(
                stringStringMultiValueMap.getFirst(usernameFieldName),
                stringStringMultiValueMap.getFirst(passwordFieldName)
        );
    }

    private Authentication createAuthentication(String username, String password) {
        return new UsernamePasswordAuthenticationToken(
                username, password
        );
    }

    public void setUsernameFieldName(String usernameFieldName) {
        this.usernameFieldName = usernameFieldName;
    }

    public void setPasswordFieldName(String passwordFieldName) {
        this.passwordFieldName = passwordFieldName;
    }
}

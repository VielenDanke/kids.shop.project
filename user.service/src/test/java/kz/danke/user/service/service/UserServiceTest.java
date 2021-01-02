package kz.danke.user.service.service;

import kz.danke.user.service.document.Authorities;
import kz.danke.user.service.document.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

public class UserServiceTest extends AbstractServiceLayer {

    @BeforeEach
    public void setup() {
        Mockito.reset(passwordEncoder, userRepository, webClient);
    }

    @Test
    public void userService_SaveNewUser() {
        User testUser = User.builder().password(testData).build();
        String encodedPassword = String.format("encoded %s", testData);

        when(userRepository.save(testUser)).thenReturn(Mono.just(testUser));
        when(passwordEncoder.encode(testData)).thenReturn(encodedPassword);

        StepVerifier.create(userService.saveNewUser(testUser))
                .expectSubscription()
                .expectNextMatches(u -> {
                    boolean isIdNotEmpty = !StringUtils.isEmpty(u.getId());
                    boolean isPasswordEncoded = encodedPassword.equals(testUser.getPassword());
                    boolean isAuthoritiesPresent = u.getAuthorities().contains(Authorities.ROLE_USER.name());

                    return isIdNotEmpty && isPasswordEncoded && isAuthoritiesPresent;
                })
                .verifyComplete();

        verify(userRepository, times(1)).save(testUser);
        verify(passwordEncoder, times(1)).encode(testData);
    }
}

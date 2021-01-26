package kz.danke.user.service.service;

import kz.danke.user.service.config.security.UserDetailsImpl;
import kz.danke.user.service.document.User;
import kz.danke.user.service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserDetailsPasswordServiceTest extends AbstractServiceLayer {

    @BeforeEach
    public void setup() {
        Mockito.reset(passwordEncoder, userRepository, webRequestService);
    }

    @Test
    public void userDetailsService_FindByUsername() {
        final String userID = UUID.randomUUID().toString();
        final User user = User.builder().id(userID).username(testData).build();

        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(user));

        StepVerifier.create(userDetailsPasswordService.findByUsername(testData))
                .expectSubscription()
                .expectNextMatches(u -> u.getUsername().equals(testData))
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void userDetailsService_FindByUsername_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(userDetailsPasswordService.findByUsername(testData))
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void userDetailsService_ChangePassword() {
        final String userID = UUID.randomUUID().toString();
        final User user = User.builder().id(userID).username(testData).password(testData).build();
        final String updatedPassword = "updated";

        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userDetailsPasswordService.updatePassword(new UserDetailsImpl(user), updatedPassword))
                .expectSubscription()
                .expectNextMatches(u -> u.getPassword().equals(updatedPassword))
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void userDetailsService_ChangePassword_UserNotFound() {
        final User user = User.builder().username(testData).build();

        when(userRepository.findByUsername(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(userDetailsPasswordService.updatePassword(new UserDetailsImpl(user), testData))
                .expectSubscription()
                .expectError(UserNotFoundException.class)
                .verify();

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(0)).save(any(User.class));
    }
}

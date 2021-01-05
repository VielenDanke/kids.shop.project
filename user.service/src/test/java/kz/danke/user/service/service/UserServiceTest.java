package kz.danke.user.service.service;

import kz.danke.user.service.document.Authorities;
import kz.danke.user.service.document.Cart;
import kz.danke.user.service.document.User;
import kz.danke.user.service.dto.request.UserUpdateRequest;
import kz.danke.user.service.exception.UserNotAuthorizedException;
import kz.danke.user.service.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.UUID;

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

    @Test
    public void userService_GetUserInSecuritySession_ReturnExceptionUserNotAuthorized() {
        StepVerifier.create(userService.getUserInSession())
                .expectSubscription()
                .expectError(UserNotAuthorizedException.class)
                .verify();

        verify(userRepository, times(0)).findByUsername(anyString());
    }

    @Test
    public void userService_GetUserInSecuritySession_ReturnUserNotFoundException() {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                testData, testData, Collections.singleton(new SimpleGrantedAuthority(testData))));

        when(userRepository.findByUsername(testData)).thenReturn(Mono.empty());

        StepVerifier.create(
                userService
                        .getUserInSession()
                        .subscriberContext(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
        ).expectSubscription()
                .expectError(UserNotFoundException.class)
                .verify();

        verify(userRepository, times(1)).findByUsername(testData);
    }

    @Test
    public void userService_GetUserInSecuritySession() {
        final String userId = UUID.randomUUID().toString();

        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                testData, testData, Collections.singleton(new SimpleGrantedAuthority(testData))));

        when(userRepository.findByUsername(testData))
                .thenReturn(Mono.just(User.builder().username(testData).id(userId).build()));

        StepVerifier.create(
                userService
                        .getUserInSession()
                        .subscriberContext(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
        ).expectSubscription()
                .expectNextMatches(u -> u.getId().equals(userId) && u.getUsername().equals(testData))
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(testData);
    }

    @Test
    public void userService_UpdateUser_ReturnUserNotAuthorizedException() {
        StepVerifier.create(userService.updateUser(new UserUpdateRequest()))
                .expectSubscription()
                .expectError(UserNotAuthorizedException.class)
                .verify();

        verify(userRepository, times(0)).findByUsername(anyString());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void userService_UpdateUser_ReturnUserNotFoundException() {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                testData, testData, Collections.singleton(new SimpleGrantedAuthority(testData))));

        when(userRepository.findByUsername(testData)).thenReturn(Mono.empty());

        StepVerifier.create(
                userService
                .updateUser(new UserUpdateRequest(testData, testData, testData, testData, testData, testData))
                .subscriberContext(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
        ).expectSubscription()
                .expectError(UserNotFoundException.class)
                .verify();

        verify(userRepository, times(1)).findByUsername(testData);
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void userService_UpdateUser() {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                testData, testData, Collections.singleton(new SimpleGrantedAuthority(testData))));
        User user = User.builder().username(testData).build();
        String updated = "updated";

        when(userRepository.findByUsername(testData)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.updateUser(
                new UserUpdateRequest(updated, updated, updated, updated, updated, updated)
        ).subscriberContext(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext))))
                .expectSubscription()
                .expectNextMatches(u -> u.getUsername().equals(updated) && u.getFirstName().equals(updated) &&
                        u.getLastName().equals(updated) && u.getPhoneNumber().equals(updated) && u.getCity().equals(updated)
                && u.getAddress().equals(updated))
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(testData);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void userService_ReserveCartShop() {

    }
}

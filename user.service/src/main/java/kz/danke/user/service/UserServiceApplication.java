package kz.danke.user.service;

import kz.danke.user.service.config.AppConfigProperties;
import kz.danke.user.service.document.Authorities;
import kz.danke.user.service.document.User;
import kz.danke.user.service.repository.ReactiveUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

@SpringBootApplication
@EnableConfigurationProperties(value = {AppConfigProperties.class})
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(ReactiveUserRepository reactiveUserRepository,
									PasswordEncoder passwordEncoder) throws InterruptedException {
		return args -> {
			User user = User.builder()
					.id(UUID.randomUUID().toString())
					.username("first@mail.ru")
					.password(passwordEncoder.encode("qwert"))
					.authorities(Collections.singleton(Authorities.ROLE_USER.name()))
					.address("address")
					.firstName("firstName")
					.lastName("surname")
					.city("Karaganda")
					.phoneNumber("87777777777")
					.build();
			User second = User.builder()
					.id(UUID.randomUUID().toString())
					.username("second@mail.ru")
					.password(passwordEncoder.encode("qwert"))
					.authorities(Collections.singleton(Authorities.ROLE_ADMIN.name()))
					.address("address")
					.city("Karaganda")
					.firstName("firstName")
					.lastName("surname")
					.phoneNumber("87777777777")
					.build();

			reactiveUserRepository
					.deleteAll()
					.thenMany(Flux.fromIterable(Arrays.asList(user, second)))
					.flatMap(reactiveUserRepository::save)
					.blockLast();
		};
	}
}

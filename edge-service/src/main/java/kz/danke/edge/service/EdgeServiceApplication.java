package kz.danke.edge.service;

import kz.danke.edge.service.configuration.AppConfigProperties;
import kz.danke.edge.service.document.Authorities;
import kz.danke.edge.service.document.Cart;
import kz.danke.edge.service.document.User;
import kz.danke.edge.service.repository.ReactiveUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.UUID;

@SpringBootApplication
@EnableEurekaClient
@EnableConfigurationProperties({AppConfigProperties.class})
public class EdgeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdgeServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(ReactiveUserRepository reactiveUserRepository,
									PasswordEncoder passwordEncoder) {
		return args -> {
			User user = User.builder()
					.id(UUID.randomUUID().toString())
					.username("first@mail.ru")
					.password(passwordEncoder.encode("first"))
					.authorities(Collections.singleton(Authorities.ROLE_USER.name()))
					.address("address")
					.cart(new Cart())
					.firsName("firstName")
					.surname("surname")
					.phoneNumber("87777777777")
					.build();
			User second = User.builder()
					.id(UUID.randomUUID().toString())
					.username("second@mail.ru")
					.password(passwordEncoder.encode("second"))
					.authorities(Collections.singleton(Authorities.ROLE_ADMIN.name()))
					.address("address")
					.cart(new Cart())
					.firsName("firstName")
					.surname("surname")
					.phoneNumber("87777777777")
					.build();

			reactiveUserRepository
					.findByUsername("first@mail.ru")
					.switchIfEmpty(reactiveUserRepository.save(user))
					.block();
			reactiveUserRepository
					.findByUsername("second@mail.ru")
					.switchIfEmpty(reactiveUserRepository.save(second))
					.block();
		};
	}

}

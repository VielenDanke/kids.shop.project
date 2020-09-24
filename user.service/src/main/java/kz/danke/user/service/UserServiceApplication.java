package kz.danke.user.service;

import kz.danke.user.service.config.AppConfigProperties;
import kz.danke.user.service.document.User;
import kz.danke.user.service.repository.ReactiveUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManagerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.tools.agent.ReactorDebugAgent;

import java.util.Collections;
import java.util.UUID;

@SpringBootApplication
@EnableConfigurationProperties(value = {AppConfigProperties.class})
public class UserServiceApplication {

	private final ReactiveUserRepository reactiveUserRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceApplication(ReactiveUserRepository reactiveUserRepository,
								  PasswordEncoder passwordEncoder) {
		this.reactiveUserRepository = reactiveUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Bean
	public CommandLineRunner lineRunner() {
		return args -> {
			User user = User.builder()
					.id(UUID.randomUUID().toString())
					.username("first")
					.password(passwordEncoder.encode("first"))
					.authorities(Collections.singleton("ROLE_USER"))
					.build();

			User block = reactiveUserRepository.save(user).block();

			System.out.println(block.getId());
		};
	}

	public static void main(String[] args) {
//		ReactorDebugAgent.init();
		SpringApplication.run(UserServiceApplication.class, args);
	}

}

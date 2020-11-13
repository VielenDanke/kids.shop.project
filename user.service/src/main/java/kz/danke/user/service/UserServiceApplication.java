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
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.tools.agent.ReactorDebugAgent;

import java.util.Collections;
import java.util.UUID;

@SpringBootApplication
@EnableConfigurationProperties(value = {AppConfigProperties.class})
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
}

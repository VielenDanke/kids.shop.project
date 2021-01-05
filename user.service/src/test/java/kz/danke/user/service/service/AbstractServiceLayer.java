package kz.danke.user.service.service;

import kz.danke.user.service.config.AppConfigProperties;
import kz.danke.user.service.repository.ReactiveUserRepository;
import kz.danke.user.service.service.impl.UserServiceImpl;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.ClassRule;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserServiceImpl.class, AppConfigProperties.class, AbstractServiceLayer.Initializer.class, AbstractServiceLayer.WebConfig.class})
@TestExecutionListeners({
        MockitoTestExecutionListener.class,
        SpringBootDependencyInjectionTestExecutionListener.class
})
public abstract class AbstractServiceLayer {

    @Autowired
    protected UserService userService;
    protected String testData = "test";

    @MockBean
    protected ReactiveUserRepository userRepository;
    @Autowired
    protected WebClient webClient;
    @MockBean
    protected PasswordEncoder passwordEncoder;

    @ClassRule
    public static MockWebServer mockWebServer = new MockWebServer();

    static {
        try {
            mockWebServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Configuration
    static class WebConfig {
        @Bean
        public WebClient webClient() {
            return WebClient.builder().build();
        }
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "app.url.reserve_cart=" + String.format("http://localhost:%s/clothes/reserve", mockWebServer.getPort()),
                    "app.url.decline_cert=" + String.format("http://localhost:%s/clothes/reserve/decline", mockWebServer.getPort())
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}

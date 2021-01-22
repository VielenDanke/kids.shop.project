package kz.danke.user.service.router;

import kz.danke.user.service.config.AppConfigProperties;
import kz.danke.user.service.config.UserHandler;
import kz.danke.user.service.config.UserRoutesConfig;
import kz.danke.user.service.config.security.SecurityConfig;
import kz.danke.user.service.config.security.jwt.JwtService;
import kz.danke.user.service.config.security.jwt.impl.JwtServiceImpl;
import kz.danke.user.service.config.state.machine.StateMachineConfig;
import kz.danke.user.service.service.JsonObjectMapper;
import kz.danke.user.service.service.StateMachineProcessingService;
import kz.danke.user.service.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SpringBootWebSecurityConfiguration;
import org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        UserRoutesConfig.class,
        UserHandler.class,
        SecurityConfig.class,
        JwtServiceImpl.class,
        AppConfigProperties.class,
        JsonObjectMapper.class
})
@TestPropertySource("classpath:application-test.properties")
@WebFluxTest
@TestExecutionListeners(listeners = {
        MockitoTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        SpringBootDependencyInjectionTestExecutionListener.class
})
public abstract class AbstractRouterLayer {
    protected final String testData = "test";
    protected final Integer testNumber = 8;
    protected WebTestClient webTestClient;
    @Autowired
    protected JwtService<String> jwtService;
    @Autowired
    protected AppConfigProperties properties;
    @MockBean
    protected UserService userService;
    @MockBean
    protected StateMachineProcessingService stateMachineProcessingService;
}

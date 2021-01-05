package kz.danke.user.service.router;

import kz.danke.user.service.config.UserHandler;
import kz.danke.user.service.config.UserRoutesConfig;
import kz.danke.user.service.config.security.jwt.JwtService;
import kz.danke.user.service.service.JsonObjectMapper;
import kz.danke.user.service.service.StateMachineProcessingService;
import kz.danke.user.service.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        UserRoutesConfig.class,
        UserHandler.class
})
@TestPropertySource("classpath:application-test.properties")
@WebFluxTest
@TestExecutionListeners(listeners = {
        MockitoTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class
})
public abstract class AbstractRouterLayer {
    protected WebTestClient webTestClient;
    @MockBean
    protected UserService userService;
    @MockBean
    protected JsonObjectMapper jsonObjectMapper;
    @MockBean
    protected StateMachineProcessingService stateMachineProcessingService;
    @MockBean
    protected JwtService<String> jwtService;
}

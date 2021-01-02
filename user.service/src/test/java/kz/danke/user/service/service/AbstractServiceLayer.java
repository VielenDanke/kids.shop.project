package kz.danke.user.service.service;

import kz.danke.user.service.repository.ReactiveUserRepository;
import kz.danke.user.service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserServiceImpl.class})
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
    @MockBean
    protected WebClient webClient;
    @MockBean
    protected PasswordEncoder passwordEncoder;
}

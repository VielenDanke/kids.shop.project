package kz.danke.user.service.service;

import kz.danke.user.service.config.AppConfigProperties;
import kz.danke.user.service.repository.ReactiveUserRepository;
import kz.danke.user.service.service.impl.UserDetailsPasswordServiceImpl;
import kz.danke.user.service.service.impl.UserServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserServiceImpl.class, UserDetailsPasswordServiceImpl.class})
@TestExecutionListeners({
        MockitoTestExecutionListener.class,
        SpringBootDependencyInjectionTestExecutionListener.class
})
@TestPropertySource("classpath:application-test.properties")
public abstract class AbstractServiceLayer {

    @Autowired
    protected UserService userService;
    @Autowired
    protected UserDetailsPasswordServiceImpl userDetailsPasswordService;
    protected String testData = "test";

    @MockBean
    protected ReactiveUserRepository userRepository;
    @MockBean
    protected PasswordEncoder passwordEncoder;
    @MockBean
    protected WebRequestService webRequestService;

}

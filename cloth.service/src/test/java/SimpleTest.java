import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebFluxTest
public class SimpleTest {

    @Autowired
    private WebClient.Builder webClientBuilder;
    private WebClient webClient;

    {
        webClient = webClientBuilder.build();
    }

    @Test
    public void test() {

    }

    @Test
    public void secondMethod() {

    }
}

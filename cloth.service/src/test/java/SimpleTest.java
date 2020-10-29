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
        List<String> block = Mono.just("blabla, blaaaa, toooo")// your method here
                .flatMap(s -> Mono.just(Arrays.asList(s.split(",")))) //split
                .flatMapIterable(s -> s) // Flux of ids
                .parallel()
                .runOn(Schedulers.parallel())
                .log()
                .flatMap(s -> Mono.just(s.substring(0, s.length() - 1))) // call get by id
                .sequential()
                .sort() // can place comparator
                .collectList()
                .block();
    }

    @Test
    public void secondMethod() {
        Product product = webClient
                .get()
                .uri("http://localhost:9200")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ContentApiBaseResponse<Product>>() {
                })
                .map(this::getDataFromResponse)
                .onErrorReturn(new Product())
                .onErrorContinue(NullPointerException.class, (ex, obj) -> {
                    System.out.println("Aaaa NPE");
                })
                .block();
    }

    private Product getDataFromResponse(ContentApiBaseResponse<Product> contentApiBaseResponse) {
        return contentApiBaseResponse.getT();
    }

    private static class ContentApiBaseResponse<T> {
        private T t;

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }
    }

    private static class Product {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

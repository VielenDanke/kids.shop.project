package kz.danke.kids.shop.controller;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.dto.request.ClothSaveRequest;
import kz.danke.kids.shop.service.ClothService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/clothes")
public class ClothController {

    private final ClothService clothService;
    private final Environment environment;

    @Autowired
    public ClothController(ClothService clothService,
                           Environment environment) {
        this.clothService = clothService;
        this.environment = environment;
    }

    @GetMapping
    public Flux<String> findAllClothes() {
        return clothService.findAll()
                .flatMap(cloth -> {
                    Mono<String> clothDescription = Mono.just(cloth.getDescription());
                    Mono<String> serverPort = Mono.just(Objects.requireNonNull(environment.getProperty("server.port")));

                    return clothDescription.zipWith(serverPort, (clothDes, sPort) ->
                            String.format("Description is %s and port is %s", clothDes, serverPort));
                });
    }

    @PostMapping
    public Mono<Cloth> save(@RequestBody ClothSaveRequest clothSaveRequest) {
        return clothService.save(clothSaveRequest);
    }
}

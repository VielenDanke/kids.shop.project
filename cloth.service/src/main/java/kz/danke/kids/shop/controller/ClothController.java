package kz.danke.kids.shop.controller;

import kz.danke.kids.shop.document.Cloth;
import kz.danke.kids.shop.dto.request.ClothSaveRequest;
import kz.danke.kids.shop.service.ClothService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/clothes")
public class ClothController {

    private final ClothService clothService;

    @Autowired
    public ClothController(ClothService clothService) {
        this.clothService = clothService;
    }

    @GetMapping
    public Flux<Cloth> findAllClothes(@RequestParam(name = "amount", required = false) final Integer amountOfCloth) {
        return amountOfCloth == null ? clothService.findAll() : clothService.findAll()
                .take(amountOfCloth);
    }

    @PostMapping
    public Mono<Cloth> save(@RequestBody ClothSaveRequest clothSaveRequest) {
        return clothService.save(clothSaveRequest);
    }
}

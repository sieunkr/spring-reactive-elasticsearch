package com.example.demo.web;

import com.example.demo.core.Coffee;
import com.example.demo.core.CoffeeUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/coffees")
public class CoffeeController {

    private final CoffeeUseCase coffeeUseCase;

    public CoffeeController(CoffeeUseCase coffeeUseCase) {
        this.coffeeUseCase = coffeeUseCase;
    }

    @GetMapping
    public Flux<Coffee> findAll(){

        return coffeeUseCase.findAll()
                .onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Coffee> findByTitle(@RequestParam(name="title") String title){

        return coffeeUseCase.findByTitle(title);
    }

    @GetMapping("/match")
    @ResponseStatus(HttpStatus.OK)
    public Flux<Coffee> match(@RequestParam(name="title") String title){

        return coffeeUseCase.matchPhraseQueryByTitle(title);
    }
}

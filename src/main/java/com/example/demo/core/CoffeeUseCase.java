package com.example.demo.core;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface CoffeeUseCase {
    Flux<Coffee> findAll();
    Flux<Coffee> findByTitle(String title);
    Flux<Coffee> matchPhraseQueryByTitle(String title);
    Mono<Coffee> findById(String id);

}

package com.cafecoffe.product.api.controller;

import com.cafecoffe.product.api.model.Coffee;
import com.cafecoffe.product.api.model.CoffeeEvent;
import com.cafecoffe.product.api.repository.CoffeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

@RestController
@RequestMapping("/coffees")
public class CoffeeController {

    private CoffeeRepository coffeeRepository;

    public CoffeeController(CoffeeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }


    @GetMapping
    public Flux<Coffee> findAll(){
        return coffeeRepository.findAll();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Coffee>> findById(@PathVariable("id") String id){
        return coffeeRepository.findById(id)
                .map(coffee -> ResponseEntity.ok(coffee))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Coffee> createCoffee(@RequestBody Coffee coffee) {
        return coffeeRepository.save(coffee);
    }

    @PutMapping
    public Mono<ResponseEntity<Coffee>> updateCoffee(@RequestBody Coffee coffee) {
        return coffeeRepository.findById(coffee.getId())
                .flatMap(existingCoffee -> {
                    existingCoffee.setName(coffee.getName());
                    existingCoffee.setPrice(coffee.getPrice());
                    return coffeeRepository.save(existingCoffee);
                })
                .map(updateCoffee -> ResponseEntity.ok(updateCoffee))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable("id") String id) {
        return coffeeRepository.findById(id)
                .flatMap(existingCoffee -> coffeeRepository.delete(existingCoffee))
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<Void> deleteProducts() {
        return coffeeRepository.deleteAll();
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CoffeeEvent> getCoffeeEvents() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(id -> new CoffeeEvent(id, "New Coffee Event"));
    }
}

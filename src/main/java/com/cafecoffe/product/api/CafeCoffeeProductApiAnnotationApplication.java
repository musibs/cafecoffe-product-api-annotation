package com.cafecoffe.product.api;

import com.cafecoffe.product.api.model.Coffee;
import com.cafecoffe.product.api.repository.CoffeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class CafeCoffeeProductApiAnnotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CafeCoffeeProductApiAnnotationApplication.class, args);
    }

    @Bean
    CommandLineRunner init(ReactiveMongoOperations reactiveMongoOperations, CoffeeRepository coffeeRepository) {
        return args -> {
            Flux<Coffee> coffeeFlux = Flux.just(
                new Coffee(null, "Latte", 1.50),
                new Coffee(null, "Hot Chocolate", 4.50),
                new Coffee(null, "Mocha", 2.5))
                    .flatMap(coffeeRepository::save);

            coffeeFlux.thenMany(coffeeRepository.findAll())
                    .subscribe(System.out::println);

            /*Flux<Coffee> coffeeFlux = Flux.just(
                    new Coffee(null, "Latte", 1.50),
                    new Coffee(null, "Hot Chocolate", 4.50),
                    new Coffee(null, "Mocha", 2.5))
                    .flatMap(coffeeRepository::save);

            reactiveMongoOperations.collectionExists(Coffee.class)
                    .flatMap(exists -> exists ? reactiveMongoOperations.dropCollection(Coffee.class) : Mono.just(exists))
                    .log()
                    .thenMany(value -> reactiveMongoOperations.createCollection(Coffee.class))
                    .thenMany(coffeeFlux)
                    .thenMany(coffeeRepository.findAll())
                    .subscribe(System.out::println);*/
        };
    }

}

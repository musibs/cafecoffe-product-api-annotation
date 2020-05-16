package com.cafecoffe.product.api;

import com.cafecoffe.product.api.controller.CoffeeController;
import com.cafecoffe.product.api.model.Coffee;
import com.cafecoffe.product.api.model.CoffeeEvent;
import com.cafecoffe.product.api.repository.CoffeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JUnit5TestApplicationContext {

    private WebTestClient webTestClient;
    private List<Coffee> expectedCoffeeList;

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void beforeEach() {
        this.webTestClient = WebTestClient
                .bindToApplicationContext(applicationContext)
                .configureClient()
                .baseUrl("/coffees")
                .build();

        this.expectedCoffeeList = this.coffeeRepository
                .findAll().collectList().block();
    }

    @Test
    void testAllCoffees() {
        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Coffee.class)
                .isEqualTo(expectedCoffeeList);
    }

    @Test
    void testInvalidCoffeeId() {
        webTestClient.get()
                .uri("/invalid")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testValidCoffeeId() {
        webTestClient.get()
                .uri("/{id}", expectedCoffeeList.get(0).getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Coffee.class)
                .isEqualTo(expectedCoffeeList.get(0));
    }

    @Test
    void testCoffeeEvents() {
        CoffeeEvent expectedCoffeeEvent = new CoffeeEvent(0L, "New Coffee Event");

        FluxExchangeResult<CoffeeEvent> fluxExchangeResult =
                webTestClient
                        .get()
                        .uri("/events")
                        .accept(MediaType.TEXT_EVENT_STREAM)
                        .exchange()
                        .expectStatus().isOk()
                        .returnResult(CoffeeEvent.class);

        StepVerifier.create(fluxExchangeResult.getResponseBody())
                .expectNext(expectedCoffeeEvent)
                .expectNextCount(2)
                .consumeNextWith(coffeeEvent -> assertEquals(Long.valueOf(3), coffeeEvent.getEventId()))
                .thenCancel()
                .verify();
    }


}

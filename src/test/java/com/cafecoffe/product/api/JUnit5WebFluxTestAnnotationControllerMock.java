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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class JUnit5TestAnnotationControllerMock {

    private WebTestClient webTestClient;
    private List<Coffee> expectedCoffeeList;

    @MockBean
    private CoffeeRepository coffeeRepository;

    @BeforeEach
    void beforeEach() {
        this.webTestClient = WebTestClient
                .bindToController(new CoffeeController(coffeeRepository))
                .configureClient()
                .baseUrl("/coffees")
                .build();

        this.expectedCoffeeList = Arrays.asList(new Coffee("0f12300f", "Chai Latte", 2.5));
    }

    @Test
    void testAllCoffees() {
        when(coffeeRepository.findAll()).thenReturn(Flux.fromIterable(this.expectedCoffeeList));

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
        String id = "/invalid";
        when(coffeeRepository.findById(id)).thenReturn(Mono.empty());
        webTestClient.get()
                .uri("/{id}", id)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testValidCoffeeId() {
        Coffee expectedCoffee = expectedCoffeeList.get(0);

        when(coffeeRepository.findById(expectedCoffee.getId())).thenReturn(Mono.just(expectedCoffee));

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

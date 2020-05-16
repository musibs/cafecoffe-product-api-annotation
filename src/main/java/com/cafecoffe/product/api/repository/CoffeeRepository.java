package com.cafecoffe.product.api.repository;

import com.cafecoffe.product.api.model.Coffee;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeRepository extends ReactiveMongoRepository<Coffee, String> {
}

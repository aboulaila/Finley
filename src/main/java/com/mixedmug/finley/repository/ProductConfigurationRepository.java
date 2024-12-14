package com.mixedmug.finley.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.mixedmug.finley.model.ProductConfiguration;
import reactor.core.publisher.Mono;

public interface ProductConfigurationRepository extends ReactiveCrudRepository<ProductConfiguration, Long> {
    Mono<ProductConfiguration> findByProductCategory(String productCategory);
}
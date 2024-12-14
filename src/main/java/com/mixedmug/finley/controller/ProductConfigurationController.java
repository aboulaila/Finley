package com.mixedmug.finley.controller;

import com.mixedmug.finley.dto.ProductConfigurationDTO;
import com.mixedmug.finley.service.ProductConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/product-configurations")
public class ProductConfigurationController {

    private final ProductConfigurationService productConfigurationService;

    @Autowired
    public ProductConfigurationController(ProductConfigurationService productConfigurationService) {
        this.productConfigurationService = productConfigurationService;
    }

    @GetMapping
    public Flux<ProductConfigurationDTO> getAllConfigurations() {
        return productConfigurationService.getAllConfigurations();
    }

    @GetMapping("/{id}")
    public Mono<ProductConfigurationDTO> getConfigurationById(@PathVariable Long id) {
        return productConfigurationService.getConfigurationById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductConfigurationDTO> createConfiguration(@RequestBody ProductConfigurationDTO productConfigurationDTO) {
        return productConfigurationService.createConfiguration(productConfigurationDTO);
    }

    @PutMapping("/{id}")
    public Mono<ProductConfigurationDTO> updateConfiguration(@PathVariable Long id, @RequestBody ProductConfigurationDTO productConfigurationDTO) {
        return productConfigurationService.updateConfiguration(id, productConfigurationDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteConfiguration(@PathVariable Long id) {
        return productConfigurationService.deleteConfiguration(id);
    }
}
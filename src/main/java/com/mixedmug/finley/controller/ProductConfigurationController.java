package com.mixedmug.finley.controller;

import com.mixedmug.finley.dto.ProductConfigurationDTO;
import com.mixedmug.finley.service.ProductConfigurationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/product-configurations")
public class ProductConfigurationController {

    private final ProductConfigurationService productConfigurationService;

    public ProductConfigurationController(ProductConfigurationService productConfigurationService) {
        this.productConfigurationService = productConfigurationService;
    }

    @GetMapping
    public Flux<ProductConfigurationDTO> getAll() {
        return productConfigurationService.getAllConfigurations();
    }

    @GetMapping("/{id}")
    public Mono<ProductConfigurationDTO> findById(@PathVariable Long id) {
        return productConfigurationService.getConfigurationById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductConfigurationDTO> save(@RequestBody ProductConfigurationDTO productConfigurationDTO) {
        return productConfigurationService.createConfiguration(productConfigurationDTO);
    }

    @PutMapping("/{id}")
    public Mono<ProductConfigurationDTO> updateConfiguration(@PathVariable Long id, @RequestBody ProductConfigurationDTO productConfigurationDTO) {
        return productConfigurationService.updateConfiguration(id, productConfigurationDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return productConfigurationService.deleteConfiguration(id);
    }
}

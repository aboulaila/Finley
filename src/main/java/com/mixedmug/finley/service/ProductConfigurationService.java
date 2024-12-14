package com.mixedmug.finley.service;

import com.mixedmug.finley.dto.ProductConfigurationDTO;
import com.mixedmug.finley.model.ProductConfiguration;
import com.mixedmug.finley.repository.ProductConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Service
public class ProductConfigurationService {

    private final ProductConfigurationRepository productConfigurationRepository;

    @Autowired
    public ProductConfigurationService(ProductConfigurationRepository productConfigurationRepository) {
        this.productConfigurationRepository = productConfigurationRepository;
    }

    public Mono<ProductConfiguration> getConfigurationByCategory(String productCategory) {
        return productConfigurationRepository.findByProductCategory(productCategory);
    }

    /**
     * Retrieve all ProductConfigurations.
     *
     * @return A Flux emitting ProductConfigurationDTOs.
     */
    public Flux<ProductConfigurationDTO> getAllConfigurations() {
        return productConfigurationRepository.findAll()
                .map(this::mapToDTO);
    }

    /**
     * Retrieve a ProductConfiguration by ID.
     *
     * @param id The ID of the ProductConfiguration.
     * @return A Mono emitting the ProductConfigurationDTO.
     */
    public Mono<ProductConfigurationDTO> getConfigurationById(Long id) {
        return productConfigurationRepository.findById(id)
                .map(this::mapToDTO)
                .switchIfEmpty(Mono.error(new NoSuchElementException("ProductConfiguration not found with id: " + id)));
    }

    public Mono<ProductConfiguration> getById(Long id) {
        return productConfigurationRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("ProductConfiguration not found with id: " + id)));
    }

    /**
     * Create a new ProductConfiguration.
     *
     * @param productConfigurationDTO The DTO containing ProductConfiguration data.
     * @return A Mono emitting the created ProductConfigurationDTO.
     */
    public Mono<ProductConfigurationDTO> createConfiguration(ProductConfigurationDTO productConfigurationDTO) {
        ProductConfiguration config = mapToEntity(productConfigurationDTO);
        return productConfigurationRepository.save(config)
                .map(this::mapToDTO);
    }

    /**
     * Update an existing ProductConfiguration.
     *
     * @param id The ID of the ProductConfiguration to update.
     * @param productConfigurationDTO The DTO containing updated data.
     * @return A Mono emitting the updated ProductConfigurationDTO.
     */
    public Mono<ProductConfigurationDTO> updateConfiguration(Long id, ProductConfigurationDTO productConfigurationDTO) {
        return productConfigurationRepository.findById(id)
                .flatMap(existingConfig -> {
                    existingConfig.setProductCategory(productConfigurationDTO.getProductCategory());
                    existingConfig.setProductDescription(productConfigurationDTO.getProductDescription());
                    existingConfig.setInformationGatheringList(productConfigurationDTO.getInformationGatheringList());
                    return productConfigurationRepository.save(existingConfig);
                })
                .map(this::mapToDTO)
                .switchIfEmpty(Mono.error(new NoSuchElementException("ProductConfiguration not found with id: " + id)));
    }

    /**
     * Delete a ProductConfiguration by ID.
     *
     * @param id The ID of the ProductConfiguration to delete.
     * @return A Mono signaling completion.
     */
    public Mono<Void> deleteConfiguration(Long id) {
        return productConfigurationRepository.findById(id)
                .flatMap(existingConfig -> productConfigurationRepository.delete(existingConfig))
                .switchIfEmpty(Mono.error(new NoSuchElementException("ProductConfiguration not found with id: " + id)));
    }

    /**
     * Map ProductConfiguration entity to DTO.
     *
     * @param config The ProductConfiguration entity.
     * @return The corresponding DTO.
     */
    private ProductConfigurationDTO mapToDTO(ProductConfiguration config) {
        return new ProductConfigurationDTO(
                config.getId(),
                config.getProductCategory(),
                config.getProductDescription(),
                config.getInformationGatheringList()
        );
    }

    /**
     * Map ProductConfigurationDTO to entity.
     *
     * @param dto The ProductConfigurationDTO.
     * @return The corresponding entity.
     */
    private ProductConfiguration mapToEntity(ProductConfigurationDTO dto) {
        return new ProductConfiguration(
                dto.getId(),
                dto.getProductCategory(),
                dto.getProductDescription(),
                dto.getInformationGatheringList()
        );
    }
}
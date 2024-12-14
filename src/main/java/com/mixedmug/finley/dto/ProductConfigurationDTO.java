package com.mixedmug.finley.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "ProductConfigurationDTO")
public class ProductConfigurationDTO {

    private Long id;

    @Schema(description = "Product category is mandatory")
    private String productCategory;

    @Schema(description = "Product description is mandatory")
    private String productDescription;

    @Schema(description = "Information gathering list is mandatory")
    private String informationGatheringList;
}
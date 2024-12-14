package com.mixedmug.finley.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("product_configurations")
public class ProductConfiguration {

    @Id
    private Long id;

    private String productCategory;

    private String productDescription;

    private String informationGatheringList;
}
package com.example.spring_data_jpa_tutorial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductSummaryDTO {
    private Long id;
    private String name;
    private Double price;
    private String categoryName;
}
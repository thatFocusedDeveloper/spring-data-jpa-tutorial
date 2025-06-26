package com.example.spring_data_jpa_tutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
@Schema(description = "Product entity representing items for sale")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the product", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Name of the product", example = "Smartphone", required = true)
    private String name;

    @Column(nullable = false)
    @Schema(description = "Price of the product", example = "699.99", required = true)
    private Double price;

    @Schema(description = "Description of the product", example = "Latest model smartphone")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties("products")
    @Schema(description = "Category to which the product belongs")
    private Category category;

    // Constructors
    public Product() {
    }

    // Updated constructor to include category
    public Product(String name, Double price, String description, Category category) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category; // This line was missing
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", category=" + (category != null ? category.getName() : "null") + // Avoid fetching full category for toString
                '}';
    }
}
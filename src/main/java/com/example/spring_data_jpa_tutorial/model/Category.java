package com.example.spring_data_jpa_tutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Schema(description = "Category entity for grouping products")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the category", example = "1")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Name of the category", example = "Electronics", required = true)
    private String name;

    @Column(length = 500)
    @Schema(description = "Description of the category", example = "Electronic devices and accessories")
    private String description;

    // One-to-Many relationship with Product
    // mappedBy points to the 'category' field in the Product entity
    // cascade = CascadeType.ALL means if a category is deleted, its products are also deleted (optional, be careful)
    // orphanRemoval = true means if a product is removed from this set, it's deleted from DB (optional, be careful)
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("category")
    @Schema(description = "Products belonging to this category")
    private Set<Product> products = new HashSet<>(); // Using Set to ensure uniqueness and avoid duplicates

    // Constructors
    public Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Helper methods to manage bidirectional relationship (important!)
    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this); // Set the category on the product side
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null); // Remove the category reference
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

}

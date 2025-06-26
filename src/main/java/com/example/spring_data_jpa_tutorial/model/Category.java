package com.example.spring_data_jpa_tutorial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500) // Optional: Set max length for description
    private String description;

    // One-to-Many relationship with Product
    // mappedBy points to the 'category' field in the Product entity
    // cascade = CascadeType.ALL means if a category is deleted, its products are also deleted (optional, be careful)
    // orphanRemoval = true means if a product is removed from this set, it's deleted from DB (optional, be careful)
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("category")
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

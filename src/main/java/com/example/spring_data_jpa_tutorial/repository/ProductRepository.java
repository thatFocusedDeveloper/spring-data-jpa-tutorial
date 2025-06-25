package com.example.spring_data_jpa_tutorial.repository;

import com.example.spring_data_jpa_tutorial.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JpaRepository<EntityClass, PrimaryKeyType>
    // All standard CRUD, pagination, and sorting methods are inherited.
    // We'll add custom query methods here later.

    // Derived Query Methods:

    // Find products by name
    List<Product> findByName(String name);

    // Find products by price greater than a given value
    List<Product> findByPriceGreaterThan(Double price);

    // Find products by name and price
    Optional<Product> findByNameAndPrice(String name, Double price);

    // Find products by name containing a given string (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // Find products by price between two values
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
}

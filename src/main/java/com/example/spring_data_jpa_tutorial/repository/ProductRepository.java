package com.example.spring_data_jpa_tutorial.repository;

import com.example.spring_data_jpa_tutorial.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // --- Derived Query Methods (as before) ---
    List<Product> findByName(String name);
    List<Product> findByPriceGreaterThan(Double price);
    Optional<Product> findByNameAndPrice(String name, Double price);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // --- New Derived Query Methods involving Join ---

    // Find products by category name
    // Spring Data JPA intelligently creates a join based on the relationship
    List<Product> findByCategoryName(String categoryName);

    // Find products by category ID
    List<Product> findByCategoryId(Long categoryId);

    // --- Custom Queries using @Query Annotation (JPQL) ---
    // JPQL (Java Persistence Query Language) operates on entities and their fields, not table names and columns.

    // Get products with price less than X and specific category
    @Query("SELECT p FROM Product p WHERE p.price < ?1 AND p.category.name = ?2")
    List<Product> findProductsWithPriceLessThanAndCategory(Double price, String categoryName);

    // Get product name and category name for all products (projection)
    // Returns List of Object arrays, or a custom DTO
    @Query("SELECT p.name, p.price, c.name FROM Product p JOIN p.category c WHERE c.name = ?1")
    List<Object[]> findProductDetailsByCategoryName(String categoryName);

    // More complex query: Find products by part of name and category description
    @Query("SELECT p FROM Product p JOIN p.category c WHERE p.name LIKE %?1% AND c.description LIKE %?2%")
    List<Product> findProductsByNamePartAndCategoryDescription(String productNamePart, String categoryDescPart);

    // Using Named Parameters (more readable than positional parameters like ?1, ?2)
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.category.id = :categoryId")
    List<Product> findProductsInPriceRangeAndCategory(@org.springframework.data.repository.query.Param("minPrice") Double minPrice,
                                                      @org.springframework.data.repository.query.Param("maxPrice") Double maxPrice,
                                                      @org.springframework.data.repository.query.Param("categoryId") Long categoryId);

    // Query to fetch Product and its Category in a single query (Eager fetching for specific query)
    // Using JOIN FETCH to explicitly fetch the associated category immediately
    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = ?1")
    Optional<Product> findByIdWithCategory(Long productId);

    // Native SQL Query Example (use if JPQL is too restrictive or for database-specific features)
    @Query(value = "SELECT p.* FROM products p JOIN category c ON p.category_id = c.id WHERE c.name = ?1", 
           nativeQuery = true)
    List<Product> findProductsByCategoryNameNative(String categoryName);

    // Find products by category name with eager loading
    @Query("SELECT p FROM Product p JOIN FETCH p.category c WHERE c.name = ?1")
    List<Product> findByCategoryNameWithCategory(String categoryName);

    // Native SQL Query with explicit column selection returning Object[]
    @Query(value = "SELECT p.id, p.name, p.price, p.description, p.category_id " +
                   "FROM products p JOIN category c ON p.category_id = c.id WHERE c.name = ?1", 
           nativeQuery = true)
    List<Object[]>  findProductsByCategoryNameNativeAsArray(String categoryName);

    // Native SQL Query returning DTO
    @Query(value = "SELECT p.id, p.name, p.price, c.name as category_name " +
                   "FROM products p JOIN category c ON p.category_id = c.id WHERE c.name = ?1", 
           nativeQuery = true)
    List<Object[]> findProductSummaryByCategoryName(String categoryName);
}

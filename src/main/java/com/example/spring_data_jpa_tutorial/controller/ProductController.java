package com.example.spring_data_jpa_tutorial.controller;

import com.example.spring_data_jpa_tutorial.model.Category;
import com.example.spring_data_jpa_tutorial.model.Product;
import com.example.spring_data_jpa_tutorial.repository.CategoryRepository;
import com.example.spring_data_jpa_tutorial.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController // Marks this class as a REST controller
@RequestMapping("/api") // Base URL for all endpoints in this controller
public class ProductController {

    @Autowired // Injects an instance of ProductRepository
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // --- Category Endpoints ---

    // CREATE a new category
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category savedCategory = categoryRepository.save(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    // READ all categories
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // READ category by ID
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // --- Product Endpoints (updated to include category) ---

    // CREATE a new product with category
    @PostMapping("/products")
    public ResponseEntity<Object> createProduct(@RequestBody Product product) {
        try {
            // Check if product with same name already exists
            if (productRepository.findByName(product.getName()).size() > 0) {
                return new ResponseEntity<>(
                    Map.of("error", "Product with name '" + product.getName() + "' already exists"),
                    HttpStatus.CONFLICT
                );
            }
            
            // Ensure the category exists before saving the product
            if (product.getCategory() == null || product.getCategory().getId() == null) {
                return new ResponseEntity<>(
                    Map.of("error", "Category must be provided with ID"),
                    HttpStatus.BAD_REQUEST
                );
            }
            
            Optional<Category> existingCategory = categoryRepository.findById(product.getCategory().getId());
            if (existingCategory.isEmpty()) {
                return new ResponseEntity<>(
                    Map.of("error", "Category with ID " + product.getCategory().getId() + " does not exist"),
                    HttpStatus.BAD_REQUEST
                );
            }
            
            // Link to the managed category entity
            product.setCategory(existingCategory.get());
            
            Product savedProduct = productRepository.save(product);
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(
                Map.of("error", "Failed to create product: " + e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // READ all products (existing method)
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // READ product by ID (existing method)
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        // Using findByIdWithCategory to eager load category
        Optional<Product> product = productRepository.findByIdWithCategory(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // --- New Endpoints for Multi-Table Joins & Custom Queries ---

    // Find products by category name (Derived Query with eager loading)
    @GetMapping("/products/by-category-name")
    public List<Product> getProductsByCategoryName(@RequestParam String categoryName) {
        return productRepository.findByCategoryNameWithCategory(categoryName);
    }

    // Find products by category ID (Derived Query)
    @GetMapping("/products/by-category-id/{categoryId}")
    public List<Product> getProductsByCategoryId(@PathVariable Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    // Get products with price less than X and specific category (JPQL Custom Query)
    @GetMapping("/products/filter-by-price-and-category")
    public List<Product> findProductsByPriceAndCategory(@RequestParam Double price, @RequestParam String categoryName) {
        return productRepository.findProductsWithPriceLessThanAndCategory(price, categoryName);
    }

    // Get product name and category name for all products (Projection JPQL Custom Query)
    @GetMapping("/products/details-by-category-name")
    public List<Object[]> findProductDetailsByCategory(@RequestParam String categoryName) {
        return productRepository.findProductDetailsByCategoryName(categoryName);
    }

    // Get products by name part and category description part (JPQL Custom Query)
    @GetMapping("/products/search")
    public List<Product> searchProducts(
            @RequestParam String productNamePart,
            @RequestParam String categoryDescPart) {
        return productRepository.findProductsByNamePartAndCategoryDescription(productNamePart, categoryDescPart);
    }

    // Find products in price range and specific category (JPQL with Named Params)
    @GetMapping("/products/price-range-and-category")
    public List<Product> findProductsInPriceRangeAndCategory(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam Long categoryId) {
        return productRepository.findProductsInPriceRangeAndCategory(minPrice, maxPrice, categoryId);
    }

    // Find products by category name (Native Query)
    @GetMapping("/products/by-category-name-native")
    public List<Product> getProductsByCategoryNameNative(@RequestParam String categoryName) {
        return productRepository.findProductsByCategoryNameNative(categoryName);
    }

    // ... (Keep existing updateProduct and deleteProduct if desired) ...
    // UPDATE an existing product
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();
            existingProduct.setName(productDetails.getName());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setDescription(productDetails.getDescription());

            // Handle category update if provided
            if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
                Optional<Category> newCategory = categoryRepository.findById(productDetails.getCategory().getId());
                if (newCategory.isPresent()) {
                    existingProduct.setCategory(newCategory.get());
                } else {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // New category does not exist
                }
            }

            Product updatedProduct = productRepository.save(existingProduct);
            return ResponseEntity.ok(updatedProduct);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE a product
    @DeleteMapping("/products/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
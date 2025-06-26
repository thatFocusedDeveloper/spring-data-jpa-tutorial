package com.example.spring_data_jpa_tutorial.controller;

import com.example.spring_data_jpa_tutorial.dto.ProductSummaryDTO;
import com.example.spring_data_jpa_tutorial.model.Category;
import com.example.spring_data_jpa_tutorial.model.Product;
import com.example.spring_data_jpa_tutorial.repository.CategoryRepository;
import com.example.spring_data_jpa_tutorial.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Tag(name = "Product Management", description = "APIs for managing products and categories")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // --- Category Endpoints ---

    @Operation(summary = "Create a new category", description = "Creates a new category with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully", 
                    content = @Content(schema = @Schema(implementation = Category.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category savedCategory = categoryRepository.save(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all categories", description = "Returns a list of all categories")
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Operation(summary = "Get category by ID", description = "Returns a category based on its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(
            @Parameter(description = "ID of the category to retrieve") @PathVariable Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // --- Product Endpoints ---

    @Operation(summary = "Create a new product", description = "Creates a new product with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Product with same name already exists")
    })
    @PostMapping("/products")
    public ResponseEntity<Object> createProduct(@RequestBody Product product) {
        try {
            // Existing implementation...
            if (productRepository.findByName(product.getName()).size() > 0) {
                return new ResponseEntity<>(
                    Map.of("error", "Product with name '" + product.getName() + "' already exists"),
                    HttpStatus.CONFLICT
                );
            }
            
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

    @Operation(summary = "Get all products", description = "Returns a list of all products")
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Operation(summary = "Get product by ID", description = "Returns a product based on its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "ID of the product to retrieve") @PathVariable Long id) {
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
    public List<ProductSummaryDTO> getProductsByCategoryNameNative(@RequestParam String categoryName) {
        List<Object[]> results = productRepository.findProductSummaryByCategoryName(categoryName);
        return results.stream()
                .map(result -> new ProductSummaryDTO(
                        ((Number) result[0]).longValue(),
                        (String) result[1],
                        ((Number) result[2]).doubleValue(),
                        (String) result[3]))
                .collect(Collectors.toList());
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
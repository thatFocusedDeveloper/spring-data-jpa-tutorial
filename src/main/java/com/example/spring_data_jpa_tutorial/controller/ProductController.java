package com.example.spring_data_jpa_tutorial.controller;

import com.example.spring_data_jpa_tutorial.model.Product;
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
import java.util.Optional;

@RestController // Marks this class as a REST controller
@RequestMapping("/api/products") // Base URL for all endpoints in this controller
public class ProductController {

    @Autowired // Injects an instance of ProductRepository
    private ProductRepository productRepository;

    // CREATE a new product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productRepository.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // READ all products
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/paginated")
    public Page<Product> getAllProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort // Example: "name,desc"
    ) {
        Sort sortOrder = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sortOrder);
        return productRepository.findAll(pageable);
    }

    // READ product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(ResponseEntity::ok) // If product found, return 200 OK
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Else, return 404 Not Found
    }

    // READ products by name (using custom query method)
    @GetMapping("/by-name")
    public List<Product> getProductsByName(@RequestParam String name) {
        return productRepository.findByName(name);
    }

    // READ products by price greater than (using custom query method)
    @GetMapping("/price-gt/{price}")
    public List<Product> getProductsByPriceGreaterThan(@PathVariable Double price) {
        return productRepository.findByPriceGreaterThan(price);
    }

    // UPDATE an existing product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isPresent()) {
            Product existingProduct = optionalProduct.get();
            existingProduct.setName(productDetails.getName());
            existingProduct.setPrice(productDetails.getPrice());
            existingProduct.setDescription(productDetails.getDescription());

            Product updatedProduct = productRepository.save(existingProduct); // save() updates if ID exists
            return ResponseEntity.ok(updatedProduct);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE a product
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
package com.example.spring_data_jpa_tutorial.config;

import com.example.spring_data_jpa_tutorial.model.Category;
import com.example.spring_data_jpa_tutorial.model.Product;
import com.example.spring_data_jpa_tutorial.repository.CategoryRepository;
import com.example.spring_data_jpa_tutorial.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(CategoryRepository categoryRepository, ProductRepository productRepository) {
        return args -> {
            // Create categories
            Category electronics = new Category("Electronics", "Electronic devices and accessories");
            Category books = new Category("Books", "Physical and digital books");
            
            categoryRepository.save(electronics);
            categoryRepository.save(books);
            
            // Create products
            Product smartphone = new Product("Smartphone", 699.99, "Latest model smartphone", electronics);
            Product laptop = new Product("Laptop", 1299.99, "High-performance laptop", electronics);
            Product javaBook = new Product("Java Programming", 49.99, "Complete guide to Java", books);
            
            productRepository.save(smartphone);
            productRepository.save(laptop);
            productRepository.save(javaBook);
        };
    }
}
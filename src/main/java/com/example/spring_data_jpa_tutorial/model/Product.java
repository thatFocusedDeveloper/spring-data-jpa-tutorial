package com.example.spring_data_jpa_tutorial.model;
import jakarta.persistence.*; // Using Jakarta Persistence API (JPA) for Spring Boot 3+
import lombok.Data;

@Entity // Marks this class as a JPA entity, meaning it maps to a database table
@Table(name = "products")// Specifies the name of the database table (optional, defaults to class name)
@Data // Lombok annotation to automatically generate getters, setters, equals, hashCode, and toString methods
public class Product {

    @Id // Marks this field as the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures the primary key generation strategy
    private Long id;

    @Column(nullable = false, unique = true) // Maps to a database column; cannot be null, must be unique
    private String name;

    @Column(nullable = false)
    private Double price;

    private String description; // No @Column means default mapping (column name same as field name)

    // Constructors
    public Product() {
    }

    public Product(String name, Double price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }
}
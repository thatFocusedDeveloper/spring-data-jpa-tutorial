package com.example.spring_data_jpa_tutorial.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*; // Using Jakarta Persistence API (JPA) for Spring Boot 3+
import lombok.Data;

@Entity // Marks this class as a JPA entity, meaning it maps to a database table
@Table(name = "products")// Specifies the name of the database table (optional, defaults to class name)
@Data // Lombok annotation to automatically generate getters, setters, equals, hashCode, and toString methods
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Double price;

    private String description;

    // Many-to-One relationship with Category
    // Many products can belong to one category
    @ManyToOne(fetch = FetchType.LAZY) // Many products to one category
    @JoinColumn(name = "category_id", nullable = false) // Specifies the foreign key column in the products table
    @JsonIgnoreProperties("products")
    private Category category; // This field holds the associated Category object

    // Constructors
    public Product() {
    }

    // Updated constructor to include category
    public Product(String name, Double price, String description, Category category) {
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
                ", category=" + (category != null ? category.getName() : "null") + // Avoid fetching full category for toString
                '}';
    }
}
package com.example.productcatalog.service;

import org.springframework.stereotype.Service;
import com.example.productcatalog.model.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductService - Business Logic Layer
 * 
 * CONCEPT: @Service Annotation
 * 
 * @Service is a specialized version of @Component that indicates the class
 * contains business logic. It's a semantic annotation that tells Spring:
 * 
 * 1. This class is a Spring-managed component (bean)
 * 2. This class contains business logic
 * 3. This class should be treated as a service in the layered architecture
 * 
 * HOW SPRING MANAGES THIS CLASS:
 * 
 * When @SpringBootApplication scans the package:
 * 1. It finds ProductService with @Service annotation
 * 2. Creates a single instance (Singleton scope - DEFAULT)
 * 3. Stores it in Application Context (Container)
 * 4. When ProductController requests ProductService via constructor,
 *    Spring provides this managed instance
 * 
 * WHY SERVICE LAYER?
 * - Separation of Concerns: Service handles business logic
 * - Reusability: Multiple controllers can use same service
 * - Testability: Easy to test business logic independently
 * - Maintainability: Changes to logic don't affect controllers
 * 
 * SINGLETON SCOPE:
 * - Only ONE instance of ProductService is created
 * - Same instance shared across entire application
 * - Thread-safe if service is stateless (which this is)
 * - More efficient memory usage
 */
@Service
public class ProductService {
    
    /**
     * Retrieves all products from the catalog
     * 
     * In a real application, this method would:
     * - Query database via repository layer
     * - Fetch from external API
     * - Read from file storage
     * 
     * For this assignment, we return hardcoded sample data.
     * 
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        
        // Sample data - In real app, this comes from database
        products.add(new Product(1L, "Laptop", "High-performance laptop with 16GB RAM", 999.99, 10));
        products.add(new Product(2L, "Mouse", "Wireless ergonomic mouse", 29.99, 50));
        products.add(new Product(3L, "Keyboard", "Mechanical keyboard with RGB lighting", 89.99, 25));
        products.add(new Product(4L, "Monitor", "27-inch 4K display", 349.99, 15));
        products.add(new Product(5L, "Headphones", "Noise-cancelling Bluetooth headphones", 199.99, 30));
        
        return products;
    }
    
}

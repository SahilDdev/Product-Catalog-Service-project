package com.example.productcatalog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.productcatalog.model.Product;
import com.example.productcatalog.service.ProductService;
import java.util.List;

/**
 * ProductController - HTTP Request Handler (REST API Layer)
 * 
 * CONCEPT: @RestController Annotation
 * 
 * @RestController is a combination of:
 * 1. @Controller - Marks class as controller (handles HTTP requests)
 * 2. @ResponseBody - Automatically converts return values to JSON
 * 
 * WHY USE @RestController?
 * - Handles incoming HTTP requests
 * - Routes requests to appropriate methods
 * - Converts Java objects to JSON response
 * - Builds RESTful APIs
 * 
 * ============================================================================
 * DEPENDENCY INJECTION IN ACTION - CONSTRUCTOR INJECTION (RECOMMENDED)
 * ============================================================================
 * 
 * Compare these two approaches:
 * 
 * ❌ WRONG WAY (Tight Coupling):
 * ────────────────────────────
 * @RestController
 * public class ProductController {
 *     private ProductService service = new ProductService();  // ❌ Manual creation
 * }
 * 
 * Problems with manual creation:
 * - Tight coupling: Controller depends directly on ServiceImplementation
 * - Hard to test: Can't inject mock service for testing
 * - Hard to swap: If service changes, controller breaks
 * - Violates Dependency Inversion Principle
 * 
 * 
 * ✅ RIGHT WAY (Loose Coupling - What we're doing):
 * ─────────────────────────────────────────────────
 * @RestController
 * public class ProductController {
 *     private final ProductService service;
 *     
 *     public ProductController(ProductService service) {  // ✅ Constructor injection
 *         this.service = service;
 *     }
 * }
 * 
 * Benefits of constructor injection:
 * - Loose coupling: Controller doesn't create service
 * - Easy testing: Pass mock service in tests
 * - Immutability: "final" field prevents accidental changes
 * - Thread safety: Immutable dependencies are thread-safe
 * - Clear dependencies: Constructor shows what's needed
 * - Better design: Follows SOLID principles
 * 
 * 
 * HOW SPRING PROVIDES THE DEPENDENCY:
 * ─────────────────────────────────────
 * 1. Application starts (@SpringBootApplication)
 * 2. Spring scans packages and finds:
 *    - ProductService with @Service
 *    - ProductController with @RestController
 * 3. Creates beans:
 *    - Creates ProductService instance (Singleton)
 *    - Stores in Application Context (Spring Container)
 * 4. Dependency Resolution:
 *    - ProductController needs ProductService in constructor
 *    - Spring checks: Do we have ProductService bean? YES!
 *    - Spring injects: Provides the bean to constructor
 * 5. Result:
 *    - ProductController gets fully initialized with service
 *    - Controller ready to handle HTTP requests
 * 
 * 
 * APPLICATION CONTEXT (IoC CONTAINER):
 * ────────────────────────────────────
 * The Application Context is Spring's central bean container:
 * 
 * ┌─────────────────────────────────────┐
 * │      Application Context (IoC)      │
 * │  ┌──────────────────────────────┐   │
 * │  │ ProductService Instance      │   │
 * │  │ (Managed by Spring)          │   │
 * │  └──────────────────────────────┘   │
 * │  ┌──────────────────────────────┐   │
 * │  │ ProductController Instance   │   │
 * │  │ (with ProductService injected)   │
 * │  └──────────────────────────────┘   │
 * └─────────────────────────────────────┘
 */
@RestController
@RequestMapping("/products")
public class ProductController {
    
    // "final" keyword: Makes field immutable after constructor
    // This ensures the dependency never changes once set
    private final ProductService productService;
    
    /**
     * CONSTRUCTOR INJECTION
     * 
     * Spring automatically calls this constructor during initialization.
     * Spring detects ProductService as a dependency and injects it.
     * 
     * This is the ONLY place ProductService is provided to this controller.
     * We don't have to manually create it with "new ProductService()".
     * 
     * @param productService The ProductService bean provided by Spring
     */
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * GET /products
     * 
     * HTTP Mapping:
     * - @GetMapping: Maps HTTP GET requests to this method
     * - "/products" path: Combined with @RequestMapping("/products")
     * - Full URL: /products (or http://localhost:8080/products)
     * 
     * How it works:
     * 1. HTTP GET request arrives: GET /products
     * 2. Spring routes to this method
     * 3. Method calls productService.getAllProducts()
     * 4. Service returns List<Product>
     * 5. @RestController converts to JSON
     * 6. JSON sent as HTTP response
     * 
     * @return List of all products in JSON format
     */
    @GetMapping
    public List<Product> getAllProducts() {
        System.out.println("✓ GET /products endpoint called");
        System.out.println("✓ ProductService is: " + (productService != null ? "INJECTED" : "NULL"));
        System.out.println("✓ Service class: " + productService.getClass().getName());
        
        // Call injected service to get products
        List<Product> products = productService.getAllProducts();
        
        System.out.println("✓ Returning " + products.size() + " products");
        return products;
    }
    
}

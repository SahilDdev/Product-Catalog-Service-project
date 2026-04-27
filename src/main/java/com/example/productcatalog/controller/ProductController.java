package com.example.productcatalog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.productcatalog.model.Product;
import com.example.productcatalog.service.ProductService;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
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

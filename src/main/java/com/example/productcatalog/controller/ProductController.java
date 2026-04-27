package com.example.productcatalog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.example.productcatalog.model.Product;
import com.example.productcatalog.dto.ProductDTO;
import com.example.productcatalog.service.ProductService;
import com.example.productcatalog.response.SuccessResponse;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<Product>>> getAllProducts() {
        System.out.println("\n✓ GET /products endpoint called");
        
        List<Product> products = productService.getAllProducts();
        
        System.out.println("✓ Returning " + products.size() + " products");
        
        SuccessResponse<List<Product>> response = new SuccessResponse<List<Product>>(
            true,
            "Products retrieved successfully",
            products
        );
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<Product>> getProductById(@PathVariable Long id) {
        System.out.println("\n✓ GET /products/" + id + " endpoint called");
        
        Product product = productService.getProductById(id);
        
        System.out.println("✓ Product found: " + product.getName());
        
        SuccessResponse<Product> response = new SuccessResponse<Product>(
            true,
            "Product retrieved successfully",
            product
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<SuccessResponse<Product>> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        System.out.println("\n✓ POST /products endpoint called");
        System.out.println("✓ Request body received: " + productDTO.getName());
        
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        
        Product createdProduct = productService.createProduct(product);
        
        System.out.println("✓ Product created with ID: " + createdProduct.getId());
        
        SuccessResponse<Product> response = new SuccessResponse<Product>(
            true,
            "Product created successfully",
            createdProduct
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

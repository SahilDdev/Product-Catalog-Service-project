package com.example.productcatalog.service;

import org.springframework.stereotype.Service;
import com.example.productcatalog.model.Product;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private List<Product> products = new ArrayList<>();

    private ProductService() {
        products.add(new Product(1L, "Laptop", "High-performance laptop with 16GB RAM", 999.99, 10));
        products.add(new Product(2L, "Mouse", "Wireless ergonomic mouse", 29.99, 50));
        products.add(new Product(3L, "Keyboard", "Mechanical keyboard with RGB lighting", 89.99, 25));
        products.add(new Product(4L, "Monitor", "27-inch 4K display", 349.99, 15));
        products.add(new Product(5L, "Headphones", "Noise-cancelling Bluetooth headphones", 199.99, 30));
    }
    
    public List<Product> getAllProducts() {
        return products;
    }

    public Product getProductById(Long id) {
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }      

    public Product createProduct(Product product) {
        product.setId((long) (products.size() + 1));
        products.add(product);
        return product;
    }
}

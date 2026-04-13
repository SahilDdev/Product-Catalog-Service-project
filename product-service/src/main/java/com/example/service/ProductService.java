package com.example.service;

import com.example.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductService {

    private List<Product> products = new ArrayList<>();

    public ProductService() {
        products.add(new Product(1L, "Laptop", 999.99));
        products.add(new Product(2L, "Smartphone", 499.50));
        products.add(new Product(3L, "Tablet", 299.00));
        products.add(new Product(4L, "Monitor", 199.99));
        products.add(new Product(5L, "Keyboard", 49.99));
    }

    public List<Product> getProductsAbovePrice(double price) {
        return Optional.ofNullable(products)
                .orElse(Collections.emptyList())
                .stream()
                .filter(product -> product != null && product.getPrice() > price)
                .collect(Collectors.toList());
    }

    public List<String> getAllProductNames() {
        return Optional.ofNullable(products)
                .orElse(Collections.emptyList())
                .stream()
                .filter(product -> product != null)
                .map(Product::getName)
                .collect(Collectors.toList());
    }
}

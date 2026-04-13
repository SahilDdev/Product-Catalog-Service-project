package com.example;

import com.example.model.Product;
import com.example.service.ProductService;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Product product = new Product(1L, "Laptop", 999.99);
        System.out.println("Product Name: " + product.getName());

        System.out.println("\n--- Demonstrating ProductService Filters ---");
        ProductService productService = new ProductService();

        System.out.println("\nAll Product Names using map():");
        List<String> productNames = productService.getAllProductNames();
        productNames.forEach(name -> System.out.println("- " + name));

        double thresholdPrice = 300.00;
        System.out.println("\nProducts above price $" + thresholdPrice + " using Streams:");
        List<Product> expensiveProducts = productService.getProductsAbovePrice(thresholdPrice);
        expensiveProducts.forEach(p -> 
            System.out.println("- " + p.getName() + " ($" + p.getPrice() + ")")
        );
    }
}

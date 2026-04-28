package com.example.productcatalog.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.productcatalog.model.Product;
import com.example.productcatalog.repository.ProductRepository;
import com.example.productcatalog.exception.ProductNotFoundException;
import java.util.List;


@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<Product> getAllProducts() {
        System.out.println("📦 Fetching all products from database...");
        List<Product> products = productRepository.findAll();
        System.out.println("✓ Found " + products.size() + " products");
        return products;
    }
    
    public Product getProductById(Long id) {
        System.out.println("🔍 Searching for product with ID: " + id);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        System.out.println("✓ Found product: " + product.getName());
        return product;
    }
    
    public Product createProduct(Product product) {
        System.out.println("💾 Saving product to database: " + product.getName());
        Product savedProduct = productRepository.save(product);
        System.out.println("✓ Product saved with ID: " + savedProduct.getId());
        return savedProduct;
    }
    
    public Product updateProduct(Long id, Product product) {
        System.out.println("✏️  Updating product with ID: " + id);
        
        // First check if product exists
        Product existingProduct = getProductById(id);
        
        // Update fields
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        
        // Save updated product
        Product updatedProduct = productRepository.save(existingProduct);
        System.out.println("✓ Product updated successfully");
        return updatedProduct;
    }
    
    public void deleteProduct(Long id) {
        System.out.println("🗑️  Deleting product with ID: " + id);
        
        // First verify product exists
        getProductById(id);
        
        // Delete product
        productRepository.deleteById(id);
        System.out.println("✓ Product deleted successfully");
    }
    
    public List<Product> searchByKeyword(String keyword) {
        System.out.println("🔎 Searching products with keyword: " + keyword);
        List<Product> results = productRepository.searchProductsByKeyword(keyword);
        System.out.println("✓ Found " + results.size() + " matching products");
        return results;
    }
    
    public List<Product> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        System.out.println("💰 Fetching products between $" + minPrice + " and $" + maxPrice);
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        System.out.println("✓ Found " + products.size() + " products in range");
        return products;
    }
     
    public List<Product> getProductsInStock() {
        System.out.println("📊 Fetching products in stock");
        List<Product> products = productRepository.findByQuantityGreaterThan(0);
        System.out.println("✓ Found " + products.size() + " products in stock");
        return products;
    }
}

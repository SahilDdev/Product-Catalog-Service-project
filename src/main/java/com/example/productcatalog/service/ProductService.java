package com.example.productcatalog.service;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import com.example.productcatalog.model.Product;
import com.example.productcatalog.model.Review;
import com.example.productcatalog.repository.ProductRepository;
import com.example.productcatalog.repository.ReviewRepository;
import com.example.productcatalog.exception.ProductNotFoundException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public ProductService(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Product createProductWithReviews(Product product, List<Review> reviews) {
        System.out.println("💾 Starting transaction: createProductWithReviews");

        // Step 1: Save the product FIRST
        Product savedProduct = productRepository.save(product);
        System.out.println("✓ Product saved with ID: " + savedProduct.getId());

        // Step 2: FAILURE CONDITION - Check AFTER product is saved
        // This is intentional: product is already in DB at this point
        // If reviews are empty, the exception should ROLLBACK the product save too
        if (reviews == null || reviews.isEmpty()) {
            System.out.println("✗ Reviews list is empty! Throwing exception...");
            throw new RuntimeException("Reviews list cannot be empty! At least one review is required.");
        }

        // Step 3: Save all reviews with product reference
        for (Review review : reviews) {
            review.setProduct(savedProduct);
            reviewRepository.save(review);
            System.out.println("✓ Review saved: " + review.getComment());
        }

        System.out.println("✓ Transaction complete: Product + " + reviews.size() + " reviews saved");
        return savedProduct;
    }

    public Page<Product> getAllProducts(int page, int size) {
        System.out.println("📦 Fetching products from database (page: " + page + ", size: " + size + ")...");

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productsPage = productRepository.findAll(pageable);
        System.out.println("✓ Found " + productsPage.getContent().size() + " products on this page");

        long totalProducts = productRepository.count();
        System.out.println("Total Products " + totalProducts);
        return productsPage;
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

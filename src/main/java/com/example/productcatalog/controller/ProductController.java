package com.example.productcatalog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import com.example.productcatalog.model.Product;
import com.example.productcatalog.model.Review;
import com.example.productcatalog.dto.ProductDTO;
import com.example.productcatalog.dto.ProductWithReviewsDTO;
import com.example.productcatalog.service.ProductService;
import com.example.productcatalog.response.SuccessResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

        private final ProductService productService;

        public ProductController(ProductService productService) {
                this.productService = productService;
        }

        @GetMapping
        public ResponseEntity<SuccessResponse<Page<Product>>> getAllProducts(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                System.out.println("\n✓ GET /products endpoint called (page: " + page + ", size: " + size + ")");

                Page<Product> productsPage = productService.getAllProducts(page, size);

                System.out.println("✓ Returning " + productsPage.getContent().size() + " products");

                SuccessResponse<Page<Product>> response = new SuccessResponse<>(
                                true,
                                "Products retrieved successfully",
                                productsPage);

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
                                product);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/search")
        public ResponseEntity<SuccessResponse<List<Product>>> searchProducts(
                        @RequestParam String keyword) {
                System.out.println("\n✓ GET /products/search?keyword=" + keyword + " endpoint called");

                List<Product> products = productService.searchByKeyword(keyword);

                SuccessResponse<List<Product>> response = new SuccessResponse<List<Product>>(
                                true,
                                "Search results for keyword: " + keyword,
                                products);

                return ResponseEntity.ok(response);
        }

        @GetMapping("/price-range")
        public ResponseEntity<SuccessResponse<List<Product>>> getProductsByPriceRange(
                        @RequestParam Double minPrice,
                        @RequestParam Double maxPrice) {
                System.out.println(
                                "\n✓ GET /products/price-range?minPrice=" + minPrice + "&maxPrice=" + maxPrice
                                                + " endpoint called");

                List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);

                SuccessResponse<List<Product>> response = new SuccessResponse<List<Product>>(
                                true,
                                "Found " + products.size() + " products in price range $" + minPrice + " - $"
                                                + maxPrice,
                                products);

                return ResponseEntity.ok(response);
        }

        @GetMapping("/in-stock")
        public ResponseEntity<SuccessResponse<List<Product>>> getProductsInStock() {
                System.out.println("\n✓ GET /products/in-stock endpoint called");

                List<Product> products = productService.getProductsInStock();

                SuccessResponse<List<Product>> response = new SuccessResponse<List<Product>>(
                                true,
                                "Products in stock: " + products.size(),
                                products);

                return ResponseEntity.ok(response);
        }

        @PostMapping
        public ResponseEntity<SuccessResponse<Product>> createProduct(@Valid @RequestBody ProductDTO productDTO) {
                System.out.println("\n✓ POST /products endpoint called");
                System.out.println("✓ Request body received: " + productDTO.getName().getClass());

                // Convert DTO to Entity
                Product product = new Product();
                product.setName(productDTO.getName());
                product.setDescription(productDTO.getDescription());
                product.setPrice(productDTO.getPrice());
                product.setQuantity(productDTO.getQuantity());

                // Save to database
                Product createdProduct = productService.createProduct(product);

                System.out.println("✓ Product created with ID: " + createdProduct.getId());

                SuccessResponse<Product> response = new SuccessResponse<Product>(
                                true,
                                "Product created successfully",
                                createdProduct);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/{id}")
        public ResponseEntity<SuccessResponse<Product>> updateProduct(
                        @PathVariable Long id,
                        @Valid @RequestBody ProductDTO productDTO) {
                System.out.println("\n✓ PUT /products/" + id + " endpoint called");
                System.out.println("✓ Updating product with new data: " + productDTO.getName());

                // Convert DTO to Entity
                Product product = new Product();
                product.setName(productDTO.getName());
                product.setDescription(productDTO.getDescription());
                product.setPrice(productDTO.getPrice());
                product.setQuantity(productDTO.getQuantity());

                // Update in database
                Product updatedProduct = productService.updateProduct(id, product);

                System.out.println("✓ Product updated successfully");

                SuccessResponse<Product> response = new SuccessResponse<Product>(
                                true,
                                "Product updated successfully",
                                updatedProduct);

                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<SuccessResponse<Void>> deleteProduct(@PathVariable Long id) {
                System.out.println("\n✓ DELETE /products/" + id + " endpoint called");

                // Delete from database
                productService.deleteProduct(id);

                System.out.println("✓ Product deleted successfully");

                SuccessResponse<Void> response = new SuccessResponse<Void>(
                                true,
                                "Product deleted successfully",
                                null);

                return ResponseEntity.ok(response);
        }

        @PostMapping("/with-reviews")
        public ResponseEntity<SuccessResponse<Product>> createProductWithReviews(
                        @RequestBody ProductWithReviewsDTO dto) {
                System.out.println("\n✓ POST /products/with-reviews endpoint called");

                // Convert DTO to Product entity
                Product product = new Product();
                product.setName(dto.getName());
                product.setDescription(dto.getDescription());
                product.setPrice(dto.getPrice());
                product.setQuantity(dto.getQuantity());

                // Convert review DTOs to Review entities
                List<Review> reviews = null;
                if (dto.getReviews() != null) {
                        reviews = dto.getReviews().stream()
                                        .map(r -> {
                                                Review review = new Review();
                                                review.setComment(r.getComment());
                                                return review;
                                        })
                                        .collect(Collectors.toList());
                }

                // Call transactional method
                Product createdProduct = productService.createProductWithReviews(product, reviews);

                System.out.println("✓ Product created with reviews, ID: " + createdProduct.getId());

                SuccessResponse<Product> response = new SuccessResponse<Product>(
                                true,
                                "Product created with reviews successfully",
                                createdProduct);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
}

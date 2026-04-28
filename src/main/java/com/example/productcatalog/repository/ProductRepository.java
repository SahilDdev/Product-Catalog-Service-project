package com.example.productcatalog.repository;

import com.example.productcatalog.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByNameIgnoreCase(String name);
    
    List<Product> findByNameContainingIgnoreCase(String keyword);
    
    List<Product> findByPriceGreaterThan(Double minPrice);
    
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    
    List<Product> findByQuantityGreaterThan(Integer minQuantity);
    
    @Query("SELECT p FROM Product p WHERE p.price > :minPrice ORDER BY p.price DESC")
    List<Product> findExpensiveProductsSortedByPrice(@Param("minPrice") Double minPrice);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.name ASC")
    List<Product> searchProductsByKeyword(@Param("keyword") String keyword);
    
    boolean existsByNameIgnoreCase(String name);
    
    long countByPriceGreaterThan(Double minPrice);
}

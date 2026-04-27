package com.example.productcatalog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product - Model Class (Data Entity)
 * 
 * This class represents a Product in the application.
 * The Lombok annotations reduce boilerplate code dramatically.
 * 
 * LOMBOK ANNOTATIONS EXPLAINED:
 * 
 * @Data
 * - Automatically generates:
 *   * Getters for all fields
 *   * Setters for all fields
 *   * toString() method
 *   * equals() method
 *   * hashCode() method
 * 
 * @AllArgsConstructor
 * - Generates constructor with all fields as parameters
 * - Example: public Product(Long id, String name, ...) {}
 * 
 * @NoArgsConstructor
 * - Generates no-argument constructor
 * - Required by many frameworks (Spring, JSON deserializers)
 * - Example: public Product() {}
 * 
 * WHY USE LOMBOK?
 * Without Lombok: 50+ lines of getters, setters, constructors
 * With Lombok: Just 6 lines of code
 * 
 * BENEFIT FOR SPRING:
 * Spring uses reflection and requires these methods for:
 * - JSON serialization/deserialization
 * - Object instantiation
 * - Property access
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
}

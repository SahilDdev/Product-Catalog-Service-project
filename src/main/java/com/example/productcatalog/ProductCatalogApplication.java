package com.example.productcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ProductCatalogApplication - Main Entry Point
 * 
 * CONCEPT: @SpringBootApplication
 * 
 * This annotation enables:
 * 1. Component Scanning (@ComponentScan)
 *    - Automatically finds and registers @Component, @Service, @RestController, etc.
 *    - Scans from current package and sub-packages
 * 
 * 2. Auto-Configuration (@EnableAutoConfiguration)
 *    - Spring Boot automatically configures your application based on dependencies
 *    - Detects spring-boot-starter-web and configures:
 *      * Embedded Tomcat servlet container
 *      * Spring MVC dispatcher servlet
 *      * Jackson JSON serialization
 * 
 * 3. Configuration Support (@Configuration)
 *    - Allows Java-based configuration
 *    - Processes application.properties
 * 
 * When SpringApplication.run() is called:
 * - Application Context is created
 * - All beans are instantiated and registered
 * - Dependency injection happens automatically
 * - Server starts on configured port (default: 8080)
 */
@SpringBootApplication
public class ProductCatalogApplication {

    public static void main(String[] args) {
        // SpringApplication.run() method starts the Spring Boot application
        SpringApplication.run(ProductCatalogApplication.class, args);
    }

}

# 📚 Day 3: Spring Boot Core - Complete Explanation Guide

## Table of Contents
1. [What is Spring Boot](#what-is-spring-boot)
2. [Project Structure](#project-structure)
3. [Maven & Dependencies](#maven--dependencies)
4. [Inversion of Control (IoC)](#inversion-of-control-ioc)
5. [Dependency Injection (DI)](#dependency-injection-di)
6. [Spring Beans & Application Context](#spring-beans--application-context)
7. [Core Annotations](#core-annotations)
8. [Layered Architecture](#layered-architecture)
9. [How Everything Works Together](#how-everything-works-together)
10. [Running the Application](#running-the-application)

---

## What is Spring Boot

Spring Boot is a framework built on top of Spring that simplifies Java application development by:

### Key Features:

| Feature | Benefit | Example |
|---------|---------|---------|
| **Auto-Configuration** | Automatically configures your app based on dependencies | Detects `spring-boot-starter-web` and sets up embedded Tomcat |
| **Embedded Server** | No need to deploy to external servlet container | Tomcat runs inside the app |
| **Starter Dependencies** | Simplified dependency management | `spring-boot-starter-web` includes Spring MVC, Tomcat, etc. |
| **Production Ready** | Built-in features like metrics, health checks | Easy to deploy to production |
| **Convention over Configuration** | Sensible defaults reduce setup | Works with minimal configuration |

### Comparison: Spring Boot vs Node.js Express

```
Aspect            Spring Boot          Express.js
─────────────────────────────────────────────────────
Framework         Spring Boot          Express
Dependency        Built-in (Maven)     npm
Injection         Yes, automatic       Manual or with library
Server            Embedded Tomcat      Node runtime
Config Files      application.properties  .env
Language          Java                 JavaScript/TypeScript
Performance       Fast, optimized      Lightweight, fast
Scaling           Vertical/Horizontal  Horizontal
```

---

## Project Structure

```
Product-Catalog-Service/
├── pom.xml                                    (Maven build file)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/productcatalog/
│   │   │       ├── ProductCatalogApplication.java    (Main app with @SpringBootApplication)
│   │   │       ├── controller/
│   │   │       │   └── ProductController.java        (@RestController - HTTP handlers)
│   │   │       ├── service/
│   │   │       │   └── ProductService.java           (@Service - Business logic)
│   │   │       └── model/
│   │   │           └── Product.java                  (Data entity with Lombok)
│   │   └── resources/
│   │       └── application.properties        (Configuration)
│   └── test/
│       └── java/                             (Unit tests)
└── README.md
```

### Layered Architecture Explanation:

```
HTTP Request
    ↓
┌─────────────────────────────────┐
│  PRESENTATION LAYER             │
│  ProductController              │
│  - Handles HTTP requests        │
│  - Receives JSON body           │
│  - Validates input              │
│  - Returns JSON response        │
└──────────┬──────────────────────┘
           ↓
┌─────────────────────────────────┐
│  BUSINESS LOGIC LAYER           │
│  ProductService                 │
│  - Implements business rules    │
│  - Processes data               │
│  - Enforces constraints         │
│  - Performs calculations        │
└──────────┬──────────────────────┘
           ↓
┌─────────────────────────────────┐
│  DATA LAYER                     │
│  Product Model                  │
│  - Represents data entity       │
│  - Defines structure            │
│  - Contains getters/setters     │
│  - Maps to database (later)     │
└─────────────────────────────────┘
           ↓
    Database (Future)
```

---

## Maven & Dependencies

### What is Maven?

Maven is a build automation tool for Java projects that:
- Manages dependencies (libraries your project needs)
- Builds projects (compiles, packages, tests)
- Manages project structure
- Provides plugins for tasks

### pom.xml Explained

```xml
<project>
    <modelVersion>4.0.0</modelVersion>  <!-- POM format version -->
    
    <groupId>com.example</groupId>      <!-- Organization/company domain -->
    <artifactId>product-catalog-service</artifactId>  <!-- Project name -->
    <version>1.0.0</version>            <!-- Project version -->
    
    <parent>                            <!-- Inherits from Spring Boot parent -->
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.4</version>
    </parent>
    
    <dependencies>
        <!-- Spring Web (REST APIs, Embedded Tomcat) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Lombok (Reduces boilerplate code) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
</project>
```

### Key Dependencies:

#### 1. `spring-boot-starter-web`
Includes everything needed to build web applications:
- **Spring MVC** - Web framework for handling HTTP requests
- **Embedded Tomcat** - Application server (no need for external deployment)
- **Jackson** - JSON serialization/deserialization
- **Validation** - Input validation framework

#### 2. `lombok`
Reduces boilerplate code by auto-generating:
- Getters and setters
- Constructors (all fields, no fields)
- `toString()`, `equals()`, `hashCode()`

---

## Inversion of Control (IoC)

### Traditional Approach (Without IoC)

```java
// ❌ MANUAL OBJECT CREATION (Tight Coupling)
public class ProductController {
    private ProductService service;
    
    public ProductController() {
        this.service = new ProductService();  // WE create the object
    }
}
```

**Problems:**
- Controller is tightly coupled to ProductService
- Can't test controller without actual service
- Hard to change implementation
- Violates Dependency Inversion Principle

### Spring Approach (With IoC)

```java
// ✅ SPRING MANAGES OBJECT CREATION (Loose Coupling)
@Service
public class ProductService {
    // Spring creates and manages this
}

@RestController
public class ProductController {
    private final ProductService service;
    
    public ProductController(ProductService service) {
        this.service = service;  // Spring PROVIDES the object
    }
}
```

**Benefits:**
- Spring controls when/how objects are created
- Controller is loosely coupled from service
- Easy to test with mock services
- Flexible and maintainable

### Key Idea: Inversion of Control

**Without IoC:**
```
Your Code
  ↓
Creates Objects
  ↓
Manages Objects
```

**With IoC (Spring):**
```
Your Code
  ↓
Spring creates objects for you
  ↓
Spring manages objects
  ↓
Your code uses the objects
```

**You don't control object creation → The framework does (Inversion of Control)**

---

## Dependency Injection (DI)

### What is a Dependency?

A dependency is something your class needs to function.

```java
public class ProductController {
    // ProductService is a DEPENDENCY
    private ProductService service;
}
```

### Dependency Injection Methods

#### ❌ Method 1: Manual Creation (NOT RECOMMENDED)
```java
@RestController
public class ProductController {
    private ProductService service = new ProductService();  // ❌ Wrong
}
```

**Problems:**
- Tight coupling
- Hard to test
- Can't use mocks

#### ❌ Method 2: Field Injection (NOT RECOMMENDED)
```java
@RestController
public class ProductController {
    @Autowired  // ❌ Outdated approach
    private ProductService service;
}
```

**Problems:**
- Service can be null before injection
- Hard to test
- Violates immutability
- Hidden dependencies

#### ✅ Method 3: Constructor Injection (RECOMMENDED)
```java
@RestController
public class ProductController {
    private final ProductService service;  // ✅ Immutable
    
    public ProductController(ProductService service) {  // ✅ Injected here
        this.service = service;
    }
}
```

**Benefits:**
- Immutable (marked as `final`)
- Dependencies are clear in constructor
- Easy to test (pass mock service)
- Null safety (can't be null)
- Thread-safe

### How Constructor Injection Works in Spring

```
1. Application Starts
   ├─ @SpringBootApplication scans packages
   ├─ Finds ProductService with @Service
   └─ Finds ProductController with @RestController

2. Bean Creation Phase
   ├─ Creates ProductService bean (Singleton)
   ├─ Stores in Application Context
   └─ Ready to inject

3. Dependency Resolution
   ├─ ProductController needs ProductService
   ├─ Spring checks: "Do we have ProductService bean?"
   ├─ Response: YES! (Already created)
   └─ Spring injects it into constructor

4. Initialization Complete
   ├─ ProductController fully initialized
   ├─ Has all dependencies
   └─ Ready to handle HTTP requests
```

---

## Spring Beans & Application Context

### What is a Bean?

A **Bean** is an object managed by Spring. It's created, configured, and destroyed by the Spring container.

```java
@Service                    // This makes ProductService a bean
public class ProductService {
    public List<Product> getAllProducts() { }
}
```

### What is Application Context?

**Application Context** is Spring's IoC container that:
- Creates and manages all beans
- Stores beans for later use
- Handles dependency injection
- Manages bean lifecycle

### Application Context Visualization

```
┌────────────────────────────────────────┐
│    Application Context (IoC Container)│
├────────────────────────────────────────┤
│                                        │
│  Beans Registry:                       │
│  ┌──────────────────────────────────┐ │
│  │ Bean: ProductService             │ │
│  │ Scope: Singleton                 │ │
│  │ Status: Ready to inject          │ │
│  └──────────────────────────────────┘ │
│                                        │
│  ┌──────────────────────────────────┐ │
│  │ Bean: ProductController          │ │
│  │ Scope: Singleton                 │ │
│  │ Dependencies: ProductService ✓   │ │
│  │ Status: Ready to use             │ │
│  └──────────────────────────────────┘ │
│                                        │
└────────────────────────────────────────┘
         Spring's Bean Factory
```

### Bean Scopes

| Scope | Description | Use Case |
|-------|-------------|----------|
| **Singleton** (Default) | One bean per application | Stateless services (our ProductService) |
| **Prototype** | New bean instance each time | Stateful objects |
| **Request** | One bean per HTTP request | Web apps |
| **Session** | One bean per user session | Web apps with user state |

Our application uses **Singleton** scope (default):
```java
@Service        // Default scope is Singleton
public class ProductService {
    // Only ONE instance created for entire application
}
```

---

## Core Annotations

### 1. @SpringBootApplication

```java
@SpringBootApplication
public class ProductCatalogApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApplication.class, args);
    }
}
```

**Combines three annotations:**
- `@Configuration` - Marks as configuration source
- `@EnableAutoConfiguration` - Enables auto-configuration
- `@ComponentScan` - Scans for components in current package

**What it does:**
1. Starts Spring Boot application
2. Scans packages for @Component, @Service, @RestController
3. Auto-configures Spring beans
4. Starts embedded Tomcat server

### 2. @Service

```java
@Service
public class ProductService {
    public List<Product> getAllProducts() {
        // Business logic here
    }
}
```

**Marks class as:**
- Spring-managed component (bean)
- Business logic handler
- Default scope: Singleton

**When to use:**
- Contains business logic
- Performs operations
- Has methods for other classes to call

### 3. @RestController

```java
@RestController
@RequestMapping("/products")
public class ProductController {
    // HTTP handlers here
}
```

**Combines:**
- `@Controller` - Marks as HTTP handler
- `@ResponseBody` - Converts return value to JSON

**What it does:**
1. Marks class as REST API endpoint
2. Automatically converts return values to JSON
3. Handles HTTP requests (GET, POST, PUT, DELETE)

**When to use:**
- Handling HTTP requests
- Building REST APIs
- Returning JSON responses

### 4. @GetMapping

```java
@RestController
public class ProductController {
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
}
```

**Maps:**
- HTTP GET requests
- To specific URL path
- To this method

**Equivalent to:**
```java
@RequestMapping(value = "/products", method = RequestMethod.GET)
```

### 5. @Autowired (Field Injection - NOT RECOMMENDED)

```java
@RestController
public class ProductController {
    @Autowired          // ❌ Not recommended
    private ProductService service;
}
```

**Why we DON'T use it:**
- Service can be null before injection
- Hidden dependencies
- Hard to test
- Mutable field (not final)

**We use constructor injection instead.**

### 6. Lombok Annotations

#### @Data
```java
@Data
public class Product {
    private Long id;
    private String name;
}
```

**Auto-generates:**
- Getters for all fields
- Setters for all fields
- toString()
- equals()
- hashCode()

#### @AllArgsConstructor
```java
@AllArgsConstructor
public class Product { }
```

**Generates:**
```java
public Product(Long id, String name, String description, 
               Double price, Integer quantity) { }
```

#### @NoArgsConstructor
```java
@NoArgsConstructor
public class Product { }
```

**Generates:**
```java
public Product() { }
```

---

## Layered Architecture

### Why Layered Architecture?

Separating concerns makes code:
- **Maintainable** - Easy to find and fix issues
- **Testable** - Each layer can be tested independently
- **Scalable** - Easy to add new features
- **Reusable** - Services can be used by multiple controllers

### The Three Layers

#### Layer 1: Controller Layer (Presentation)

```java
@RestController
@RequestMapping("/products")
public class ProductController {
    // Responsibilities:
    // - Receive HTTP requests
    // - Validate input
    // - Call service layer
    // - Return HTTP response (JSON)
}
```

**Responsibilities:**
- Handle HTTP requests
- Validate request data
- Call appropriate service
- Return responses

**Should NOT:**
- Access database directly
- Contain business logic
- Know about database

#### Layer 2: Service Layer (Business Logic)

```java
@Service
public class ProductService {
    // Responsibilities:
    // - Implement business rules
    // - Process data
    // - Perform calculations
    // - Enforce constraints
}
```

**Responsibilities:**
- Implement business logic
- Process data
- Make decisions
- Validate business rules

**Should NOT:**
- Handle HTTP directly
- Return JSON
- Know about controllers

#### Layer 3: Model Layer (Data)

```java
@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
}
```

**Responsibilities:**
- Define data structure
- Hold data values
- Represent entities

**Should NOT:**
- Contain business logic
- Handle HTTP requests
- Make decisions

### Data Flow Through Layers

```
HTTP GET /products
    ↓
Controller receives request
    ↓
Controller calls Service.getAllProducts()
    ↓
Service executes business logic
    ↓
Service returns List<Product>
    ↓
Controller converts to JSON
    ↓
HTTP 200 OK + JSON Response
```

---

## How Everything Works Together

### Step-by-Step Flow

#### 1. Application Startup

```
java -jar app.jar
    ↓
Main method called
    ↓
SpringApplication.run() starts Spring Boot
    ↓
@SpringBootApplication annotation processed
```

#### 2. Component Scanning

```
Spring scans packages
    ↓
Finds ProductCatalogApplication (@SpringBootApplication)
Finds ProductService (@Service)
Finds ProductController (@RestController)
Finds Product (entity class)
    ↓
Classes identified for bean creation
```

#### 3. Bean Creation (Dependency Injection)

```
Create ProductService bean
    ├─ Instantiate: new ProductService()
    ├─ Scope: Singleton (one instance)
    └─ Store in Application Context
        ↓
Create ProductController bean
    ├─ See constructor needs: ProductService
    ├─ Check Application Context: "Do we have ProductService?" YES!
    ├─ Inject: Pass ProductService instance to constructor
    ├─ Instantiate: new ProductController(productService)
    └─ Store in Application Context
        ↓
All beans created successfully
```

#### 4. Server Startup

```
Tomcat embedded server starts
    ↓
Listens on port 8080
    ↓
Spring registers request handlers
    ↓
Application ready to receive requests
```

#### 5. HTTP Request Processing

```
Client: GET /products

Spring Dispatcher Servlet receives request
    ↓
Matches to @GetMapping("/products")
    ↓
Routes to ProductController.getAllProducts()
    ↓
Controller calls injected ProductService
    ↓
ProductService.getAllProducts() returns List<Product>
    ↓
Spring converts to JSON
    ↓
Sends HTTP 200 OK + JSON response

Client receives: [{ id: 1, name: "Laptop", ... }, ...]
```

---

## Running the Application

### Build with Maven

```bash
# From project root directory
mvn clean install
```

**This:**
1. Cleans previous build artifacts
2. Downloads dependencies
3. Compiles Java code
4. Runs tests
5. Packages into JAR

### Run the Application

```bash
# Option 1: Using Maven
mvn spring-boot:run

# Option 2: Using compiled JAR (after mvn package)
java -jar target/product-catalog-service-1.0.0.jar
```

### Test the Endpoints

```bash
# Get all products
curl http://localhost:8080/products
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "High-performance laptop with 16GB RAM",
    "price": 999.99,
    "quantity": 10
  },
  {
    "id": 2,
    "name": "Mouse",
    "description": "Wireless ergonomic mouse",
    "price": 29.99,
    "quantity": 50
  },
  ...
]
```

### Check Console Output

```
✓ GET /products endpoint called
✓ ProductService is: INJECTED
✓ Service class: com.example.productcatalog.service.ProductService
✓ Returning 5 products
```

This proves that:
- ✅ ProductService was successfully injected
- ✅ Dependency injection worked
- ✅ Spring created and managed the bean
- ✅ LayeredArchitecture is functioning

---

## Key Mental Models

### Model 1: Spring Container

```
Without Spring:
    YourCode
        ↓
    Creates Objects
        ↓
    Manages Objects


With Spring:
    YourCode
        ↓
    Spring Container (IoC)
        ├─ Creates beans
        ├─ Manages lifecycle
        ├─ Injects dependencies
        └─ Provides to your code
        ↓
    YourCode uses objects
```

### Model 2: Dependency Injection

```
Before DI:
    ProductController → new ProductService()
    (Tight Coupling)


After DI:
    ProductService (created by Spring)
        ↓
    Application Context (holds bean)
        ↓
    ProductController (receives injected bean)
    (Loose Coupling)
```

### Model 3: Bean Lifecycle

```
1. CREATION
   Spring detects @Service
   Creates bean instance

2. CONFIGURATION
   Sets properties
   Calls @PostConstruct methods

3. INJECTION
   Injects into other beans
   Provides where needed

4. USE
   Your code uses bean

5. DESTRUCTION
   @PreDestroy methods called
   Bean removed from memory
```

---

## Summary Table

| Concept | Definition | Example | Purpose |
|---------|-----------|---------|---------|
| **IoC** | Framework controls object creation | Spring creates beans | Loose coupling |
| **DI** | Dependencies provided to classes | Constructor injection | Flexibility, testability |
| **Bean** | Object managed by Spring | ProductService instance | Centralized management |
| **Application Context** | Container holding beans | Spring's heart | Managing all beans |
| **@Service** | Marks business logic class | ProductService | Semantic clarity |
| **@RestController** | Marks HTTP handler | ProductController | REST API endpoint |
| **@GetMapping** | Maps GET requests | /products endpoint | Request routing |
| **Layered Architecture** | Separation of concerns | Controller→Service→Model | Maintainability |

---

## What You've Learned

By completing this assignment, you now understand:

✅ What Spring Boot is and why we use it  
✅ How Inversion of Control (IoC) works  
✅ How Dependency Injection (DI) improves code  
✅ Why constructor injection is recommended  
✅ What Spring Beans and Application Context do  
✅ Core Spring annotations (@Service, @RestController, @GetMapping)  
✅ Layered architecture principles  
✅ How Spring creates and manages objects  
✅ How to build a simple REST API  
✅ How to test your API endpoints  

---

## Next Steps

🚀 **Ready for Day 4:**
- Database integration with Spring Data JPA
- Creating repository layer
- CRUD operations
- Database queries

---

**Congratulations! You've mastered Spring Boot Core Concepts! 🎉**

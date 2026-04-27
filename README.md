# Day 3: Spring Boot Core - Dependency Injection and Inversion of Control

## Assignment Overview

This assignment demonstrates practical implementation of:

- **Inversion of Control (IoC)** - Spring manages object lifecycle
- **Dependency Injection (DI)** - Dependencies provided via constructor
- **Spring Annotations** - @Service, @RestController, @GetMapping
- **Layered Architecture** - Controller → Service → Model
- **Maven Build System** - Dependency management
- **Spring Beans & Application Context** - Central container managing all beans

---

## 📋 Assignment Requirements

### ✅ Completed Tasks

- [x] Create Spring Boot project using Maven
- [x] Add dependencies: Spring Web, Lombok
- [x] Create package structure: controller/, service/, model/
- [x] Create Product model with Lombok annotations
- [x] Create ProductService with @Service annotation
- [x] Create ProductController with @RestController
- [x] Implement constructor-based dependency injection
- [x] Add @GetMapping("/products") endpoint
- [x] Return sample list of products
- [x] Add comprehensive documentation
- [x] Configure application.properties

---

## 📁 Project Structure

```
Product-Catalog-Service/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/example/productcatalog/
│   │   │   ├── ProductCatalogApplication.java
│   │   │   ├── controller/ProductController.java
│   │   │   ├── service/ProductService.java
│   │   │   └── model/Product.java
│   │   └── resources/application.properties
│   └── test/
├── COMPLETE-EXPLANATION.md
├── QUICK-START.md
└── README.md
```

---

## 🚀 Quick Start

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

### Test
```bash
curl http://localhost:8080/products
```

---

## 📚 Documentation

- **COMPLETE-EXPLANATION.md** - Detailed explanation of all concepts (Recommended!)
- **QUICK-START.md** - Step-by-step guide to run application
- **pom.xml** - Dependency configuration
- **Source code** - Well-commented inline explanations

---

## 🎯 Key Concepts

### 1. Inversion of Control (IoC)
Spring controls object creation instead of manual instantiation.

### 2. Dependency Injection (DI)
Constructor injection provides dependencies for loose coupling.

### 3. Spring Beans
Objects managed by Spring's Application Context.

### 4. Layered Architecture
Separation: Controller → Service → Model

---

## ✨ What You'll Learn

- How Spring manages objects automatically
- Why dependency injection improves code quality
- Best practices for REST API design
- Maven-based project structure
- Lombok for reducing boilerplate

---

## 🔗 Resources

- [Spring Boot Guide](https://spring.io/guides/gs/spring-boot/)
- [Dependency Injection](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)
- [Spring Annotations](https://www.baeldung.com/spring-component-annotation)
- [Lombok Documentation](https://projectlombok.org/)

---

**Start with: QUICK-START.md → Run the app → Read COMPLETE-EXPLANATION.md**
# 🚀 Quick Start Guide - Day 3 Assignment

## Prerequisites

- Java 17 installed
- Maven installed
- IDE (IntelliJ, VS Code, Eclipse) - Optional but recommended

---

## Building the Project

### Step 1: Navigate to project root

```bash
cd /Users/sahilmistry/java-projects/Product-Catalog-Service-project
```

### Step 2: Build with Maven

```bash
mvn clean install
```

**What this command does:**
1. `clean` - Removes previous build artifacts
2. `install` - Downloads dependencies, compiles, runs tests, packages

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

---

## Running the Application

### Option 1: Using Maven (Recommended)

```bash
mvn spring-boot:run
```

### Option 2: Using Compiled JAR

```bash
mvn package
java -jar target/product-catalog-service-1.0.0.jar
```

---

## Expected Console Output

When application starts successfully, you'll see:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/

2026-04-27 10:30:00.123  INFO 12345 --- [main] c.e.p.ProductCatalogApplication : 
Starting ProductCatalogApplication v1.0.0
...
2026-04-27 10:30:02.456  INFO 12345 --- [main] o.s.b.w.embedded.tomcat.TomcatWebServer  : 
Tomcat started on port(s): 8080 (http)
2026-04-27 10:30:02.789  INFO 12345 --- [main] c.e.p.ProductCatalogApplication : 
Started ProductCatalogApplication in 3.456 seconds (JVM running for 4.123)
```

---

## Testing the API

### Using curl

```bash
# Test 1: Get all products
curl http://localhost:8080/products

# Test 2: Pretty print the response
curl http://localhost:8080/products | jq .
```

### Using Postman

1. Open Postman
2. Create new request
3. Method: `GET`
4. URL: `http://localhost:8080/products`
5. Click "Send"

### Using Browser

Simply go to: `http://localhost:8080/products`

---

## Expected Response

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
  {
    "id": 3,
    "name": "Keyboard",
    "description": "Mechanical keyboard with RGB lighting",
    "price": 89.99,
    "quantity": 25
  },
  {
    "id": 4,
    "name": "Monitor",
    "description": "27-inch 4K display",
    "price": 349.99,
    "quantity": 15
  },
  {
    "id": 5,
    "name": "Headphones",
    "description": "Noise-cancelling Bluetooth headphones",
    "price": 199.99,
    "quantity": 30
  }
]
```

---

## Console Output (Proof of Dependency Injection)

Check your IDE console or terminal for:

```
✓ GET /products endpoint called
✓ ProductService is: INJECTED
✓ Service class: com.example.productcatalog.service.ProductService
✓ Returning 5 products
```

**This proves:**
- ✅ ProductService was successfully injected into ProductController
- ✅ Spring's dependency injection is working
- ✅ IoC container created and managed the beans

---

## Stopping the Application

Press `Ctrl+C` in your terminal:

```
^C2026-04-27 10:35:00.123  INFO 12345 --- [shutdownHook] 
o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat shut down gracefully
```

---

## Troubleshooting

### Issue: Build fails with "Maven not found"

```bash
# Check if Maven is installed
mvn --version

# If not installed:
# macOS with Homebrew
brew install maven

# Linux
sudo apt-get install maven

# Windows
# Download from https://maven.apache.org/
```

### Issue: Port 8080 already in use

```bash
# Find what's using port 8080 (macOS/Linux)
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or change port in application.properties
server.port=8081
```

### Issue: Java version mismatch

```bash
# Check Java version
java --version

# Must be Java 17 or higher
# Update Java if needed
```

### Issue: "Cannot find symbol" compilation errors

```bash
# Clean and rebuild
mvn clean
mvn compile

# If still fails, try:
mvn clean install -U  # -U forces update of dependencies
```

---

## Understanding What Just Happened

### Application Lifecycle

```
1. JVM starts
   ↓
2. main() method called
   ↓
3. @SpringBootApplication scans packages
   ↓
4. Finds @Service and @RestController classes
   ↓
5. Creates beans:
   - ProductService (Singleton)
   - ProductController (with ProductService injected)
   ↓
6. Embedded Tomcat server starts
   ↓
7. Application ready at http://localhost:8080
   ↓
8. HTTP GET /products received
   ↓
9. Request routed to ProductController.getAllProducts()
   ↓
10. Controller calls injected ProductService
   ↓
11. Service returns product list
   ↓
12. Spring converts to JSON
   ↓
13. HTTP 200 OK + JSON response sent
```

---

## File Structure Review

```
pom.xml                                    ← Maven configuration
src/
├── main/
│   ├── java/com/example/productcatalog/
│   │   ├── ProductCatalogApplication.java ← @SpringBootApplication (Main)
│   │   ├── controller/
│   │   │   └── ProductController.java     ← @RestController (HTTP handler)
│   │   ├── service/
│   │   │   └── ProductService.java        ← @Service (Business logic)
│   │   └── model/
│   │       └── Product.java               ← @Data (Data entity)
│   └── resources/
│       └── application.properties         ← Configuration
└── test/
    └── java/                              ← Unit tests
```

---

## Key Concepts Verified

- ✅ **IoC (Inversion of Control)** - Spring managed bean creation
- ✅ **DI (Dependency Injection)** - ProductService injected into ProductController
- ✅ **@Service** - ProductService marked as business logic
- ✅ **@RestController** - ProductController handles HTTP requests
- ✅ **@GetMapping** - /products endpoint mapped
- ✅ **Layered Architecture** - Controller → Service → Model separation
- ✅ **Maven** - Dependency management and build automation
- ✅ **Lombok** - Reduced boilerplate with @Data

---

## Next: Try These Exercises

1. **Add a new field** to Product (e.g., category)
2. **Create a new endpoint** to get product by ID
3. **Add logging** to ProductService
4. **Change port** in application.properties
5. **Add validation** to Product model

---

## Documentation Files

- **COMPLETE-EXPLANATION.md** - Comprehensive guide with all concepts
- **README.md** - Project overview
- **pom.xml** - Dependencies and build configuration
- **Source code** - Well-commented with inline explanations

---

## Support Resources

- Spring Boot Official Guide: https://spring.io/guides/gs/spring-boot/
- Dependency Injection: https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring
- Spring Annotations: https://www.baeldung.com/spring-component-annotation
- Maven Guide: https://maven.apache.org/guides/

---

**Happy Learning! 🎉**

You now have a fully functional Spring Boot application demonstrating IoC and DI!

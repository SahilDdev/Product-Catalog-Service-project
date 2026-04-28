# JPA & HIBERNATE ASSIGNMENT - Complete Guide

## Overview
This assignment transforms a Spring Boot REST API from using **in-memory storage** to **persistent database storage** using Spring Data JPA and Hibernate ORM.

---

## PART 1: CONCEPTS EXPLAINED

### 1. **ID Generation Strategies**

#### What is an ID?
- Unique identifier for each database record
- Must be unique (no duplicates allowed)
- Primary key in database terminology
- Auto-generated to prevent user errors

#### Four Strategies:

```
┌─────────────────────────────────────────────────────────────┐
│ STRATEGY    │ CODE                           │ DATABASE      │
├─────────────────────────────────────────────────────────────┤
│ AUTO        │ @GeneratedValue(                │ Provider      │
│             │   strategy = AUTO)             │ decides       │
├─────────────────────────────────────────────────────────────┤
│ IDENTITY    │ @GeneratedValue(                │ Auto-         │
│             │   strategy = IDENTITY)         │ increment     │
│             │                                │ (Used for H2) │
├─────────────────────────────────────────────────────────────┤
│ SEQUENCE    │ @GeneratedValue(                │ Database      │
│             │   strategy = SEQUENCE)         │ sequence      │
│             │                                │ (Oracle/      │
│             │                                │ PostgreSQL)   │
├─────────────────────────────────────────────────────────────┤
│ TABLE       │ @GeneratedValue(                │ JPA           │
│             │   strategy = TABLE)            │ maintains     │
│             │                                │ ID table      │
└─────────────────────────────────────────────────────────────┘
```

#### What We Used:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

**Why IDENTITY for H2?**
- H2 supports AUTO_INCREMENT
- Database generates ID automatically
- When you save: ID starts as NULL → Database assigns next ID
- No extra queries needed

**Example Flow:**
```
1. CREATE: Product p = new Product("Laptop", "...", 999.99, 10)
2. SAVE: productRepository.save(p)
   → SQL: INSERT INTO products (name, description, price, quantity) VALUES (...)
   → Database auto-generates ID = 1
3. RESULT: p.getId() = 1 (now set by database)
```

---

### 2. **Entity Relationships**

#### @ManyToOne vs @OneToMany

```
Example: A Category has many Products

┌──────────────┐              ┌──────────────┐
│  Category    │◄─────────────┤   Product    │
│              │  ONE  MANY   │              │
└──────────────┘              └──────────────┘
   Electronics       ←  Laptop
                     ←  Mouse
                     ←  Keyboard
                     ← Monitor
```

**@ManyToOne: Product → Category**
```java
@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // Many Products point to ONE Category
    @ManyToOne
    @JoinColumn(name = "category_id")  // Foreign key
    private Category category;  // Can be null or one category
}
```

**@OneToMany: Category → Products**
```java
@Entity
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // ONE Category has MANY Products
    @OneToMany(mappedBy = "category")  // Reference to Product.category field
    private List<Product> products;  // Collection of products
}
```

**Database Structure:**
```
CATEGORIES TABLE:
┌────┬──────────────┐
│ id │ name         │
├────┼──────────────┤
│ 1  │ Electronics  │
│ 2  │ Furniture    │
└────┴──────────────┘

PRODUCTS TABLE:
┌────┬──────────┬──────────┬────────┬──────────────┐
│ id │ name     │ price    │ qty    │ category_id  │  ← Foreign Key
├────┼──────────┼──────────┼────────┼──────────────┤
│ 1  │ Laptop   │ 999.99   │ 10     │ 1            │  ← Points to Electronics
│ 2  │ Mouse    │ 29.99    │ 50     │ 1            │  ← Points to Electronics
│ 3  │ Chair    │ 199.99   │ 5      │ 2            │  ← Points to Furniture
└────┴──────────┴──────────┴────────┴──────────────┘
```

**Note:** In this assignment, we only use Product entity (no Category yet).

---

### 3. **Lazy vs Eager Loading**

#### Default Behaviors:

```
┌─────────────────────────────────────────────────────────┐
│ @ManyToOne   → DEFAULT: EAGER LOADING                   │
│ @OneToMany   → DEFAULT: LAZY LOADING                    │
└─────────────────────────────────────────────────────────┘
```

#### EAGER LOADING: Load Related Data Immediately

```java
// In Product.java
@ManyToOne(fetch = FetchType.EAGER)  // Explicit EAGER
@JoinColumn(name = "category_id")
private Category category;
```

**Query Flow:**
```
Code:
    Product product = productRepository.findById(1).get();
    System.out.println(product.getCategory().getName());

Database Queries (2 total):
    1. SELECT * FROM products WHERE id = 1
    2. SELECT * FROM categories WHERE id = ? (auto-executed)
    
Result: Category already loaded, no extra query when accessing
```

**When to Use:**
- Need related data immediately
- Prevent lazy loading errors
- Avoid performance hiccups

---

#### LAZY LOADING: Load Related Data Only When Accessed

```java
// In Category.java
@OneToMany(fetch = FetchType.LAZY)  // Explicit LAZY (default)
private List<Product> products;
```

**Query Flow:**
```
Code:
    Category cat = categoryRepository.findById(1).get();
    // Products NOT loaded yet
    
    List<Product> products = cat.getProducts();  // NOW products load
    // Extra query executed here

Database Queries:
    1. SELECT * FROM categories WHERE id = 1
    2. SELECT * FROM products WHERE category_id = 1  (executed lazily)

Result: Faster initial load, but extra query when accessing products
```

**When to Use:**
- Large related collections
- May not need related data
- Save memory and queries

**Common Issue: LazyInitializationException**
```java
// Within service/repository:
Category cat = categoryRepository.findById(1).get();
// Session is still open, can access lazy collections

return cat;  // Return to controller
// Session closed, can't access lazy collections anymore!
cat.getProducts();  // ERROR: LazyInitializationException
```

**Solutions:**
1. Use `@Transactional` to keep session open
2. Use EAGER loading if needed
3. Initialize collection in service: `cat.getProducts().size()`

---

### 4. **Custom Queries (@Query)**

#### Why @Query?

Default JpaRepository methods are limited:
```java
// Built-in methods (limited):
findAll()           // Get all
findById(id)        // Get by ID
save(entity)        // Create/Update
deleteById(id)      // Delete

// Want to find products above $500? No built-in method!
// Want products in stock with name containing "Laptop"? Complicated!
```

#### Solution: Write Custom Queries

**Method 1: Query Naming Convention (Spring Data creates SQL)**
```java
// Spring Data JPA interprets method name:
List<Product> findByNameContainingIgnoreCase(String name);

// Generated SQL:
// SELECT * FROM products WHERE LOWER(name) LIKE LOWER('%value%')

// Usage:
List<Product> laptops = productRepository
    .findByNameContainingIgnoreCase("laptop");
    
// Matches: "Dell Laptop", "LAPTOP Gaming", "gaming laptop"
```

**Method 2: @Query with JPQL (You write explicit query)**
```java
@Query("SELECT p FROM Product p WHERE p.price > :minPrice ORDER BY p.price DESC")
List<Product> findExpensiveProductsSortedByPrice(@Param("minPrice") Double minPrice);

// JPQL: Java Persistence Query Language (entity-based, not SQL)
// SELECT p FROM Product p = SELECT * FROM products
// WHERE p.price > :minPrice = WHERE price > value
// ORDER BY p.price DESC = ORDER BY price DESC

// Usage:
List<Product> expensive = productRepository.findExpensiveProductsSortedByPrice(500.0);
// Returns products priced above $500, sorted most expensive first
```

**Method 3: @Query with Native SQL (Raw database SQL)**
```java
@Query(value = "SELECT * FROM products WHERE price > ? ORDER BY price DESC", 
       nativeQuery = true)
List<Product> findExpensiveProductsNative(Double minPrice);

// Caution: Not portable across different databases!
// Better to use JPQL when possible
```

#### Common Query Examples:

| Purpose | Query Method | Generated SQL |
|---------|-------------|---------------|
| Exact name match | `findByNameIgnoreCase("Laptop")` | `WHERE LOWER(name) = LOWER('Laptop')` |
| Partial match | `findByNameContainingIgnoreCase("top")` | `WHERE LOWER(name) LIKE LOWER('%top%')` |
| Price range | `findByPriceBetween(10, 500)` | `WHERE price BETWEEN 10 AND 500` |
| Above price | `findByPriceGreaterThan(500)` | `WHERE price > 500` |
| In stock | `findByQuantityGreaterThan(0)` | `WHERE quantity > 0` |
| Exists check | `existsByNameIgnoreCase("Laptop")` | `SELECT COUNT(*) FROM ...` |

---

### 5. **Indexing and Performance**

#### What is an Index?

**Analogy: Book Index**
```
Without Index:
    Find all references to "Spring Boot"
    → Read every page sequentially
    → Slow!

With Index:
    Look up "Spring Boot" in back-of-book index
    → Jump directly to pages
    → Fast!
```

**Database Index:**
- Data structure (B-tree) that speeds up lookups
- Takes extra disk space
- Slows down INSERT/UPDATE/DELETE (must update index too)
- Speeds up SELECT (queries)

#### Types of Indexes:

```java
@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_price", columnList = "price")
    }
)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Index on name column
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    // Index on price column
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private Double price;
    
    // No index on quantity (not frequently searched)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
```

#### Generated SQL (H2):
```sql
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL
);

-- Indexes automatically created by Hibernate
CREATE INDEX idx_product_name ON products(name);
CREATE INDEX idx_product_price ON products(price);
```

#### When to Index:

| Column | Index? | Reason |
|--------|--------|--------|
| id | ✓ | Primary key (auto-indexed) |
| name | ✓ | Frequently searched |
| price | ✓ | Used in range queries/filters |
| description | ✗ | Rarely searched directly |
| quantity | ✗ | Rarely filtered individually |

#### Performance Impact:

```
Query without index on name:
    SELECT * FROM products WHERE name = 'Laptop'
    → Scans all 1,000,000 rows
    → Takes 5 seconds

Query with index on name:
    SELECT * FROM products WHERE name = 'Laptop'
    → Uses index, finds directly
    → Takes 0.001 seconds
    
Speed improvement: 5000x faster!
```

#### Best Practices:

✓ **DO:**
- Index columns used in WHERE clause
- Index foreign keys
- Index columns used in ORDER BY
- Index UNIQUE columns

✗ **DON'T:**
- Over-index (slows down writes)
- Index low-cardinality columns (only few distinct values)
- Index columns rarely queried

---

## PART 2: WHAT WAS IMPLEMENTED

### File Changes Summary:

| File | Change | Why |
|------|--------|-----|
| `pom.xml` | Added Spring Data JPA & H2 dependencies | Enable JPA and database |
| `application.properties` | H2 & Hibernate configuration | Connect to database |
| `Product.java` | Added JPA annotations (@Entity, @Id, etc) | Map to database table |
| `ProductRepository.java` | Created new interface | Database operations |
| `ProductService.java` | Refactored to use repository | Business logic with DB |
| `ProductController.java` | Added PUT/DELETE endpoints, search endpoints | Complete CRUD API |

---

### Step-by-Step Changes

#### **STEP 1: Dependencies (pom.xml)**

```xml
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- H2 Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

**What Each Does:**
- `spring-boot-starter-data-jpa`: Provides JpaRepository, Hibernate, entity mapping
- `h2`: In-memory database (perfect for development/testing)

---

#### **STEP 2: Database Configuration (application.properties)**

```properties
# Database URL - in-memory H2 database
spring.datasource.url=jdbc:h2:mem:productdb

# Database credentials
spring.datasource.username=sa
spring.datasource.password=

# Hibernate DDL - create-drop: drop tables on shutdown, create on startup
spring.jpa.hibernate.ddl-auto=create-drop

# Show SQL statements in console
spring.jpa.show-sql=true

# H2 Console UI - view data at http://localhost:8080/h2-console
spring.h2.console.enabled=true
```

**What Each Setting Does:**
- `jdbc:h2:mem:productdb`: Creates in-memory database (lost when app stops)
- `ddl-auto=create-drop`: Auto-creates tables from entities, drops on shutdown
- `show-sql=true`: Logs all SQL queries to console (for debugging)

---

#### **STEP 3: Convert Product to Entity (Product.java)**

**BEFORE (Plain Java class):**
```java
@Data
@NoArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
}
```

**AFTER (JPA Entity):**
```java
@Entity                          // Maps to database table
@Table(name = "products", 
       indexes = {...})           // Table name & performance indexes
public class Product {
    
    @Id                          // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
    private Long id;
    
    @Column(name = "name", 
            nullable = false, 
            length = 100)         // DB column constraints
    private String name;
    
    @Column(name = "price", 
            nullable = false, 
            precision = 10, 
            scale = 2)            // 10 digits total, 2 after decimal
    private Double price;
    
    // ... other fields
}
```

**Key Annotations:**
| Annotation | Purpose | Example |
|-----------|---------|---------|
| `@Entity` | Mark class as database entity | Must have `@Entity` |
| `@Table` | Specify table name & properties | `@Table(name="products")` |
| `@Id` | Mark primary key field | `@Id private Long id;` |
| `@GeneratedValue` | Auto-generate ID values | `@GeneratedValue(strategy=IDENTITY)` |
| `@Column` | Specify column properties | `@Column(length=100, nullable=false)` |

---

#### **STEP 4: Create ProductRepository (ProductRepository.java)**

**What is a Repository?**
- DAO (Data Access Object) pattern
- Abstracts database operations
- Spring Data JPA generates implementation automatically

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Inherited CRUD methods:
    // save(Product)              - INSERT or UPDATE
    // findById(Long)             - SELECT by ID
    // findAll()                  - SELECT all
    // deleteById(Long)           - DELETE by ID
    
    // Custom methods (Spring Data generates SQL):
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByPriceBetween(Double min, Double max);
    List<Product> findByQuantityGreaterThan(Integer qty);
    
    // Custom JPQL queries:
    @Query("SELECT p FROM Product p WHERE p.price > :minPrice ORDER BY p.price DESC")
    List<Product> findExpensiveProductsSortedByPrice(@Param("minPrice") Double minPrice);
}
```

**How It Works:**
```
1. Spring sees: interface ProductRepository extends JpaRepository<Product, Long>
2. Spring generates: ProductRepository implementation with all CRUD methods
3. Spring injects: @Autowired ProductRepository productRepository
4. Code uses: productRepository.save(), findById(), etc.

No need to write SQL! Spring Data handles it.
```

**Method Naming Convention:**
```
findBy + FieldName + Operator

Examples:
- findByName()                          → WHERE name = ?
- findByNameIgnoreCase()                → WHERE LOWER(name) = LOWER(?)
- findByNameContaining()                → WHERE name LIKE '%value%'
- findByPriceGreaterThan()              → WHERE price > ?
- findByPriceBetween()                  → WHERE price BETWEEN ? AND ?
- findByQuantityGreaterThan()           → WHERE quantity > ?
- existsByNameIgnoreCase()              → SELECT COUNT(...) > 0
- countByPriceGreaterThan()             → SELECT COUNT(...) WHERE price > ?
```

---

#### **STEP 5: Update ProductService**

**BEFORE (In-Memory):**
```java
@Service
public class ProductService {
    private List<Product> products = new ArrayList<>();  // RAM storage
    
    private ProductService() {
        products.add(new Product(1L, "Laptop", ...));   // Hardcoded data
        // Data lost when app stops!
    }
    
    public List<Product> getAllProducts() {
        return products;  // Just return list
    }
}
```

**AFTER (Database-Backed):**
```java
@Service
public class ProductService {
    private final ProductRepository productRepository;  // DB access
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<Product> getAllProducts() {
        System.out.println("📦 Fetching all products from database...");
        List<Product> products = productRepository.findAll();  // Query DB
        return products;  // Data persists in database!
    }
    
    public Product createProduct(Product product) {
        System.out.println("💾 Saving product to database...");
        Product savedProduct = productRepository.save(product);
        // Hibernate generates:
        // INSERT INTO products (name, description, price, quantity)
        // VALUES ('Laptop', '...', 999.99, 10)
        // Database auto-assigns ID
        return savedProduct;  // ID now set by database
    }
}
```

**Flow:**
```
Controller
    ↓ HTTP POST /products with JSON
ProductService.createProduct()
    ↓ productRepository.save(product)
ProductRepository (JPA)
    ↓ Generates SQL INSERT
H2 Database
    ↓ Executes INSERT, auto-generates ID
ProductRepository returns saved Product
    ↓ ID now = 1
ProductService returns product
    ↓
Controller returns HTTP 201 CREATED with product data
```

---

#### **STEP 6: Update ProductController**

**NEW ENDPOINTS ADDED:**

```
GET  /products/search?keyword=laptop
     → Search products by name

GET  /products/price-range?minPrice=100&maxPrice=500
     → Filter by price range

GET  /products/in-stock
     → Get products with quantity > 0

PUT  /products/{id}
     → Update existing product

DELETE /products/{id}
     → Delete product
```

**Example Workflows:**

**Create Product:**
```json
POST /products
Content-Type: application/json

{
  "name": "USB Cable",
  "description": "High-speed USB-C",
  "price": 19.99,
  "quantity": 100
}

Response (201 CREATED):
{
  "success": true,
  "message": "Product created successfully",
  "data": {
    "id": 1,
    "name": "USB Cable",
    "description": "High-speed USB-C",
    "price": 19.99,
    "quantity": 100
  }
}
```

**Update Product:**
```json
PUT /products/1
Content-Type: application/json

{
  "name": "Premium USB Cable",
  "description": "High-speed USB-C (updated)",
  "price": 24.99,
  "quantity": 80
}

Database Operation:
UPDATE products 
SET name='Premium USB Cable', 
    description='High-speed USB-C (updated)', 
    price=24.99, 
    quantity=80 
WHERE id=1
```

**Delete Product:**
```
DELETE /products/1

Database Operation:
DELETE FROM products WHERE id=1

Response (200 OK):
{
  "success": true,
  "message": "Product deleted successfully",
  "data": null
}
```

---

## PART 3: HOW TO RUN AND TEST

### Start the Application:

```bash
cd /Users/sahilmistry/java-projects/Product-Catalog-Service-project

# Run the application
mvn spring-boot:run
```

**Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.7.18)

2026-04-28 10:30:00 INFO  - Started ProductCatalogApplication in 2.123 seconds
Server started on http://localhost:8080
```

### Access H2 Console (View Database):

**URL:** http://localhost:8080/h2-console

**Credentials:**
- JDBC URL: `jdbc:h2:mem:productdb`
- User Name: `sa`
- Password: (leave blank)

**In H2 Console, see:**
```sql
-- Table created by Hibernate from @Entity
SELECT * FROM products;

-- Indexes created from @Index
SELECT * FROM information_schema.indexes WHERE table_name='PRODUCTS';
```

---

### Test Endpoints with Postman/cURL:

#### 1. **Create Product**
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "quantity": 10
  }'
```

#### 2. **Get All Products**
```bash
curl http://localhost:8080/products
```

#### 3. **Search Products**
```bash
curl "http://localhost:8080/products/search?keyword=laptop"
```

#### 4. **Filter by Price Range**
```bash
curl "http://localhost:8080/products/price-range?minPrice=100&maxPrice=500"
```

#### 5. **Update Product**
```bash
curl -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Laptop",
    "description": "Updated description",
    "price": 1199.99,
    "quantity": 5
  }'
```

#### 6. **Delete Product**
```bash
curl -X DELETE http://localhost:8080/products/1
```

---

## PART 4: CONCEPTS DEMONSTRATED

### ✓ ID Generation Strategies
- Used `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- Database auto-increments ID
- Each new product gets unique ID automatically

### ✓ Entity Relationships
- Created `@Entity` with `@Id` mapping
- Database schema auto-generated
- Demonstrated one-to-many concept (Category-Product example in docs)

### ✓ Lazy vs Eager Loading
- Explained default behaviors
- Products (entities) load eagerly
- Relationships would use lazy by default

### ✓ Custom Queries (@Query)
- Implemented multiple repository methods
- `findByNameContainingIgnoreCase()` - naming convention
- `searchProductsByKeyword()` - custom @Query with JPQL
- `findByPriceBetween()` - complex filtering

### ✓ Indexing and Performance
- Added indexes on frequently searched columns:
  ```java
  @Index(name = "idx_product_name", columnList = "name")
  @Index(name = "idx_product_price", columnList = "price")
  ```
- Speeds up search and filter queries

### ✓ Clean Architecture
```
HTTP Request
    ↓
ProductController (REST API layer)
    ↓
ProductService (Business logic layer)
    ↓
ProductRepository (Data access layer - JPA)
    ↓
H2 Database (Persistence layer)
```

---

## KEY TAKEAWAYS

| Concept | Before | After |
|---------|--------|-------|
| Storage | In-memory ArrayList | H2 Database |
| ID Generation | Manual assignment | Auto-increment (IDENTITY) |
| Data Persistence | Lost on app restart | Persists in database |
| Queries | List filtering in memory | JPA repository methods + SQL |
| Performance | O(n) for searches | O(log n) with indexes |
| Scalability | Limited to RAM | Database can store millions |
| Maintainability | Service manages list | Repository manages DB |

---

## TROUBLESHOOTING

### Problem: LazyInitializationException
```
Cause: Accessing lazy-loaded collection outside session
Fix: Use @Transactional or EAGER loading
```

### Problem: Column constraint violation
```
Cause: Saving NULL to @NotNull column
Fix: Validate data before saving with @Valid
```

### Problem: Duplicate ID
```
Cause: Not using @GeneratedValue
Fix: Add @GeneratedValue(strategy = IDENTITY)
```

### Problem: Data lost on restart
```
Cause: Using in-memory database (create-drop)
Fix: Change spring.jpa.hibernate.ddl-auto=update or use persistent database
```

---

## NEXT STEPS TO LEARN

1. **Relationships**: Add Category entity with @OneToMany relationship
2. **Transactions**: Use @Transactional for multi-entity operations
3. **Pagination**: Implement PagingAndSortingRepository for large result sets
4. **Validation**: Add database constraints with unique indexes
5. **Testing**: Write unit tests with @DataJpaTest
6. **Caching**: Add Spring Cache for frequently accessed products
7. **Migrations**: Use Flyway for database version control
8. **Performance**: Analyze slow queries with Hibernate statistics


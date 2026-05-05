# Java Transaction Management - Complete Enterprise Guide

## What is a Transaction?

A transaction is a **group of operations that must ALL succeed or ALL fail together**.

**Real-world analogy: Bank Transfer**
```
Transfer $500 from Account A → Account B

Step 1: Debit $500 from Account A    ✓
Step 2: Credit $500 to Account B     ✗ (server crash!)

WITHOUT Transaction: A lost $500, B got nothing! Money vanished!
WITH Transaction:    Step 1 is ROLLED BACK. Both accounts unchanged.
```

**ACID Properties:**
```
A - Atomicity    → All or nothing (either all steps succeed or none)
C - Consistency  → Data goes from one valid state to another
I - Isolation    → Concurrent transactions don't interfere
D - Durability   → Once committed, data survives crashes
```

---

## 1. Advanced Propagation Types

### What is Propagation?

Propagation defines **what happens when a transactional method calls another transactional method**.

### All 7 Propagation Types

| Propagation | Behavior |
|---|---|
| **REQUIRED** | Join existing TX, or create new if none exists |
| **REQUIRES_NEW** | Always create NEW TX (suspend existing) |
| **SUPPORTS** | Join existing TX, or run without TX if none |
| **NOT_SUPPORTED** | Always run WITHOUT TX (suspend existing) |
| **MANDATORY** | MUST have existing TX, throw error if none |
| **NEVER** | MUST NOT have TX, throw error if one exists |
| **NESTED** | Create SAVEPOINT within existing TX |

### Enterprise Scenario 1: REQUIRED (Default)

**Scenario: E-commerce Order Placement** — save order + update inventory + charge payment. All must succeed or all must fail.

```java
@Service
public class OrderService {

    @Transactional(propagation = Propagation.REQUIRED) // default
    public void placeOrder(Order order) {
        orderRepository.save(order);              // Step 1
        inventoryService.reduceStock(order);       // Step 2 - JOINS this TX
        paymentService.chargeCustomer(order);      // Step 3 - JOINS this TX
        // If Step 3 fails → Step 1 and Step 2 are ROLLED BACK
    }
}

@Service
public class InventoryService {
    @Transactional(propagation = Propagation.REQUIRED)
    public void reduceStock(Order order) {
        // JOINS the OrderService transaction (doesn't create new one)
        inventory.setQuantity(inventory.getQuantity() - order.getQuantity());
    }
}
```

**What happens internally:**
```
placeOrder() starts    → TX-1 CREATED
  orderRepository.save → runs inside TX-1
  reduceStock()        → sees TX-1 exists, JOINS TX-1
  chargeCustomer()     → sees TX-1 exists, JOINS TX-1

If chargeCustomer() throws exception → TX-1 ROLLBACK (all undone)
If everything succeeds              → TX-1 COMMIT (all saved)
```

### Enterprise Scenario 2: REQUIRES_NEW

**Scenario: Audit Logging that must ALWAYS be saved**, even if the main operation fails.

```java
@Service
public class AuditService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAttempt(String action, Object data) {
        // Creates its OWN transaction, SUSPENDS the outer one
        auditRepository.save(new AuditLog(action, data, LocalDateTime.now()));
    }
    // This TX commits INDEPENDENTLY of the caller
}
```

```
placeOrder() starts    → TX-1 CREATED
  logAttempt()         → TX-1 SUSPENDED, TX-2 CREATED
    save audit log     → TX-2 COMMITTED ✓, TX-1 RESUMED
  charge()             → FAILS!
placeOrder() fails     → TX-1 ROLLBACK

Result: Order NOT saved ✗, Audit log IS saved ✓
```

### Enterprise Scenario 3: MANDATORY

**Scenario:** A method that updates financial records must ALWAYS run inside a caller's transaction.

```java
@Transactional(propagation = Propagation.MANDATORY)
public void debitAccount(Long accountId, BigDecimal amount) {
    // If called without TX → TransactionRequiredException thrown!
    Account acc = accountRepository.findById(accountId).get();
    acc.setBalance(acc.getBalance().subtract(amount));
}
```

### Enterprise Scenario 4: NOT_SUPPORTED

**Scenario:** Heavy report generation that shouldn't hold DB locks.

```java
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public ReportData generateMonthlyReport() {
    // Suspends any existing transaction
    // Runs WITHOUT a TX to avoid holding DB locks for long reads
    return buildReport(orderRepository.findByMonth(currentMonth));
}
```

---

## 2. Nested Transactions

A nested transaction creates a **SAVEPOINT** within the outer transaction. If the nested TX fails, it rolls back to the savepoint but the outer TX can continue.

```
┌─── OUTER TRANSACTION ───────────────────────┐
│  Step 1: Save Order              ✓           │
│  ┌─── NESTED TX (SAVEPOINT) ──────────┐     │
│  │  Step 2: Apply Coupon    ✗ (fails) │     │
│  │  → Rolls back to SAVEPOINT         │     │
│  └────────────────────────────────────┘     │
│  Step 3: Continue without discount  ✓        │
│  → OUTER TX COMMITS                         │
└──────────────────────────────────────────────┘
```

```java
@Service
public class CouponService {
    @Transactional(propagation = Propagation.NESTED)
    public void applyCoupon(Order order, String code) {
        Coupon coupon = couponRepository.findByCode(code);
        if (coupon == null || coupon.isExpired()) {
            throw new InvalidCouponException("Coupon invalid!");
            // → Rolls back ONLY coupon changes (savepoint)
            // → Outer order TX continues
        }
        order.applyDiscount(coupon.getDiscount());
    }
}
```

### NESTED vs REQUIRES_NEW

| Aspect | NESTED | REQUIRES_NEW |
|---|---|---|
| Inner fails | Savepoint rollback only | Inner TX rollback only |
| Outer fails | Everything rolls back | Inner already committed! |
| DB Connection | Same | Different |
| Use case | Optional steps in a flow | Independent audit logs |

---

## 3. Read-Only Transactions

Tells Hibernate: "This method only READS data, never writes."

```java
@Transactional(readOnly = true)
public List<Product> getAllProducts() {
    return productRepository.findAll();
}
```

### Why Use Read-Only?

```
Normal TX:    Load entity → take SNAPSHOT → dirty check at end → CPU + memory cost
Read-Only TX: Load entity → NO snapshot → NO dirty check → much faster!
```

- Hibernate skips dirty checking (saves CPU)
- No entity snapshots in memory (saves RAM)
- Database can route to read replica
- Database can use shared locks instead of exclusive locks

---

## 4. Transaction Management in Distributed Systems

### The Problem
In microservices, one operation spans MULTIPLE databases. Local `@Transactional` can't span all.

### Solution 1: Two-Phase Commit (2PC)
Coordinator asks all participants: "Can you commit?" → if all say YES → COMMIT ALL. If any says NO → ROLLBACK ALL.

**Problem:** Slow, blocking, single point of failure. Not suited for microservices over HTTP.

### Solution 2: Saga Pattern (Preferred)
Sequence of **local transactions** with **compensating actions** for rollback.

```
Step 1: Order Service   → createOrder()    → COMMITTED locally
Step 2: Payment Service → charge()         → COMMITTED locally
Step 3: Inventory Service → reduceStock()  → FAILS!

COMPENSATION (reverse):
Step 2 UNDO: Payment → refund()   → COMMITTED
Step 1 UNDO: Order   → cancel()   → COMMITTED
```

### Solution 3: Transactional Outbox Pattern
Atomically update DB AND publish an event by writing both to the same DB transaction, then a poller publishes events to Kafka.

---

## 5. Spring AOP - How @Transactional Works Internally

### The Proxy Mechanism

Spring creates a **proxy** around your class. The proxy intercepts method calls and manages transactions.

```
What YOU write:                    What Spring CREATES:
┌──────────────────┐              ┌──────────────────────────────┐
│ @Service         │              │ ProductService$$Proxy         │
│ ProductService   │              │                              │
│                  │    ───→      │  save(Product p) {           │
│  @Transactional  │              │    1. txManager.begin()      │
│  save(Product p) │              │    2. actual.save(p) ←YOUR   │
│    repo.save(p)  │              │    3. txManager.commit()     │
│                  │              │    catch → rollback()        │
└──────────────────┘              └──────────────────────────────┘
```

### Critical Gotcha: Self-Invocation

```java
@Service
public class ProductService {
    @Transactional
    public void methodA() {
        this.methodB(); // ⚠️ BYPASSES PROXY! @Transactional on B is IGNORED!
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodB() { /* ... */ }
}
```

**Fix:** Move `methodB` to a separate service, or inject `self` proxy.

### Rollback Rules

```java
@Transactional                                // Rolls back on RuntimeException only
@Transactional(rollbackFor = Exception.class) // Rolls back on ALL exceptions
@Transactional(noRollbackFor = EmailException.class) // Skips rollback for email failures
```

### AOP Concepts

| AOP Term | Transaction Context |
|---|---|
| Aspect | Transaction management logic |
| Advice | Begin TX / Commit / Rollback |
| Pointcut | Methods with @Transactional |
| Join Point | The actual method execution |
| Proxy | Generated wrapper around your bean |

---

## Quick Reference

| Scenario | Solution |
|---|---|
| Multiple DB writes must be atomic | `@Transactional` (REQUIRED) |
| Audit logs must survive failures | `REQUIRES_NEW` |
| Optional step in a workflow | `NESTED` |
| Read-heavy dashboard/reports | `readOnly = true` |
| Cross-microservice operations | Saga Pattern |
| Must always have caller's TX | `MANDATORY` |

# Raport – Exercițiu de Măsurare a Performanței și Connection Pooling (HikariCP)

## 1. Scopul lucrării
Scopul acestei lucrări este analizarea performanței accesului la baza de date în două scenarii:
- utilizarea conexiunilor JDBC fără pooling
- utilizarea unui connection pool (HikariCP)

De asemenea, se urmărește observarea comportamentului sistemului în cazul gestionării incorecte a conexiunilor (scurgeri de conexiuni).

---

## 2. Tehnologii utilizate
- Java 21/24
- Hibernate ORM 6.x
- PostgreSQL
- HikariCP (connection pooling)
- JDBC Driver PostgreSQL

---

## 3. Sarcina A – Overhead-ul creării conexiunilor

### Metodologie
S-a implementat un test care realizează 100 de conexiuni la baza de date în două moduri:

#### 1. Fără pooling (DriverManager)
```java
Connection conn = DriverManager.getConnection(url, user, pass);
```

#### 2. Cu pooling (HikariCP)
```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl(url);
config.setUsername(user);
config.setPassword(pass);
HikariDataSource ds = new HikariDataSource(config);

Connection conn = ds.getConnection();
```

---

### Rezultate obținute

#### Fără connection pooling:
- Timp total: 4435.82 ms
- Timp mediu per conexiune: 44.36 ms

#### Cu HikariCP:
- Timp total: 15.89 ms
- Timp mediu per conexiune: 0.16 ms

#### Comparație:
- ~279x mai rapid cu pooling

---

### Analiză
Fără pooling, fiecare conexiune necesită inițializarea completă a conexiunii TCP cu baza de date.

Cu HikariCP, conexiunile sunt reutilizate, reducând drastic timpul de răspuns.

---

## 4. Sarcina B – Detectarea scurgerilor de conexiuni

### Cod problematic (leak)

```java
Connection conn = dataSource.getConnection();
// NU se închide conexiunea
```

### Rezultat:
- pool-ul se epuizează
- apare eroarea:

```
Connection is not available, request timed out
```

---

### Cod corect (fix)

```java
try (Connection conn = dataSource.getConnection()) {
    // utilizare conexiune
}
```

---

## 5. Comparație JDBC vs ORM

### Înainte (JDBC)

```java
String sql = "SELECT * FROM employees WHERE department_id = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setInt(1, departmentId);
ResultSet rs = stmt.executeQuery();
```

---

### După (Hibernate / JPA)

```java
List<Employee> employees = entityManager
        .createQuery(
                "SELECT e FROM Employee e WHERE e.department.id = :deptId",
                Employee.class)
        .setParameter("deptId", departmentId)
        .getResultList();
```

---

## 6. Diferențe

| Aspect | JDBC | ORM |
|------|------|------|
| SQL | manual | JPQL |
| Mapare | manuală | automată |
| Complexitate | mare | redusă |
| Mentenanță | dificilă | ușoară |

---

## 7. Concluzii
- Connection pooling reduce drastic timpul de acces la baza de date
- HikariCP oferă performanță foarte ridicată
- Gestionarea corectă a conexiunilor este esențială
- ORM simplifică semnificativ dezvoltarea aplicațiilor

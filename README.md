# 🏪 Inventory Management System (IMS)

A full-stack **Inventory Management System** built with **Spring Boot** (back-end) and **vanilla HTML/CSS/JavaScript** (front-end), backed by a **MySQL** relational database.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Database Setup](#database-setup)
  - [Backend Setup](#backend-setup)
  - [Frontend](#frontend)
- [Pages](#pages)
- [License](#license)

---

## 📖 Overview

The IMS allows store managers to:
- Manage **stores** and their inventory
- Add, edit, and delete **products** (master catalogue)
- Manage **inventory stock levels** per store
- Place **customer orders** with real-time stock validation
- View **customer reviews** for products per store

---

## ✨ Features

| Feature | Description |
|--------|-------------|
| 🏬 Store Management | Add new stores and validate store IDs |
| 📦 Product Catalogue | Add / edit / delete master products with category, price, and SKU |
| 🗃️ Inventory Management | Assign products to stores, manage stock levels per store |
| 🔍 Search & Filter | Filter products by name and category within a store |
| 🛒 Order Placement | Place customer orders with live stock validation and auto stock deduction |
| ⭐ Customer Reviews | View star-rated customer reviews per product per store |
| 🔔 Suggestions | Auto-complete product name suggestions while typing |

---

## 🛠 Tech Stack

### Back-End
| Technology | Version |
|-----------|---------|
| Java | 17 |
| Spring Boot | 3.4.3 |
| Spring Data JPA | — |
| Spring Web (REST) | — |
| Spring Validation | — |
| MySQL Connector/J | — |
| Maven | 3.8+ |

### Front-End
| Technology | Details |
|-----------|---------|
| HTML5 / CSS3 | Vanilla |
| JavaScript (ES6+) | Vanilla, Fetch API |
| Bootstrap | 5.3.3 (CDN) |
| Font Awesome | 6.6.0 (CDN — review star icons) |

### Database
| Technology | Details |
|-----------|---------|
| MySQL | 8.x recommended |

---

## 📁 Project Structure

```
Java-Database-Final/
├── data.sql                            # Sample seed data
├── reviews.json                        # Sample reviews data
├── front-end/                          # Development frontend files
│   ├── index.html                      # Main dashboard (4 tabs)
│   ├── add-product.html                # Add product to a store's inventory
│   ├── add-parent-product.html         # Add product to master catalogue
│   ├── edit-product.html               # Edit inventory-level product + stock
│   ├── edit-parent-product.html        # Edit master product details
│   ├── reviews.html                    # Customer reviews for a product
│   ├── script.js                       # All frontend JavaScript logic
│   ├── frontend.css                    # Custom styles
│   └── images/
│       └── Logo.png
└── back-end/
    └── src/main/
        ├── java/com/project/code/
        │   ├── CodeApplication.java
        │   ├── config/
        │   │   └── WebConfig.java       # CORS + root redirect config
        │   ├── Controller/
        │   │   ├── ProductController.java
        │   │   ├── InventoryController.java
        │   │   ├── StoreController.java
        │   │   ├── ReviewController.java
        │   │   └── GlobalExceptionHandler.java
        │   ├── Model/
        │   │   ├── Product.java
        │   │   ├── Inventory.java
        │   │   ├── Store.java
        │   │   ├── Customer.java
        │   │   ├── OrderDetails.java
        │   │   ├── OrderItem.java
        │   │   ├── Review.java
        │   │   ├── PlaceOrderRequestDTO.java
        │   │   ├── PurchaseProductDTO.java
        │   │   └── CombinedRequest.java
        │   ├── Repo/
        │   │   ├── ProductRepository.java
        │   │   ├── InventoryRepository.java
        │   │   ├── StoreRepository.java
        │   │   ├── CustomerRepository.java
        │   │   ├── OrderDetailsRepository.java
        │   │   ├── OrderItemRepository.java
        │   │   └── ReviewRepository.java
        │   └── Service/
        │       ├── ServiceClass.java
        │       └── OrderService.java
        └── resources/
            ├── application.properties
            └── static/                  # Served by Spring Boot (mirrors front-end/)
```

---

## 🗄️ Database Schema

Tables are **auto-created by Hibernate** on startup (`ddl-auto=update`):

```
product        — Master product catalogue
                 (id, name, category, price, sku)

store          — Store locations
                 (id, name, address)

inventory      — Stock level per product per store
                 (id, product_id, store_id, stockLevel)

customer       — Customer records
                 (id, name, email, phone)

order_details  — Order header
                 (id, customer_id, store_id, totalPrice, orderDate)

order_item     — Order line items
                 (id, order_id, product_id, quantity, price)

review         — Product reviews
                 (id, customer_id, product_id, store_id, rating, comment)
```

---

## 🌐 API Endpoints

Base URL: `http://localhost:8080`

### Store — `/store`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/store` | Add a new store |
| `GET` | `/store/validate/{storeId}` | Check if a store exists |
| `POST` | `/store/placeOrder` | Place a customer order |

### Product — `/product`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/product` | List all products |
| `POST` | `/product` | Add a new product |
| `PUT` | `/product` | Update a product |
| `DELETE` | `/product/{id}` | Delete a product |
| `GET` | `/product/product/{id}` | Get product by ID |
| `GET` | `/product/searchProduct/{name}` | Search products by name |
| `GET` | `/product/category/{name}/{category}` | Filter by name and/or category |

### Inventory — `/inventory`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/inventory/{storeId}` | Get all products for a store |
| `POST` | `/inventory` | Add product to store inventory |
| `PUT` | `/inventory` | Update product details + stock level |
| `DELETE` | `/inventory/{id}` | Remove product from inventory |
| `GET` | `/inventory/search/{name}/{storeId}` | Search products in a store |
| `GET` | `/inventory/filter/{category}/{name}/{storeId}` | Filter by category and/or name |
| `GET` | `/inventory/validate/{quantity}/{storeId}/{productId}` | Validate stock availability |

### Reviews — `/reviews`
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/reviews/{storeId}/{productId}` | Get reviews for a product at a store |

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
- **MySQL 8.x** — [Download](https://dev.mysql.com/downloads/)
- A modern web browser (Chrome, Firefox, Edge)

---

### Database Setup

1. Start your MySQL server.
2. The database is auto-created on first run, but you can create it manually:
   ```sql
   CREATE DATABASE IF NOT EXISTS inventory;
   ```
3. *(Optional)* Load sample seed data:
   ```bash
   mysql -u root -p inventory < data.sql
   ```

---

### Backend Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/Java-Database-Final.git
   cd Java-Database-Final/back-end
   ```

2. **Configure your database credentials** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/inventory?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

3. **Build and run:**
   ```bash
   # macOS / Linux
   ./mvnw spring-boot:run

   # Windows
   mvnw.cmd spring-boot:run
   ```

4. The server starts at **`http://localhost:8080`**.

---

### Frontend

The frontend is served **automatically** by Spring Boot as static content from `back-end/src/main/resources/static/`.

Open your browser and go to:

```
http://localhost:8080
```

> **Note:** The `front-end/` folder is the development source. The `back-end/src/main/resources/static/` folder is the production copy served by Spring Boot. Keep both in sync when making frontend changes.

---

## 🖥️ Pages

| Page | URL | Description |
|------|-----|-------------|
| Main Dashboard | `http://localhost:8080/` | 4 tabs: Add Store · Manage Products · Manage Inventory · Place Order |
| Add Product to Inventory | `add-product.html?storeId=X` | Assign an existing product to a store |
| Add New Master Product | `add-parent-product.html` | Create a brand-new product in the catalogue |
| Edit Inventory Product | `edit-product.html?productId=X&storeId=Y&stockLevel=Z` | Edit product details and stock level |
| Edit Master Product | `edit-parent-product.html?productId=X` | Edit master catalogue product |
| Customer Reviews | `reviews.html?productId=X&storeId=Y&productName=Z` | View star-rated reviews for a product |

---

## 📝 License

This project is licensed under the terms of the [LICENSE](LICENSE) file included in this repository.


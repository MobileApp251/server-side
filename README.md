# Backend System – Marco-service Architecture

## 📌 Tổng quan
Hệ thống backend được xây dựng theo kiến trúc **marcro-service**, bao gồm **2 service độc lập**:

- **Spring Boot Service** – xử lý nghiệp vụ chính và logic phức tạp
- **PHP Service** – xử lý các chức năng hỗ trợ / tích hợp / legacy

Hai service được phát triển và triển khai **độc lập** trên nền tảng **Render**, giao tiếp với nhau thông qua **HTTP REST API**.

---

## 🏗️ Kiến trúc tổng thể

```
Client (Web / Mobile)
        |
        v
-------------------------
|   Backend Services   |
-------------------------
     |           |
     v           v
Spring Boot    PHP Service
```

---

## 🧩 Danh sách Service

### 1️⃣ Spring Boot Service
- Java 21
- Spring Boot
- Spring Web / JPA / Security

### 2️⃣ PHP Service
- PHP 8.2
- PHP thuần

---

## 🚀 Deployment
- Triển khai độc lập trên **Render**
- Auto deploy khi push code
- Mỗi service có URL riêng

---

## ▶️ Chạy local

### Spring Boot
```bash
cd clonejira
./mvnw spring-boot:run
```

### PHP
```bash
cd calendar-sync-module
composer install
php -S localhost:8000 -t public
```

---

## 📄 License
MIT License

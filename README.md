# 💳 Rev-Pay: Next-Gen Financial Microservices Platform

![Rev-Pay Banner](https://img.shields.io/badge/Rev--Pay-Microservices-blue?style=for-the-badge&logo=spring)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)
![Angular](https://img.shields.io/badge/Angular-18%2B-red?style=for-the-badge&logo=angular)

## 🚀 Overview

**Rev-Pay** is an enterprise-grade financial management platform built on a robust **Microservices Architecture**. It enables users and businesses to manage wallets, perform seamless transactions, generate invoices, and apply for loans—all within a unified, high-performance ecosystem.

---

## 🏗️ Why Microservices?

Choosing a microservices architecture for Rev-Pay was a strategic decision to ensure the platform can scale and evolve as a modern fintech solution:

- **Scalability**: Individual services like `transaction-service` can be scaled independently during peak hours without affecting `loan-service`.
- **Fault Isolation**: A failure in the `notification-service` does not bring down the core `wallet-service` or `transaction-service`.
- **Independent Deployment**: Teams can deploy updates to the `frontend` or `user-service` without redeploying the entire system.
- **Technology Flexibility**: Each service can potentially use a different database or tech stack optimized for its specific business logic.

---

## 🗺️ System Architecture

Rev-Pay follows a decoupled microservices pattern, where each service handles a specific business domain.

```mermaid
graph TD
    User((User)) -->|HTTPS| AG[API Gateway: 8080]
    
    subgraph "Infrastructure"
        AG --> ES[Eureka Server: 8761]
        AG --> CS[Config Server: 8888]
    end

    subgraph "Core Services"
        AG --> US[User Service: 8081]
        AG --> WS[Wallet Service: 8082]
        AG --> TS[Transaction Service: 8083]
        AG --> IS[Invoice Service: 8084]
        AG --> NS[Notification Service: 8085]
        AG --> LS[Loan Service: 8086]
    end

    subgraph "Persistence"
        US --> UDB[(User DB)]
        WS --> WDB[(Wallet DB)]
        TS --> TDB[(Transaction DB)]
        IS --> IDB[(Invoice DB)]
        NS --> NDB[(Notification DB)]
        LS --> LDB[(Loan DB)]
    end

    FE[Angular Frontend: 4200] --> AG
```

---

## ✨ Key Features

### 👤 User & Business Ecosystem
- **Omni-Channel Auth**: Secure JWT-based authentication for both individual customers and business accounts.
- **Dynamic Profiles**: Customizable user profiles with role-based dashboard views (Admin vs. Customer vs. Merchant).

### 💰 Digital Wallet & Ledger
- **Instant Settlement**: Real-time balance updates and fund management.
- **Audit Trails**: Complete ledger of every wallet movement for transparency and compliance.

### 💸 High-Performance Transactions
- **P2P & B2B Transfers**: Seamless money transfers between any account type in the ecosystem.
- **Payment Requests**: Built-in flow for requesting funds from other users or clients.

### 📑 Professional Invoicing
- **Automated Billing**: Generate professional PDFs for invoices with auto-calculated taxes and discounts.
- **Client Management**: Track customer payment history and manage debtors efficiently.

### 🏦 Digital Loan Lifecycle
- **End-to-End Application**: Streamlined workflow from loan application to approval and disbursement.
- **Real-time Tracking**: Interactive status updates for active loan requests.

---

## 🛡️ Security Implementation

Security is at the heart of Rev-Pay. We implement a multi-layered security model:

- **JWT Authentication**: Stateless authentication using JSON Web Tokens ensures secure and scalable user sessions.
- **Centralized API Gateway**: Acting as a security barrier, the API Gateway handles authentication checks before requests even reach the microservices.
- **Role-Based Access Control (RBAC)**: Fine-grained permissions ensure users only access the data and actions relevant to their roles (e.g., Customers cannot access Admin panels).
- **Service-to-Service Security**: Internal communication between services is restricted and monitored.

---

## 🏆 Challenges Solved

Building a distributed system comes with unique challenges. Rev-Pay successfully addresses:

- **Service Discovery**: Using **Netflix Eureka**, services find and communicate with each other dynamically without hardcoded URLs.
- **Distributed Configuration**: **Spring Cloud Config** centralizes all environment variables across 10+ services, enabling "change once, apply everywhere."
- **Data Consistency**: Managing distributed transactions across multiple databases while maintaining eventual consistency.
- **Centralized Routing**: The **API Gateway** simplifies frontend complexity by providing a single entry point for all 6+ backend APIs.

---

## 🛠️ Tech Stack

### Backend (Java/Spring)
- **Framework**: Spring Boot 3.x
- **Cloud Native**: Spring Cloud Gateway, Eureka, Config Server.
- **Security**: Spring Security + JWT.
- **Data**: Spring Data JPA / Hibernate / MySQL 8.0.

### Frontend (TypeScript)
- **Framework**: Angular 18+
- **Styling**: CoreUI (Angular), Bootstrap 5, SCSS.
- **Graphics**: Chart.js for real-time financial data visualization.

---

## ⚡ Getting Started

### Prerequisites
- **Docker** & **Docker Compose**
- **Java 17+** & **Node.js 18+**

### Launching the Platform
1. **Start Services**:
   ```bash
   docker-compose up -d --build
   ```
2. **Access Points**:
   - **Frontend**: [http://localhost:4200](http://localhost:4200)
   - **Discovery Port**: [http://localhost:8761](http://localhost:8761)
   - **Central API**: [http://localhost:8080](http://localhost:8080)

---

## 📡 Service Map

| Service | Port | Key Role |
| :--- | :--- | :--- |
| **API Gateway** | 8080 | Security Guard & Request Router |
| **User Service** | 8081 | Identity & Access Management |
| **Wallet Service** | 8082 | Funds & Balance Ledger |
| **Transaction Service** | 8083 | Movement of Money (P2P/Requests) |
| **Invoice Service** | 8084 | Billing & Customer Records |
| **Loan Service** | 8086 | Lending Workflows |
| **Eureka Server** | 8761 | Service Directory |

---

## 🔮 Future Enhancements

- **Real-time Notifications**: Integration with WebSocket/Firebase for push alerts.
- **AI Financial Insights**: Predictive analysis of spending patterns and loan eligibility.
- **Multiple Gateways**: Integration with Stripe and PayPal for external fund transfers.
- **Mobile Companion App**: Cross-platform mobile version using Flutter or Ionic.

---

## 👨‍💻 Author

**Priyanshu Wahane**  
📧 priyanshugwahane@gmail.com  🔗 GitHub: https://github.com/wahanePriyanshu

---
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/e470ff85-4cf5-41df-a758-dac41ff2ed10" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/34ee09e3-a86d-4fcf-ac2e-eba5e64920d6" />

<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/218b6e2a-6f83-4f73-8d65-460a76d19da9" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/4bde56d6-71f8-48e6-ad58-2596819c97a0" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/211d0db7-3aa9-4680-ae5a-7d0c4d9c9205" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/64fd3b78-7a8d-47bd-9b75-005f43f9b5f3" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/484f3ae6-9cbf-4850-a0ef-545b2e7c1ea3" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/ee83e788-af41-4fbd-b6fc-acf0bb13a272" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/8b154db9-402f-4339-b1ea-a4c87a2b3da3" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/92e56def-d300-467b-818f-6160b3584e66" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/2a19ace7-92c7-4940-846d-2d5a55e88ec4" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/3fb8010e-25a8-41d1-93c1-a7b17fade419" />
<img width="1920" height="1080" alt="Image" src="https://github.com/user-attachments/assets/9abbb4b8-1748-4c9d-ad6d-65509e1f9b4e" />

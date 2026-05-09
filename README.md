# AML System - Anti-Money Laundering Solution

A comprehensive Multi-Tenant Anti-Money Laundering (AML) system designed for financial institutions to monitor, detect, and report suspicious activities.

## 🛠 Tech Stack

### Backend
- **Core:** Java 17, Spring Boot 3.4+
- **Security:** Spring Security, JWT (jjwt 0.12.6)
- **Data:** Spring Data JPA, PostgreSQL, Flyway (Migrations)
- **Processing:** Spring Batch (Bulk Data Processing)
- **Storage:** Cloudinary (File Management)
- **Utilities:** MapStruct (Mapping), Lombok, Dotenv
- **Reporting:** OpenHtmlToPdf (PDF Generation)

### Frontend
- **Framework:** Angular 19.2
- **Language:** TypeScript
- **State Management:** RxJS
- **Styling:** Bootstrap / Component-based styling
- **Architecture:** Modular (Core, Layout, Features)

---

## 🏗 System Architecture

The system follows a **Multi-Tenant (Schema-per-Tenant)** architecture:
- **Public Schema:** Stores system-level data (Tenants/Banks registration, System Admins).
- **Tenant Schemas:** Each Bank has its own isolated schema for Customers, Transactions, Rules, Alerts, and Cases.
- **Workflow:** File Upload (Batch) → Rule Engine Execution → Alert Generation → Case Management → STR Filing.

---

## 📂 Package Structure

### Backend (`src/main/java/com/tss/AmlSystem`)
- `batch`: Batch job configurations (Reader, Processor, Writer)
- `config`: Application and Security configurations
- `controller`: REST API Endpoints
- `dto`: Request and Response data transfer objects
- `entity`: Database entities and Enums
- `exception`: Global error handling and custom exceptions
- `mapper`: MapStruct interfaces for DTO-Entity conversion
- `repository`: JPA repositories
- `security`: JWT filters, Entry points, and UserDetails implementation
- `service`: Business logic implementation
- `utils`: Helper classes and Constants

### Frontend (`src/app`)
- `core`: Guards, Interceptors, Services (Singleton)
- `features`: Functional modules (Alerts, Cases, Rules, Dashboard)
- `layout`: Structural components (Sidebar, Navbar, Footer)

---

## 🧩 Modules List

| Module | Description |
| :--- | :--- |
| **Auth & IAM** | Multi-role authentication (System Admin, Bank Admin, Compliance Officer) |
| **Tenant Management** | Onboarding new banks and managing bank-level users |
| **Rule Engine** | Dynamic rule evaluation with sliding window support |
| **Batch Processing** | High-volume upload of Customers, Accounts, and Transactions |
| **Alert Dashboard** | Real-time tracking and filtering of system-generated alerts |
| **Case Management** | Investigation workflow, assignment, and status tracking |
| **STR Filing** | Suspicious Transaction Report generation (PDF) and archival |

---

## 📊 Database Indexes (Tenant Schema)

| Index Name | Table Name | Columns | Purpose |
| :--- | :--- | :--- | :--- |
| `idx_txn_type_date` | `transactions` | `transaction_type, transaction_date` | Optimization for sliding window rule engine queries |
| `idx_acc_client_num` | `accounts` | `client_number` | Efficient fetching of accounts for a specific client |
| `idx_alert_case_id` | `alerts` | `case_id` | Fast retrieval of all alerts associated with a Case |
| `idx_alert_status_client` | `alerts` | `status, client_number` | Dashboard performance for filtered alert views |
| `idx_fve_file_id` | `file_validation_errors` | `file_id` | Rapid access to validation errors for uploaded files |

---

## 🚀 API Endpoints

### Authentication & User Management
- `POST /api/v1/auth/login`: User login & JWT issuance
- `POST /api/v1/auth/refreshtoken`: Silent token refresh
- `POST /api/v1/auth/banks/register`: Register a new Bank (System Admin only)
- `POST /api/v1/auth/bank-officers/register`: Register Bank Staff (Bank Admin only)

### File & Data Management
- `POST /api/v1/files/upload`: Multi-part file upload for batch processing
- `GET /api/v1/files`: List all uploaded files with status
- `GET /api/v1/files/{fileNumber}/errors`: Fetch validation errors for a file

### Alerts & Cases
- `GET /api/v1/alerts`: List alerts with pagination and filters
- `POST /api/v1/cases`: Convert alerts into a case
- `GET /api/v1/cases`: List cases for investigation
- `PUT /api/v1/cases`: Update case status or assignment

### Rules & Engine
- `GET /api/v1/rules`: List all system rules
- `PUT /api/v1/bank-rules/{ruleCode}/parameters`: Configure rule thresholds for a bank
- `POST /api/v1/run-engine`: Manually trigger rule engine execution

### Reporting
- `GET /api/v1/str-filings`: List filed STRs
- `GET /api/v1/customers/{customerNumber}/transactions/pdf`: Generate transaction summary PDF

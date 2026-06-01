# Software Requirements Specification

## Project Name

Hands Of Retail Backend

## Document Purpose

This document describes the functional and non-functional requirements for the Hands Of Retail backend service. It is based on the implemented Spring Boot application, REST controllers, security configuration, database entities, and response contracts in the current codebase.

## 1. Introduction

### 1.1 System Overview

Hands Of Retail is a RESTful backend service that manages retail clients, stores, and sales reports. It provides JWT-authenticated APIs for administrators and client users. Administrators can create and maintain clients, stores, and report records. Client users can view only the stores and reports that belong to them.

### 1.2 Goals

The system shall:

- authenticate users through JWT login
- support role-based access for ADMIN and CLIENT users
- manage client and store master data
- create, update, and query daily, monthly, and yearly reports
- return standardized API responses and validation errors
- expose OpenAPI/Swagger documentation for the backend APIs

### 1.3 Scope

The backend currently covers the following business domains:

- authentication
- client management
- store management
- daily reporting
- monthly reporting
- yearly reporting

The application also includes global exception handling, H2 console support for local development, and a JSON response envelope for all APIs.

## 2. Product Perspective

### 2.1 System Type

The product is a stateless Spring Boot REST API service.

### 2.2 Runtime Characteristics

- Java 21 runtime
- Spring Boot 3.4.x
- Spring Security with JWT-based authentication
- Spring Data JPA persistence
- H2 database by default, with MySQL supported through configuration changes
- Swagger/OpenAPI UI enabled for API exploration

### 2.3 Authentication and Authorization Model

The service uses bearer-style JWT authentication. Security rules are role-based:

- `/api/v1/auth/**` is publicly accessible
- `/api/v1/admin/**` requires the ADMIN role
- `/api/v1/client/**` requires the CLIENT role
- all other endpoints require authentication

## 3. User Classes and Roles

### 3.1 Administrator

An administrator manages clients, stores, and all report records. Administrators can create, update, and query data across all client accounts.

### 3.2 Client User

A client user can only view stores and report data associated with their own client account.

### 3.3 Anonymous User

An anonymous user can only access the login endpoint and the public API documentation endpoints.

## 4. Functional Requirements

### 4.1 Authentication

FR-1: The system shall accept login requests with email and password.

FR-2: The system shall authenticate valid credentials and return a JWT token with the user role, email, and full name.

FR-3: The system shall reject invalid credentials with an authorization error.

FR-4: The system shall validate login payloads and return structured field-level validation errors when the request is invalid.

### 4.2 Client Management

FR-5: The system shall allow administrators to create client accounts.

FR-6: The system shall allow administrators to retrieve all clients.

FR-7: The system shall allow administrators to update existing client accounts.

FR-8: The system shall store client status and role information for each client account.

### 4.3 Store Management

FR-9: The system shall allow administrators to create stores for a client.

FR-10: The system shall allow administrators to retrieve stores with optional filtering by client and store status.

FR-11: The system shall allow administrators to retrieve a single store by store identifier.

FR-12: The system shall allow administrators to update store details.

FR-13: The system shall allow administrators to update store status independently.

### 4.4 Daily Reports

FR-14: The system shall allow administrators to create daily reports for a store.

FR-15: The system shall allow administrators to retrieve daily reports filtered by store, client, and date range.

FR-16: The system shall allow administrators to retrieve all daily reports for a specific store.

FR-17: The system shall allow administrators to update existing daily reports.

FR-18: The system shall allow client users to retrieve daily reports only for stores that belong to their client account.

### 4.5 Monthly Reports

FR-19: The system shall allow administrators to create monthly reports for a store.

FR-20: The system shall allow administrators to retrieve monthly reports filtered by store, client, year, and month.

FR-21: The system shall allow administrators to retrieve all monthly reports for a specific store.

FR-22: The system shall allow administrators to update existing monthly reports.

FR-23: The system shall allow client users to retrieve monthly reports only for stores that belong to their client account.

### 4.6 Yearly Reports

FR-24: The system shall allow administrators to create yearly reports for a store.

FR-25: The system shall allow administrators to retrieve yearly reports filtered by store, client, and year.

FR-26: The system shall allow administrators to retrieve all yearly reports for a specific store.

FR-27: The system shall allow administrators to update existing yearly reports.

FR-28: The system shall allow client users to retrieve yearly reports only for stores that belong to their client account.

### 4.7 API Response Handling

FR-29: The system shall return a standard response envelope containing success, message, data, errors, and timestamp fields.

FR-30: The system shall return a clear error envelope for validation failures, business rule violations, unauthorized requests, forbidden requests, not found errors, duplicate resource errors, and unexpected server errors.

## 5. Data Requirements

### 5.1 Core Entities

The system persists the following core entities:

- AdminUser
  - adminId
  - fullName
  - email
  - passwordHash
  - role

- ClientUser
  - clientId
  - fullName
  - email
  - passwordHash
  - phoneNumber
  - address
  - status
  - role

- Store
  - storeId
  - client reference
  - storeName
  - storeCode
  - address
  - contactNumber
  - status

- DailyReport
  - dailyReportId
  - store reference
  - reportDate
  - groceryTotal
  - volume
  - cashDeposit
  - checkDeposit
  - overShort

- MonthlyReport
  - monthlyReportId
  - store reference
  - reportMonth
  - reportYear
  - departmentId
  - departmentName
  - gross
  - discount
  - promotion
  - refund
  - voidAmount
  - netSales

- YearlyReport
  - yearlyReportId
  - store reference
  - reportYear
  - annualSummary

### 5.2 Common Metadata

All persisted entities inherit creation and update timestamps from a shared base entity.

### 5.3 Data Integrity Rules

- client email values shall be unique
- store code values shall be unique
- admin email values shall be unique
- reports shall always reference an existing store
- stores shall always reference an existing client
- password values shall be stored as hashes, not plain text

## 6. External Interface Requirements

### 6.1 API Base Path

The backend listens on port 8080 and serves REST APIs under `/api/v1`.

### 6.2 Authentication Endpoint

- `POST /api/v1/auth/login`

### 6.3 Administrative Endpoints

- `POST /api/v1/admin/clients`
- `GET /api/v1/admin/clients`
- `PUT /api/v1/admin/clients/{id}`
- `POST /api/v1/admin/stores`
- `GET /api/v1/admin/stores`
- `GET /api/v1/admin/stores/{storeId}`
- `PATCH /api/v1/admin/stores/{storeId}/status`
- `PUT /api/v1/admin/stores/{storeId}`
- `POST /api/v1/admin/daily-reports`
- `GET /api/v1/admin/daily-reports`
- `GET /api/v1/admin/daily-reports/store/{storeId}`
- `PUT /api/v1/admin/daily-reports/{dailyReportId}`
- `POST /api/v1/admin/monthly-reports`
- `GET /api/v1/admin/monthly-reports`
- `GET /api/v1/admin/monthly-reports/store/{storeId}`
- `PUT /api/v1/admin/monthly-reports/{monthlyReportId}`
- `POST /api/v1/admin/yearly-reports`
- `GET /api/v1/admin/yearly-reports`
- `GET /api/v1/admin/yearly-reports/store/{storeId}`
- `PUT /api/v1/admin/yearly-reports/{yearlyReportId}`

### 6.4 Client Endpoints

- `GET /api/v1/client/stores`
- `GET /api/v1/client/daily-reports/store/{storeId}`
- `GET /api/v1/client/monthly-reports/store/{storeId}`
- `GET /api/v1/client/yearly-reports/store/{storeId}`

### 6.5 Documentation Endpoints

The application exposes Swagger/OpenAPI UI and JSON docs for API exploration.

### 6.6 Persistence Interfaces

The default runtime uses an H2 file-based database stored under `./database/h2/retail-db`. The project is also structured to support MySQL through configuration changes.

## 7. Non-Functional Requirements

### 7.1 Security

NFR-1: The system shall enforce JWT-based authentication for protected endpoints.

NFR-2: The system shall enforce role-based authorization for admin and client resources.

NFR-3: The system shall use hashed passwords and shall not expose password hashes in API responses.

NFR-4: The system shall disable server-side sessions and operate statelessly for API requests.

### 7.2 Validation and Error Handling

NFR-5: The system shall validate request bodies before processing them.

NFR-6: The system shall return consistent HTTP status codes for bad requests, conflicts, unauthorized access, forbidden access, and missing resources.

### 7.3 Maintainability

NFR-7: The system shall use a shared response envelope to keep API responses consistent.

NFR-8: The system shall expose API documentation through Swagger/OpenAPI.

### 7.4 Portability

NFR-9: The system shall run locally on H2 without external database dependencies.

NFR-10: The system shall be configurable to use MySQL in deployment environments.

## 8. Business Rules

- BR-1: A client may own multiple stores.
- BR-2: A store belongs to exactly one client.
- BR-3: A store may have multiple daily, monthly, and yearly reports.
- BR-4: Client users may only access data that belongs to their own client account.
- BR-5: Administrator users may access and manage all client-linked operational data.
- BR-6: Store status is limited to ACTIVE or INACTIVE.
- BR-7: Client status is limited to ACTIVE or INACTIVE.

## 9. Assumptions and Dependencies

- The backend assumes a valid JWT secret is configured.
- The backend assumes the authentication service can resolve the logged-in user's role and identity from the JWT.
- The backend assumes client and store identifiers are provided by authorized callers when creating or updating reports.
- The backend assumes a separate front-end or API client will consume the JSON API.

## 10. Out of Scope

The following are not part of the current backend implementation:

- front-end UI implementation
- automated email or WhatsApp delivery flows
- payment processing
- inventory management
- advanced analytics or forecasting beyond the stored report fields

## 11. Acceptance Criteria

The backend shall be considered functionally complete when:

- a user can log in and receive a JWT
- administrators can manage clients, stores, and reports through the documented endpoints
- client users can view only their own stores and reports
- validation failures and business errors return standardized error responses
- Swagger/OpenAPI documentation is available for all public endpoints

## 12. Revision History

- Initial SRS generated from the current backend implementation.

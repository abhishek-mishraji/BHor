# Hands Of Retail Backend

## Table of Contents

1.  [Project Overview](#1-project-overview)
    - [System Description](#11-system-description)
    - [Goals](#12-goals)
    - [Key Features](#13-key-features)
    - [Core Business Domains](#14-core-business-domains)
2.  [Technologies Used](#2-technologies-used)
3.  [Getting Started](#3-getting-started)
    - [Prerequisites](#31-prerequisites)
    - [Local Development Setup](#32-local-development-setup)
    - [Running Locally](#33-running-locally)
    - [Building for Production](#34-building-for-production)
    - [Running in Production](#35-running-in-production)
4.  [Configuration](#4-configuration)
    - [Environment Variables (`.env` / `.env.prod`)](#41-environment-variables-env--envprod)
    - [Application Profiles](#42-application-profiles)
    - [Database Configuration](#43-database-configuration)
5.  [API Documentation](#5-api-documentation)
6.  [Authentication & Authorization](#6-authentication--authorization)
7.  [Data Entities](#7-data-entities)
8.  [Branching Workflow](#8-branching-workflow)
9.  [Best Practices & Notes](#9-best-practices--notes)
10. [Out of Scope](#10-out-of-scope)
11. [License](#11-license)

---

## 1. Project Overview

### 1.1 System Description

The Hands Of Retail Backend is a stateless Spring Boot REST API service designed to manage retail operations, specifically focusing on clients, stores, and various sales reports. It provides a secure, JWT-authenticated interface for both administrative and client-level users. Administrators have comprehensive control over clients, stores, and all report types, while client users are restricted to viewing data associated with their own accounts.

### 1.2 Goals

The primary goals of this system are to:

- Implement robust JWT-based user authentication and role-based authorization (ADMIN/CLIENT).
- Provide comprehensive management APIs for client and store master data.
- Enable the creation, updating, and querying of daily, monthly, and yearly sales reports.
- Ensure standardized API responses and detailed validation error handling.
- Offer discoverable API documentation via OpenAPI/Swagger UI.

### 1.3 Key Features

- JWT-based authentication for secure API access.
- Role-based access control (ADMIN and CLIENT roles).
- Client Management (Create, Retrieve, Update client accounts).
- Store Management (Create, Retrieve, Update store details and status).
- Daily, Monthly, and Yearly Sales Report management.
- Bulk upload functionality for Monthly Reports via Excel (.xlsx) files.
- Global exception handling and standardized JSON response envelopes.
- H2 Console support for local development.

### 1.4 Core Business Domains

The backend covers the following business areas:

- Authentication and User Security
- Client Master Data Management
- Store Master Data Management
- Daily Sales Reporting
- Monthly Sales Reporting
- Yearly Sales Reporting

## 2. Technologies Used

- **Runtime:** Java 21+ / JDK 21
- **Framework:** Spring Boot 3.4.x
- **Security:** Spring Security with JWT (JSON Web Tokens)
- **Persistence:** Spring Data JPA
- **Database:** H2 (local development), MySQL (production)
- **Build Tool:** Gradle (with Gradle Wrapper)
- **Database Migrations:** Flyway (implicit from `V1__init_schema.sql` in `db/migration`)
- **API Documentation:** Swagger/OpenAPI UI

## 3. Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes, as well as guidance for production deployment.

### 3.1 Prerequisites

- Java Development Kit (JDK) 21 or newer.
- A compatible IDE (e.g., VS Code with Java extensions, IntelliJ IDEA).
- Git for version control.
- A running MySQL instance for production-like testing, or rely on the embedded H2 database for local development.

### 3.2 Local Development Setup

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/your-username/hor.git # Replace with your repo URL
    cd hor
    ```

2.  **Create `.env` file:**
    Create a file named `.env` in the project root directory. This file will contain environment variables for local H2 database configuration and other settings. **Do NOT commit this file to Git.**
    ```properties
    # .env (Example content - adjust as needed)
    DB_URL=jdbc:h2:file:./database/h2/retail-db;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE
    DB_USERNAME=sa
    DB_PASSWORD=
    JPA_HIBERNATE_DDL_AUTO=update
    JWT_SECRET=YourSuperSecretKeyForLocalDevelopmentOnly
    ```
    _Note: `JPA_HIBERNATE_DDL_AUTO=update` is suitable for local development with H2, but should be `validate` or `none` in production, relying on Flyway for schema management._

### 3.3 Running Locally

There are two primary ways to run the application locally:

- **Option 1: Load `.env` and use `bootRun` (Recommended for development)**
  This method loads your `.env` variables directly into the shell session and then starts the Spring Boot application using Gradle.

  **PowerShell (Windows):**

  ```powershell
  Get-Content .env | ForEach-Object { if ($_ -match '^\s*#') { continue } ; $p = $_ -split '=',2 ; if ($p.Length -eq 2) { Set-Item -Path Env:$($p[0].Trim()) -Value $p[1].Trim() } }
  ./gradlew.bat bootRun

  Option 2: Pass profile explicitly (without loading .env)
  This method activates the local Spring profile, which will pick up properties from application-local.properties. You would typically define local H2 properties directly in this file or as system properties/command-line arguments if not using .env.
  ./gradlew bootRun --args='--spring.profiles.active=local'
  ```

  The application will start on port 8080.

  3.4 Building for Production
  To create an executable JAR file for production deployment:
  ./gradlew bootJar
  This will produce a JAR file in the libs directory (e.g., hor-0.0.1-SNAPSHOT.jar).
  3.5 Running in Production
  Set Environment Variables: On your production host, set the necessary environment variables for your MySQL database and JWT secret. DO NOT use .env.prod directly on a production server; prefer your cloud provider's secret management service.

Example of environment variables you'd set:
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:mysql://your_mysql_host:3306/your_database
export DB_USERNAME=your_mysql_user
export DB_PASSWORD=your_mysql_password
export JPA_HIBERNATE_DDL_AUTO=validate # Or 'none'
export JWT_SECRET=YourHighlySecureAndComplexProductionSecretKey
Start the Application:
Navigate to the directory containing your built JAR file (libs) and run:

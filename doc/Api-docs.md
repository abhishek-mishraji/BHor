# Hands Of Retail API Documentation

Base URL:

```bash
http://localhost:8080
```

---

# Authentication APIs

## 1. Login

### Endpoint

```http
POST /api/v1/auth/login
```

### Purpose

Authenticate user and return JWT token.

### Request Body

```json
{
  "email": "admin@gmail.com",
  "password": "password123"
}
```

### Response

```json
{
  "success": true,
  "message": "Login Successful",
  "data": {
    "token": "jwt-token",
    "role": "ADMIN",
    "email": "admin@gmail.com",
    "fullName": "Admin User"
  },
  "errors": {},
  "timestamp": "2026-05-28T13:51:03.542Z"
}
```

---

# Client APIs (Admin)

## 2. Create Client

### Endpoint

```http
POST /api/v1/admin/clients
```

### Purpose

Create new client user.

### Request Body

```json
{
  "fullName": "John Doe",
  "email": "john@gmail.com",
  "password": "password123",
  "phoneNumber": "9876543210",
  "address": "New York"
}
```

### Response Fields

| Field       | Type   |
| ----------- | ------ |
| clientId    | Long   |
| fullName    | String |
| email       | String |
| phoneNumber | String |
| address     | String |
| status      | String |
| role        | String |

---

## 3. Get All Clients

### Endpoint

```http
GET /api/v1/admin/clients
```

### Purpose

Fetch all clients.

### Response

```json
{
  "success": true,
  "message": "Clients fetched successfully",
  "data": [
    {
      "clientId": 1,
      "fullName": "John Doe",
      "email": "john@gmail.com",
      "phoneNumber": "9876543210",
      "address": "New York",
      "status": "ACTIVE",
      "role": "CLIENT"
    }
  ]
}
```

---

## 4. Update Client

### Endpoint

```http
PUT /api/v1/admin/clients/{id}
```

### Purpose

Update existing client.

### Path Variable

| Name | Type |
| ---- | ---- |
| id   | Long |

### Request Body

```json
{
  "fullName": "Updated Name",
  "email": "updated@gmail.com",
  "password": "newpassword",
  "phoneNumber": "9999999999",
  "address": "Updated Address"
}
```

---

# Store APIs (Admin)

## 5. Create Store

### Endpoint

```http
POST /api/v1/admin/stores
```

### Purpose

Create store for client.

### Request Body

```json
{
  "clientId": 1,
  "storeName": "Walmart Downtown",
  "storeCode": "WM001",
  "address": "California",
  "contactNumber": "9876543210"
}
```

### Response Fields

| Field         | Type   |
| ------------- | ------ |
| storeId       | Long   |
| clientId      | Long   |
| clientName    | String |
| storeName     | String |
| storeCode     | String |
| address       | String |
| contactNumber | String |
| status        | String |

---

## 6. Get All Stores

### Endpoint

```http
GET /api/v1/admin/stores
```

### Purpose

Get all stores.

### Query Params

| Param    | Type              |
| -------- | ----------------- |
| clientId | Long              |
| status   | ACTIVE / INACTIVE |

---

## 7. Get Store By ID

### Endpoint

```http
GET /api/v1/admin/stores/{storeId}
```

### Purpose

Fetch store details by store id.

---

## 8. Update Store

### Endpoint

```http
PUT /api/v1/admin/stores/{storeId}
```

### Purpose

Update store details.

### Request Body

```json
{
  "clientId": 1,
  "storeName": "Updated Store",
  "storeCode": "WM002",
  "address": "Updated Address",
  "contactNumber": "9999999999"
}
```

---

## 9. Update Store Status

### Endpoint

```http
PATCH /api/v1/admin/stores/{storeId}/status
```

### Purpose

Activate or deactivate store.

### Query Params

| Param  | Example |
| ------ | ------- |
| status | ACTIVE  |

---

# Daily Report APIs (Admin)

## 10. Create Daily Report

### Endpoint

```http
POST /api/v1/admin/daily-reports
```

### Purpose

Create daily report.

### Request Body

```json
{
  "storeId": 1,
  "reportDate": "2026-05-28",
  "groceryTotal": 10000,
  "volume": 200,
  "cashDeposit": 5000,
  "checkDeposit": 2000,
  "overShort": 100
}
```

### Response Fields

| Field         | Type   |
| ------------- | ------ |
| dailyReportId | Long   |
| storeId       | Long   |
| storeName     | String |
| reportDate    | Date   |
| groceryTotal  | Number |
| volume        | Number |
| cashDeposit   | Number |
| checkDeposit  | Number |
| overShort     | Number |

---

## 11. Get Daily Reports

### Endpoint

```http
GET /api/v1/admin/daily-reports
```

### Purpose

Fetch daily reports.

### Query Params

| Param    | Type |
| -------- | ---- |
| storeId  | Long |
| clientId | Long |
| from     | Date |
| to       | Date |

---

## 12. Get Daily Reports By Store

### Endpoint

```http
GET /api/v1/admin/daily-reports/store/{storeId}
```

### Purpose

Fetch daily reports for specific store.

---

## 13. Update Daily Report

### Endpoint

```http
PUT /api/v1/admin/daily-reports/{dailyReportId}
```

### Purpose

Update daily report.

### Request Body

```json
{
  "storeId": 1,
  "reportDate": "2026-05-28",
  "groceryTotal": 12000,
  "volume": 250,
  "cashDeposit": 6000,
  "checkDeposit": 2500,
  "overShort": 50
}
```

---

# Monthly Report APIs (Admin)

## 14. Create Monthly Report

### Endpoint

```http
POST /api/v1/admin/monthly-reports
```

### Purpose

Create monthly report.

### Request Body

```json
{
  "storeId": 1,
  "reportMonth": 5,
  "reportYear": 2026,
  "departmentId": 1,
  "departmentName": "Grocery",
  "gross": 50000,
  "discount": 5000,
  "promotion": 2000,
  "refund": 1000,
  "voidAmount": 500,
  "netSales": 41500
}
```

---

## 15. Get Monthly Reports

### Endpoint

```http
GET /api/v1/admin/monthly-reports
```

### Purpose

Fetch monthly reports.

### Query Params

| Param    | Type    |
| -------- | ------- |
| storeId  | Long    |
| clientId | Long    |
| year     | Integer |
| month    | Integer |

---

## 16. Get Monthly Reports By Store

### Endpoint

```http
GET /api/v1/admin/monthly-reports/store/{storeId}
```

### Purpose

Fetch monthly reports by store.

---

## 17. Update Monthly Report

### Endpoint

```http
PUT /api/v1/admin/monthly-reports/{monthlyReportId}
```

### Purpose

Update monthly report.

### Request Body

```json
{
  "storeId": 1,
  "reportMonth": 5,
  "reportYear": 2026,
  "departmentId": 1,
  "departmentName": "Electronics",
  "gross": 60000,
  "discount": 3000,
  "promotion": 1500,
  "refund": 500,
  "voidAmount": 200,
  "netSales": 54800
}
```

---

# Yearly Report APIs (Admin)

## 18. Create Yearly Report

### Endpoint

```http
POST /api/v1/admin/yearly-reports
```

### Purpose

Create yearly report.

### Request Body

```json
{
  "storeId": 1,
  "reportYear": 2026,
  "annualSummary": "Excellent yearly sales growth"
}
```

---

## 19. Get Yearly Reports

### Endpoint

```http
GET /api/v1/admin/yearly-reports
```

### Purpose

Fetch yearly reports.

### Query Params

| Param    | Type    |
| -------- | ------- |
| storeId  | Long    |
| clientId | Long    |
| year     | Integer |

---

## 20. Get Yearly Reports By Store

### Endpoint

```http
GET /api/v1/admin/yearly-reports/store/{storeId}
```

### Purpose

Fetch yearly reports by store.

---

## 21. Update Yearly Report

### Endpoint

```http
PUT /api/v1/admin/yearly-reports/{yearlyReportId}
```

### Purpose

Update yearly report.

### Request Body

```json
{
  "storeId": 1,
  "reportYear": 2026,
  "annualSummary": "Updated annual summary"
}
```

---

# Client APIs

## 22. Get Client Stores

### Endpoint

```http
GET /api/v1/client/stores
```

### Purpose

Client can view only their stores.

---

## 23. Get Client Daily Reports

### Endpoint

```http
GET /api/v1/client/daily-reports/store/{storeId}
```

### Purpose

Client can view daily reports of their store.

---

## 24. Get Client Monthly Reports

### Endpoint

```http
GET /api/v1/client/monthly-reports/store/{storeId}
```

### Purpose

Client can view monthly reports of their store.

---

## 25. Get Client Yearly Reports

### Endpoint

```http
GET /api/v1/client/yearly-reports/store/{storeId}
```

### Purpose

Client can view yearly reports of their store.

---

# Common Response Structure

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {},
  "errors": {},
  "timestamp": "2026-05-28T13:51:03.542Z"
}
```

---

# Authentication

Use JWT token in Authorization header.

```http
Authorization: Bearer YOUR_JWT_TOKEN
```

---

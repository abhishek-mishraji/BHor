# Analytics API — Software Requirements Specification

**Version:** 2.0  
**Date:** 2026-08-05  
**Base URL:** http://localhost:8080  
**Auth Scheme:** HttpOnly cookies (`access_token`)

> This document describes the analytics endpoints that are implemented in the current codebase. It reflects the controller, request DTO, response DTO, and analytics service behavior that is actually present in the workspace.

---

## 1. Overview

The analytics API exposes dynamic aggregated queries for daily, monthly, and gas-monthly reports. The endpoint returns a chart-ready payload with `labels`, `datasets`, and `meta`.

### Implemented report types

| Report type   | Endpoint                                                                 | Notes                                               |
| ------------- | ------------------------------------------------------------------------ | --------------------------------------------------- |
| `DAILY`       | `/api/v1/admin/analytics/reports` and `/api/v1/client/analytics/reports` | Aggregates daily report values                      |
| `MONTHLY`     | `/api/v1/admin/analytics/reports` and `/api/v1/client/analytics/reports` | Aggregates monthly report values                    |
| `GAS_MONTHLY` | `/api/v1/admin/analytics/reports` and `/api/v1/client/analytics/reports` | Compares two gas-monthly periods for a single store |

---

## 2. Common contract

### Request parameters

The analytics endpoint accepts query parameters via `@ModelAttribute` binding.

Example request body (JSON):

```json
{
  "reportType": "MONTHLY",
  "groupBy": "MONTH",
  "metric": ["gross", "netSales"],
  "aggregate": "SUM",
  "storeIds": [1],
  "year": [2026]
}
```

| Parameter          | Type             | Required                   | Description                                                               |
| ------------------ | ---------------- | -------------------------- | ------------------------------------------------------------------------- |
| `reportType`       | enum             | yes                        | `DAILY`, `MONTHLY`, or `GAS_MONTHLY`                                      |
| `groupBy`          | enum             | yes                        | `DATE`, `MONTH`, `QUARTER`, `YEAR`, `STORE`, `DEPARTMENT`                 |
| `metric`           | list of strings  | yes                        | One or more metric keys                                                   |
| `aggregate`        | enum             | no                         | `SUM`, `AVG`, `MAX`, or `MIN` (defaults to `SUM`)                         |
| `storeIds`         | list of longs    | no                         | Admin may pass store IDs directly; client uses only stores they belong to |
| `clientId`         | long             | no                         | Admin-only scope selector; client requests ignore this field              |
| `from`             | date             | no                         | Daily-only start date                                                     |
| `to`               | date             | no                         | Daily-only end date                                                       |
| `month`            | integer          | no                         | Monthly-only filter                                                       |
| `year`             | list of integers | no                         | Monthly-only year filter                                                  |
| `departmentId`     | string           | no                         | Monthly-only department filter                                            |
| `comparisonAMonth` | integer          | required for `GAS_MONTHLY` | First comparison month                                                    |
| `comparisonAYear`  | integer          | required for `GAS_MONTHLY` | First comparison year                                                     |
| `comparisonBMonth` | integer          | required for `GAS_MONTHLY` | Second comparison month                                                   |
| `comparisonBYear`  | integer          | required for `GAS_MONTHLY` | Second comparison year                                                    |

### Response shape

```json
{
  "success": true,
  "message": "Analytics fetched",
  "data": {
    "labels": ["Jan", "Feb"],
    "datasets": [
      {
        "label": "Gross",
        "metric": "gross",
        "data": [1200, 1500]
      }
    ],
    "meta": {
      "reportType": "MONTHLY",
      "groupBy": "MONTH",
      "aggregate": "SUM",
      "storeIds": [1],
      "year": [2026],
      "totalDataPoints": 2
    }
  },
  "timestamp": "2026-08-05T12:00:00Z"
}
```

### Response fields

| Field               | Type     | Description                              |
| ------------------- | -------- | ---------------------------------------- |
| `labels`            | string[] | X-axis values                            |
| `datasets`          | object[] | One dataset per requested metric         |
| `datasets[].label`  | string   | Human-readable label                     |
| `datasets[].metric` | string   | Raw metric key                           |
| `datasets[].data`   | number[] | Aggregated values                        |
| `meta`              | object   | Query metadata echoed back to the caller |

---

## 3. Validation rules

### 3.1 Common validation

- `reportType` is required.
- `groupBy` is required.
- `metric` must contain at least one item.
- `aggregate` defaults to `SUM`.
- `from` must be before or equal to `to` when both are supplied.
- `month` must be between `1` and `12`.

### 3.2 Daily analytics

Valid `groupBy` values:

- `DATE`
- `STORE`

Valid metrics:

- `groceryTotal`
- `volume`
- `cashDeposit`
- `checkDeposit`
- `overShort`
- `noSale`
- `lineVoid`
- `voidAmount`
- `refunds`

### 3.3 Monthly analytics

Valid `groupBy` values:

- `MONTH`
- `QUARTER`
- `YEAR`
- `STORE`
- `DEPARTMENT`

Valid metrics:

- `gross`
- `netSales`
- `discount`
- `promotion`
- `refund`
- `voidAmount`

Additional rules:

- `year` is required when `groupBy` is `MONTH`, `QUARTER`, or `YEAR`.
- `storeIds` and `year` are required when `groupBy` is `DEPARTMENT`.

### 3.4 Gas-monthly analytics

Valid `groupBy` value:

- `MONTH` only

Valid metrics:

- `CREDIT_FEES`
- `TOTAL_VOLUME_SOLD`
- `NET_PROFIT`
- `NET_PROFIT_PER_GALLON`

Additional rules:

- Exactly one `storeId` is required.
- `comparisonAMonth`, `comparisonAYear`, `comparisonBMonth`, and `comparisonBYear` are all required.
- Each comparison period must be a valid month/year pair.

The response for gas-monthly analytics always uses two labels: `comparisonA` and `comparisonB`, and includes extra fields in each dataset:

- `valueA`
- `valueB`
- `difference`
- `percentageDifference`

---

## 4. Endpoint details

### `GET /api/v1/admin/analytics/reports`

- Auth: `ADMIN`
- Scope: the admin may query by `storeIds` or by `clientId`.
- Validation:
  - `storeIds` and `clientId` cannot both be provided.
  - at least one scope selector is required.

Example request (JSON):

```json
{
  "reportType": "MONTHLY",
  "groupBy": "MONTH",
  "metric": ["gross", "netSales"],
  "aggregate": "SUM",
  "storeIds": [1],
  "year": [2026]
}
```

Example response (JSON):

```json
{
  "success": true,
  "message": "Analytics fetched",
  "data": {
    "labels": ["Jan", "Feb"],
    "datasets": [
      {
        "label": "Gross",
        "metric": "gross",
        "data": [1200, 1500]
      }
    ],
    "meta": {
      "reportType": "MONTHLY",
      "groupBy": "MONTH",
      "aggregate": "SUM",
      "storeIds": [1],
      "year": [2026]
    }
  },
  "timestamp": "2026-08-05T12:00:00Z"
}
```

### `GET /api/v1/client/analytics/reports`

- Auth: `CLIENT`
- Scope: the authenticated client’s allowed stores are resolved automatically.
- Behavior:
  - If `storeIds` are supplied, they must be a subset of the client’s accessible stores.
  - If `storeIds` are omitted, all the client’s stores are included.
  - `clientId` is ignored by the client endpoint.

Example request (JSON):

```json
{
  "reportType": "DAILY",
  "groupBy": "DATE",
  "metric": ["groceryTotal"],
  "storeIds": [1],
  "from": "2026-01-01",
  "to": "2026-01-31"
}
```

Example response (JSON):

```json
{
  "success": true,
  "message": "Analytics fetched",
  "data": {
    "labels": ["2026-01-01", "2026-01-02"],
    "datasets": [
      {
        "label": "Grocery Total",
        "metric": "groceryTotal",
        "data": [1200.5, 1350.75]
      }
    ],
    "meta": {
      "reportType": "DAILY",
      "groupBy": "DATE",
      "aggregate": "SUM",
      "storeIds": [1],
      "from": "2026-01-01",
      "to": "2026-01-31"
    }
  },
  "timestamp": "2026-08-05T12:00:00Z"
}
```

---

## 5. Admin and client behavior examples

### 5.1 Daily example

```http
GET /api/v1/admin/analytics/reports?reportType=DAILY&groupBy=DATE&metric=groceryTotal&metric=volume&storeIds=1&from=2026-01-01&to=2026-01-31
```

### 5.2 Monthly example

```http
GET /api/v1/admin/analytics/reports?reportType=MONTHLY&groupBy=MONTH&metric=gross&metric=netSales&storeIds=1&year=2026&aggregate=SUM
```

### 5.3 Gas-monthly comparison example

```http
GET /api/v1/admin/analytics/reports?reportType=GAS_MONTHLY&groupBy=MONTH&metric=CREDIT_FEES&storeIds=1&comparisonAMonth=1&comparisonAYear=2026&comparisonBMonth=2&comparisonBYear=2026
```

---

## 6. Implementation notes

- The analytics layer uses Criteria API and JPA tuples rather than multiple application-side aggregations.
- The response is intentionally chart-oriented and can be consumed by chart libraries such as Chart.js, Recharts, or ApexCharts.
- The current implementation does not expose yearly report analytics; only daily, monthly, and gas-monthly report analytics are implemented.

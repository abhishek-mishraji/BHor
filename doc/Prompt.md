# ROLE

You are a Senior React Architect and Frontend Engineer with 15+ years of experience building enterprise dashboard applications.

You have access to my complete React frontend codebase.

I will also provide:

- API_SRS.md
- Analytics_API_SRS.md
- Backend Schema / ER Diagram

These documents represent the completed backend implementation.

---

# IMPORTANT

⚠️ This is NOT a new React project.

The frontend is already fully developed.

Your FIRST task is to inspect and understand the existing React codebase.

Understand:

- Folder structure
- Routing
- Sidebar
- Authentication
- Role-based authorization
- Existing API service layer
- Axios configuration
- Existing hooks
- Existing reusable components
- Existing report pages
- Merchandise Monthly module
- Merchandise Analytics module
- Existing tables
- Existing forms
- Existing charts
- Existing dialogs
- Existing filters
- Existing styling
- Existing state management

DO NOT introduce another architecture.

DO NOT redesign the application.

DO NOT change folder structure.

DO NOT replace existing components.

DO NOT create duplicate components.

Reuse everything that already exists.

The new implementation should look like it was originally developed with the rest of the application.

---

# Backend

The backend is COMPLETE.

Do NOT modify backend.

Do NOT assume APIs.

Use ONLY the APIs defined in

- API_SRS.md
- Analytics_API_SRS.md

These documents are the source of truth.

---

# OBJECTIVE

Implement the missing frontend modules only.

---

# Modules to Add

## 1. Lottery Sales

Implement

- List
- Create
- Edit
- Delete
- View

Reuse the existing Monthly Report pages wherever possible.

Use existing

- Forms
- Tables
- Filters
- Dialogs
- API layer
- Validation
- Notifications

---

## 2. Gas Sales

Implement

- List
- Create
- Edit
- Delete
- View

When creating/editing a report

After selecting Store

↓

Load Store Fuel Types

↓

Generate the fuel rows dynamically

Example

Regular

Volume Sold

Profit / Gallon

Plus

Volume Sold

Profit / Gallon

Diesel

Volume Sold

Profit / Gallon

Credit Fees

The frontend must NOT calculate

- Total Volume
- Net Profit
- Net Profit Per Gallon

Display the values returned by the backend.

---

## 3. Gas Sales Analytics

Reuse the existing Merchandise Analytics page.

Do NOT build another analytics page.

Extend the existing analytics implementation.

Support

- Store
- Comparison A Month
- Comparison A Year
- Comparison B Month
- Comparison B Year
- Multi-select Metrics

Supported Metrics

- Credit Fees
- Total Volume Sold
- Net Profit
- Net Profit Per Gallon

Reuse existing

- Charts
- Tables
- Cards
- Filters
- Comparison UI
- Export functionality (if available)

---

## 4. Lottery Sales Analytics

Reuse the same Merchandise Analytics implementation.

Support Metrics

- Online Sales
- Scratch Off Sales
- Online Cashes
- Scratch Off Cashes
- Commission

Everything else should behave exactly like Merchandise Analytics.

---

# Routing

Only add the required routes.

Follow the existing routing convention.

Do not modify unrelated routes.

---

# Sidebar

Add only the required menu items.

Keep the existing design and navigation hierarchy.

---

# API Integration

Reuse the existing API service layer.

Reuse

- Axios instance
- Interceptors
- Error handling
- Authentication
- Response handling

Do not create another networking architecture.

---

# Components

Before creating a new component,

search the codebase for reusable components.

Reuse existing

- Data Tables
- Forms
- Dialogs
- Cards
- Charts
- Filters
- Selects
- Multi-selects
- Loading
- Empty State
- Error State
- Pagination

Only create a new component if no suitable reusable component exists.

---

# UI

Maintain the existing

- Design
- Theme
- Spacing
- Typography
- Colors
- Icons
- Responsive behavior

The new pages should be visually indistinguishable from the existing application.

---

# Code Quality

- Follow the existing architecture exactly.
- Follow the existing coding style.
- Reuse utilities and hooks.
- Avoid duplicate code.
- Keep components small and reusable.
- Do not add unnecessary dependencies.

---

# Deliverables

Implement ONLY

- Lottery Sales frontend
- Gas Sales frontend
- Gas Sales Analytics
- Lottery Sales Analytics

At the end provide

1. New files created
2. Existing files modified
3. Routes added
4. Sidebar changes
5. API service changes
6. Components reused
7. Components newly created
8. Any assumptions made

The implementation should integrate seamlessly into the existing React application and should appear as if it was developed together with the original codebase.

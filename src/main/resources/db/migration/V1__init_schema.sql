-- Initial schema for Hands Of Retail
-- Creates core tables used by the application

CREATE TABLE IF NOT EXISTS client_users (
  client_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  phone_number VARCHAR(50),
  address TEXT,
  status VARCHAR(20) NOT NULL,
  role VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS admin_users (
  admin_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stores (
  store_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  client_id BIGINT NOT NULL,
  store_name VARCHAR(255) NOT NULL,
  store_code VARCHAR(100) NOT NULL UNIQUE,
  address TEXT,
  contact_number VARCHAR(50),
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,
  CONSTRAINT fk_store_client FOREIGN KEY (client_id) REFERENCES client_users(client_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS daily_reports (
  daily_report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  store_id BIGINT NOT NULL,
  report_date DATE NOT NULL,
  grocery_total DECIMAL(15,2),
  volume DECIMAL(15,2),
  cash_deposit DECIMAL(15,2),
  check_deposit DECIMAL(15,2),
  over_short DECIMAL(15,2),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,
  CONSTRAINT fk_daily_store FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS monthly_reports (
  monthly_report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  store_id BIGINT NOT NULL,
  report_month INT NOT NULL,
  report_year INT NOT NULL,
  department_id INT,
  department_name VARCHAR(255),
  gross DECIMAL(15,2),
  discount DECIMAL(15,2),
  promotion DECIMAL(15,2),
  refund DECIMAL(15,2),
  void_amount DECIMAL(15,2),
  net_sales DECIMAL(15,2),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,
  CONSTRAINT fk_monthly_store FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS yearly_reports (
  yearly_report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  store_id BIGINT NOT NULL,
  report_year INT NOT NULL,
  annual_summary TEXT,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP,
  CONSTRAINT fk_yearly_store FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS fuel_types (
    fuel_type_id BIGSERIAL PRIMARY KEY,
    fuel_name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT uk_fuel_types_name UNIQUE (fuel_name)
);

CREATE TABLE IF NOT EXISTS store_fuel_types (
    store_fuel_type_id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    fuel_type_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_store_fuel_types_store
        FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE,
    CONSTRAINT fk_store_fuel_types_fuel_type
        FOREIGN KEY (fuel_type_id) REFERENCES fuel_types(fuel_type_id) ON DELETE CASCADE,
    CONSTRAINT uk_store_fuel_types_store_fuel_type
        UNIQUE (store_id, fuel_type_id)
);

CREATE INDEX IF NOT EXISTS idx_store_fuel_types_store
    ON store_fuel_types(store_id);
CREATE INDEX IF NOT EXISTS idx_store_fuel_types_fuel_type
    ON store_fuel_types(fuel_type_id);

CREATE TABLE IF NOT EXISTS gas_sales_reports_monthly (
    gas_sales_report_monthly_id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    report_month INTEGER NOT NULL,
    report_year INTEGER NOT NULL,
    credit_fees NUMERIC(19,4) NOT NULL,
    total_volume_sold NUMERIC(19,4) NOT NULL,
    net_profit_per_gallon NUMERIC(19,4) NOT NULL,
    net_profit NUMERIC(19,4) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_gas_sales_reports_monthly_store
        FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE,
    CONSTRAINT uk_gas_sales_reports_monthly_store_period
        UNIQUE (store_id, report_month, report_year)
);

CREATE INDEX IF NOT EXISTS idx_gas_sales_reports_monthly_store
    ON gas_sales_reports_monthly(store_id);

CREATE TABLE IF NOT EXISTS gas_sales_report_details (
    gas_sales_report_detail_id BIGSERIAL PRIMARY KEY,
    gas_sales_report_monthly_id BIGINT NOT NULL,
    fuel_type_id BIGINT NOT NULL,
    volume_sold NUMERIC(19,4) NOT NULL,
    profit_per_gallon NUMERIC(19,4) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_gas_sales_report_details_monthly_report
        FOREIGN KEY (gas_sales_report_monthly_id)
        REFERENCES gas_sales_reports_monthly(gas_sales_report_monthly_id) ON DELETE CASCADE,
    CONSTRAINT fk_gas_sales_report_details_fuel_type
        FOREIGN KEY (fuel_type_id) REFERENCES fuel_types(fuel_type_id) ON DELETE CASCADE,
    CONSTRAINT uk_gas_sales_report_details_report_fuel_type
        UNIQUE (gas_sales_report_monthly_id, fuel_type_id)
);

CREATE INDEX IF NOT EXISTS idx_gas_sales_report_details_monthly_report
    ON gas_sales_report_details(gas_sales_report_monthly_id);
CREATE INDEX IF NOT EXISTS idx_gas_sales_report_details_fuel_type
    ON gas_sales_report_details(fuel_type_id);
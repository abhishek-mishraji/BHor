CREATE TABLE IF NOT EXISTS lottery_sales_reports_monthly (
    lottery_sales_report_monthly_id BIGSERIAL PRIMARY KEY,
    store_id BIGINT NOT NULL,
    report_month INTEGER NOT NULL,
    report_year INTEGER NOT NULL,
    online_sales NUMERIC(15,2) NOT NULL,
    scratch_off_sales NUMERIC(15,2) NOT NULL,
    online_cashes NUMERIC(15,2) NOT NULL,
    scratch_off_cashes NUMERIC(15,2) NOT NULL,
    commission NUMERIC(15,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_lottery_sales_report_monthly_store
        FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE,
    CONSTRAINT uk_lottery_sales_report_monthly_store_period
        UNIQUE (store_id, report_month, report_year)
);
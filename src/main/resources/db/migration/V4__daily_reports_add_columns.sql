-- Add no_sale, line_void, void_amount, refunds to daily_reports

ALTER TABLE daily_reports
    ADD COLUMN no_sale      NUMERIC(15,2) DEFAULT 0,
    ADD COLUMN line_void    NUMERIC(15,2) DEFAULT 0,
    ADD COLUMN void_amount  NUMERIC(15,2) DEFAULT 0,
    ADD COLUMN refunds      NUMERIC(15,2) DEFAULT 0;

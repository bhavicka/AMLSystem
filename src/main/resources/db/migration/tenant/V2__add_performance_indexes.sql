--For sliding window rule engine query
CREATE INDEX IF NOT EXISTS idx_txn_type_date
    ON transactions (transaction_type, transaction_date);

--for fetching transactions of a particular client
CREATE INDEX IF NOT EXISTS idx_acc_client_num
    ON accounts (client_number);

--for fetching all alerts for a particular case
CREATE INDEX IF NOT EXISTS idx_alert_case_id
    ON alerts (case_id);

--for alert dashboard - if there are more alerts
CREATE INDEX IF NOT EXISTS idx_alert_status_client
    ON alerts (status, client_number);

--will work if there are more errors for a file
CREATE INDEX IF NOT EXISTS idx_fve_file_id
    ON file_validation_errors (file_id);
ALTER TABLE diwi.data_exchange_state
    ADD COLUMN client_id uuid DEFAULT NULL;

ALTER TABLE diwi.data_exchange_state
    ADD COLUMN user_id uuid DEFAULT NULL;


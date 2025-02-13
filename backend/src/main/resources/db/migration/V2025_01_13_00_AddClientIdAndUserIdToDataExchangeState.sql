ALTER TABLE diwi.data_exchange_state
    ADD COLUMN client_id uuid DEFAULT NULL;

ALTER TABLE diwi.data_exchange_state
    ADD COLUMN user_id uuid DEFAULT NULL;

ALTER TABLE diwi.data_exchange_state
    ADD CONSTRAINT unique_client_user UNIQUE (client_id, user_id);

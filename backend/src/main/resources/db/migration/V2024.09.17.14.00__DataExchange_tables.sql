CREATE TYPE diwi.data_exchange_type AS ENUM (
    'ESRI_ZUID_HOLLAND'
);

CREATE TABLE diwi.data_exchange (
    id UUID NOT NULL
);

ALTER TABLE ONLY diwi.data_exchange
    ADD CONSTRAINT data_exchange_pkey PRIMARY KEY (id);


CREATE TABLE diwi.data_exchange_state (
    id UUID NOT NULL,
    data_exchange_id UUID NOT NULL,
    name TEXT NOT NULL,
    type diwi.data_exchange_type NOT NULL,
    api_key TEXT,
    project_url TEXT,
    project_detail_url TEXT,
    create_user_id UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_user_id UUID,
    change_end_date timestamp with time zone
);

ALTER TABLE ONLY diwi.data_exchange_state
    ADD CONSTRAINT data_exchange_state_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.data_exchange_state
    ADD CONSTRAINT fk_data_exchange_state__data_exchange FOREIGN KEY ("data_exchange_id") REFERENCES diwi."data_exchange"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_state
    ADD CONSTRAINT fk_data_exchange_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_state
    ADD CONSTRAINT fk_data_exchange_state__create_user FOREIGN KEY ("create_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TYPE diwi.ownership_category_type AS ENUM(
    'KOOP1',
    'KOOP2',
    'KOOP3',
    'KOOP4',
    'KOOP_ONB',
    'HUUR1',
    'HUUR2',
    'HUUR3',
    'HUUR4',
    'HUUR_ONB'
);

CREATE TABLE IF NOT EXISTS diwi.data_exchange_price_category_mapping(
    id uuid NOT NULL,
    data_exchange_id uuid NOT NULL,
    ownership_category diwi.ownership_category_type NOT NULL
);

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping
    ADD CONSTRAINT data_exchange_price_category_mapping_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping
    ADD CONSTRAINT fk_data_exchange_price_category_mapping__data_exchange FOREIGN KEY ("data_exchange_id") REFERENCES diwi."data_exchange"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS diwi.data_exchange_price_category_mapping_state(
    id uuid NOT NULL,
    data_exchange_price_category_mapping_id uuid NOT NULL,
    price_range_id UUID NOT NULL,
    create_user_id uuid NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_user_id uuid,
    change_end_date timestamp with time zone
);

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping_state
    ADD CONSTRAINT data_exchange_price_category_mapping_state_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping_state
    ADD CONSTRAINT fk_data_exchange_price_category_mapping_state__data_exchange_price_category_mapping FOREIGN KEY ("data_exchange_price_category_mapping_id") REFERENCES diwi."data_exchange_price_category_mapping"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping_state
    ADD CONSTRAINT fk_data_exchange_price_category_mapping_state__property_range_category_value FOREIGN KEY ("price_range_id") REFERENCES diwi."property_range_category_value"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping_state
    ADD CONSTRAINT fk_data_exchange_price_category_mapping_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping_state
    ADD CONSTRAINT fk_data_exchange_price_category_mapping_state__create_user FOREIGN KEY ("create_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

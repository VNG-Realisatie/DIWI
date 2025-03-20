CREATE TABLE diwi.data_exchange_price_category_mapping IF NOT EXISTS (
    id UUID NOT NULL,
    data_exchange_id UUID NOT NULL,
);

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping
    ADD CONSTRAINT data_exchange_price_category_mapping_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping
    ADD CONSTRAINT fk_data_exchange_price_category_mapping__data_exchange FOREIGN KEY ("data_exchange_id") REFERENCES diwi."data_exchange"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


CREATE TABLE diwi.data_exchange_price_category_mapping_state IF NOT EXISTS (
    id UUID NOT NULL,
    data_exchange_price_category_mapping_id UUID NOT NULL,
    -- property_id UUID NOT NULL,
    create_user_id UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_user_id UUID,
    change_end_date timestamp with time zone
);

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping_state
    ADD CONSTRAINT data_exchange_price_category_mapping_state_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping_state
    ADD CONSTRAINT fk_data_exchange_price_category_mapping_state__data_exchange_price_category_mapping FOREIGN KEY ("data_exchange_price_category_mapping_id") REFERENCES diwi."data_exchange_price_category_mapping"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping_state
    ADD CONSTRAINT fk_data_exchange_price_category_mapping_state__property FOREIGN KEY ("property_id") REFERENCES diwi."property"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping_state
    ADD CONSTRAINT fk_data_exchange_price_category_mapping_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_price_category_mapping_state
    ADD CONSTRAINT fk_data_exchange_price_category_mapping_state__create_user FOREIGN KEY ("create_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

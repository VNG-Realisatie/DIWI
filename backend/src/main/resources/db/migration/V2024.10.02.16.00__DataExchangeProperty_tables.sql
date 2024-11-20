CREATE TABLE diwi.data_exchange_property (
    id UUID NOT NULL,
    data_exchange_id UUID NOT NULL,
    data_exchange_property_name TEXT NOT NULL,
    object_type diwi.maatwerk_object_soort NOT NULL,
    property_type diwi.maatwerk_eigenschap_type[] NOT NULL,
    mandatory BOOL NOT NULL,
    single_select BOOL
);

ALTER TABLE ONLY diwi.data_exchange_property
    ADD CONSTRAINT data_exchange_property_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.data_exchange_property
    ADD CONSTRAINT fk_data_exchange_property__data_exchange FOREIGN KEY ("data_exchange_id") REFERENCES diwi."data_exchange"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


CREATE TABLE diwi.data_exchange_property_state (
    id UUID NOT NULL,
    data_exchange_property_id UUID NOT NULL,
    property_id UUID NOT NULL,
    create_user_id UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_user_id UUID,
    change_end_date timestamp with time zone
);

ALTER TABLE ONLY diwi.data_exchange_property_state
    ADD CONSTRAINT data_exchange_property_state_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.data_exchange_property_state
    ADD CONSTRAINT fk_data_exchange_property_state__data_exchange_property FOREIGN KEY ("data_exchange_property_id") REFERENCES diwi."data_exchange_property"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_property_state
    ADD CONSTRAINT fk_data_exchange_property_state__property FOREIGN KEY ("property_id") REFERENCES diwi."property"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_property_state
    ADD CONSTRAINT fk_data_exchange_property_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_property_state
    ADD CONSTRAINT fk_data_exchange_property_state__create_user FOREIGN KEY ("create_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


CREATE TABLE diwi.data_exchange_option (
    id UUID NOT NULL,
    data_exchange_property_id UUID NOT NULL,
    data_exchange_option_name TEXT NOT NULL
);

ALTER TABLE ONLY diwi.data_exchange_option
    ADD CONSTRAINT data_exchange_option_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.data_exchange_option
    ADD CONSTRAINT fk_data_exchange_option__data_exchange_property FOREIGN KEY ("data_exchange_property_id") REFERENCES diwi."data_exchange_property"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


CREATE TABLE diwi.data_exchange_option_state (
    id UUID NOT NULL,
    data_exchange_option_id UUID NOT NULL,
    property_category_value_id UUID,
    property_ordinal_value_id UUID,
    create_user_id UUID NOT NULL,
    change_start_date timestamp with time zone NOT NULL,
    change_user_id UUID,
    change_end_date timestamp with time zone
);

ALTER TABLE ONLY diwi.data_exchange_option_state
    ADD CONSTRAINT data_exchange_option_state_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.data_exchange_option_state
    ADD CONSTRAINT fk_data_exchange_option_state__data_exchange_option FOREIGN KEY ("data_exchange_option_id") REFERENCES diwi."data_exchange_option"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_option_state
    ADD CONSTRAINT fk_data_exchange_option_state__property_category_value FOREIGN KEY ("property_category_value_id") REFERENCES diwi."property_category_value"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_option_state
    ADD CONSTRAINT fk_data_exchange_option_state__property_ordinal_value FOREIGN KEY ("property_ordinal_value_id") REFERENCES diwi."property_ordinal_value"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_option_state
    ADD CONSTRAINT fk_data_exchange_option_state__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.data_exchange_option_state
    ADD CONSTRAINT fk_data_exchange_option_state__create_user FOREIGN KEY ("create_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


CREATE TABLE diwi.property_range_category_value (
    id UUID NOT NULL,
    property_id UUID NOT NULL
);

ALTER TABLE ONLY diwi.property_range_category_value
    ADD CONSTRAINT property_range_category_value_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.property_range_category_value
    ADD CONSTRAINT fk_range_category__property FOREIGN KEY ("property_id") REFERENCES diwi.property("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

CREATE TABLE diwi.property_range_category_value_state (
    id UUID NOT NULL,
    range_category_value_id UUID NOT NULL,
    name text NOT NULL,
    min numeric(16,6) NOT NULL,
    max numeric(16,6),
    create_user_id UUID NOT NULL,
    change_user_id UUID,
    change_start_date timestamp with time zone NOT NULL,
    change_end_date timestamp with time zone
);

ALTER TABLE ONLY diwi.property_range_category_value_state
    ADD CONSTRAINT property_range_category_value_state_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.property_range_category_value_state
    ADD CONSTRAINT fk_range_category_value__range_category FOREIGN KEY ("range_category_value_id") REFERENCES diwi.property_range_category_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.property_range_category_value_state
    ADD CONSTRAINT fk_range_category_value__change_user FOREIGN KEY ("change_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.property_range_category_value_state
    ADD CONSTRAINT fk_range_category_value__create_user FOREIGN KEY ("create_user_id") REFERENCES diwi."user"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TYPE diwi.maatwerk_eigenschap_type ADD VALUE 'RANGE_CATEGORY';

CREATE TABLE diwi.blueprint_to_category (
    id UUID NOT NULL,
    blueprint_state_id UUID NOT NULL,
    plan_category_id UUID NOT NULL
);

ALTER TABLE ONLY diwi.blueprint_to_category
    ADD CONSTRAINT blueprint_to_category_pkey PRIMARY KEY (id);

ALTER TABLE ONLY diwi.blueprint_to_category
    ADD CONSTRAINT fk_blueprint_to_category__blueprint_state FOREIGN KEY ("blueprint_state_id") REFERENCES diwi."blueprint_state"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.blueprint_to_category
    ADD CONSTRAINT fk_blueprint_to_category__category FOREIGN KEY ("plan_category_id") REFERENCES diwi."plan_category"("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

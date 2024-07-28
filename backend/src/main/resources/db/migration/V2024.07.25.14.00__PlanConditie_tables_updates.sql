ALTER TABLE diwi.plan_conditie_state DROP COLUMN plan_id;

ALTER TABLE diwi.plan_conditie
    ADD COLUMN IF NOT EXISTS plan_id UUID NOT NULL;
ALTER TABLE ONLY diwi.plan_conditie
    ADD CONSTRAINT fk_plan_conditie__plan FOREIGN KEY ("plan_id") REFERENCES diwi.plan("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

DROP TABLE diwi.plan_state_soort_value;
ALTER TABLE diwi.plan_soort RENAME TO plan_category;
ALTER TABLE diwi.plan_soort_state RENAME TO plan_category_state;
ALTER TABLE diwi.plan_category_state RENAME COLUMN plan_soort_id TO plan_category_id;
ALTER TABLE diwi.plan_category_state RENAME COLUMN waarde_label TO value_label;

ALTER TABLE diwi.plan_state
    ADD COLUMN IF NOT EXISTS plan_category_id UUID;
ALTER TABLE ONLY diwi.plan_state
    ADD CONSTRAINT fk_plan_state__plan_category FOREIGN KEY ("plan_category_id") REFERENCES diwi.plan_category("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi.plan_conditie_type_en_fysiek_fysiek_value
    DROP COLUMN fysiek_voorkomen;
ALTER TABLE diwi.plan_conditie_type_en_fysiek_fysiek_value
    ADD COLUMN IF NOT EXISTS property_value_id UUID NOT NULL;
ALTER TABLE ONLY diwi.plan_conditie_type_en_fysiek_fysiek_value
    ADD CONSTRAINT fk_plan_conditie_type_en_fysiek_fysiek_value__property_category_value FOREIGN KEY ("property_value_id") REFERENCES diwi.property_category_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

DROP TYPE diwi.fysiek_voorkomen;

ALTER TABLE diwi.plan_conditie_doelgroep_value
    DROP COLUMN doelgroep;
ALTER TABLE diwi.plan_conditie_doelgroep_value
    ADD COLUMN IF NOT EXISTS property_value_id UUID NOT NULL;
ALTER TABLE ONLY diwi.plan_conditie_doelgroep_value
    ADD CONSTRAINT fk_pplan_conditie_doelgroep_value__property_category_value FOREIGN KEY ("property_value_id") REFERENCES diwi.property_category_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

DROP TYPE diwi.doelgroep;

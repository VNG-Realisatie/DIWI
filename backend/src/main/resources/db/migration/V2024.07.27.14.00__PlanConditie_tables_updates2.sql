ALTER TABLE diwi.plan_conditie_kadastraal RENAME TO plan_conditie_registry_link;
ALTER TABLE diwi.plan_conditie_kadastraal_value RENAME TO plan_conditie_registry_link_value;
ALTER TABLE diwi.plan_conditie_registry_link_value
    RENAME COLUMN plan_conditie_kadastraal_id TO plan_conditie_registry_link_id;

DROP TABLE diwi.plan_conditie_eigendom_en_waarde_soort_value;

ALTER TABLE diwi.plan_conditie_eigendom_en_waarde
    ADD COLUMN IF NOT EXISTS eigendom_soort diwi.eigendom_soort NOT NULL;

ALTER TABLE diwi.plan_conditie_eigendom_en_waarde
    DROP COLUMN waarde_value_type,
    DROP COLUMN huurbedrag_value_type,
    DROP COLUMN waarde_value_range,
    DROP COLUMN huurbedrag_value_range;

ALTER TABLE diwi.plan_conditie_eigendom_en_waarde
    ADD COLUMN IF NOT EXISTS waarde_value_range INT4RANGE,
    ADD COLUMN IF NOT EXISTS huurbedrag_value_range INT4RANGE,
    ADD COLUMN IF NOT EXISTS ownership_property_value_id UUID,
    ADD COLUMN IF NOT EXISTS rental_property_value_id UUID;

ALTER TABLE ONLY diwi.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT fk_plan_conditie_eigendom_en_waarde__ownership_property_value FOREIGN KEY ("ownership_property_value_id") REFERENCES diwi.property_range_category_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.plan_conditie_eigendom_en_waarde
    ADD CONSTRAINT fk_plan_conditie_eigendom_en_waarde__rental_property_value FOREIGN KEY ("rental_property_value_id") REFERENCES diwi.property_range_category_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi.woningblok_eigendom_en_waarde_changelog
    ADD COLUMN IF NOT EXISTS ownership_property_value_id UUID;

ALTER TABLE diwi.woningblok_eigendom_en_waarde_changelog
    ADD COLUMN IF NOT EXISTS rental_property_value_id UUID;

ALTER TABLE ONLY diwi.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__ownership_property_value FOREIGN KEY ("ownership_property_value_id") REFERENCES diwi.property_range_category_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE ONLY diwi.woningblok_eigendom_en_waarde_changelog
    ADD CONSTRAINT fk_woningblok_eigendom_en_waarde_changelog__rental_property_value FOREIGN KEY ("rental_property_value_id") REFERENCES diwi.property_range_category_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi.woningblok_eigendom_en_waarde_changelog
    DROP COLUMN waarde_value_type;
ALTER TABLE diwi.woningblok_eigendom_en_waarde_changelog
    DROP COLUMN huurbedrag_value_type;

ALTER TABLE diwi.property_state
    ADD COLUMN mandatory BOOL NOT NULL DEFAULT false;

ALTER TABLE diwi.property_state
    ADD COLUMN single_select BOOL;

UPDATE diwi.property_state
    SET single_select = false WHERE property_type = 'CATEGORY';

DROP INDEX diwi.idx_unique_maatwerk_eigenschap_state__eigenschap_naam;

CREATE UNIQUE INDEX idx_unique_property_state__name_mandatory_singleselect
    ON diwi.property_state (property_name, mandatory, single_select)
    WHERE change_end_date IS NULL;

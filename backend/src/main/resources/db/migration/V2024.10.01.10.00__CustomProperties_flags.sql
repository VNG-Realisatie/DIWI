ALTER TABLE diwi.property_state
    ADD COLUMN mandatory BOOL NOT NULL DEFAULT false;

ALTER TABLE diwi.property_state
    ADD COLUMN single_select BOOL;

UPDATE diwi.property_state
    SET single_select = false WHERE property_type = 'CATEGORY';


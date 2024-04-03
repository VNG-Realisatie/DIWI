CREATE TYPE diwi_testset.property_type AS ENUM (
    'FIXED',
    'CUSTOM'
);

ALTER TABLE ONLY diwi_testset.maatwerk_eigenschap
    ADD COLUMN IF NOT EXISTS type diwi_testset.property_type NOT NULL DEFAULT 'CUSTOM';

ALTER TABLE diwi_testset.maatwerk_eigenschap_state
    RENAME COLUMN eigenschap_id TO property_id;
ALTER TABLE diwi_testset.maatwerk_eigenschap_state
    RENAME COLUMN eigenschap_naam TO property_name;
ALTER TABLE diwi_testset.maatwerk_eigenschap_state
    RENAME COLUMN eigenschap_type TO property_type;
ALTER TABLE diwi_testset.maatwerk_eigenschap_state
    RENAME COLUMN eigenschap_object_soort TO property_object_type;

ALTER TABLE diwi_testset.maatwerk_categorie_waarde
    RENAME COLUMN maatwerk_eigenschap_id TO property_id;
ALTER TABLE diwi_testset.maatwerk_categorie_waarde_state
    RENAME COLUMN categorie_waarde_id TO category_value_id;
ALTER TABLE diwi_testset.maatwerk_categorie_waarde_state
    RENAME COLUMN waarde_label TO value_label;

ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde
    RENAME COLUMN maatwerk_eigenschap_id TO property_id;
ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde_state
    RENAME COLUMN ordinaal_waarde_id TO ordinal_value_id;
ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde_state
    RENAME COLUMN waarde_label TO value_label;
ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde_state
    RENAME COLUMN ordinaal_niveau TO ordinal_level;


ALTER TABLE diwi_testset.maatwerk_eigenschap RENAME TO property;
ALTER TABLE diwi_testset.maatwerk_eigenschap_state RENAME TO property_state;

ALTER TABLE diwi_testset.maatwerk_categorie_waarde RENAME TO property_category_value;
ALTER TABLE diwi_testset.maatwerk_categorie_waarde_state RENAME TO property_category_value_state;

ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde RENAME TO property_ordinal_value;
ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde_state RENAME TO property_ordinal_value_state;

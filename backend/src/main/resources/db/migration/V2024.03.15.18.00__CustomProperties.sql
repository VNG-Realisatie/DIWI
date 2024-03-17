ALTER TABLE diwi_testset.maatwerk_categorie_waarde_state
    DROP COLUMN maatwerk_eigenschap_id;
ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde_state
    DROP COLUMN maatwerk_eigenschap_id;

ALTER TABLE diwi_testset.maatwerk_categorie_waarde
    ADD COLUMN IF NOT EXISTS maatwerk_eigenschap_id UUID NOT NULL;

ALTER TABLE ONLY diwi_testset.maatwerk_categorie_waarde
    ADD CONSTRAINT fk_maatwerk_categorie_waarde__maatwerk_eigenschap FOREIGN KEY ("maatwerk_eigenschap_id") REFERENCES diwi_testset.maatwerk_eigenschap("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE diwi_testset.maatwerk_ordinaal_waarde
    ADD COLUMN IF NOT EXISTS maatwerk_eigenschap_id UUID NOT NULL;

ALTER TABLE ONLY diwi_testset.maatwerk_ordinaal_waarde
    ADD CONSTRAINT fk_maatwerk_ordinal_waarde__maatwerk_eigenschap FOREIGN KEY ("maatwerk_eigenschap_id") REFERENCES diwi_testset.maatwerk_eigenschap("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TYPE diwi_testset.maatwerk_eigenschap_type ADD VALUE 'TEXT';

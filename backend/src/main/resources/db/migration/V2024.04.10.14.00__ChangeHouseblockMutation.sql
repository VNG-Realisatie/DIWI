ALTER TABLE diwi_testset.woningblok_mutatie_changelog
    DROP COLUMN bruto_plancapaciteit;
ALTER TABLE diwi_testset.woningblok_mutatie_changelog
    DROP COLUMN sloop;

ALTER TABLE diwi_testset.woningblok_mutatie_changelog
    RENAME COLUMN netto_plancapaciteit TO amount;
DELETE FROM diwi_testset.woningblok_mutatie_changelog
    WHERE amount IS NULL OR amount <= 0;
ALTER TABLE diwi_testset.woningblok_mutatie_changelog
    ALTER COLUMN amount SET NOT NULL;

CREATE TYPE diwi_testset.mutation_kind AS ENUM (
    'CONSTRUCTION',
    'DEMOLITION'
);

ALTER TABLE diwi_testset.woningblok_mutatie_changelog
    ADD COLUMN mutation_kind diwi_testset.mutation_kind;
UPDATE diwi_testset.woningblok_mutatie_changelog
    SET mutation_kind = 'CONSTRUCTION';
ALTER TABLE diwi_testset.woningblok_mutatie_changelog
    ALTER COLUMN mutation_kind SET NOT NULL;

DROP TABLE diwi_testset.woningblok_mutatie_changelog_soort_value;
DROP TYPE diwi_testset.mutatie_soort;

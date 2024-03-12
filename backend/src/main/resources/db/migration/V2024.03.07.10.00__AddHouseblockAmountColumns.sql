ALTER TABLE diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
    ADD COLUMN IF NOT EXISTS amount integer NOT NULL;
ALTER TABLE diwi_testset.woningblok_type_en_fysiek_changelog_type_value
    ADD COLUMN IF NOT EXISTS amount integer NOT NULL;
ALTER TABLE diwi_testset.woningblok_grondpositie_changelog_value
    ADD COLUMN IF NOT EXISTS amount integer NOT NULL;
ALTER TABLE diwi_testset.woningblok_eigendom_en_waarde_changelog
    ADD COLUMN IF NOT EXISTS amount integer NOT NULL;

ALTER TABLE diwi_testset.woningblok_doelgroep_changelog_value
    ADD COLUMN IF NOT EXISTS amount integer NOT NULL;

ALTER TABLE diwi_testset.woningblok_eigendom_en_waarde_changelog
    ADD COLUMN IF NOT EXISTS eigendom_soort diwi_testset.eigendom_soort NOT NULL;

DROP TABLE IF EXISTS diwi_testset.woningblok_state;
DROP TABLE IF EXISTS diwi_testset.woningblok_eigendom_en_waarde_changelog_soort_value;

ALTER TABLE diwi_testset.woningblok_eigendom_en_waarde_changelog
    DROP COLUMN waarde_value_range,
    DROP COLUMN huurbedrag_value_range;

ALTER TABLE diwi_testset.woningblok_eigendom_en_waarde_changelog
    ADD COLUMN IF NOT EXISTS waarde_value_range INT4RANGE,
    ADD COLUMN IF NOT EXISTS huurbedrag_value_range INT4RANGE;


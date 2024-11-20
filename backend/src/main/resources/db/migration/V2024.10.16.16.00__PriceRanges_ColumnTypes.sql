ALTER TABLE diwi.woningblok_eigendom_en_waarde_changelog
    ALTER COLUMN waarde_value TYPE BIGINT;
ALTER TABLE diwi.woningblok_eigendom_en_waarde_changelog
    ALTER COLUMN huurbedrag_value TYPE BIGINT;
ALTER TABLE diwi.woningblok_eigendom_en_waarde_changelog
    ALTER COLUMN huurbedrag_value_range TYPE int8range
        USING (
            CASE
                WHEN huurbedrag_value_range IS NULL THEN null
                ELSE int8range(LOWER(huurbedrag_value_range), UPPER(huurbedrag_value_range))
            END);

ALTER TABLE diwi.woningblok_eigendom_en_waarde_changelog
    ALTER COLUMN waarde_value_range TYPE int8range
        USING (
            CASE
                WHEN waarde_value_range IS NULL THEN null
                ELSE int8range(LOWER(waarde_value_range), UPPER(waarde_value_range))
            END);

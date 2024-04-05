WITH prop_postfix AS (
    SELECT id, row_number() OVER (PARTITION BY eigenschap_naam) rn
    FROM diwi_testset.maatwerk_eigenschap_state WHERE change_end_date IS NULL
)
UPDATE diwi_testset.maatwerk_eigenschap_state mes
    SET eigenschap_naam = eigenschap_naam || CASE WHEN prop_postfix.rn = 1 THEN '' ELSE '_' || (prop_postfix.rn-1) END
    FROM prop_postfix
    WHERE prop_postfix.id = mes.id;

CREATE UNIQUE INDEX idx_unique_maatwerk_eigenschap_state__eigenschap_naam
    ON diwi_testset.maatwerk_eigenschap_state (eigenschap_naam)
    WHERE change_end_date IS NULL;

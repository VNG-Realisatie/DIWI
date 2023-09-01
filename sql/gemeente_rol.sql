SELECT
    json_agg(pgvs)
FROM
    diwi_testset_simplified.project_gemeenterol_value_state pgvs
WHERE
    pgvs.change_end_date IS NULL

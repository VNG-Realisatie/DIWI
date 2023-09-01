SELECT
    json_agg(gs)
FROM
    diwi_testset_simplified.gemeente_state gs
WHERE
    gs.change_end_date IS NULL

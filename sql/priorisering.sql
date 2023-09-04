SELECT
    json_agg(ppvs)
FROM
    diwi_testset_simplified.project_priorisering_value_state ppvs
WHERE
    ppvs.change_end_date IS NULL

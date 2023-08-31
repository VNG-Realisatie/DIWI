SELECT
    json_agg(bs)
FROM
    diwi_testset_simplified.buurt_state bs
WHERE
    bs.change_end_date IS NULL

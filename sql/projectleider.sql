SELECT
    json_agg("as")
FROM
    diwi_testset_simplified.actor_state AS "as"
WHERE
    "as".change_end_date IS NULL

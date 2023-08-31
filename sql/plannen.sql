WITH
plannen AS (
    SELECT
        plan_state.*
    FROM
        diwi_testset_simplified.plan AS plan
        INNER JOIN diwi_testset_simplified.plan_state AS plan_state ON plan_state."plan_ID" = plan."ID"
)
SELECT
    json_agg(to_jsonb(p))
FROM
    plannen AS p

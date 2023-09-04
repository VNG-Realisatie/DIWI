WITH
plannen AS (
    SELECT
        plan_state.*
    FROM
        diwi_testset_simplified.plan AS plan
        INNER JOIN diwi_testset_simplified.plan_state AS plan_state ON plan_state."plan_ID" = plan."ID"
    ORDER BY
        plan."ID" ASC
),
plannen_with_condities AS (
    SELECT
        to_jsonb (p) AS plan
    FROM
        plannen AS p
    GROUP BY
        plan
)
SELECT
    json_agg(p)
FROM
    plannen_with_condities AS p

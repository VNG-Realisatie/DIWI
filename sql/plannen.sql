WITH
plan_condities AS (
    SELECT
        pc."ID" as id,
        pcs."plan_ID" as plan_id,
        pcs.conditie_type
    FROM
        diwi_testset_simplified.plan_conditie pc
        LEFT JOIN diwi_testset_simplified.plan_conditie_state pcs ON pcs."plan_conditie_ID" = pc."ID"
            AND pcs.change_end_date IS NULL
    ORDER BY
        pc."ID" ASC
),
plannen AS (
    SELECT
        plan."ID" AS id,
        plan_state.*,
        pss.waarde_label AS plan_soort
    FROM
        diwi_testset_simplified.plan AS plan
        INNER JOIN diwi_testset_simplified.plan_state AS plan_state ON plan_state."plan_ID" = plan."ID"
            AND plan_state.change_end_date IS NULL
        INNER JOIN diwi_testset_simplified.plan_soort_state AS pss ON pss."plan_soort_ID" = plan_state."plan_soort_ID"
            AND pss.change_end_date IS NULL
    ORDER BY
        plan."ID" ASC
),
plannen_with_condities AS (
    SELECT
        to_jsonb (p) AS plan,
        COALESCE(json_agg(c.*) FILTER (WHERE c.id IS NOT NULL), '[]') AS condities,
        COALESCE(json_agg(d.*) FILTER (WHERE d.id IS NOT NULL), '[]') AS doelen
    FROM
        plannen AS p
        LEFT JOIN plan_condities AS c ON c.plan_id = p.id
            AND c.conditie_type = 'plan_conditie'
        LEFT JOIN plan_condities AS d ON d.plan_id = p.id
            AND d.conditie_type = 'doel_conditie'
    GROUP BY
        plan
)
SELECT
    json_agg(p)
FROM
    plannen_with_condities AS p

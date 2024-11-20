DROP FUNCTION IF EXISTS diwi.apply_policy_goals;

CREATE OR REPLACE FUNCTION diwi.apply_policy_goals (
    _snapshot_date_ date,
    _user_role_ text,
    _user_uuid_ uuid
)

	RETURNS TABLE (
        id              UUID,
        name            TEXT,
        category        TEXT,
        goal            NUMERIC,
        goalType        diwi.goal_type,
        goalDirection   diwi.goal_direction,
        totalAmount     BIGINT,
        amount          BIGINT,
        percentage      NUMERIC
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

WITH houseblocks_view AS (
        SELECT * FROM diwi.get_houseblocks_view( _snapshot_date_ , _user_role_ , _user_uuid_ )
    )
SELECT
    pgv.id          AS goalId,
    pgv.name        AS goalName,
    pgv.category_label AS goalCategory,
    pgv.goal_value     AS goalValue,
    pgv.type        AS goalType,
    pgv.direction   AS goalDirection,
    (SELECT COALESCE(SUM(hv.no_of_houses * hv.mutation_sign), 0)
     FROM houseblocks_view hv
     WHERE hv.delivery_date >= pgv.start_date AND hv.delivery_date <= pgv.end_date
    )               AS totalAmount,
    (
        CASE
            WHEN pgv.property_category_options IS NOT NULL THEN
                (
                    SELECT COALESCE(SUM(hv.no_of_houses * hv.mutation_sign), 0)
                    FROM
                        houseblocks_view hv
                    WHERE hv.delivery_date >= pgv.start_date AND hv.delivery_date <= pgv.end_date
                      AND pgv.property_category_options && hv.property_category_options
                )
            WHEN pgv.physical_appearance_options IS NOT NULL THEN
                (
                    WITH physical_appearance_values AS
                        (
                            SELECT unnest(hv.physical_appearance) AS pa_val, hv.mutation_sign
                            FROM
                                houseblocks_view hv
                            WHERE hv.delivery_date >= pgv.start_date AND hv.delivery_date <= pgv.end_date
                        )
                    SELECT COALESCE ( SUM(cast (pav.pa_val->>'amount' AS integer) * pav.mutation_sign), 0)
                    FROM physical_appearance_values pav
                    WHERE (pav.pa_val->>'physical_appearance') = ANY (pgv.physical_appearance_options)
                )
            WHEN pgv.house_type_options IS NOT NULL THEN
                (
                    WITH housetype_values AS
                        (
                            SELECT unnest(hv.house_type) AS ht_val, hv.mutation_sign
                            FROM
                                houseblocks_view hv
                            WHERE hv.delivery_date >= pgv.start_date AND hv.delivery_date <= pgv.end_date
                        )
                    SELECT COALESCE ( SUM(cast (htv.ht_val->>'amount' AS integer) * htv.mutation_sign), 0)
                    FROM housetype_values htv
                    WHERE (htv.ht_val->>'house_type') = ANY (pgv.house_type_options)
                )
            WHEN pgv.target_group_options IS NOT NULL THEN
                (
                    WITH targetgroup_values AS
                        (
                            SELECT unnest(hv.target_group) AS tg_val, hv.mutation_sign
                            FROM
                                houseblocks_view hv
                            WHERE hv.delivery_date >= pgv.start_date AND hv.delivery_date <= pgv.end_date
                        )
                    SELECT COALESCE ( SUM(cast (tgv.tg_val->>'amount' AS integer) * tgv.mutation_sign), 0)
                    FROM targetgroup_values tgv
                    WHERE (tgv.tg_val->>'target_group') = ANY (pgv.target_group_options)
                )
            WHEN pgv.ground_position_options IS NOT NULL THEN
                (
                    WITH groundposition_values AS
                        (
                            SELECT unnest(hv.ground_position) AS ht_val, hv.mutation_sign
                            FROM
                                houseblocks_view hv
                            WHERE hv.delivery_date >= pgv.start_date AND hv.delivery_date <= pgv.end_date
                        )
                    SELECT COALESCE ( SUM(cast (gpv.ht_val->>'amount' AS integer) * gpv.mutation_sign), 0)
                    FROM groundposition_values gpv
                    WHERE (gpv.ht_val->>'ground_position') = ANY (pgv.ground_position_options)
                )
            WHEN pgv.programming IS NOT NULL THEN
                (
                    SELECT COALESCE(SUM(no_of_houses * mutation_sign), 0)
                    FROM
                        houseblocks_view hv
                    WHERE hv.delivery_date >= pgv.start_date AND hv.delivery_date <= pgv.end_date
                      AND pgv.programming = hv.programming
                )
            WHEN pgv.property_boolean_option IS NOT NULL THEN
                (
                    SELECT COALESCE(SUM(no_of_houses * mutation_sign), 0)
                    FROM
                        houseblocks_view hv
                    WHERE hv.delivery_date >= pgv.start_date AND hv.delivery_date <= pgv.end_date
                      AND pgv.property_boolean_option = ANY (hv.property_boolean_options )
                )
            WHEN pgv.ownership_value_options IS NOT NULL THEN
                (
                    WITH houseblock_ownership_values AS
                        (
                            SELECT
                                hv_ownership.ht_val ->> 'amount' AS amount,
                                hv_ownership.ht_val ->> 'property_value_id' AS property_value_id,
                                hv_ownership.ht_val ->> 'ownership_type' AS ownership_type,
                                hv_ownership.mutation_sign AS mutation_sign
                            FROM
                            ( SELECT unnest(hv.ownership_value_options) AS ht_val, hv.mutation_sign
                                FROM
                                    houseblocks_view hv
                                WHERE hv.delivery_date >= pgv.start_date AND hv.delivery_date <= pgv.end_date
                            ) AS hv_ownership
                        ),
                    policy_goals_options AS
                        (
                            SELECT
                                q.pgv_ownership ->> 'ownership_type' AS ownership_type,
                                q.pgv_ownership ->> 'property_value_id' AS property_value_id
                            FROM
                            (
                                SELECT unnest(p.ownership_value_options) AS pgv_ownership
                                FROM diwi.policy_goals_view p
                                WHERE p.id = pgv.id
                            ) AS q
                        )

                    SELECT
                        COALESCE(SUM(cast (hov.amount AS integer) * hov.mutation_sign), 0)
                    FROM
                        policy_goals_options pgo
                            JOIN houseblock_ownership_values hov ON pgo.property_value_id = hov.property_value_id AND pgo.ownership_type = hov.ownership_type
                )
            ELSE (
                SELECT COALESCE(SUM(hv.no_of_houses * hv.mutation_sign), 0)
                FROM houseblocks_view hv
                WHERE hv.delivery_date >= pgv.start_date AND hv.delivery_date <= pgv.end_date
            )
        END
    ) AS amount,
    null::NUMERIC            AS percentage

FROM diwi.policy_goals_view pgv;

END;$$

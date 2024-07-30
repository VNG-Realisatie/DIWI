DROP FUNCTION IF EXISTS diwi.get_active_plans;

CREATE OR REPLACE FUNCTION diwi.get_active_plans (
  _plan_uuid_ uuid
)
	RETURNS TABLE (
        id UUID,
        name TEXT,
        startDate DATE,
        endDate DATE,
        goalType diwi.goal_type,
        goalDirection diwi.goal_direction,
        goalValue NUMERIC,
        planCategory JSONB,
        geographyConditionId UUID,
        geographyOptions JSONB,
        programmingConditionId UUID,
        programmingValue BOOL,
        groundPositionConditionId UUID,
        groundPositionOptions TEXT[],
        houseTypeConditionId UUID,
        houseTypeOptions TEXT[],
        ownershipCondition JSONB
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

 WITH
     plan_details AS (
         SELECT
             ps.plan_id AS plan_id, ps.name, ps.start_date, ps.deadline, ps.goal_type, ps.goal_direction, ps.goal_value,
             CASE
                WHEN pcs.plan_category_id IS NOT NULL THEN jsonb_build_object('id', pcs.plan_category_id, 'name', pcs.value_label)
                ELSE null
             END AS plan_category
         FROM diwi.plan_state ps
             LEFT JOIN diwi.plan_category_state pcs ON  ps.plan_category_id = pcs.plan_category_id AND pcs.change_end_date IS NULL
         WHERE ps.change_end_date IS NULL AND
             CASE
                 WHEN _plan_uuid_ IS NOT NULL THEN ps.plan_id = _plan_uuid_
                 WHEN _plan_uuid_ IS NULL THEN 1 = 1
             END
         ORDER BY ps.name
     ),
     plan_conditions AS (
        SELECT pc.plan_id AS plan_id, pc.id AS condition_id, pcs.condition_type AS condition_type
        FROM
             plan_details pd
                 JOIN diwi.plan_conditie pc ON pd.plan_id = pc.plan_id
                 JOIN diwi.plan_conditie_state pcs ON pc.id = pcs.plan_conditie_id AND pcs.change_end_date IS NULL
     ),
     plan_geography AS (
         SELECT
             pc.plan_id AS plan_id, pc.condition_id AS geographyConditionId,
             to_jsonb(array_agg(jsonb_build_object('brkGemeenteCode', rlv.brk_gemeente_code, 'brkSectie', rlv.brk_sectie, 'brkPerceelNummer', rlv.brk_perceelnummer))) AS geographyOptions
         FROM
             plan_conditions pc
                 JOIN diwi.plan_conditie_registry_link rl ON pc.condition_id = rl.plan_conditie_id AND rl.change_end_date IS NULL
                 JOIN diwi.plan_conditie_registry_link_value rlv ON rl.id = rlv.plan_conditie_registry_link_id
         GROUP BY pc.plan_id, pc.condition_id
     ),
     plan_programming AS (
         SELECT
             pc.plan_id AS plan_id, pc.condition_id AS programmingConditionId, pp.programmering AS programmingValue
         FROM
             plan_conditions pc
                 JOIN diwi.plan_conditie_programmering pp ON pc.condition_id = pp.plan_conditie_id AND pp.change_end_date IS NULL
     ),
     plan_ground_positions AS (
         SELECT
             pc.plan_id AS plan_id, pc.condition_id AS groundPositionConditionId, array_agg(pgv.grondpositie::TEXT) AS groundPositionOptions
         FROM
             plan_conditions pc
                 JOIN diwi.plan_conditie_grondpositie pg ON pc.condition_id = pg.plan_conditie_id AND pg.change_end_date IS NULL
                 JOIN diwi.plan_conditie_grondpositie_value pgv ON pgv.plan_conditie_grondpositie_id = pg.id
         GROUP BY pc.plan_id, pc.condition_id
     ),
     plan_house_types AS (
         SELECT
             pc.plan_id AS plan_id, pc.condition_id AS houseTypeConditionId, array_agg(pctfv.woning_type::TEXT) AS houseTypeOptions
         FROM
             plan_conditions pc
                 JOIN diwi.plan_conditie_type_en_fysiek pctf ON pc.condition_id = pctf.plan_conditie_id AND pctf.change_end_date IS NULL
                 JOIN diwi.plan_conditie_type_en_fysiek_type_value pctfv ON pctfv.plan_conditie_type_en_fysiek_id = pctf.id
         GROUP BY pc.plan_id, pc.condition_id
     ),
     plan_ownership AS (
         SELECT
             pc.plan_id AS plan_id,
             CASE
                WHEN pew.eigendom_soort = 'KOOPWONING' THEN
                    jsonb_build_object('ownershipConditionId', pc.condition_id, 'ownershipType', pew.eigendom_soort, 'ownershipValue', pew.waarde_value,
                        'ownershipValueRangeMin', lower(pew.waarde_value_range), 'ownershipValueRangeMax', upper(pew.waarde_value_range) - 1,
                        'ownershipRangeCategoryId', pew.ownership_property_value_id, 'ownershipRangeCategoryName', prc1.name)
                ELSE jsonb_build_object('ownershipConditionId', pc.condition_id, 'ownershipType', pew.eigendom_soort, 'ownershipValue', pew.huurbedrag_value,
                        'ownershipValueRangeMin', lower(pew.huurbedrag_value_range), 'ownershipValueRangeMax', upper(pew.huurbedrag_value_range) - 1,
                        'ownershipRangeCategoryId', pew.rental_property_value_id, 'ownershipRangeCategoryName', prc2.name)
             END AS ownershipCondition
         FROM
             plan_conditions pc
                 JOIN diwi.plan_conditie_eigendom_en_waarde pew ON pc.condition_id = pew.plan_conditie_id AND pew.change_end_date IS NULL
                 LEFT JOIN diwi.property_range_category_value_state prc1 ON prc1.range_category_value_id = pew.ownership_property_value_id AND prc1.change_end_date IS NULL
                 LEFT JOIN diwi.property_range_category_value_state prc2 ON prc2.range_category_value_id = pew.rental_property_value_id AND prc2.change_end_date IS NULL
     )


 SELECT pd.plan_id                      AS id,
        pd.name                         AS name,
        pd.start_date                   AS startDate,
        pd.deadline                     AS endDate,
        pd.goal_type                    AS goalType,
        pd.goal_direction               AS goalDirection,
        pd.goal_value                   AS goalValue,
        pd.plan_category                AS planCategory,
        pg.geographyConditionId         AS geographyConditionId,
        pg.geographyOptions             AS geographyOptions,
        pp.programmingConditionId       AS programmingConditionId,
        pp.programmingValue             AS programmingValue,
        pgp.groundPositionConditionId   AS groundPositionConditionId,
        pgp.groundPositionOptions       AS groundPositionOptions,
        pht.houseTypeConditionId        AS houseTypeConditionId,
        pht.houseTypeOptions            AS houseTypeOptions,
        po.ownershipCondition           AS ownershipCondition
 FROM
     plan_details pd
         LEFT JOIN plan_geography pg ON pd.plan_id = pg.plan_id
         LEFT JOIN plan_programming pp ON pd.plan_id = pp.plan_id
         LEFT JOIN plan_ground_positions pgp ON pd.plan_id = pgp.plan_id
         LEFT JOIN plan_house_types pht ON pd.plan_id = pht.plan_id
         LEFT JOIN plan_ownership po ON pd.plan_id = po.plan_id
;

END;$$

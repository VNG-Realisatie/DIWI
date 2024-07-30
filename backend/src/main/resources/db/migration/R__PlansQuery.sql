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
        ownershipConditionId UUID,
        ownershipOptions JSONB,
        propertyConditions JSONB
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
             pc.plan_id AS plan_id, pc.condition_id AS ownershipConditionId,
             to_jsonb(array_agg(jsonb_build_object('ownershipType', pew.eigendom_soort, 'value', pew.waarde_value, 'rentValue', pew.huurbedrag_value,
                        'valueRangeMin', lower(pew.waarde_value_range), 'valueRangeMax', upper(pew.waarde_value_range) - 1,
                        'rentValueRangeMin', lower(pew.waarde_value_range), 'rentValueRangeMax', upper(pew.waarde_value_range) - 1,
                        'rangeCategoryId', pew.ownership_property_value_id, 'rangeCategoryName', prc1.name,
                        'rentRangeCategoryId', pew.rental_property_value_id, 'rentRangeCategoryName', prc2.name))) AS ownershipOptions
         FROM
             plan_conditions pc
                 JOIN diwi.plan_conditie_eigendom_en_waarde pew ON pc.condition_id = pew.plan_conditie_id AND pew.change_end_date IS NULL
                 LEFT JOIN diwi.property_range_category_value_state prc1 ON prc1.range_category_value_id = pew.ownership_property_value_id AND prc1.change_end_date IS NULL
                 LEFT JOIN diwi.property_range_category_value_state prc2 ON prc2.range_category_value_id = pew.rental_property_value_id AND prc2.change_end_date IS NULL
         GROUP BY pc.plan_id, pc.condition_id
     ),
     plan_properties AS (
         WITH all_properties AS (
             SELECT
                 pc.plan_id AS plan_id,
                 pc.condition_id AS conditionId,
                 p.id AS propertyId,
                 ps.property_name AS propertyName,
                 p.type AS propertyKind,
                 ps.property_type AS propertyType,
                 pb.value AS booleanValue,
                 null AS categoryOptions
             FROM
                 plan_conditions pc
                     JOIN diwi.plan_conditie_property_boolean pb ON pc.condition_id = pb.plan_conditie_id AND pb.change_end_date IS NULL
                     JOIN diwi.property p ON pb.property_id = p.id
                     JOIN diwi.property_state ps ON ps.property_id = p.id AND ps.change_end_date IS NULL

             UNION

             SELECT
                 pc.plan_id AS plan_id,
                 pc.condition_id AS conditionId,
                 p.id AS propertyId,
                 ps.property_name AS propertyName,
                 p.type AS propertyKind,
                 ps.property_type AS propertyType,
                 null AS booleanValue,
                 to_jsonb(array_agg(jsonb_build_object('id', pcpcv.property_value_id, 'name', pcvs.value_label))) AS categoryOptions
             FROM
                 plan_conditions pc
                     JOIN diwi.plan_conditie_property_category pcpc ON pc.condition_id = pcpc.plan_conditie_id AND pcpc.change_end_date IS NULL
                     JOIN diwi.plan_conditie_property_category_value pcpcv ON pcpc.id = pcpcv.plan_conditie_property_category_id
                     JOIN diwi.property p ON pcpc.property_id = p.id
                     JOIN diwi.property_state ps ON ps.property_id = p.id AND ps.change_end_date IS NULL
                     JOIN diwi.property_category_value_state pcvs ON pcvs.category_value_id = pcpcv.property_value_id
             GROUP BY pc.plan_id, pc.condition_id, p.id, ps.property_name, p.type, ps.property_type

             UNION

             SELECT
                 pc.plan_id AS plan_id,
                 pc.condition_id AS conditionId,
                 ps.property_id AS propertyId,
                 ps.property_name AS propertyName,
                 'FIXED' AS propertyKind,
                 ps.property_type AS propertyType,
                 null AS booleanValue,
                 to_jsonb(array_agg(jsonb_build_object('id', pcdv.property_value_id, 'name', pcvs.value_label))) AS categoryOptions
             FROM
                 plan_conditions pc
                     JOIN diwi.plan_conditie_doelgroep pcd ON pc.condition_id = pcd.plan_conditie_id AND pcd.change_end_date IS NULL
                     JOIN diwi.plan_conditie_doelgroep_value pcdv ON pcd.id = pcdv.plan_conditie_doelgroep_id
                     JOIN diwi.property_state ps ON ps.property_name = 'targetGroup' AND ps.change_end_date IS NULL
                     JOIN diwi.property_category_value_state pcvs ON pcvs.category_value_id = pcdv.property_value_id
             GROUP BY pc.plan_id, pc.condition_id, ps.property_id, ps.property_name, ps.property_type

                 UNION

             SELECT
                 pc.plan_id AS plan_id,
                 pc.condition_id AS conditionId,
                 ps.property_id AS propertyId,
                 ps.property_name AS propertyName,
                 'FIXED' AS propertyKind,
                 ps.property_type AS propertyType,
                 null AS booleanValue,
                 to_jsonb(array_agg(jsonb_build_object('id', pctfv.property_value_id, 'name', pcvs.value_label))) AS categoryOptions
             FROM
                 plan_conditions pc
                     JOIN diwi.plan_conditie_type_en_fysiek pctf ON pc.condition_id = pctf.plan_conditie_id AND pctf.change_end_date IS NULL
                     JOIN diwi.plan_conditie_type_en_fysiek_fysiek_value pctfv ON pctf.id = pctfv.plan_conditie_type_en_fysiek_id
                     JOIN diwi.property_state ps ON ps.property_name = 'physicalAppearance' AND ps.change_end_date IS NULL
                     JOIN diwi.property_category_value_state pcvs ON pcvs.category_value_id = pctfv.property_value_id
             GROUP BY pc.plan_id, pc.condition_id, ps.property_id, ps.property_name, ps.property_type
         )
         SELECT
            ap.plan_id,
            to_jsonb(array_agg(jsonb_build_object('conditionId', ap.conditionId, 'propertyId', ap.propertyId, 'propertyName', ap.propertyName,
                               'propertyKind', ap.propertyKind, 'propertyType', ap.propertyType, 'booleanValue', ap.booleanValue,
                               'categoryOptions', ap.categoryOptions))) AS propertyConditions
         FROM all_properties ap
         GROUP BY ap.plan_id
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
        po.ownershipConditionId         AS ownershipConditionId,
        po.ownershipOptions             AS ownershipOptions,
        prop.propertyConditions         AS propertyConditions

 FROM
     plan_details pd
         LEFT JOIN plan_geography pg ON pd.plan_id = pg.plan_id
         LEFT JOIN plan_programming pp ON pd.plan_id = pp.plan_id
         LEFT JOIN plan_ground_positions pgp ON pd.plan_id = pgp.plan_id
         LEFT JOIN plan_house_types pht ON pd.plan_id = pht.plan_id
         LEFT JOIN plan_ownership po ON pd.plan_id = po.plan_id
         LEFT JOIN plan_properties prop ON pd.plan_id = prop.plan_id
;

END;$$

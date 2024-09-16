
DROP MATERIALIZED VIEW IF EXISTS diwi.policy_goals_view;

CREATE MATERIALIZED VIEW diwi.policy_goals_view AS

    WITH
        plan_details AS (
            SELECT
                ps.plan_id AS plan_id,
                ps.name, ps.start_date, ps.deadline, ps.goal_type,
                ps.goal_direction, ps.goal_value, pcs.value_label AS plan_category
            FROM
                diwi.plan_state ps
                    LEFT JOIN diwi.plan_category_state pcs ON ps.plan_category_id = pcs.plan_category_id AND pcs.change_end_date IS NULL
            WHERE ps.change_end_date IS NULL
            ORDER BY ps.name
        ),
        plan_conditions AS (
            SELECT
                pc.plan_id AS plan_id, pc.id AS condition_id
            FROM
                plan_details pd
                    JOIN diwi.plan_conditie pc ON pd.plan_id = pc.plan_id
                    JOIN diwi.plan_conditie_state pcs ON pc.id = pcs.plan_conditie_id AND pcs.change_end_date IS NULL
        ),
        plan_category_properties AS (
            SELECT
                pc.plan_id AS plan_id,
                array_agg(pcpcv.property_value_id) AS category_options
            FROM
                plan_conditions pc
                    JOIN diwi.plan_conditie_property_category pcpc ON pc.condition_id = pcpc.plan_conditie_id AND pcpc.change_end_date IS NULL
                    JOIN diwi.plan_conditie_property_category_value pcpcv ON pcpc.id = pcpcv.plan_conditie_property_category_id
                    JOIN diwi.property p ON pcpc.property_id = p.id
                    JOIN diwi.property_state ps ON ps.property_id = p.id AND ps.change_end_date IS NULL
                    JOIN diwi.property_category_value_state pcvs ON pcvs.category_value_id = pcpcv.property_value_id
            GROUP BY pc.plan_id
        ),
        plan_boolean_properties AS (
            SELECT
                pc.plan_id AS plan_id,
                jsonb_build_object('property_id', pcpb.property_id, 'boolean_value', pcpb.value) AS boolean_option
            FROM
                plan_conditions pc
                    JOIN diwi.plan_conditie_property_boolean pcpb ON pc.condition_id = pcpb.plan_conditie_id AND pcpb.change_end_date IS NULL
                    JOIN diwi.property p ON pcpb.property_id = p.id
                    JOIN diwi.property_state ps ON ps.property_id = p.id AND ps.change_end_date IS NULL
        ),
        plan_house_types_physical_appearance AS (
            SELECT
                pc.plan_id AS plan_id,
                array_agg(pctftv.woning_type::text) FILTER (WHERE pctftv.woning_type IS NOT NULL) AS house_type_options,
                array_agg(pctffv.property_value_id::text) FILTER (WHERE pctffv.property_value_id IS NOT NULL) AS physical_appearance_options
            FROM
                plan_conditions pc
                    JOIN diwi.plan_conditie_type_en_fysiek pctf ON pc.condition_id = pctf.plan_conditie_id AND pctf.change_end_date IS NULL
                    LEFT JOIN diwi.plan_conditie_type_en_fysiek_type_value pctftv ON pctftv.plan_conditie_type_en_fysiek_id = pctf.id
                    LEFT JOIN diwi.plan_conditie_type_en_fysiek_fysiek_value pctffv ON pctffv.plan_conditie_type_en_fysiek_id = pctf.id
            GROUP BY pc.plan_id
        ),
        plan_target_group AS (
            SELECT
                pc.plan_id AS plan_id,
                array_agg(pcdgv.property_value_id::text) FILTER (WHERE pcdgv.property_value_id IS NOT NULL) AS target_group_options
            FROM
                plan_conditions pc
                    JOIN diwi.plan_conditie_doelgroep pcdg ON pc.condition_id = pcdg.plan_conditie_id AND pcdg.change_end_date IS NULL
                    JOIN diwi.plan_conditie_doelgroep_value pcdgv ON pcdgv.plan_conditie_doelgroep_id = pcdg.id
            GROUP BY pc.plan_id
        ),
        plan_ground_position AS (
            SELECT
                pc.plan_id AS plan_id,
                array_agg(pcgpv.grondpositie::text) FILTER (WHERE pcgpv.grondpositie IS NOT NULL) AS ground_position_options
            FROM
                plan_conditions pc
                    JOIN diwi.plan_conditie_grondpositie pcgp ON pc.condition_id = pcgp.plan_conditie_id AND pcgp.change_end_date IS NULL
                    JOIN diwi.plan_conditie_grondpositie_value pcgpv ON pcgpv.plan_conditie_grondpositie_id = pcgp.id
            GROUP BY pc.plan_id
        ),
        plan_programming AS (
            SELECT
                pc.plan_id AS plan_id,
                pcp.programmering AS programming
            FROM
                plan_conditions pc
                    JOIN diwi.plan_conditie_programmering pcp ON pc.condition_id = pcp.plan_conditie_id AND pcp.change_end_date IS NULL
        ),
        plan_ownership_value AS (
            SELECT
                pc.plan_id AS plan_id,
                array_agg(jsonb_build_object('ownership_type', pcew.eigendom_soort,
                    'property_value_id', COALESCE(pcew.ownership_property_value_id, pcew.rental_property_value_id))) AS ownership_value_options
            FROM
                plan_conditions pc
                    JOIN diwi.plan_conditie_eigendom_en_waarde pcew ON pc.condition_id = pcew.plan_conditie_id AND pcew.change_end_date IS NULL
            WHERE pcew.ownership_property_value_id IS NOT NULL OR pcew.rental_property_value_id IS NOT NULL
            GROUP BY pc.plan_id
        )

    SELECT
        pd.plan_id                      AS id,
        pd.name                         AS name,
        pd.start_date                   AS start_date,
        pd.deadline                     AS end_date,
        pd.goal_type                    AS type,
        pd.goal_direction               AS direction,
        pd.goal_value                   AS goal_value,
        pd.plan_category                AS category_label,
        pcp.category_options            AS property_category_options,
        pbp.boolean_option              AS property_boolean_option,
        phtpa.house_type_options        AS house_type_options,
        phtpa.physical_appearance_options AS physical_appearance_options,
        ptg.target_group_options        AS target_group_options,
        pgp.ground_position_options     AS ground_position_options,
        pp.programming                  AS programming,
        pov.ownership_value_options     AS ownership_value_options


    FROM
        plan_details pd
            LEFT JOIN plan_category_properties pcp ON pd.plan_id = pcp.plan_id
            LEFT JOIN plan_house_types_physical_appearance phtpa ON pd.plan_id = phtpa.plan_id
            LEFT JOIN plan_target_group ptg ON pd.plan_id = ptg.plan_id
            LEFT JOIN plan_ground_position pgp ON pd.plan_id = pgp.plan_id
            LEFT JOIN plan_programming pp ON pd.plan_id = pp.plan_id
            LEFT JOIN plan_boolean_properties pbp ON pd.plan_id = pbp.plan_id
            LEFT JOIN plan_ownership_value pov ON pd.plan_id = pov.plan_id

;

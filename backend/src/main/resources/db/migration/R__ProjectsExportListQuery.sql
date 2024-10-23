DROP FUNCTION IF EXISTS diwi.get_projects_export_list;

CREATE OR REPLACE FUNCTION diwi.get_projects_export_list (
  _export_date_ date,
  _user_role_ text,
  _user_uuid_ uuid,
  _allowed_confidentialities_ TEXT[]
)
	RETURNS TABLE (
        projectId UUID,
        name TEXT,
        confidentiality diwi.confidentiality,
        startDate DATE,
        endDate DATE,
        planType TEXT[],
        projectPhase diwi.project_phase,
        planningPlanStatus TEXT[],
        textProperties JSONB,
        numericProperties JSONB,
        booleanProperties JSONB,
        categoryProperties JSONB
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

SELECT  q.projectId,
        q.projectName AS name,
        q.confidentiality,
        q.startDate,
        q.endDate,
        q.planType,
        q.projectPhase,
        q.planningPlanStatus,
        q.textProperties,
        q.numericProperties,
        q.booleanProperties,
        q.categoryProperties
FROM (

    WITH
        project_owners AS (
            SELECT
                q.project_id    AS project_id,
                array_agg(q.user_id) AS users
            FROM (
                     SELECT DISTINCT
                         ps.project_id as project_id,
                         us.user_id AS user_id
                     FROM diwi.project_state ps
                              JOIN diwi.usergroup_to_project ugtp ON ps.project_id = ugtp.project_id AND ugtp.change_end_date IS NULL
                              JOIN diwi.usergroup_state ugs ON ugtp.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                              LEFT JOIN diwi.user_to_usergroup utug ON ugtp.usergroup_id = utug.usergroup_id AND utug.change_end_date IS NULL
                              LEFT JOIN diwi.user_state us ON utug.user_id = us.user_id AND us.change_end_date IS NULL
                     WHERE
                         ps.change_end_date IS NULL
                 ) AS q
            GROUP BY q.project_id
        ),
        active_projects AS (
            SELECT
                p.id, ps.confidentiality_level AS confidentiality, sms.date AS startDate, ems.date AS endDate
            FROM
                diwi.project p
                    JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_state ps ON ps.project_id = p.id AND ps.change_end_date IS NULL
                    JOIN project_owners po ON po.project_id = p.id
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
                AND
                (   -- general project visibility rules
                    (_user_uuid_ = ANY(po.users)) OR
                    (_user_role_ IN ('User', 'UserPlus') AND ps.confidentiality_level != 'PRIVATE') OR
                    (_user_role_ = 'Management' AND ps.confidentiality_level NOT IN ('PRIVATE', 'INTERNAL_CIVIL')) OR
                    (_user_role_ = 'Council' AND ps.confidentiality_level NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT'))
                )
                AND ps.confidentiality_level::TEXT = ANY(_allowed_confidentialities_)
        ),
        active_project_names AS (
            SELECT
                pnc.project_id, pnc.name
            FROM
                active_projects ap
                    JOIN diwi.project_name_changelog pnc ON pnc.project_id = ap.id AND pnc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
        ),
        active_project_fases AS (
            SELECT
                pfc.project_id, pfc.project_fase
            FROM
                active_projects ap
                    JOIN diwi.project_fase_changelog pfc ON pfc.project_id = ap.id AND pfc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pfc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pfc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
        ),
        active_project_plan_types AS (
            SELECT
                pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
            FROM
                active_projects ap
                    JOIN diwi.project_plan_type_changelog pptc ON pptc.project_id = ap.id AND pptc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pptc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pptc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
            GROUP BY pptc.project_id
        ),
        active_project_planologische_planstatus AS (
            SELECT
                pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
            FROM
                active_projects ap
                    JOIN diwi.project_planologische_planstatus_changelog pppc ON ap.id = pppc.project_id AND pppc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pppc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pppc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
            GROUP BY pppc.project_id
        ),
        active_project_textCP AS (
            SELECT
                ptc.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', ptc.property_id, 'textValue', ptc.value))) AS text_properties
            FROM
                active_projects ap
                    JOIN diwi.project_text_changelog ptc ON ap.id = ptc.project_id AND ptc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = ptc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = ptc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
            GROUP BY ptc.project_id
        ),
        active_project_numericCP AS (
            SELECT
                pnc.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', pnc.eigenschap_id, 'value', pnc.value, 'min', LOWER(pnc.value_range),
                                                   'max', UPPER(pnc.value_range)))) AS numeric_properties
            FROM
                active_projects ap
                    JOIN diwi.project_maatwerk_numeriek_changelog pnc ON ap.id = pnc.project_id AND pnc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
            GROUP BY pnc.project_id
        ),
        active_project_booleanCP AS (
            SELECT
                pbc.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', pbc.eigenschap_id, 'booleanValue', pbc.value))) AS boolean_properties
            FROM
                active_projects ap
                    JOIN diwi.project_maatwerk_boolean_changelog pbc ON ap.id = pbc.project_id AND pbc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pbc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pbc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
            GROUP BY pbc.project_id
        ),
        active_project_categoryCP AS (
            WITH prj_cat_props AS (
                SELECT
                    pcc.project_id, pcc.property_id, array_agg(pccv.property_value_id) AS category_options
                FROM
                    active_projects ap
                        JOIN diwi.project_category_changelog pcc ON ap.id = pcc.project_id AND pcc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = pcc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = pcc.end_milestone_id AND ems.change_end_date IS NULL
                        JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                WHERE
                    sms.date <= _export_date_ AND _export_date_ < ems.date
                GROUP BY pcc.project_id, pcc.property_id
            )
            SELECT
                pcp.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', pcp.property_id, 'optionValues', pcp.category_options))) AS category_properties
            FROM prj_cat_props pcp
            GROUP BY pcp.project_id
        ),
        past_projects AS (
            SELECT
                p.id, ps.confidentiality_level AS confidentiality, sms.date AS startDate, ems.date AS endDate, ems.milestone_id AS end_milestone_id
            FROM
                diwi.project p
                    JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_state ps ON ps.project_id = p.id AND ps.change_end_date IS NULL
                    JOIN project_owners po ON po.project_id = p.id
            WHERE
                ems.date <= _export_date_
                AND
                (   -- general project visibility rules
                    (_user_uuid_ = ANY(po.users)) OR
                    (_user_role_ IN ('User', 'UserPlus') AND ps.confidentiality_level != 'PRIVATE') OR
                    (_user_role_ = 'Management' AND ps.confidentiality_level NOT IN ('PRIVATE', 'INTERNAL_CIVIL')) OR
                    (_user_role_ = 'Council' AND ps.confidentiality_level NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT'))
                )
                AND ps.confidentiality_level::TEXT = ANY(_allowed_confidentialities_)
        ),
        past_project_names AS (
            SELECT
                pnc.project_id, pnc.name
            FROM
                past_projects pp
                    JOIN diwi.project_name_changelog pnc ON pp.id = pnc.project_id
                        AND pnc.end_milestone_id = pp.end_milestone_id AND pnc.change_end_date IS NULL
        ),
        past_project_fases AS (
            SELECT
                 pfc.project_id, pfc.project_fase
            FROM
                past_projects pp
                    JOIN diwi.project_fase_changelog pfc ON pp.id = pfc.project_id
                        AND pfc.end_milestone_id = pp.end_milestone_id AND pfc.change_end_date IS NULL
        ),
        past_project_plan_types AS (
            SELECT
                pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
            FROM
                past_projects pp
                    JOIN diwi.project_plan_type_changelog pptc ON pp.id = pptc.project_id
                        AND pptc.end_milestone_id = pp.end_milestone_id AND pptc.change_end_date IS NULL
                    JOIN diwi.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
            GROUP BY pptc.project_id
        ),
        past_project_planologische_planstatus AS (
            SELECT
                pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
            FROM
                past_projects pp
                    JOIN diwi.project_planologische_planstatus_changelog pppc ON pp.id = pppc.project_id
                        AND pppc.end_milestone_id = pp.end_milestone_id AND pppc.change_end_date IS NULL
                    JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
            GROUP BY pppc.project_id
        ),
        past_project_textCP AS (
            SELECT
                ptc.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', ptc.property_id, 'textValue', ptc.value))) AS text_properties
            FROM
                past_projects pp
                    JOIN diwi.project_text_changelog ptc ON pp.id = ptc.project_id AND ptc.end_milestone_id = pp.end_milestone_id AND ptc.change_end_date IS NULL
            GROUP BY ptc.project_id
        ),
        past_project_numericCP AS (
            SELECT
                pnc.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', pnc.eigenschap_id, 'value', pnc.value, 'min', LOWER(pnc.value_range),
                                                                      'max', UPPER(pnc.value_range)))) AS numeric_properties
            FROM
                past_projects pp
                    JOIN diwi.project_maatwerk_numeriek_changelog pnc ON pp.id = pnc.project_id AND pnc.end_milestone_id = pp.end_milestone_id AND pnc.change_end_date IS NULL
            GROUP BY pnc.project_id
        ),
        past_project_booleanCP AS (
            SELECT
                pbc.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', pbc.eigenschap_id, 'booleanValue', pbc.value))) AS boolean_properties
            FROM
                past_projects pp
                    JOIN diwi.project_maatwerk_boolean_changelog pbc ON pp.id = pbc.project_id AND pbc.end_milestone_id = pp.end_milestone_id AND pbc.change_end_date IS NULL
            GROUP BY pbc.project_id
        ),
        past_project_categoryCP AS (
            WITH prj_cat_props AS (
                SELECT
                    pcc.project_id, pcc.property_id, array_agg(pccv.property_value_id) AS category_options
                FROM
                    past_projects pp
                        JOIN diwi.project_category_changelog pcc ON pp.id = pcc.project_id AND pcc.end_milestone_id = pp.end_milestone_id  AND pcc.change_end_date IS NULL
                        JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                GROUP BY pcc.project_id, pcc.property_id
            )
            SELECT
                pcp.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', pcp.property_id, 'optionValues', pcp.category_options))) AS category_properties
            FROM prj_cat_props pcp
            GROUP BY pcp.project_id
        )

    SELECT ap.id                    AS projectId,
           apn.name                 AS projectName,
           ap.confidentiality       AS confidentiality,
           ap.startDate             AS startDate,
           ap.endDate               AS endDate,
           appt.plan_types          AS planType,
           apf.project_fase         AS projectPhase,
           appp.planning_planstatus AS planningPlanStatus,
           apt.text_properties      AS textProperties,
           apnp.numeric_properties  AS numericProperties,
           apb.boolean_properties   AS booleanProperties,
           apc.category_properties  AS categoryProperties
    FROM
        active_projects ap
            LEFT JOIN active_project_names apn ON apn.project_id = ap.id
            LEFT JOIN active_project_plan_types appt ON appt.project_id = ap.id
            LEFT JOIN active_project_fases apf ON apf.project_id = ap.id
            LEFT JOIN active_project_planologische_planstatus appp ON appp.project_id = ap.id
            LEFT JOIN active_project_textCP apt ON apt.project_id = ap.id
            LEFT JOIN active_project_numericCP apnp ON apnp.project_id = ap.id
            LEFT JOIN active_project_booleanCP apb ON apb.project_id = ap.id
            LEFT JOIN active_project_categoryCP apc ON apc.project_id = ap.id

    UNION

    SELECT pp.id                    AS projectId,
           ppn.name                 AS projectName,
           pp.confidentiality       AS confidentiality,
           pp.startDate             AS startDate,
           pp.endDate               AS endDate,
           pppt.plan_types          AS planType,
           ppf.project_fase         AS projectPhase,
           pppp.planning_planstatus AS planningPlanStatus,
           ppt.text_properties      AS textProperties,
           ppnp.numeric_properties  AS numericProperties,
           ppb.boolean_properties   AS booleanProperties,
           ppc.category_properties  AS categoryProperties
    FROM
        past_projects pp
            LEFT JOIN past_project_names ppn ON ppn.project_id = pp.id
            LEFT JOIN past_project_plan_types pppt ON pppt.project_id = pp.id
            LEFT JOIN past_project_fases ppf ON ppf.project_id = pp.id
            LEFT JOIN past_project_planologische_planstatus pppp ON pppp.project_id = pp.id
            LEFT JOIN past_project_textCP ppt ON ppt.project_id = pp.id
            LEFT JOIN past_project_numericCP ppnp ON ppnp.project_id = pp.id
            LEFT JOIN past_project_booleanCP ppb ON ppb.project_id = pp.id
            LEFT JOIN past_project_categoryCP ppc ON ppc.project_id = pp.id

) AS q

ORDER BY q.projectName COLLATE "diwi_numeric" ASC;

END;$$

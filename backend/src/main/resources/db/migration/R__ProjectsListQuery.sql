DROP FUNCTION IF EXISTS get_active_and_future_projects_list;
DROP FUNCTION IF EXISTS diwi.get_active_and_future_projects_list;

CREATE OR REPLACE FUNCTION diwi.get_active_and_future_projects_list (
  _now_ date,
  _offset_ int,
  _limit_ int,
  _sortColumn_ text,
  _sortDirection_ text,
  _filterColumn_ text,
  _filterValues_ text[],
  _filterCondition_ text,
  _user_role_ text,
  _user_uuid_ uuid
)
	RETURNS TABLE (
        projectId UUID,
        projectStateId UUID,
        projectName TEXT,
        projectOwnersArray TEXT[][],
        projectColor TEXT,
        latitude FLOAT8,
        longitude FLOAT8,
        confidentialityLevel diwi.confidentiality,
        startDate DATE,
        endDate DATE,
        planType TEXT[],
        priority JSONB,
        projectPhase diwi.project_phase,
        planningPlanStatus TEXT[],
        municipalityRole JSONB,
        totalValue BIGINT,
        municipality JSONB,
        district JSONB,
        neighbourhood JSONB,
        geometry TEXT
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

SELECT  q.projectId,
        q.projectStateId,
        q.projectName,
        q.projectOwners            AS projectOwnersArray,
        q.projectColor,
        q.latitude,
        q.longitude,
        q.confidentialityLevel,
        q.startDate,
        q.endDate,
        q.planType,
        q.priorityList            AS priority,
        q.projectPhase,
        q.planningPlanStatus,
        q.municipalityRoleList    AS municipalityRole,
        q.totalValue,
        q.municipalityList               AS municipality,
        q.districtList             AS district,
        q.neighbourhoodList        AS neighbourhood,
        null                       AS geometry
FROM (

    WITH
        active_projects AS (
            SELECT
                p.id, sms.date AS startDate, ems.date AS endDate
            FROM
                diwi.project p
                    JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date
        ),
        active_project_names AS (
            SELECT
                pnc.project_id, pnc.name
            FROM
                diwi.project_name_changelog pnc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pnc.change_end_date IS NULL
        ),
        active_project_fases AS (
            SELECT
                pfc.project_id, pfc.project_fase
            FROM
                diwi.project_fase_changelog pfc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pfc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pfc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pfc.change_end_date IS NULL
        ),
        active_project_plan_types AS (
            SELECT
                pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
            FROM
                diwi.project_plan_type_changelog pptc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pptc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pptc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pptc.change_end_date IS NULL
            GROUP BY pptc.project_id
        ),
        active_project_planologische_planstatus AS (
            SELECT
                pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
            FROM
                diwi.project_planologische_planstatus_changelog pppc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pppc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pppc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pppc.change_end_date IS NULL
            GROUP BY pppc.project_id
        ),
        active_project_woningblok_totalvalue AS (
            SELECT
                w.project_id,
                SUM(wmc.amount *
                    CASE wmc.mutation_kind
                        WHEN 'CONSTRUCTION' THEN 1
                        WHEN 'DEMOLITION' THEN -1
                    END) AS total_value
            FROM
                diwi.woningblok_mutatie_changelog wmc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = wmc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = wmc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.woningblok w ON wmc.woningblok_id = w.id
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND wmc.change_end_date IS NULL
            GROUP BY w.project_id
        ),
        active_project_fixed_props AS (
            SELECT
                pcc.project_id, ps.property_name AS fixedPropertyName,
                to_jsonb(array_agg(jsonb_build_object('id', pcvs.category_value_id, 'name', pcvs.value_label) ORDER BY pcvs.value_label ASC)) AS fixedPropValuesList,
                array_agg(pcvs.value_label ORDER BY pcvs.value_label ASC) AS fixedPropValuesNamesList
            FROM
                diwi.project_category_changelog pcc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pcc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pcc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.property p ON p.id = pcc.property_id AND p.type = 'FIXED'
                    JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                    JOIN diwi.property_category_value_state pcvs ON pccv.property_value_id = pcvs.category_value_id AND pcvs.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pcc.change_end_date IS NULL
            GROUP BY pcc.project_id, ps.property_name
        ),
        active_project_ordinal_fixed_props AS (
            SELECT
                ppc.project_id, ps.property_name AS fixedPropertyName,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN array_agg(vs.ordinal_level || ' ' || vs.value_label)
                    WHEN ppc.value_type = 'RANGE' THEN array_agg(vsMin.ordinal_level || ' ' || vsMin.value_label) || array_agg(vsMax.ordinal_level || ' ' || vsMax.value_label)
                    END AS ordinalValuesNamesList,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN  to_jsonb(array_agg(jsonb_build_object('id', vs.ordinal_value_id, 'name', vs.ordinal_level || ' ' || vs.value_label)))
                    WHEN ppc.value_type = 'RANGE' THEN to_jsonb(array_agg(jsonb_build_object('id', vsMin.ordinal_value_id, 'name', vsMin.ordinal_level || ' ' || vsMin.value_label)) ||
                                                                array_agg(jsonb_build_object('id', vsMax.ordinal_value_id, 'name', vsMax.ordinal_level || ' ' || vsMax.value_label)))
                    END AS ordinalValuesList
            FROM
                diwi.project_ordinal_changelog ppc
                    JOIN diwi.milestone_state sms ON sms.milestone_id = ppc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = ppc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.property p ON p.id = ppc.property_id AND p.type = 'FIXED'
                    JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vs
                              ON ppc.value_id = vs.ordinal_value_id AND vs.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vsMin
                              ON ppc.min_value_id = vsMin.ordinal_value_id AND vsMin.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vsMax
                              ON ppc.max_value_id = vsMax.ordinal_value_id AND vsMax.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND ppc.change_end_date IS NULL
            GROUP BY ppc.project_id, ps.property_name, ppc.value_type
        ),
        future_projects AS (
            SELECT
                p.id, sms.date AS startDate, ems.date AS endDate, sms.milestone_id AS start_milestone_id
            FROM
                diwi.project p
                    JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date > _now_
        ),
        future_project_names AS (
            SELECT
                pnc.project_id, pnc.name
            FROM
                future_projects fp
                    JOIN diwi.project_name_changelog pnc ON fp.id = pnc.project_id
                        AND pnc.start_milestone_id = fp.start_milestone_id AND pnc.change_end_date IS NULL
        ),
        future_project_fases AS (
            SELECT
                pfc.project_id, pfc.project_fase
            FROM
                future_projects fp
                    JOIN diwi.project_fase_changelog pfc ON fp.id = pfc.project_id
                        AND pfc.start_milestone_id = fp.start_milestone_id AND pfc.change_end_date IS NULL
        ),
        future_project_plan_types AS (
            SELECT
                pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
            FROM
                future_projects fp
                    JOIN diwi.project_plan_type_changelog pptc ON fp.id = pptc.project_id
                        AND pptc.start_milestone_id = fp.start_milestone_id AND pptc.change_end_date IS NULL
                    JOIN diwi.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
            GROUP BY pptc.project_id
        ),
        future_project_planologische_planstatus AS (
            SELECT
                pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
            FROM
                future_projects fp
                    JOIN diwi.project_planologische_planstatus_changelog pppc ON fp.id = pppc.project_id
                        AND pppc.start_milestone_id = fp.start_milestone_id AND pppc.change_end_date IS NULL
                    JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
            GROUP BY  pppc.project_id
        ),
        future_project_woningblok_totalvalue AS (
            SELECT
                w.project_id,
                SUM(wmc.amount *
                    CASE wmc.mutation_kind
                        WHEN 'CONSTRUCTION' THEN 1
                        WHEN 'DEMOLITION' THEN -1
                    END) AS total_value
            FROM
                future_projects fp
                    JOIN diwi.woningblok w ON fp.id = w.project_id
                    JOIN diwi.woningblok_mutatie_changelog wmc ON w.id = wmc.woningblok_id
                        AND wmc.start_milestone_id = fp.start_milestone_id AND wmc.change_end_date IS NULL
            GROUP BY w.project_id
        ),
        future_project_fixed_props AS (
            SELECT
                pcc.project_id, ps.property_name AS fixedPropertyName,
                to_jsonb(array_agg(jsonb_build_object('id', pcvs.category_value_id, 'name', pcvs.value_label)  ORDER BY pcvs.value_label ASC)) AS fixedPropValuesList,
                array_agg(pcvs.value_label ORDER BY pcvs.value_label ASC) AS fixedPropValuesNamesList
            FROM
                future_projects fp
                    JOIN diwi.project_category_changelog pcc ON fp.id = pcc.project_id
                        AND pcc.start_milestone_id = fp.start_milestone_id AND pcc.change_end_date IS NULL
                    JOIN diwi.property p ON p.id = pcc.property_id AND p.type = 'FIXED'
                    JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                    JOIN diwi.property_category_value_state pcvs ON pccv.property_value_id = pcvs.category_value_id AND pcvs.change_end_date IS NULL
            GROUP BY pcc.project_id, ps.property_name
        ),
        future_project_ordinal_fixed_props AS (
            SELECT
                ppc.project_id, ps.property_name AS fixedPropertyName,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN array_agg(vs.ordinal_level || ' ' || vs.value_label)
                    WHEN ppc.value_type = 'RANGE' THEN array_agg(vsMin.ordinal_level || ' ' || vsMin.value_label) || array_agg(vsMax.ordinal_level || ' ' || vsMax.value_label)
                    END AS ordinalValuesNamesList,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN  to_jsonb(array_agg(jsonb_build_object('id', vs.ordinal_value_id, 'name', vs.ordinal_level || ' ' || vs.value_label)))
                    WHEN ppc.value_type = 'RANGE' THEN to_jsonb(array_agg(jsonb_build_object('id', vsMin.ordinal_value_id, 'name', vsMin.ordinal_level || ' ' || vsMin.value_label)) ||
                                                                array_agg(jsonb_build_object('id', vsMax.ordinal_value_id, 'name', vsMax.ordinal_level || ' ' || vsMax.value_label)))
                    END AS ordinalValuesList
            FROM
                future_projects fp
                    JOIN diwi.project_ordinal_changelog ppc ON fp.id = ppc.project_id
                        AND ppc.start_milestone_id = fp.start_milestone_id AND ppc.change_end_date IS NULL
                    JOIN diwi.property p ON p.id = ppc.property_id AND p.type = 'FIXED'
                    JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vs
                              ON ppc.value_id = vs.ordinal_value_id AND vs.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vsMin
                              ON ppc.min_value_id = vsMin.ordinal_value_id AND vsMin.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vsMax
                              ON ppc.max_value_id = vsMax.ordinal_value_id AND vsMax.change_end_date IS NULL
            GROUP BY ppc.project_id, ps.property_name, ppc.value_type
        ),
        past_projects AS (
            SELECT
                p.id, sms.date AS startDate, ems.date AS endDate, ems.milestone_id AS end_milestone_id
            FROM
                diwi.project p
                    JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                ems.date <= _now_
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
        past_project_woningblok_totalvalue AS (
            SELECT
                w.project_id,
                SUM(wmc.amount *
                    CASE wmc.mutation_kind
                        WHEN 'CONSTRUCTION' THEN 1
                        WHEN 'DEMOLITION' THEN -1
                    END) AS total_value
            FROM
                past_projects pp
                    JOIN diwi.woningblok w ON pp.id = w.project_id
                    JOIN diwi.woningblok_mutatie_changelog wmc ON w.id = wmc.woningblok_id
                        AND wmc.end_milestone_id = pp.end_milestone_id AND wmc.change_end_date IS NULL
            GROUP BY w.project_id
        ),
        past_project_fixed_props AS (
            SELECT
                pcc.project_id, ps.property_name AS fixedPropertyName,
                to_jsonb(array_agg(jsonb_build_object('id', pcvs.category_value_id, 'name', pcvs.value_label)  ORDER BY pcvs.value_label ASC)) AS fixedPropValuesList,
                array_agg(pcvs.value_label ORDER BY pcvs.value_label ASC) AS fixedPropValuesNamesList
            FROM
                past_projects pp
                    JOIN diwi.project_category_changelog pcc ON pp.id = pcc.project_id
                    AND pcc.end_milestone_id = pp.end_milestone_id AND pcc.change_end_date IS NULL
                    JOIN diwi.property p ON p.id = pcc.property_id AND p.type = 'FIXED'
                    JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                    JOIN diwi.property_category_value_state pcvs ON pccv.property_value_id = pcvs.category_value_id AND pcvs.change_end_date IS NULL
            GROUP BY pcc.project_id, ps.property_name
        ),
        past_project_ordinal_fixed_props AS (
            SELECT
                ppc.project_id, ps.property_name AS fixedPropertyName,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN array_agg(vs.ordinal_level || ' ' || vs.value_label)
                    WHEN ppc.value_type = 'RANGE' THEN array_agg(vsMin.ordinal_level || ' ' || vsMin.value_label) || array_agg(vsMax.ordinal_level || ' ' || vsMax.value_label)
                    END AS ordinalValuesNamesList,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN  to_jsonb(array_agg(jsonb_build_object('id', vs.ordinal_value_id, 'name', vs.ordinal_level || ' ' || vs.value_label)))
                    WHEN ppc.value_type = 'RANGE' THEN to_jsonb(array_agg(jsonb_build_object('id', vsMin.ordinal_value_id, 'name', vsMin.ordinal_level || ' ' || vsMin.value_label)) ||
                                                                array_agg(jsonb_build_object('id', vsMax.ordinal_value_id, 'name', vsMax.ordinal_level || ' ' || vsMax.value_label)))
                    END AS ordinalValuesList
            FROM
                past_projects pp
                    JOIN diwi.project_ordinal_changelog ppc ON pp.id = ppc.project_id
                        AND ppc.end_milestone_id = pp.end_milestone_id AND ppc.change_end_date IS NULL
                    JOIN diwi.property p ON p.id = ppc.property_id AND p.type = 'FIXED'
                    JOIN diwi.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vs
                              ON ppc.value_id = vs.ordinal_value_id AND vs.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vsMin
                              ON ppc.min_value_id = vsMin.ordinal_value_id AND vsMin.change_end_date IS NULL
                    LEFT JOIN diwi.property_ordinal_value_state vsMax
                              ON ppc.max_value_id = vsMax.ordinal_value_id AND vsMax.change_end_date IS NULL
            GROUP BY ppc.project_id, ps.property_name, ppc.value_type
        ),
        project_users AS (
            SELECT
                q.project_id    AS project_id,
                array_agg(array[q.usergroup_id::TEXT, q.usergroup_name, q.user_id::TEXT, q.user_initials, q.user_last_name, q.user_first_name]) AS users,
                array_agg(q.user_initials ORDER BY q.user_initials)      AS users_initials
            FROM (
                SELECT DISTINCT
                    ps.project_id as project_id,
                    us.user_id AS user_id,
                    LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS user_initials,
                    us.last_name AS user_last_name,
                    us.first_name AS user_first_name,
                    ugs.usergroup_id AS usergroup_id,
                    ugs.naam AS usergroup_name
                FROM diwi.project_state ps
                    JOIN diwi.usergroup_to_project ugtp ON ps.project_id = ugtp.project_id AND ugtp.change_end_date IS NULL
                    JOIN diwi.usergroup_state ugs ON ugtp.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                    LEFT JOIN diwi.user_to_usergroup utug ON ugtp.usergroup_id = utug.usergroup_id
                    LEFT JOIN diwi.user_state us ON utug.user_id = us.user_id AND us.change_end_date IS NULL
                WHERE
                    ps.change_end_date IS NULL
                ) AS q
            GROUP BY q.project_id
        )

    SELECT ap.id                    AS projectId,
           ps.id                    AS projectStateId,
           apn.name                 AS projectName,
           ps.project_colour        AS projectColor,
           ps.latitude              AS latitude,
           ps.longitude             AS longitude,
           ps.confidentiality_level AS confidentialityLevel,
           owners.users                  AS projectOwners,
           owners.users_initials         AS projectOwnersInitials,
           ap.startDate             AS startDate,
           to_char( ap.startDate, 'YYYY-MM-DD') AS startDateStr,
           ap.endDate               AS endDate,
           to_char( ap.endDate, 'YYYY-MM-DD') AS endDateStr,
           appt.plan_types          AS planType,
           apop.ordinalValuesNamesList   AS priorityNamesList,
           apop.ordinalValuesList   AS priorityList,
           apf.project_fase         AS projectPhase,
           appp.planning_planstatus AS planningPlanStatus,
           apmr.fixedPropValuesList         AS municipalityRoleList,
           apmr.fixedPropValuesNamesList    AS municipalityRoleNamesList,
           apwv.total_value         AS totalValue,
           apr.fixedPropValuesList         AS municipalityList,
           apr.fixedPropValuesNamesList    AS municipalityNamesList,
           apd.fixedPropValuesList         AS districtList,
           apd.fixedPropValuesNamesList    AS districtNamesList,
           apne.fixedPropValuesList         AS neighbourhoodList,
           apne.fixedPropValuesNamesList   AS neighbourhoodNamesList
    FROM
        active_projects ap
            LEFT JOIN diwi.project_state ps ON ps.project_id = ap.id AND ps.change_end_date IS NULL
            LEFT JOIN active_project_names apn ON apn.project_id = ap.id
            LEFT JOIN active_project_plan_types appt ON appt.project_id = ap.id
            LEFT JOIN active_project_fases apf ON apf.project_id = ap.id
            LEFT JOIN active_project_planologische_planstatus appp ON appp.project_id = ap.id
            LEFT JOIN active_project_woningblok_totalvalue apwv ON apwv.project_id = ap.id
            LEFT JOIN active_project_ordinal_fixed_props apop ON apop.project_id = ap.id AND apop.fixedPropertyName = 'priority'
            LEFT JOIN active_project_fixed_props apmr ON apmr.project_id = ap.id AND apmr.fixedPropertyName = 'municipalityRole'
            LEFT JOIN active_project_fixed_props apr ON apr.project_id = ap.id AND apr.fixedPropertyName = 'municipality'
            LEFT JOIN active_project_fixed_props apd ON apd.project_id = ap.id AND apd.fixedPropertyName = 'district'
            LEFT JOIN active_project_fixed_props apne ON apne.project_id = ap.id AND apne.fixedPropertyName = 'neighbourhood'
            LEFT JOIN project_users owners ON ps.project_id = owners.project_id

    UNION

    SELECT fp.id                    AS projectId,
           ps.id                    AS projectStateId,
           fpn.name                 AS projectName,
           ps.project_colour        AS projectColor,
           ps.latitude              AS latitude,
           ps.longitude             AS longitude,
           ps.confidentiality_level AS confidentialityLevel,
           owners.users                  AS projectOwners,
           owners.users_initials         AS projectOwnersInitials,
           fp.startDate             AS startDate,
           to_char( fp.startDate, 'YYYY-MM-DD') AS startDateStr,
           fp.endDate               AS endDate,
           to_char( fp.endDate, 'YYYY-MM-DD') AS endDateStr,
           fppt.plan_types          AS planType,
           fpop.ordinalValuesNamesList   AS priorityNamesList,
           fpop.ordinalValuesList   AS priorityList,
           fpf.project_fase         AS projectPhase,
           fppp.planning_planstatus AS planningPlanStatus,
           fpmr.fixedPropValuesList         AS municipalityRoleList,
           fpmr.fixedPropValuesNamesList    AS municipalityRoleNamesList,
           fpwv.total_value         AS totalValue,
           fpr.fixedPropValuesList         AS municipalityList,
           fpr.fixedPropValuesNamesList    AS municipalityNamesList,
           fpd.fixedPropValuesList         AS districtList,
           fpd.fixedPropValuesNamesList    AS districtNamesList,
           fpne.fixedPropValuesList         AS neighbourhoodList,
           fpne.fixedPropValuesNamesList   AS neighbourhoodNamesList
    FROM
        future_projects fp
            LEFT JOIN diwi.project_state ps ON ps.project_id = fp.id AND ps.change_end_date IS NULL
            LEFT JOIN future_project_names fpn ON fpn.project_id = fp.id
            LEFT JOIN future_project_plan_types fppt ON fppt.project_id = fp.id
            LEFT JOIN future_project_fases fpf ON fpf.project_id = fp.id
            LEFT JOIN future_project_planologische_planstatus fppp ON fppp.project_id = fp.id
            LEFT JOIN future_project_woningblok_totalvalue fpwv ON fpwv.project_id = fp.id
            LEFT JOIN future_project_ordinal_fixed_props fpop ON fpop.project_id = fp.id AND fpop.fixedPropertyName = 'priority'
            LEFT JOIN future_project_fixed_props fpmr ON fpmr.project_id = fp.id AND fpmr.fixedPropertyName = 'municipalityRole'
            LEFT JOIN future_project_fixed_props fpr ON fpr.project_id = fp.id AND fpr.fixedPropertyName = 'municipality'
            LEFT JOIN future_project_fixed_props fpd ON fpd.project_id = fp.id AND fpd.fixedPropertyName = 'district'
            LEFT JOIN future_project_fixed_props fpne ON fpne.project_id = fp.id AND fpne.fixedPropertyName = 'neighbourhood'
            LEFT JOIN project_users owners ON ps.project_id = owners.project_id

    UNION

    SELECT pp.id                    AS projectId,
           ps.id                    AS projectStateId,
           ppn.name                 AS projectName,
           ps.project_colour        AS projectColor,
           ps.latitude              AS latitude,
           ps.longitude             AS longitude,
           ps.confidentiality_level AS confidentialityLevel,
           owners.users                  AS projectOwners,
           owners.users_initials         AS projectOwnersInitials,
           pp.startDate             AS startDate,
           to_char( pp.startDate, 'YYYY-MM-DD') AS startDateStr,
           pp.endDate               AS endDate,
           to_char( pp.endDate, 'YYYY-MM-DD') AS endDateStr,
           pppt.plan_types          AS planType,
           ppop.ordinalValuesNamesList   AS priorityNamesList,
           ppop.ordinalValuesList   AS priorityList,
           ppf.project_fase         AS projectPhase,
           pppp.planning_planstatus AS planningPlanStatus,
           ppmr.fixedPropValuesList         AS municipalityRoleList,
           ppmr.fixedPropValuesNamesList    AS municipalityRoleNamesList,
           ppwv.total_value         AS totalValue,
           ppr.fixedPropValuesList         AS municipalityList,
           ppr.fixedPropValuesNamesList    AS municipalityNamesList,
           ppd.fixedPropValuesList         AS districtList,
           ppd.fixedPropValuesNamesList    AS districtNamesList,
           ppne.fixedPropValuesList         AS neighbourhoodList,
           ppne.fixedPropValuesNamesList   AS neighbourhoodNamesList
    FROM
        past_projects pp
            LEFT JOIN diwi.project_state ps ON ps.project_id = pp.id AND ps.change_end_date IS NULL
            LEFT JOIN past_project_names ppn ON ppn.project_id = pp.id
            LEFT JOIN past_project_plan_types pppt ON pppt.project_id = pp.id
            LEFT JOIN past_project_fases ppf ON ppf.project_id = pp.id
            LEFT JOIN past_project_planologische_planstatus pppp ON pppp.project_id = pp.id
            LEFT JOIN past_project_woningblok_totalvalue ppwv ON ppwv.project_id = pp.id
            LEFT JOIN past_project_ordinal_fixed_props ppop ON ppop.project_id = pp.id AND ppop.fixedPropertyName = 'priority'
            LEFT JOIN past_project_fixed_props ppmr ON ppmr.project_id = pp.id AND ppmr.fixedPropertyName = 'municipalityRole'
            LEFT JOIN past_project_fixed_props ppr ON ppr.project_id = pp.id AND ppr.fixedPropertyName = 'municipality'
            LEFT JOIN past_project_fixed_props ppd ON ppd.project_id = pp.id AND ppd.fixedPropertyName = 'district'
            LEFT JOIN past_project_fixed_props ppne ON ppne.project_id = pp.id AND ppne.fixedPropertyName = 'neighbourhood'
            LEFT JOIN project_users owners ON ps.project_id = owners.project_id

) AS q
  WHERE
    (
      ( _user_uuid_::TEXT IN (select owners.id from unnest(q.projectOwners) with ordinality owners(id,n) where owners.n % 6 = 3)) OR
      ( _user_role_ IN ('User', 'UserPlus') AND q.confidentialityLevel != 'PRIVATE') OR
      ( _user_role_ = 'Management' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL') ) OR
      ( _user_role_ = 'Council' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT') )
    )
    AND
        CASE
            WHEN _filterCondition_ = 'CONTAINS' AND _filterColumn_  = 'projectName' THEN q.projectName ILIKE '%' || _filterValues_[1] || '%'
            WHEN _filterCondition_ = 'CONTAINS' AND  _filterColumn_  = 'startDate' THEN q.startDateStr ILIKE '%' || _filterValues_[1] || '%'
            WHEN _filterCondition_ = 'CONTAINS' AND  _filterColumn_  = 'endDate' THEN q.endDateStr ILIKE '%' || _filterValues_[1] || '%'
            WHEN _filterCondition_ = 'ANY_OF' AND  _filterColumn_  = 'confidentialityLevel' THEN q.confidentialityLevel = ANY(_filterValues_::diwi.confidentiality[])
            WHEN _filterCondition_ = 'ANY_OF' AND  _filterColumn_  = 'projectPhase' THEN q.projectPhase = ANY(_filterValues_::diwi.project_phase[])
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'planType' THEN q.planType && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'priority' THEN q.priorityNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'planningPlanStatus' THEN q.planningPlanStatus && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'municipalityRole' THEN q.municipalityRoleNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'municipality' THEN q.municipalityNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'district' THEN q.districtNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'neighbourhood' THEN q.neighbourhoodNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'projectOwners' THEN q.projectOwnersInitials && _filterValues_
            WHEN _filterColumn_ IS NULL THEN 1 = 1
        END

    ORDER BY
        CASE WHEN _sortColumn_ = 'projectName' AND _sortDirection_ = 'ASC' THEN q.projectName END ASC,
        CASE WHEN _sortColumn_ = 'totalValue' AND _sortDirection_ = 'ASC' THEN q.totalValue END ASC,
        CASE WHEN _sortColumn_ = 'endDate' AND _sortDirection_ = 'ASC' THEN q.endDate END ASC,
        CASE WHEN _sortColumn_ = 'startDate' AND _sortDirection_ = 'ASC' THEN q.startDate END ASC,
        CASE WHEN _sortColumn_ = 'confidentialityLevel' AND _sortDirection_ = 'ASC' THEN q.confidentialityLevel END ASC,
        CASE WHEN _sortColumn_ = 'projectPhase' AND _sortDirection_ = 'ASC' THEN q.projectPhase END ASC,
        CASE WHEN _sortColumn_ = 'planType' AND _sortDirection_ = 'ASC' THEN q.planType END ASC,
        CASE WHEN _sortColumn_ = 'priority' AND _sortDirection_ = 'ASC' THEN q.priorityNamesList COLLATE "diwi_numeric" END ASC,
        CASE WHEN _sortColumn_ = 'planningPlanStatus' AND _sortDirection_ = 'ASC' THEN q.planningPlanStatus END ASC,
        CASE WHEN _sortColumn_ = 'municipalityRole' AND _sortDirection_ = 'ASC' THEN q.municipalityRoleNamesList END ASC,
        CASE WHEN _sortColumn_ = 'municipality' AND _sortDirection_ = 'ASC' THEN q.municipalityNamesList END ASC,
        CASE WHEN _sortColumn_ = 'district' AND _sortDirection_ = 'ASC' THEN q.districtNamesList END ASC,
        CASE WHEN _sortColumn_ = 'neighbourhood' AND _sortDirection_ = 'ASC' THEN q.neighbourhoodNamesList END ASC,
        CASE WHEN _sortColumn_ = 'projectOwners' AND _sortDirection_ = 'ASC' THEN q.projectOwnersInitials END ASC,

        CASE WHEN _sortColumn_ = 'projectName' AND _sortDirection_ = 'DESC' THEN q.projectName END DESC,
        CASE WHEN _sortColumn_ = 'totalValue' AND _sortDirection_ = 'DESC' THEN q.totalValue END DESC,
        CASE WHEN _sortColumn_ = 'endDate' AND _sortDirection_ = 'DESC' THEN q.endDate END DESC,
        CASE WHEN _sortColumn_ = 'startDate' AND _sortDirection_ = 'DESC' THEN q.startDate END DESC,
        CASE WHEN _sortColumn_ = 'confidentialityLevel' AND _sortDirection_ = 'DESC' THEN q.confidentialityLevel END DESC,
        CASE WHEN _sortColumn_ = 'projectPhase' AND _sortDirection_ = 'DESC' THEN q.projectPhase END DESC,
        CASE WHEN _sortColumn_ = 'planType' AND _sortDirection_ = 'DESC' THEN q.planType END DESC,
        CASE WHEN _sortColumn_ = 'priority' AND _sortDirection_ = 'DESC' THEN q.priorityNamesList COLLATE "diwi_numeric" END DESC,
        CASE WHEN _sortColumn_ = 'planningPlanStatus' AND _sortDirection_ = 'DESC' THEN q.planningPlanStatus END DESC,
        CASE WHEN _sortColumn_ = 'municipalityRole' AND _sortDirection_ = 'DESC' THEN q.municipalityRoleNamesList END DESC,
        CASE WHEN _sortColumn_ = 'municipality' AND _sortDirection_ = 'DESC' THEN q.municipalityNamesList END DESC,
        CASE WHEN _sortColumn_ = 'district' AND _sortDirection_ = 'DESC' THEN q.districtNamesList END DESC,
        CASE WHEN _sortColumn_ = 'neighbourhood' AND _sortDirection_ = 'DESC' THEN q.neighbourhoodNamesList END DESC,
        CASE WHEN _sortColumn_ = 'projectOwners' AND _sortDirection_ = 'DESC' THEN q.projectOwnersInitials END DESC

    LIMIT _limit_ OFFSET _offset_;

END;$$

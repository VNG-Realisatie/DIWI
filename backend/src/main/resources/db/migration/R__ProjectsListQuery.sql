DROP FUNCTION IF EXISTS get_active_and_future_projects_list;

CREATE OR REPLACE FUNCTION get_active_and_future_projects_list (
  _now_ date,
  _offset_ int,
  _limit_ int,
  _sortColumn_ text,
  _sortDirection_ text,
  _filterColumn_ text,
  _filterValues_ text[],
  _filterCondition_ text
)
	RETURNS TABLE (
        projectId UUID,
        projectStateId UUID,
        projectName TEXT,
        projectOwnersArray TEXT[][],
        projectLeadersArray TEXT[][],
        projectColor TEXT,
        latitude FLOAT8,
        longitude FLOAT8,
        confidentialityLevel diwi_testset.confidentiality,
        startDate DATE,
        endDate DATE,
        planType TEXT[],
        priority JSONB,
        projectPhase diwi_testset.project_phase,
        planningPlanStatus TEXT[],
        municipalityRole JSONB,
        totalValue BIGINT,
        municipality JSONB,
        district JSONB,
        neighbourhood JSONB
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

SELECT  q.projectId,
        q.projectStateId,
        q.projectName,
        q.projectOwners            AS projectOwnersArray,
        q.projectLeaders           AS projectLeadersArray,
        q.projectColor,
        q.latitude,
        q.longitude,
        q.confidentialityLevel,
        q.startDate,
        q.endDate,
        q.planType,
        q.priorityModel            AS priority,
        q.projectPhase,
        q.planningPlanStatus,
        q.municipalityRoleModel    AS municipalityRole,
        q.totalValue,
        q.municipalityList               AS municipality,
        q.districtList             AS district,
        q.neighbourhoodList        AS neighbourhood
FROM (

    WITH
        active_projects AS (
            SELECT
                p.id, sms.date AS startDate, ems.date AS endDate
            FROM
                diwi_testset.project p
                    JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date
        ),
        active_project_names AS (
            SELECT
                pnc.project_id, pnc.name
            FROM
                diwi_testset.project_name_changelog pnc
                    JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pnc.change_end_date IS NULL
        ),
        active_project_fases AS (
            SELECT
                pfc.project_id, pfc.project_fase
            FROM
                diwi_testset.project_fase_changelog pfc
                    JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pfc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pfc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pfc.change_end_date IS NULL
        ),
        active_project_plan_types AS (
            SELECT
                pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
            FROM
                diwi_testset.project_plan_type_changelog pptc
                    JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pptc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pptc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi_testset.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pptc.change_end_date IS NULL
            GROUP BY pptc.project_id
        ),
        active_project_planologische_planstatus AS (
            SELECT
                pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
            FROM
                diwi_testset.project_planologische_planstatus_changelog pppc
                    JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pppc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pppc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi_testset.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pppc.change_end_date IS NULL
            GROUP BY pppc.project_id
        ),
        active_project_priorities AS (
            SELECT ppc.project_id,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN array_agg(vs.ordinal_level || ' ' || vs.value_label)
                    WHEN ppc.value_type = 'RANGE' THEN array_agg(vsMin.ordinal_level || ' ' || vsMin.value_label) || array_agg(vsMax.ordinal_level || ' ' || vsMax.value_label)
                END AS project_priorities,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN  to_jsonb(array_agg(jsonb_build_object('id', vs.project_priorisering_value_id, 'name', vs.ordinal_level || ' ' || vs.value_label)))
                    WHEN ppc.value_type = 'RANGE' THEN to_jsonb(array_agg(jsonb_build_object('id', vsMin.project_priorisering_value_id, 'name', vsMin.ordinal_level || ' ' || vsMin.value_label)) ||
                                                                array_agg(jsonb_build_object('id', vsMax.project_priorisering_value_id, 'name', vsMax.ordinal_level || ' ' || vsMax.value_label)))
                END AS project_prioritiesModel
            FROM
                diwi_testset.project_priorisering_changelog ppc
                    JOIN diwi_testset.milestone_state sms ON sms.milestone_id = ppc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state ems ON ems.milestone_id = ppc.end_milestone_id AND ems.change_end_date IS NULL
                    LEFT JOIN diwi_testset.project_priorisering_value_state vs
                        ON ppc.project_priorisering_value_id = vs.project_priorisering_value_id AND vs.change_end_date IS NULL
                    LEFT JOIN diwi_testset.project_priorisering_value_state vsMin
                        ON ppc.project_priorisering_min_value_id = vsMin.project_priorisering_value_id AND vsMin.change_end_date IS NULL
                    LEFT JOIN diwi_testset.project_priorisering_value_state vsMax
                        ON ppc.project_priorisering_max_value_id = vsMax.project_priorisering_value_id AND vsMax.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND ppc.change_end_date IS NULL
            GROUP BY ppc.project_id, ppc.value_type
        ),
        active_project_gemeenterol AS (
            SELECT
                pgc.project_id,
                to_jsonb(array_agg(jsonb_build_object('id', pgvs.project_gemeenterol_value_id, 'name', pgvs.value_label) ORDER BY pgvs.value_label ASC)) AS municipality_roleModel,
                array_agg(pgvs.value_label ORDER BY pgvs.value_label ASC) AS municipality_role
            FROM
                diwi_testset.project_gemeenterol_changelog pgc
                    JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pgc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pgc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi_testset.project_gemeenterol_value_state pgvs
                        ON pgvs.project_gemeenterol_value_id = pgc.project_gemeenterol_value_id AND pgvs.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pgc.change_end_date IS NULL
            GROUP BY pgc.project_id
        ),
        active_project_woningblok_totalvalue AS (
            SELECT
                w.project_id, SUM(COALESCE(wmc.bruto_plancapaciteit, 0)) AS total_value
            FROM
                diwi_testset.woningblok_mutatie_changelog wmc
                    JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wmc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wmc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi_testset.woningblok w ON wmc.woningblok_id = w.id
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND wmc.change_end_date IS NULL
            GROUP BY w.project_id
        ),
        active_project_fixed_props AS (
            SELECT
                pcc.project_id, ps.property_name AS fixedPropertyName,
                to_jsonb(array_agg(jsonb_build_object('id', pcvs.category_value_id, 'name', pcvs.value_label) ORDER BY pcvs.value_label ASC)) AS locationList,
                array_agg(pcvs.value_label ORDER BY pcvs.value_label ASC) AS locationNamesList
            FROM
                diwi_testset.project_category_changelog pcc
                    JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pcc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pcc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi_testset.property p ON p.id = pcc.property_id AND p.type = 'FIXED'
                    JOIN diwi_testset.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    JOIN diwi_testset.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                    JOIN diwi_testset.property_category_value_state pcvs ON pccv.property_value_id = pcvs.category_value_id AND pcvs.change_end_date IS NULL
            WHERE
                sms.date <= _now_ AND _now_ < ems.date AND pcc.change_end_date IS NULL
            GROUP BY pcc.project_id, ps.property_name
        ),
        future_projects AS (
            SELECT
                p.id, sms.date AS startDate, ems.date AS endDate, sms.milestone_id AS start_milestone_id
            FROM
                diwi_testset.project p
                    JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date > _now_
        ),
        future_project_names AS (
            SELECT
                pnc.project_id, pnc.name
            FROM
                future_projects fp
                    JOIN diwi_testset.project_name_changelog pnc ON fp.id = pnc.project_id
                        AND pnc.start_milestone_id = fp.start_milestone_id AND pnc.change_end_date IS NULL
        ),
        future_project_fases AS (
            SELECT
                pfc.project_id, pfc.project_fase
            FROM
                future_projects fp
                    JOIN diwi_testset.project_fase_changelog pfc ON fp.id = pfc.project_id
                        AND pfc.start_milestone_id = fp.start_milestone_id AND pfc.change_end_date IS NULL
        ),
        future_project_plan_types AS (
            SELECT
                pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
            FROM
                future_projects fp
                    JOIN diwi_testset.project_plan_type_changelog pptc ON fp.id = pptc.project_id
                        AND pptc.start_milestone_id = fp.start_milestone_id AND pptc.change_end_date IS NULL
                    JOIN diwi_testset.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
            GROUP BY pptc.project_id
        ),
        future_project_planologische_planstatus AS (
            SELECT
                pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
            FROM
                future_projects fp
                    JOIN diwi_testset.project_planologische_planstatus_changelog pppc ON fp.id = pppc.project_id
                        AND pppc.start_milestone_id = fp.start_milestone_id AND pppc.change_end_date IS NULL
                    JOIN diwi_testset.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
            GROUP BY  pppc.project_id
        ),
        future_project_priorities AS (
            SELECT
                ppc.project_id,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN array_agg(vs.ordinal_level || ' ' || vs.value_label)
                    WHEN ppc.value_type = 'RANGE' THEN array_agg(vsMin.ordinal_level || ' ' || vsMin.value_label) || array_agg(vsMax.ordinal_level || ' ' || vsMax.value_label)
                END AS project_priorities,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN  to_jsonb(array_agg(jsonb_build_object('id', vs.project_priorisering_value_id, 'name', vs.ordinal_level || ' ' || vs.value_label)))
                    WHEN ppc.value_type = 'RANGE' THEN to_jsonb(array_agg(jsonb_build_object('id', vsMin.project_priorisering_value_id, 'name', vsMin.ordinal_level || ' ' || vsMin.value_label)) ||
                                                                array_agg(jsonb_build_object('id', vsMax.project_priorisering_value_id, 'name', vsMax.ordinal_level || ' ' || vsMax.value_label)))
                END AS project_prioritiesModel
            FROM
                future_projects fp
                    JOIN diwi_testset.project_priorisering_changelog ppc ON fp.id = ppc.project_id
                        AND ppc.start_milestone_id = fp.start_milestone_id AND ppc.change_end_date IS NULL
                    LEFT JOIN diwi_testset.project_priorisering_value_state vs
                           ON ppc.project_priorisering_value_id = vs.project_priorisering_value_id AND vs.change_end_date IS NULL
                    LEFT JOIN diwi_testset.project_priorisering_value_state vsMin
                           ON ppc.project_priorisering_min_value_id = vsMin.project_priorisering_value_id AND vsMin.change_end_date IS NULL
                    LEFT JOIN diwi_testset.project_priorisering_value_state vsMax
                           ON ppc.project_priorisering_max_value_id = vsMax.project_priorisering_value_id AND vsMax.change_end_date IS NULL
            GROUP BY ppc.project_id, ppc.value_type
        ),
        future_project_gemeenterol AS (
            SELECT
                pgc.project_id,
                to_jsonb(array_agg(jsonb_build_object('id', pgvs.project_gemeenterol_value_id, 'name', pgvs.value_label) ORDER BY pgvs.value_label ASC)) AS municipality_roleModel,
                array_agg(pgvs.value_label ORDER BY pgvs.value_label ASC) AS municipality_role
            FROM
                future_projects fp
                    JOIN diwi_testset.project_gemeenterol_changelog pgc ON fp.id = pgc.project_id
                        AND pgc.start_milestone_id = fp.start_milestone_id AND pgc.change_end_date IS NULL
                    JOIN diwi_testset.project_gemeenterol_value_state pgvs
                        ON pgvs.project_gemeenterol_value_id = pgc.project_gemeenterol_value_id AND pgvs.change_end_date IS NULL
            GROUP BY  pgc.project_id
        ),
        future_project_woningblok_totalvalue AS (
            SELECT
                w.project_id, SUM(COALESCE(wmc.bruto_plancapaciteit, 0)) AS total_value
            FROM
                future_projects fp
                    JOIN diwi_testset.woningblok w ON fp.id = w.project_id
                    JOIN diwi_testset.woningblok_mutatie_changelog wmc ON w.id = wmc.woningblok_id
                        AND wmc.start_milestone_id = fp.start_milestone_id AND wmc.change_end_date IS NULL
            GROUP BY w.project_id
        ),
        future_project_fixed_props AS (
            SELECT
                pcc.project_id, ps.property_name AS fixedPropertyName,
                to_jsonb(array_agg(jsonb_build_object('id', pcvs.category_value_id, 'name', pcvs.value_label)  ORDER BY pcvs.value_label ASC)) AS locationList,
                array_agg(pcvs.value_label ORDER BY pcvs.value_label ASC) AS locationNamesList
            FROM
                future_projects fp
                    JOIN diwi_testset.project_category_changelog pcc ON fp.id = pcc.project_id
                        AND pcc.start_milestone_id = fp.start_milestone_id AND pcc.change_end_date IS NULL
                    JOIN diwi_testset.property p ON p.id = pcc.property_id AND p.type = 'FIXED'
                    JOIN diwi_testset.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    JOIN diwi_testset.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                    JOIN diwi_testset.property_category_value_state pcvs ON pccv.property_value_id = pcvs.category_value_id AND pcvs.change_end_date IS NULL
            GROUP BY pcc.project_id, ps.property_name
        ),
        past_projects AS (
            SELECT
                p.id, sms.date AS startDate, ems.date AS endDate, ems.milestone_id AS end_milestone_id
            FROM
                diwi_testset.project p
                    JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                ems.date <= _now_
        ),
        past_project_names AS (
            SELECT
                pnc.project_id, pnc.name
            FROM
                past_projects pp
                    JOIN diwi_testset.project_name_changelog pnc ON pp.id = pnc.project_id
                        AND pnc.end_milestone_id = pp.end_milestone_id AND pnc.change_end_date IS NULL
        ),
        past_project_fases AS (
            SELECT
                 pfc.project_id, pfc.project_fase
            FROM
                past_projects pp
                    JOIN diwi_testset.project_fase_changelog pfc ON pp.id = pfc.project_id
                        AND pfc.end_milestone_id = pp.end_milestone_id AND pfc.change_end_date IS NULL
        ),
        past_project_plan_types AS (
            SELECT
                pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
            FROM
                past_projects pp
                    JOIN diwi_testset.project_plan_type_changelog pptc ON pp.id = pptc.project_id
                        AND pptc.end_milestone_id = pp.end_milestone_id AND pptc.change_end_date IS NULL
                    JOIN diwi_testset.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
            GROUP BY pptc.project_id
        ),
        past_project_planologische_planstatus AS (
            SELECT
                pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
            FROM
                past_projects pp
                    JOIN diwi_testset.project_planologische_planstatus_changelog pppc ON pp.id = pppc.project_id
                        AND pppc.end_milestone_id = pp.end_milestone_id AND pppc.change_end_date IS NULL
                    JOIN diwi_testset.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
            GROUP BY pppc.project_id
        ),
        past_project_priorities AS (
            SELECT
                ppc.project_id,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN array_agg(vs.ordinal_level || ' ' || vs.value_label)
                    WHEN ppc.value_type = 'RANGE' THEN array_agg(vsMin.ordinal_level || ' ' || vsMin.value_label) || array_agg(vsMax.ordinal_level || ' ' || vsMax.value_label)
                    END AS project_priorities,
                CASE
                    WHEN ppc.value_type = 'SINGLE_VALUE' THEN  to_jsonb(array_agg(jsonb_build_object('id', vs.project_priorisering_value_id, 'name', vs.ordinal_level || ' ' || vs.value_label)))
                    WHEN ppc.value_type = 'RANGE' THEN to_jsonb(array_agg(jsonb_build_object('id', vsMin.project_priorisering_value_id, 'name', vsMin.ordinal_level || ' ' || vsMin.value_label)) ||
                                                                array_agg(jsonb_build_object('id', vsMax.project_priorisering_value_id, 'name', vsMax.ordinal_level || ' ' || vsMax.value_label)))
                END AS project_prioritiesModel
            FROM
                past_projects pp
                    JOIN diwi_testset.project_priorisering_changelog ppc ON pp.id = ppc.project_id
                        AND ppc.end_milestone_id = pp.end_milestone_id AND ppc.change_end_date IS NULL
                    LEFT JOIN diwi_testset.project_priorisering_value_state vs
                           ON ppc.project_priorisering_value_id = vs.project_priorisering_value_id AND vs.change_end_date IS NULL
                    LEFT JOIN diwi_testset.project_priorisering_value_state vsMin
                           ON ppc.project_priorisering_min_value_id = vsMin.project_priorisering_value_id AND vsMin.change_end_date IS NULL
                    LEFT JOIN diwi_testset.project_priorisering_value_state vsMax
                           ON ppc.project_priorisering_max_value_id = vsMax.project_priorisering_value_id AND vsMax.change_end_date IS NULL
            GROUP BY ppc.project_id, ppc.value_type
        ),
        past_project_gemeenterol AS (
            SELECT
                pgc.project_id,
                to_jsonb(array_agg(jsonb_build_object('id', pgvs.project_gemeenterol_value_id, 'name', pgvs.value_label) ORDER BY pgvs.value_label ASC)) AS municipality_roleModel,
                array_agg(pgvs.value_label ORDER BY pgvs.value_label ASC) AS municipality_role
            FROM
                past_projects pp
                    JOIN diwi_testset.project_gemeenterol_changelog pgc ON pp.id = pgc.project_id
                        AND pgc.end_milestone_id = pp.end_milestone_id AND pgc.change_end_date IS NULL
                    JOIN diwi_testset.project_gemeenterol_value_state pgvs
                        ON pgvs.project_gemeenterol_value_id = pgc.project_gemeenterol_value_id AND pgvs.change_end_date IS NULL
            GROUP BY  pgc.project_id
        ),
        past_project_woningblok_totalvalue AS (
            SELECT
                w.project_id, SUM(COALESCE(wmc.bruto_plancapaciteit, 0)) AS total_value
            FROM
                past_projects pp
                    JOIN diwi_testset.woningblok w ON pp.id = w.project_id
                    JOIN diwi_testset.woningblok_mutatie_changelog wmc ON w.id = wmc.woningblok_id
                        AND wmc.end_milestone_id = pp.end_milestone_id AND wmc.change_end_date IS NULL
            GROUP BY w.project_id
        ),
        past_project_fixed_props AS (
            SELECT
                pcc.project_id, ps.property_name AS fixedPropertyName,
                to_jsonb(array_agg(jsonb_build_object('id', pcvs.category_value_id, 'name', pcvs.value_label)  ORDER BY pcvs.value_label ASC)) AS locationList,
                array_agg(pcvs.value_label ORDER BY pcvs.value_label ASC) AS locationNamesList
            FROM
                past_projects pp
                    JOIN diwi_testset.project_category_changelog pcc ON pp.id = pcc.project_id
                    AND pcc.end_milestone_id = pp.end_milestone_id AND pcc.change_end_date IS NULL
                    JOIN diwi_testset.property p ON p.id = pcc.property_id AND p.type = 'FIXED'
                    JOIN diwi_testset.property_state ps ON p.id = ps.property_id AND ps.change_end_date IS NULL
                    JOIN diwi_testset.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                    JOIN diwi_testset.property_category_value_state pcvs ON pccv.property_value_id = pcvs.category_value_id AND pcvs.change_end_date IS NULL
            GROUP BY pcc.project_id, ps.property_name
        ),
        project_users AS (
            SELECT
                q.project_id    AS project_id,
                q.project_rol   AS project_rol,
                array_agg(array[q.organization_id::TEXT, q.organization_name, q.user_id::TEXT, q.user_initials, q.user_last_name, q.user_first_name]) AS users,
                array_agg(q.user_initials ORDER BY q.user_initials)      AS users_initials
            FROM (
                SELECT DISTINCT
                    ps.project_id as project_id,
                    otp.project_rol AS project_rol,
                    us.user_id AS user_id,
                    LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS user_initials,
                    us.last_name AS user_last_name,
                    us.first_name AS user_first_name,
                    os.organization_id AS organization_id,
                    os.naam AS organization_name
                FROM diwi_testset.project_state ps
                    JOIN diwi_testset.organization_to_project otp ON ps.project_id = otp.project_id AND otp.change_end_date IS NULL
                    JOIN diwi_testset.organization_state os ON otp.organization_id = os.organization_id AND os.change_end_date IS NULL
                    JOIN diwi_testset.user_to_organization uto ON otp.organization_id = uto.organization_id
                    JOIN diwi_testset.user_state us ON uto.user_id = us.user_id AND us.change_end_date IS NULL
                WHERE
                    ps.change_end_date IS NULL
                ) AS q
            GROUP BY q.project_id, q.project_rol
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
           app.project_priorities   AS priority,
           app.project_prioritiesModel  AS priorityModel,
           apf.project_fase         AS projectPhase,
           appp.planning_planstatus AS planningPlanStatus,
           apg.municipality_role    AS municipalityRole,
           apg.municipality_roleModel    AS municipalityRoleModel,
           apwv.total_value         AS totalValue,
           apr.locationList         AS municipalityList,
           apr.locationNamesList    AS municipalityNamesList,
           apd.locationList         AS districtList,
           apd.locationNamesList    AS districtNamesList,
           apne.locationList         AS neighbourhoodList,
           apne.locationNamesList   AS neighbourhoodNamesList,
           leaders.users            AS projectLeaders,
           leaders.users_initials   AS projectLeadersInitials
    FROM
        active_projects ap
            LEFT JOIN diwi_testset.project_state ps ON ps.project_id = ap.id AND ps.change_end_date IS NULL
            LEFT JOIN active_project_names apn ON apn.project_id = ap.id
            LEFT JOIN active_project_plan_types appt ON appt.project_id = ap.id
            LEFT JOIN active_project_fases apf ON apf.project_id = ap.id
            LEFT JOIN active_project_planologische_planstatus appp ON appp.project_id = ap.id
            LEFT JOIN active_project_priorities app ON app.project_id = ap.id
            LEFT JOIN active_project_gemeenterol apg ON apg.project_id = ap.id
            LEFT JOIN active_project_woningblok_totalvalue apwv ON apwv.project_id = ap.id
            LEFT JOIN active_project_fixed_props apr ON apr.project_id = ap.id AND apr.fixedPropertyName = 'municipality'
            LEFT JOIN active_project_fixed_props apd ON apd.project_id = ap.id AND apd.fixedPropertyName = 'district'
            LEFT JOIN active_project_fixed_props apne ON apne.project_id = ap.id AND apne.fixedPropertyName = 'neighbourhood'
            LEFT JOIN project_users leaders ON ps.project_id = leaders.project_id AND leaders.project_rol = 'PROJECT_LEIDER'
            LEFT JOIN project_users owners ON ps.project_id = owners.project_id AND owners.project_rol = 'OWNER'

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
           fpp.project_priorities   AS priority,
           fpp.project_prioritiesModel  AS priorityModel,
           fpf.project_fase         AS projectPhase,
           fppp.planning_planstatus AS planningPlanStatus,
           fpg.municipality_role    AS municipalityRole,
           fpg.municipality_roleModel    AS municipalityRoleModel,
           fpwv.total_value         AS totalValue,
           fpr.locationList         AS municipalityList,
           fpr.locationNamesList    AS municipalityNamesList,
           fpd.locationList         AS districtList,
           fpd.locationNamesList    AS districtNamesList,
           fpne.locationList         AS neighbourhoodList,
           fpne.locationNamesList   AS neighbourhoodNamesList,
           leaders.users            AS projectLeaders,
           leaders.users_initials   AS projectLeadersInitials
    FROM
        future_projects fp
            LEFT JOIN diwi_testset.project_state ps ON ps.project_id = fp.id AND ps.change_end_date IS NULL
            LEFT JOIN future_project_names fpn ON fpn.project_id = fp.id
            LEFT JOIN future_project_plan_types fppt ON fppt.project_id = fp.id
            LEFT JOIN future_project_fases fpf ON fpf.project_id = fp.id
            LEFT JOIN future_project_planologische_planstatus fppp ON fppp.project_id = fp.id
            LEFT JOIN future_project_priorities fpp ON fpp.project_id = fp.id
            LEFT JOIN future_project_gemeenterol fpg ON fpg.project_id = fp.id
            LEFT JOIN future_project_woningblok_totalvalue fpwv ON fpwv.project_id = fp.id
            LEFT JOIN future_project_fixed_props fpr ON fpr.project_id = fp.id AND fpr.fixedPropertyName = 'municipality'
            LEFT JOIN future_project_fixed_props fpd ON fpd.project_id = fp.id AND fpd.fixedPropertyName = 'district'
            LEFT JOIN future_project_fixed_props fpne ON fpne.project_id = fp.id AND fpne.fixedPropertyName = 'neighbourhood'
            LEFT JOIN project_users leaders ON ps.project_id = leaders.project_id AND leaders.project_rol = 'PROJECT_LEIDER'
            LEFT JOIN project_users owners ON ps.project_id = owners.project_id AND owners.project_rol = 'OWNER'

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
           ppp.project_priorities   AS priority,
           ppp.project_prioritiesModel  AS priorityModel,
           ppf.project_fase         AS projectPhase,
           pppp.planning_planstatus AS planningPlanStatus,
           ppg.municipality_role    AS municipalityRole,
           ppg.municipality_roleModel    AS municipalityRoleModel,
           ppwv.total_value         AS totalValue,
           ppr.locationList         AS municipalityList,
           ppr.locationNamesList    AS municipalityNamesList,
           ppd.locationList         AS districtList,
           ppd.locationNamesList    AS districtNamesList,
           ppne.locationList         AS neighbourhoodList,
           ppne.locationNamesList   AS neighbourhoodNamesList,
           leaders.users            AS projectLeaders,
           leaders.users_initials   AS projectLeadersInitials
    FROM
        past_projects pp
            LEFT JOIN diwi_testset.project_state ps ON ps.project_id = pp.id AND ps.change_end_date IS NULL
            LEFT JOIN past_project_names ppn ON ppn.project_id = pp.id
            LEFT JOIN past_project_plan_types pppt ON pppt.project_id = pp.id
            LEFT JOIN past_project_fases ppf ON ppf.project_id = pp.id
            LEFT JOIN past_project_planologische_planstatus pppp ON pppp.project_id = pp.id
            LEFT JOIN past_project_priorities ppp ON ppp.project_id = pp.id
            LEFT JOIN past_project_gemeenterol ppg ON ppg.project_id = pp.id
            LEFT JOIN past_project_woningblok_totalvalue ppwv ON ppwv.project_id = pp.id
            LEFT JOIN past_project_fixed_props ppr ON ppr.project_id = pp.id AND ppr.fixedPropertyName = 'municipality'
            LEFT JOIN past_project_fixed_props ppd ON ppd.project_id = pp.id AND ppd.fixedPropertyName = 'district'
            LEFT JOIN past_project_fixed_props ppne ON ppne.project_id = pp.id AND ppne.fixedPropertyName = 'neighbourhood'
            LEFT JOIN project_users leaders ON ps.project_id = leaders.project_id AND leaders.project_rol = 'PROJECT_LEIDER'
            LEFT JOIN project_users owners ON ps.project_id = owners.project_id AND owners.project_rol = 'OWNER'

) AS q
  WHERE
        CASE
            WHEN _filterCondition_ = 'CONTAINS' AND _filterColumn_  = 'projectName' THEN q.projectName ILIKE '%' || _filterValues_[1] || '%'
            WHEN _filterCondition_ = 'CONTAINS' AND  _filterColumn_  = 'startDate' THEN q.startDateStr ILIKE '%' || _filterValues_[1] || '%'
            WHEN _filterCondition_ = 'CONTAINS' AND  _filterColumn_  = 'endDate' THEN q.endDateStr ILIKE '%' || _filterValues_[1] || '%'
            WHEN _filterCondition_ = 'ANY_OF' AND  _filterColumn_  = 'confidentialityLevel' THEN q.confidentialityLevel = ANY(_filterValues_::diwi_testset.confidentiality[])
            WHEN _filterCondition_ = 'ANY_OF' AND  _filterColumn_  = 'projectPhase' THEN q.projectPhase = ANY(_filterValues_::diwi_testset.project_phase[])
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'planType' THEN q.planType && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'priority' THEN q.priority && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'planningPlanStatus' THEN q.planningPlanStatus && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'municipalityRole' THEN q.municipalityRole && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'municipality' THEN q.municipalityNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'district' THEN q.districtNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'neighbourhood' THEN q.neighbourhoodNamesList && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'projectOwners' THEN q.projectOwnersInitials && _filterValues_
            WHEN _filterCondition_ = 'ANY_OF' AND _filterColumn_ = 'projectLeaders' THEN q.projectLeadersInitials && _filterValues_
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
        CASE WHEN _sortColumn_ = 'priority' AND _sortDirection_ = 'ASC' THEN q.priority COLLATE "diwi_numeric" END ASC,
        CASE WHEN _sortColumn_ = 'planningPlanStatus' AND _sortDirection_ = 'ASC' THEN q.planningPlanStatus END ASC,
        CASE WHEN _sortColumn_ = 'municipalityRole' AND _sortDirection_ = 'ASC' THEN q.municipalityRole END ASC,
        CASE WHEN _sortColumn_ = 'municipality' AND _sortDirection_ = 'ASC' THEN q.municipalityNamesList END ASC,
        CASE WHEN _sortColumn_ = 'district' AND _sortDirection_ = 'ASC' THEN q.districtNamesList END ASC,
        CASE WHEN _sortColumn_ = 'neighbourhood' AND _sortDirection_ = 'ASC' THEN q.neighbourhoodNamesList END ASC,
        CASE WHEN _sortColumn_ = 'projectOwners' AND _sortDirection_ = 'ASC' THEN q.projectOwnersInitials END ASC,
        CASE WHEN _sortColumn_ = 'projectLeaders' AND _sortDirection_ = 'ASC' THEN q.projectLeadersInitials END ASC,

        CASE WHEN _sortColumn_ = 'projectName' AND _sortDirection_ = 'DESC' THEN q.projectName END DESC,
        CASE WHEN _sortColumn_ = 'totalValue' AND _sortDirection_ = 'DESC' THEN q.totalValue END DESC,
        CASE WHEN _sortColumn_ = 'endDate' AND _sortDirection_ = 'DESC' THEN q.endDate END DESC,
        CASE WHEN _sortColumn_ = 'startDate' AND _sortDirection_ = 'DESC' THEN q.startDate END DESC,
        CASE WHEN _sortColumn_ = 'confidentialityLevel' AND _sortDirection_ = 'DESC' THEN q.confidentialityLevel END DESC,
        CASE WHEN _sortColumn_ = 'projectPhase' AND _sortDirection_ = 'DESC' THEN q.projectPhase END DESC,
        CASE WHEN _sortColumn_ = 'planType' AND _sortDirection_ = 'DESC' THEN q.planType END DESC,
        CASE WHEN _sortColumn_ = 'priority' AND _sortDirection_ = 'DESC' THEN q.priority COLLATE "diwi_numeric" END DESC,
        CASE WHEN _sortColumn_ = 'planningPlanStatus' AND _sortDirection_ = 'DESC' THEN q.planningPlanStatus END DESC,
        CASE WHEN _sortColumn_ = 'municipalityRole' AND _sortDirection_ = 'DESC' THEN q.municipalityRole END DESC,
        CASE WHEN _sortColumn_ = 'municipality' AND _sortDirection_ = 'DESC' THEN q.municipalityNamesList END DESC,
        CASE WHEN _sortColumn_ = 'district' AND _sortDirection_ = 'DESC' THEN q.districtNamesList END DESC,
        CASE WHEN _sortColumn_ = 'neighbourhood' AND _sortDirection_ = 'DESC' THEN q.neighbourhoodNamesList END DESC,
        CASE WHEN _sortColumn_ = 'projectOwners' AND _sortDirection_ = 'DESC' THEN q.projectOwnersInitials END DESC,
        CASE WHEN _sortColumn_ = 'projectLeaders' AND _sortDirection_ = 'DESC' THEN q.projectLeadersInitials END DESC

    LIMIT _limit_ OFFSET _offset_;

END;$$

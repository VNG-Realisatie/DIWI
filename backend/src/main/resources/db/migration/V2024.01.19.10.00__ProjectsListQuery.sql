CREATE OR REPLACE FUNCTION get_active_and_future_projects_list (
  _now_ date
)
	RETURNS TABLE (
        projectId UUID,
        projectStateId UUID,
        projectName TEXT,
        projectColor TEXT,
        confidentialityLevel diwi_testset.confidentiality,
        organizationName TEXT,
        startDate DATE,
        endDate DATE,
        planType TEXT[],
        priority TEXT[],
        projectPhase diwi_testset.project_phase,
        planningPlanStatus TEXT[],
        municipalityRole TEXT[]
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

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
            pptc.project_id, array_agg(pptcv.plan_type::TEXT) AS plan_types
        FROM
            diwi_testset.project_plan_type_changelog pptc
                JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pptc.start_milestone_id AND sms.change_end_date IS NULL
                JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pptc.end_milestone_id AND ems.change_end_date IS NULL
                JOIN diwi_testset.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
        WHERE
            sms.date <= _now_ AND _now_ < ems.date AND pptc.change_end_date IS NULL
        GROUP BY  pptc.project_id
    ),
    active_project_planologische_planstatus AS (
        SELECT
            pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT) AS planning_planstatus
        FROM
            diwi_testset.project_planologische_planstatus_changelog pppc
                JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pppc.start_milestone_id AND sms.change_end_date IS NULL
                JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pppc.end_milestone_id AND ems.change_end_date IS NULL
                JOIN diwi_testset.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
        WHERE
            sms.date <= _now_ AND _now_ < ems.date AND pppc.change_end_date IS NULL
        GROUP BY  pppc.project_id
    ),
    active_project_priorities AS (
        SELECT
            ppc.project_id,
            CASE
                WHEN ppc.value_type = 'single_value' THEN array_agg(vs.ordinal_level || ' ' || vs.value_label)
                WHEN ppc.value_type = 'range' THEN array_agg(vsMin.ordinal_level || ' ' || vsMin.value_label) || array_agg(vsMax.ordinal_level || ' ' || vsMax.value_label)
                END AS project_priorities
        FROM
            diwi_testset.project_priorisering_changelog ppc
                JOIN diwi_testset.milestone_state sms ON sms.milestone_id = ppc.start_milestone_id AND sms.change_end_date IS NULL
                JOIN diwi_testset.milestone_state ems ON ems.milestone_id = ppc.end_milestone_id AND ems.change_end_date IS NULL
                LEFT JOIN diwi_testset.project_priorisering_value_state vs ON ppc.project_priorisering_value_id = vs.project_priorisering_value_id AND vs.change_end_date IS NULL
                LEFT JOIN diwi_testset.project_priorisering_value_state vsMin ON ppc.project_priorisering_min_value_id = vsMin.project_priorisering_value_id AND vsMin.change_end_date IS NULL
                LEFT JOIN diwi_testset.project_priorisering_value_state vsMax ON ppc.project_priorisering_max_value_id = vsMax.project_priorisering_value_id AND vsMax.change_end_date IS NULL
        WHERE
            sms.date <= _now_ AND _now_ < ems.date AND ppc.change_end_date IS NULL
        GROUP BY ppc.project_id, ppc.value_type
    ),
    active_project_gemeenterol AS (
        SELECT
            pgc.project_id, array_agg(pgvs.value_label) AS municipality_role
        FROM
            diwi_testset.project_gemeenterol_changelog pgc
                JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pgc.start_milestone_id AND sms.change_end_date IS NULL
                JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pgc.end_milestone_id AND ems.change_end_date IS NULL
                JOIN diwi_testset.project_gemeenterol_value_state pgvs ON pgvs.project_gemeenterol_value_id = pgc.project_gemeenterol_value_id AND pgvs.change_end_date IS NULL
        WHERE
            sms.date <= _now_ AND _now_ < ems.date AND pgc.change_end_date IS NULL
        GROUP BY  pgc.project_id
    )

SELECT
    ap.id AS projectId,
    ps.id AS projectStateId,
    apn.name AS projectName,
    ps.project_colour AS projectColor,
    ps.confidentiality_level AS confidentialityLevel,
    os.naam AS organizationName,
    ap.startDate AS startDate,
    ap.endDate AS endDate,
    appt.plan_types AS planType,
    app.project_priorities AS priority,
    apf.project_fase AS projectPhase,
    appp.planning_planstatus AS planningPlanStatus,
    apg.municipality_role AS municipalityRole
FROM active_projects ap
         LEFT JOIN diwi_testset.project_state ps ON ps.project_id = ap.id AND ps.change_end_date IS NULL
         LEFT JOIN diwi_testset.organization_state os ON ps.owner_organization_id = os.organization_id AND os.change_end_date IS NULL
         LEFT JOIN active_project_names apn ON apn.project_id = ap.id
         LEFT JOIN active_project_plan_types appt ON appt.project_id = ap.id
         LEFT JOIN active_project_fases apf ON apf.project_id = ap.id
         LEFT JOIN active_project_planologische_planstatus appp ON appp.project_id = ap.id
         LEFT JOIN active_project_priorities app ON app.project_id = ap.id
         LEFT JOIN active_project_gemeenterol apg ON apg.project_id = ap.id;

END;$$

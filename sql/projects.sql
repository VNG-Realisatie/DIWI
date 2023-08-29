WITH woningblokken AS (
    SELECT
        w."ID" AS id,
        wnc.naam AS "naam",
        wmc.bruto_plancapaciteit AS bruto_plancapaciteit,
        wmc.netto_plancapaciteit AS netto_plancapaciteit,
        wmc.sloop AS sloop,
        wmc.mutatie_soort AS mutatie_soort,
        -- extra id's for debugging, might not be needed for UI
        wnc."ID" AS woningblok_naam_changelog_id,
        wmc."ID" AS woningblok_mutatie_changelog_id,
        ws."ID" AS woningblok_state_id,
        ws."project_ID" AS project_id
    FROM
        diwi_testset_simplified.woningblok w
        LEFT JOIN diwi_testset_simplified.woningblok_state ws ON w."ID" = ws."woningblok_ID"
            AND ws.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.woningblok_mutatie_changelog wmc ON wmc."woningblok_ID" = w."ID"
            AND wmc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.woningblok_naam_changelog wnc ON wnc."woningblok_ID" = w."ID"
            AND wnc.change_end_date IS NULL
        ORDER BY
            w."ID" ASC
),
actor_role AS (
    SELECT
        parc."project_ID",
        actor_state."name" AS name,
        parvs.value_label AS rol,
        parc.change_end_date
    FROM
        diwi_testset_simplified.project_actor_rol_changelog parc
    LEFT JOIN diwi_testset_simplified.project_actor_rol_value_state parvs ON parc."project_actor_rol_ID" = parvs."ID"
        LEFT JOIN diwi_testset_simplified.actor_state actor_state ON actor_state."actor_ID" = parc."actor_ID"
),
current_milestone AS (
    SELECT
        pfc.*
    FROM
        diwi_testset_simplified.project_fase_changelog AS pfc
        LEFT JOIN diwi_testset_simplified.milestone sm ON pfc."start_milestone_ID" = sm."ID"
        LEFT JOIN diwi_testset_simplified.milestone_state sms ON sms."milestone_ID" = sm."ID"
        LEFT JOIN diwi_testset_simplified.milestone em ON pfc."end_milestone_iD" = em."ID"
        LEFT JOIN diwi_testset_simplified.milestone_state ems ON ems."milestone_ID" = em."ID"
    WHERE
        sms.status = 'gerealiseerd'
        AND ems.status != 'gerealiseerd'
),
current_programmering AS (
    SELECT
        ppc."ID",
        ppc.programmering,
        ppc."project_ID",
        ppc.change_end_date
    FROM
        diwi_testset_simplified.project_programmering_changelog ppc
        LEFT JOIN diwi_testset_simplified.milestone_state sms ON sms."milestone_ID" = ppc."start_milestone_ID"
        LEFT JOIN diwi_testset_simplified.milestone_state ems ON ems."milestone_ID" = ppc."end_milestone_ID"
    WHERE
        sms.status = 'gerealiseerd'
        AND ems.status != 'gerealiseerd'
),
current_planstatus AS (
    SELECT
        pppc."ID" AS "ID",
        pppc."project_ID" AS "project_ID",
        pppc.planologische_planstatus AS planologische_planstatus,
        pppc.change_end_date
    FROM
        diwi_testset_simplified.project_planologische_planstatus_changelog pppc
        LEFT JOIN diwi_testset_simplified.milestone_state sms ON sms."milestone_ID" = pppc."start_milestone_ID"
        LEFT JOIN diwi_testset_simplified.milestone_state ems ON ems."milestone_ID" = pppc."end_milestone_iD"
    WHERE
        sms.status = 'gerealiseerd'
        AND ems.status != 'gerealiseerd'
),
projecten AS (
    SELECT
        p."ID" AS id,
        pnc."name" AS "name",
        os.naam AS "eigenaar",
        ps.confidentiality_level AS "vertrouwlijkheidsniveau",
        ppvs.value_label AS "priorisering",
        project_plan_type_changelog.plan_type AS "plan type",
        milestone_start_state."date" AS "start datum",
        milestone_end_state."date" AS "eind datum",
        pgvs.value_label AS "rol gemeente",
        cm.project_fase AS "project fase",
        actor_role."name" AS "project leider",
        current_planstatus.planologische_planstatus AS "planologische plan status",
        current_programmering.programmering AS "programmering",
        -- extra id's for debugging, might not be needed for UI
        ps."ID" AS project_state_id,
        cm."ID" AS project_fase_changelog_id,
        pnc."ID" AS project_name_changelog_id,
        o."ID" AS organization_id,
        os."ID" AS organization_state_id,
        pgc."ID" AS project_gemeenterol_changelog_id,
        pgv."ID" AS project_gemeenterol_value_id,
        pgvs."ID" AS project_gemeenterol_value_state_id,
        ppc."ID" AS project_priorisering_changelog_id,
        ppv."ID" AS project_priorisering_value_id,
        ppvs."ID" AS project_priorisering_value_state_id
    FROM
        diwi_testset_simplified.project AS p
        LEFT JOIN diwi_testset_simplified.project_name_changelog AS pnc ON pnc."project_ID" = p."ID"
            AND pnc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.project_state AS ps ON ps."project_ID" = p."ID"
            AND ps.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.organization AS o ON o."ID" = ps."owner_organization_ID"
        LEFT JOIN diwi_testset_simplified.organization_state AS os ON os."organization_ID" = o."ID"
            AND os.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.project_gemeenterol_changelog AS pgc ON pgc."project_ID" = p."ID"
            AND pgc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.project_gemeenterol_value AS pgv ON pgv."ID" = pgc."project_gemeenterol_value_ID"
        LEFT JOIN diwi_testset_simplified.project_gemeenterol_value_state AS pgvs ON pgvs."project_gemeenterol_value_ID" = pgv."ID"
            AND pgvs.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.project_priorisering_changelog ppc ON ppc."project_ID" = p."ID"
            AND ppc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.project_priorisering_value ppv ON ppv."ID" = ppc."project_priorisering_value_ID"
        LEFT JOIN diwi_testset_simplified.project_priorisering_value_state ppvs ON ppvs."project_priorisering_value_ID" = ppv."ID"
            AND ppvs.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.project_duration_changelog pdc ON pdc."project_ID" = p."ID"
            AND pdc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.project_plan_type_changelog project_plan_type_changelog ON project_plan_type_changelog."project_ID" = p."ID"
            AND project_plan_type_changelog.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.milestone milestone_start ON milestone_start."ID" = project_plan_type_changelog."start_milestone_ID"
        LEFT JOIN diwi_testset_simplified.milestone_state milestone_start_state ON milestone_start_state."milestone_ID" = milestone_start."ID"
            AND milestone_start_state.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.milestone milestone_end ON milestone_start."ID" = project_plan_type_changelog."end_milestone_iD"
        LEFT JOIN diwi_testset_simplified.milestone_state milestone_end_state ON milestone_end_state."milestone_ID" = milestone_start."ID"
            AND milestone_end_state.change_end_date IS NULL
        LEFT JOIN current_milestone cm ON cm."project_ID" = p."ID"
            AND cm.change_end_date IS NULL
        LEFT JOIN actor_role ON actor_role."project_ID" = p."ID"
            AND actor_role.change_end_date IS NULL
            AND actor_role.rol = 'projectleider'
        LEFT JOIN current_planstatus ON current_planstatus."project_ID" = p."ID"
            AND current_planstatus.change_end_date IS NULL
        LEFT JOIN current_programmering ON current_programmering."project_ID" = p."ID"
            AND current_programmering.change_end_date IS NULL
        ORDER BY
            p."ID" ASC
),
plannen AS (
    SELECT
        plan."ID" AS plan_id,
        plan_state.doel_soort AS doel_soort,
        plan_state.doel_richting AS doel_richting,
        plan_state.doel_waarde AS doel_waarde
    FROM
        diwi_testset_simplified.plan AS plan
    INNER JOIN diwi_testset_simplified.plan_state AS plan_state ON plan_state."plan_ID" = plan."ID"
),
projecten_with_woningblokken AS (
    SELECT
        to_jsonb (p) AS project,
    COALESCE(json_agg(w.*) FILTER (WHERE w.id IS NOT NULL), '[]') AS woningblokken
FROM
    projecten AS p
        LEFT JOIN woningblokken AS w ON w.project_id = p.id
    GROUP BY
        project
)
SELECT
    json_agg(p)
FROM
    projecten_with_woningblokken AS p

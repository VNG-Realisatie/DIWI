WITH
current_woningblok_eigendom_en_waarde AS (
    SELECT
        wewc.*,
        sms.date AS "milestone_start_date",
        ems.date AS "milestone_end_date"
    FROM
        diwi_testset_simplified.woningblok_eigendom_en_waarde_changelog wewc
        LEFT JOIN diwi_testset_simplified.milestone_state sms ON sms."milestone_ID" = wewc."start_milestone_ID"
        LEFT JOIN diwi_testset_simplified.milestone_state ems ON ems."milestone_ID" = wewc."end_milestone_ID"
    WHERE
        sms.status = 'gerealiseerd'
        AND ems.status != 'gerealiseerd'
),
woningblokken AS (
    SELECT
        w."ID" AS id,
        wnc.naam AS "naam",
        wmc.bruto_plancapaciteit AS bruto_plancapaciteit,
        wmc.netto_plancapaciteit AS netto_plancapaciteit,
        wmc.sloop AS sloop,
        wmc.mutatie_soort AS mutatie_soort,
        bs.waarde_label AS buurt,
        wijk_state.waarde_label AS wijk,
        CASE WHEN wtfvc.fysiek_voorkomen IS NULL THEN NULL ELSE json_build_object(wtfvc.fysiek_voorkomen, wmc.netto_plancapaciteit) END AS fysiek_voorkomen,
        CASE WHEN wtfvc.woning_type IS NULL THEN NULL ELSE json_build_object(wtfvc.woning_type, wmc.netto_plancapaciteit) END AS woning_type,
        CASE WHEN wewc.eigendom_soort IS NULL THEN NULL ELSE json_build_object(wewc.eigendom_soort, wmc.netto_plancapaciteit) END AS eigendom_soort,
        wewc.waarde AS waarde,
        wewc.huurbedrag AS huurbedrag,
        wgc.grootte AS grootte,
        CASE WHEN wdc.doelgroep IS NULL THEN NULL ELSE json_build_object(wdc.doelgroep, wmc.netto_plancapaciteit) END AS doelgroep,
        CASE WHEN wgpc.grondpositie IS NULL THEN NULL ELSE json_build_object(wgpc.grondpositie, wmc.netto_plancapaciteit) END AS grondpositie,
        woningblok_milestone_start_state."date" AS "start datum",
        woningblok_milestone_end_state."date" AS "eind datum",
        -- extra id's for debugging, might not be needed for UI
        wnc."ID" AS woningblok_naam_changelog_id,
        wmc."ID" AS woningblok_mutatie_changelog_id,
        ws."ID" AS woningblok_state_id,
        wtfvc."ID" AS woningblok_type_en_fysiek_voorkomen_changelog_id,
        wewc."ID" AS woningblok_eigendom_en_waarde_changelog_id,
        wgc."ID" AS woningblok_grootte_changelog_id,
        wdc."ID" AS woningblok_doelgroep_changelog_id,
        wgpc."ID" AS woningblok_grondpositie_changelog_id,
        ws."project_ID" AS project_id
    FROM
        diwi_testset_simplified.woningblok w
        LEFT JOIN diwi_testset_simplified.woningblok_state ws ON w."ID" = ws."woningblok_ID"
            AND ws.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.woningblok_mutatie_changelog wmc ON wmc."woningblok_ID" = w."ID"
            AND wmc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.woningblok_naam_changelog wnc ON wnc."woningblok_ID" = w."ID"
            AND wnc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.woningblok_buurt_changelog wbc ON wbc."woningblok_ID" = w."ID"
            AND wbc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.buurt_state bs ON bs."buurt_ID" = wbc."buurt_ID"
            AND bs.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.woningblok_wijk_changelog wwc ON wwc."woningblok_ID" = w."ID"
            AND wwc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.wijk_state wijk_state ON wijk_state."ID" = wwc."wijk_ID"
            AND wijk_state.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.woningblok_type_en_fysiek_voorkomen_changelog wtfvc ON wtfvc."woningblok_ID" = w."ID"
            AND wtfvc.change_end_date IS NULL
        LEFT JOIN current_woningblok_eigendom_en_waarde AS wewc ON wewc."woningblok_ID" = w."ID"
            AND wewc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.woningblok_grootte_changelog wgc ON wgc."woningblok_ID" = w."ID"
            AND wgc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.woningblok_doelgroep_changelog wdc ON wdc."woningblok_ID" = w."ID"
            AND wdc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.woningblok_grondpositie_changelog wgpc ON wgpc."woningblok_ID" = w."ID"
            AND wgpc.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.woningblok_duration_changelog woningblok_duration_changelog ON woningblok_duration_changelog."woningblok_ID" = w."ID"
            AND woningblok_duration_changelog.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.milestone woningblok_milestone_start ON woningblok_milestone_start."ID" = woningblok_duration_changelog."start_milestone_ID"
        LEFT JOIN diwi_testset_simplified.milestone_state woningblok_milestone_start_state ON woningblok_milestone_start_state."milestone_ID" = woningblok_milestone_start."ID"
            AND woningblok_milestone_start_state.change_end_date IS NULL
        LEFT JOIN diwi_testset_simplified.milestone woningblok_milestone_end ON woningblok_milestone_end."ID" = woningblok_duration_changelog."end_milestone_ID"
        LEFT JOIN diwi_testset_simplified.milestone_state woningblok_milestone_end_state ON woningblok_milestone_end_state."milestone_ID" = woningblok_milestone_end."ID"
            AND woningblok_milestone_end_state.change_end_date IS NULL
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
current_project_fase AS (
    SELECT
        pfc.*,
        sms.date AS "milestone_start_date",
        ems.date AS "milestone_end_date"
    FROM
        diwi_testset_simplified.project_fase_changelog AS pfc
        LEFT JOIN diwi_testset_simplified.milestone_state sms ON sms."milestone_ID" = pfc."start_milestone_ID"
        LEFT JOIN diwi_testset_simplified.milestone_state ems ON ems."milestone_ID" = pfc."end_milestone_ID"
    WHERE
        sms.status = 'gerealiseerd'
        AND ems.status != 'gerealiseerd'
),
current_programmering AS (
    SELECT
        ppc.*,
        sms.date AS "milestone_start_date",
        ems.date AS "milestone_end_date"
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
        pppc.*,
        sms.date AS "milestone_start_date",
        ems.date AS "milestone_end_date"
    FROM
        diwi_testset_simplified.project_planologische_planstatus_changelog pppc
        LEFT JOIN diwi_testset_simplified.milestone_state sms ON sms."milestone_ID" = pppc."start_milestone_ID"
        LEFT JOIN diwi_testset_simplified.milestone_state ems ON ems."milestone_ID" = pppc."end_milestone_ID"
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
        LEFT JOIN diwi_testset_simplified.milestone milestone_end ON milestone_end."ID" = project_plan_type_changelog."end_milestone_ID"
        LEFT JOIN diwi_testset_simplified.milestone_state milestone_end_state ON milestone_end_state."milestone_ID" = milestone_end."ID"
            AND milestone_end_state.change_end_date IS NULL
        LEFT JOIN current_project_fase cm ON cm."project_ID" = p."ID"
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

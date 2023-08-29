WITH woningblokken AS (
    SELECT
        w."ID" AS id,
        wnc."ID" AS woningblok_naam_changelog_id,
        wnc.naam AS "naam",
        wmc."ID" AS woningblok_mutatie_changelog_id,
        wmc.bruto_plancapaciteit AS bruto_plancapaciteit,
        wmc.netto_plancapaciteit AS netto_plancapaciteit,
        wmc.sloop AS sloop,
        wmc.mutatie_soort AS mutatie_soort,
        ws."ID" AS woningblok_state_id,
        ws."project_ID" AS project_id
    FROM
        diwi_testset_simplified.woningblok w
        LEFT JOIN diwi_testset_simplified.woningblok_state ws ON w."ID" = ws."woningblok_ID"
        LEFT JOIN diwi_testset_simplified.woningblok_mutatie_changelog wmc ON wmc."woningblok_ID" = w."ID"
        LEFT JOIN diwi_testset_simplified.woningblok_naam_changelog wnc ON wnc."woningblok_ID" = w."ID"
    WHERE
        wnc.change_end_date IS NULL
        AND ws.change_end_date IS NULL
        AND wmc.change_end_date IS NULL
        AND wnc.change_end_date IS NULL
),
projecten AS (
    SELECT
        p."ID" AS id,
        pnc."name" AS "name",
        os.naam AS "eigenaar",
        pgvs.value_label AS "rol gemeente",

        ps."ID" AS project_state_id,
        pnc."ID" AS project_name_changelog_id,
        o."ID" AS organization_id,
        os."ID" AS organization_state_id,
        pgc."ID" AS project_gemeenterol_changelog_id,
        pgv."ID" AS project_gemeenterol_value_id,
        pgvs."ID" AS project_gemeenterol_value_state_id
    FROM
        diwi_testset_simplified.project AS p
        LEFT JOIN diwi_testset_simplified.project_name_changelog AS pnc ON pnc."project_ID" = p."ID"
        LEFT JOIN diwi_testset_simplified.project_state AS ps ON ps."project_ID" = p."ID"
        LEFT JOIN diwi_testset_simplified.organization AS o ON o."ID" = ps."owner_organization_ID"
        LEFT JOIN diwi_testset_simplified.organization_state AS os ON os."organization_ID" = o."ID"
        LEFT JOIN diwi_testset_simplified.project_gemeenterol_changelog AS pgc ON pgc."project_ID" = p."ID"
        LEFT JOIN diwi_testset_simplified.project_gemeenterol_value AS pgv ON pgv."ID" = pgc."project_gemeenterol_value_ID"
        LEFT JOIN diwi_testset_simplified.project_gemeenterol_value_state AS pgvs ON pgvs."project_gemeenterol_value_ID" = pgv."ID"
    WHERE
        pnc.change_end_date IS NULL
        AND ps.change_end_date IS NULL
        AND os.change_end_date IS NULL
        AND pgc.change_end_date IS NULL
        AND pgvs.change_end_date IS NULL
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

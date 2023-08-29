WITH woningblokken AS (
    SELECT
        w."ID" AS id,
        wnc.naam AS "naam",
        wmc.bruto_plancapaciteit AS bruto_plancapaciteit,
        wmc.netto_plancapaciteit AS netto_plancapaciteit,
        wmc.sloop AS sloop,
        wmc.mutatie_soort AS mutatie_soort,
        ws."project_ID" AS project_id
    FROM
        diwi_testset_simplified.woningblok w
        LEFT JOIN diwi_testset_simplified.woningblok_state ws ON w."ID" = ws."project_ID"
        LEFT JOIN diwi_testset_simplified.woningblok_mutatie_changelog wmc ON wmc."woningblok_ID" = w."ID"
        LEFT JOIN diwi_testset_simplified.woningblok_naam_changelog wnc ON wnc."woningblok_ID" = w."ID"
),
projecten AS (
    SELECT
        p."ID" AS id,
        pnc."name" AS "name",
        os.naam AS "organization"
    FROM
        diwi_testset_simplified.project AS p
        LEFT JOIN diwi_testset_simplified.project_name_changelog pnc ON pnc."project_ID" = p."ID"
        LEFT JOIN diwi_testset_simplified.project_state ps ON ps."project_ID" = p."ID"
        LEFT JOIN diwi_testset_simplified.organization o ON o."ID" = ps."owner_organization_ID"
        LEFT JOIN diwi_testset_simplified.organization_state os ON os."organization_ID" = o."ID"
    WHERE
        pnc.change_end_date IS NULL
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
        p.id AS id,
        p."name" AS "name",
        coalesce(json_agg(w.*) FILTER (WHERE w.id IS NOT NULL), '[]') AS woningblokken
FROM
    projecten AS p
        LEFT JOIN woningblokken AS w ON w.project_id = p.id
    GROUP BY
        p.id,
        p."name"
)
SELECT
    json_agg(p)
FROM
    projecten_with_woningblokken AS p

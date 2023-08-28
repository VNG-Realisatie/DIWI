WITH woningblokken AS (
    SELECT
        w."ID" AS id,
        wnc.naam AS "naam",
        ws."project_ID" AS project_id
    FROM
        diwi_testset_simplified.woningblok w
        INNER JOIN diwi_testset_simplified.woningblok_state ws ON w."ID" = ws."project_ID"
        INNER JOIN diwi_testset_simplified.woningblok_naam_changelog wnc ON wnc."woningblok_ID" = w."ID"
),
projecten AS (
    SELECT
        p."ID" AS id,
        pnc."name" AS "name",
        os.naam AS "organization"
    FROM
        diwi_testset_simplified.project AS p
        INNER JOIN diwi_testset_simplified.project_name_changelog pnc ON pnc."project_ID" = p."ID"
        INNER JOIN diwi_testset_simplified.project_state ps ON ps."project_ID" = p."ID"
        INNER JOIN diwi_testset_simplified.organization o ON o."ID" = ps."owner_organization_ID"
        INNER JOIN diwi_testset_simplified.organization_state os ON os."organization_ID" = o."ID"
    WHERE
        pnc.change_end_date IS NULL
),
projects_with_woningblokken AS (
    SELECT
        p.id AS id,
        p."name" AS "name",
        coalesce(json_agg(w.*) FILTER (where w.id is not NULL), '[]') AS woningblokken
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
    projects_with_woningblokken AS p

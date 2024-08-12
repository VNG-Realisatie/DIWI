DROP FUNCTION IF EXISTS diwi.get_project_dashboard_snapshot;

CREATE OR REPLACE FUNCTION diwi.get_project_dashboard_snapshot (
  _project_uuid_ uuid,
  _snapshot_date_ date,
  _user_role_ text,
  _user_uuid_ uuid
)
	RETURNS TABLE (
        projectId          UUID,
        physicalAppearance JSONB,
        priceCategoryOwn   JSONB,
        priceCategoryRent  JSONB,
        planning           JSONB
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

SELECT
    q.projectId          AS projectId,
    q.physicalAppearance AS physicalAppearance,
    q.priceCategoryOwn   AS priceCategoryOwn,
    q.priceCategoryRent  AS priceCategoryRent,
    q.planning           AS planning
FROM (

        WITH
            current_project AS (
                SELECT
                    p.id, sms.date AS startDate, ems.date AS endDate
                FROM
                    diwi.project p
                        JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                WHERE
                    sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date AND p.id = _project_uuid_
            ),
            woningbloks AS (
                SELECT
                    w.id, w.project_id, sms.date AS startDate, ems.date AS endDate
                FROM
                    diwi.woningblok w
                        JOIN diwi.woningblok_state ws ON ws.woningblok_id = w.id AND ws.change_end_date IS NULL
                        JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                WHERE  sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date AND w.project_id = _project_uuid_
            ),
            woningbloks_physical_appearance AS (
                SELECT
                    pcvs.value_label AS label, SUM(wcfv.amount) AS amount
                FROM
                    woningbloks w
                        JOIN diwi.woningblok_type_en_fysiek_changelog wtfc ON w.id = wtfc.woningblok_id AND wtfc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = wtfc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = wtfc.end_milestone_id AND ems.change_end_date IS NULL
                        JOIN diwi.woningblok_type_en_fysiek_changelog_fysiek_value wcfv ON wcfv.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                        JOIN diwi.property_category_value_state pcvs ON pcvs.category_value_id = wcfv.property_value_id AND pcvs.change_end_date IS NULL
                WHERE
                    sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date
                GROUP BY pcvs.value_label
            ),
            woningbloks_pricecategory_own AS (
                SELECT
                    prcvs.id AS id, prcvs.name AS label, prcvs.min AS min, prcvs.max AS max, SUM(wewc.amount) AS amount
                FROM
                    woningbloks w
                        JOIN diwi.woningblok_eigendom_en_waarde_changelog wewc ON w.id = wewc.woningblok_id AND wewc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = wewc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = wewc.end_milestone_id AND ems.change_end_date IS NULL
                        JOIN diwi.property_range_category_value_state prcvs ON prcvs.range_category_value_id = wewc.ownership_property_value_id AND prcvs.change_end_date IS NULL
                WHERE
                    sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date AND wewc.eigendom_soort = 'KOOPWONING'
                GROUP BY prcvs.id, prcvs.name
            ),
            woningbloks_pricecategory_rent AS (
                SELECT
                    prcvs.id AS id, prcvs.name AS label,  prcvs.min AS min, prcvs.max AS max, SUM(wewc.amount) AS amount
                FROM
                    woningbloks w
                        JOIN diwi.woningblok_eigendom_en_waarde_changelog wewc ON w.id = wewc.woningblok_id AND wewc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = wewc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = wewc.end_milestone_id AND ems.change_end_date IS NULL
                        JOIN diwi.property_range_category_value_state prcvs ON prcvs.range_category_value_id = wewc.rental_property_value_id AND prcvs.change_end_date IS NULL
                WHERE
                    sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date AND wewc.eigendom_soort != 'KOOPWONING'
                GROUP BY prcvs.id, prcvs.name
            ),
            current_project_users AS (
                SELECT
                    q.project_id    AS project_id,
                    array_agg(array[q.usergroup_id::TEXT, q.usergroup_name, q.user_id::TEXT, q.user_initials, q.user_last_name, q.user_first_name]) AS users
                FROM (
                    SELECT DISTINCT
                        ps.project_id as project_id,
                        us.user_id AS user_id,
                        LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS user_initials,
                        us.last_name AS user_last_name,
                        us.first_name AS user_first_name,
                        ugs.usergroup_id AS usergroup_id,
                        ugs.naam AS usergroup_name
                    FROM
                        diwi.project_state ps
                            JOIN diwi.usergroup_to_project ugtp ON ps.project_id = ugtp.project_id AND ugtp.change_end_date IS NULL
                            JOIN diwi.usergroup_state ugs ON ugtp.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                            LEFT JOIN diwi.user_to_usergroup utug ON ugtp.usergroup_id = utug.usergroup_id
                            LEFT JOIN diwi.user_state us ON utug.user_id = us.user_id AND us.change_end_date IS NULL
                    WHERE
                        ps.change_end_date IS NULL AND ps.project_id = _project_uuid_
                    ) AS q
                GROUP BY q.project_id
            ),
            planning AS (

                SELECT  planningQuery.projectId             AS projectId,
                    planningQuery.projectName           AS projectName,
                    planningQuery.deliveryYear          AS deliveryYear,
                    COALESCE(planningQuery.totalValue, 0) AS amount
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
                                sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date
                        ),
                    active_project_names AS (
                        SELECT
                            pnc.project_id, pnc.name
                        FROM
                            diwi.project_name_changelog pnc
                                JOIN diwi.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                                JOIN diwi.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
                        WHERE
                            sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date AND pnc.change_end_date IS NULL
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
                                JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
                            WHERE
                                sms.date <= _snapshot_date_ AND _snapshot_date_ < ems.date AND wmc.change_end_date IS NULL
                            GROUP BY w.project_id
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
                                sms.date > _snapshot_date_
                        ),
                        future_project_names AS (
                            SELECT
                                pnc.project_id, pnc.name
                            FROM
                                future_projects fp
                                    JOIN diwi.project_name_changelog pnc ON fp.id = pnc.project_id
                                        AND pnc.start_milestone_id = fp.start_milestone_id AND pnc.change_end_date IS NULL
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
                                    JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
                                    JOIN diwi.woningblok_mutatie_changelog wmc ON w.id = wmc.woningblok_id
                                        AND wmc.start_milestone_id = fp.start_milestone_id AND wmc.change_end_date IS NULL
                            GROUP BY w.project_id
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
                                ems.date <= _snapshot_date_
                        ),
                        past_project_names AS (
                            SELECT
                                pnc.project_id, pnc.name
                            FROM
                                past_projects pp
                                    JOIN diwi.project_name_changelog pnc ON pp.id = pnc.project_id
                                        AND pnc.end_milestone_id = pp.end_milestone_id AND pnc.change_end_date IS NULL
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
                                    JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
                                    JOIN diwi.woningblok_mutatie_changelog wmc ON w.id = wmc.woningblok_id
                                        AND wmc.end_milestone_id = pp.end_milestone_id AND wmc.change_end_date IS NULL
                            GROUP BY w.project_id
                        ),
                        project_users AS (
                            SELECT
                                ps.project_id as project_id,
                                array_agg(us.user_id) AS userIds
                                    FROM diwi.project_state ps
                                        JOIN diwi.usergroup_to_project ugtp ON ps.project_id = ugtp.project_id AND ugtp.change_end_date IS NULL
                                        JOIN diwi.usergroup_state ugs ON ugtp.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                                        LEFT JOIN diwi.user_to_usergroup utug ON ugtp.usergroup_id = utug.usergroup_id
                                        LEFT JOIN diwi.user_state us ON utug.user_id = us.user_id AND us.change_end_date IS NULL
                                    WHERE
                                        ps.change_end_date IS NULL
                            GROUP BY ps.project_id
                        )

                    SELECT
                        ap.id                    AS projectId,
                        apn.name                 AS projectName,
                        ps.confidentiality_level AS confidentialityLevel,
                        owners.userIds                  AS projectOwners,
                        to_char( ap.endDate, 'YYYY') AS deliveryYear,
                        apwv.total_value         AS totalValue
                    FROM
                        active_projects ap
                            LEFT JOIN diwi.project_state ps ON ps.project_id = ap.id AND ps.change_end_date IS NULL
                            LEFT JOIN active_project_names apn ON apn.project_id = ap.id
                            LEFT JOIN active_project_woningblok_totalvalue apwv ON apwv.project_id = ap.id
                            LEFT JOIN project_users owners ON ps.project_id = owners.project_id

                    UNION

                    SELECT
                        fp.id                    AS projectId,
                        fpn.name                 AS projectName,
                        ps.confidentiality_level AS confidentialityLevel,
                        owners.userIds                  AS projectOwners,
                        to_char( fp.endDate, 'YYYY') AS deliveryYear,
                        fpwv.total_value         AS totalValue
                    FROM
                        future_projects fp
                            LEFT JOIN diwi.project_state ps ON ps.project_id = fp.id AND ps.change_end_date IS NULL
                            LEFT JOIN future_project_names fpn ON fpn.project_id = fp.id
                            LEFT JOIN future_project_woningblok_totalvalue fpwv ON fpwv.project_id = fp.id
                            LEFT JOIN project_users owners ON ps.project_id = owners.project_id

                    UNION

                    SELECT
                        pp.id                    AS projectId,
                        ppn.name                 AS projectName,
                        ps.confidentiality_level AS confidentialityLevel,
                        owners.userIds                  AS projectOwners,
                        to_char( pp.endDate, 'YYYY') AS deliveryYear,
                        ppwv.total_value         AS totalValue
                    FROM
                        past_projects pp
                            LEFT JOIN diwi.project_state ps ON ps.project_id = pp.id AND ps.change_end_date IS NULL
                            LEFT JOIN past_project_names ppn ON ppn.project_id = pp.id
                            LEFT JOIN past_project_woningblok_totalvalue ppwv ON ppwv.project_id = pp.id
                            LEFT JOIN project_users owners ON ps.project_id = owners.project_id

                ) AS planningQuery

                WHERE
                (
                    ( _user_uuid_ = ANY(planningQuery.projectOwners)) OR
                    ( _user_role_ IN ('User', 'UserPlus') AND planningQuery.confidentialityLevel != 'PRIVATE') OR
                    ( _user_role_ = 'Management' AND planningQuery.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL') ) OR
                    ( _user_role_ = 'Council' AND planningQuery.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT') )
                )

            )

        SELECT
            p.id                               AS projectId,
            ps.confidentiality_level           AS confidentialityLevel,
            owners.users                       AS projectOwners,
            wpa.physicalAppearance             AS physicalAppearance,
            wpco.priceCategoryOwn              AS priceCategoryOwn,
            wpcr.priceCategoryRent             AS priceCategoryRent,
            pl.planning                        AS planning
        FROM
            current_project p
                LEFT JOIN diwi.project_state ps ON ps.project_id = p.id AND ps.change_end_date IS NULL
                LEFT JOIN LATERAL (SELECT to_jsonb(array_agg(jsonb_build_object('name', label, 'amount', amount))) AS physicalAppearance
                                FROM woningbloks_physical_appearance) AS wpa ON true
                LEFT JOIN LATERAL (SELECT to_jsonb(array_agg(jsonb_build_object('id', id, 'name', label, 'min', min, 'max', max, 'amount', amount))) AS priceCategoryOwn
                                FROM woningbloks_pricecategory_own) AS wpco ON true
                LEFT JOIN LATERAL (SELECT to_jsonb(array_agg(jsonb_build_object('id', id, 'name', label, 'min', min, 'max', max, 'amount', amount))) AS priceCategoryRent
                                FROM woningbloks_pricecategory_rent) AS wpcr ON true
                LEFT JOIN LATERAL (SELECT to_jsonb(array_agg(jsonb_build_object('projectId', planning.projectId, 'name', planning.projectName, 'year', deliveryYear, 'amount', amount))) AS planning
                                FROM planning) AS pl ON true
                LEFT JOIN current_project_users owners ON ps.project_id = owners.project_id

        ) AS q

WHERE q.projectId = _project_uuid_ AND
    (
      ( _user_uuid_::TEXT IN (select owners.id from unnest(q.projectOwners) with ordinality owners(id,n) where owners.n % 6 = 3)) OR
      ( _user_role_ IN ('User', 'UserPlus') AND q.confidentialityLevel != 'PRIVATE') OR
      ( _user_role_ = 'Management' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL') ) OR
      ( _user_role_ = 'Council' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT') )
    )
LIMIT 1;

END;$$

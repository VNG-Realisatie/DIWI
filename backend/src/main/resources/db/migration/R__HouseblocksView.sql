DROP FUNCTION IF EXISTS diwi.get_houseblocks_view;

CREATE OR REPLACE FUNCTION diwi.get_houseblocks_view (
    _snapshot_date_ date,
    _user_role_ text,
    _user_uuid_ uuid
)

	RETURNS TABLE (
        project_id                  UUID,
        houseblock_id               UUID,
        no_of_houses                INTEGER,
        delivery_date               DATE,
        property_category_options   UUID[],
        property_boolean_options    JSONB[],
        house_type                  JSONB[],
        physical_appearance         JSONB[],
        target_group                JSONB[],
        ground_position             JSONB[],
        programming                 BOOL,
        ownership_value_options     JSONB[]
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

    WITH
        visible_projects AS (

            SELECT * FROM (

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
                            sms.date <= NOW() AND NOW() < ems.date
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
                            ems.date <= NOW()
                    ),
                    project_users AS (
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
                    )

                    SELECT
                        ap.id                    AS project_id,
                        ps.confidentiality_level AS confidentialityLevel,
                        owners.users             AS projectOwners,
                        ap.startDate             AS startDate,
                        ap.endDate               AS endDate
                    FROM
                        active_projects ap
                            LEFT JOIN diwi.project_state ps ON ps.project_id = ap.id AND ps.change_end_date IS NULL
                            LEFT JOIN project_users owners ON ps.project_id = owners.project_id

                    UNION

                    SELECT
                        pp.id                    AS project_id,
                        ps.confidentiality_level AS confidentialityLevel,
                        owners.users             AS projectOwners,
                        pp.startDate             AS startDate,
                        pp.endDate               AS endDate
                    FROM
                        past_projects pp
                            LEFT JOIN diwi.project_state ps ON ps.project_id = pp.id AND ps.change_end_date IS NULL
                            LEFT JOIN project_users owners ON ps.project_id = owners.project_id

            ) AS q

            WHERE
                (
                  ( _user_uuid_ = ANY (q.projectOwners)) OR
                  ( _user_role_ IN ('User', 'UserPlus') AND q.confidentialityLevel != 'PRIVATE') OR
                  ( _user_role_ = 'Management' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL') ) OR
                  ( _user_role_ = 'Council' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT') )
                )
        ),
        active_woningbloks AS (
            SELECT
                w.project_id AS project_id, w.id as woningblok_id
            FROM
                diwi.woningblok w
                    JOIN visible_projects vp ON w.project_id = vp.project_id
                    JOIN diwi.woningblok_state ws ON w.id = ws.woningblok_id AND ws.change_end_date IS NULL
                    JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= NOW() AND NOW() < ems.date
        ),
        active_woningbloks_totalvalue AS (
            SELECT
                aw.woningblok_id,
                wmc.amount *
                CASE wmc.mutation_kind
                    WHEN 'CONSTRUCTION' THEN 1
                    WHEN 'DEMOLITION' THEN -1
                END AS total_value
            FROM
                active_woningbloks aw
                    JOIN diwi.woningblok_mutatie_changelog wmc ON aw.woningblok_id = wmc.woningblok_id AND wmc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = wmc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = wmc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= NOW() AND NOW() < ems.date
        ),
        active_woningbloks_deliverydate AS (
            SELECT
                aw.woningblok_id,
                wdc.latest_deliverydate AS delivery_date
            FROM
                active_woningbloks aw
                    JOIN diwi.woningblok_deliverydate_changelog wdc ON aw.woningblok_id = wdc.woningblok_id AND wdc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= NOW() AND NOW() < ems.date
        ),
        active_woningbloks_project_property_categories AS (
            SELECT
                aw.project_id, array_agg(pccv.property_value_id) AS category_values
            FROM
                active_woningbloks aw
                    JOIN diwi.project_category_changelog pcc ON aw.project_id = pcc.project_id AND pcc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pcc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pcc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_category_changelog_value pccv ON pcc.id = pccv.project_category_changelog_id
            WHERE
                sms.date <= NOW() AND NOW() < ems.date
            GROUP BY aw.project_id
        ),
        active_woningbloks_property_categories AS (
            SELECT
                aw.woningblok_id, array_agg(wmccv.eigenschap_waarde_id) AS category_values
            FROM
                active_woningbloks aw
                    JOIN diwi.woningblok_maatwerk_categorie_changelog wmcc ON aw.woningblok_id = wmcc.woningblok_id AND wmcc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = wmcc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = wmcc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.woningblok_maatwerk_categorie_changelog_value wmccv ON wmcc.id = wmccv.woningblok_maatwerk_categorie_changelog_id
            WHERE
                sms.date <= NOW() AND NOW() < ems.date
            GROUP BY aw.woningblok_id
        ),
        active_woningbloks_project_boolean_properties AS (
            SELECT
                aw.project_id, array_agg(jsonb_build_object('property_id', pbc.eigenschap_id, 'boolean_value', pbc.value)) AS boolean_values
            FROM
                active_woningbloks aw
                    JOIN diwi.project_maatwerk_boolean_changelog pbc ON aw.project_id = pbc.project_id AND pbc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pbc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pbc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= NOW() AND NOW() < ems.date
            GROUP BY aw.project_id
        ),
        active_woningbloks_boolean_properties AS (
            SELECT
                aw.woningblok_id, array_agg(jsonb_build_object('property_id', wbc.eigenschap_id, 'boolean_value', wbc.value)) AS boolean_values
            FROM
                active_woningbloks aw
                    JOIN diwi.woningblok_maatwerk_boolean_changelog wbc ON aw.woningblok_id = wbc.woningblok_id AND wbc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = wbc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = wbc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= NOW() AND NOW() < ems.date
            GROUP BY aw.woningblok_id
        ),
        active_woningbloks_housetypes_physical_app AS (
            SELECT
                aw.woningblok_id,
                array_agg(jsonb_build_object('house_type', wtfctv.woning_type, 'amount', wtfctv.amount)) FILTER (WHERE wtfctv.woning_type IS NOT NULL) AS house_type,
                array_agg(jsonb_build_object('physical_appearance', wtfcfv.property_value_id, 'amount', wtfcfv.amount)) FILTER (WHERE wtfcfv.property_value_id IS NOT NULL) AS physical_appearance
            FROM
                active_woningbloks aw
                    JOIN diwi.woningblok_type_en_fysiek_changelog wtfc ON aw.woningblok_id = wtfc.woningblok_id AND wtfc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = wtfc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = wtfc.end_milestone_id AND ems.change_end_date IS NULL
                    LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value wtfctv ON wtfc.id = wtfctv.woningblok_type_en_fysiek_voorkomen_changelog_id
                    LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_fysiek_value wtfcfv ON wtfc.id = wtfcfv.woningblok_type_en_fysiek_voorkomen_changelog_id
            WHERE
                sms.date <= NOW() AND NOW() < ems.date
            GROUP BY aw.woningblok_id
        ),
        active_woningbloks_targetgroup AS (
            SELECT
                aw.woningblok_id,
                array_agg(jsonb_build_object('target_group', wdgcv.property_value_id, 'amount', wdgcv.amount)) FILTER (WHERE wdgcv.property_value_id IS NOT NULL) AS target_group
            FROM
                active_woningbloks aw
                    JOIN diwi.woningblok_doelgroep_changelog wdgc ON aw.woningblok_id = wdgc.woningblok_id AND wdgc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = wdgc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = wdgc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.woningblok_doelgroep_changelog_value wdgcv ON wdgc.id = wdgcv.woningblok_doelgroep_changelog_id
            WHERE
                sms.date <= NOW() AND NOW() < ems.date
            GROUP BY aw.woningblok_id
        ),
        active_woningbloks_ground_positions AS (
            SELECT
                aw.woningblok_id,
                array_agg(jsonb_build_object('ground_position', wgpcv.grondpositie, 'amount', wgpcv.amount)) FILTER (WHERE wgpcv.grondpositie IS NOT NULL) AS ground_position
            FROM
                active_woningbloks aw
                    JOIN diwi.woningblok_grondpositie_changelog wgpc ON aw.woningblok_id = wgpc.woningblok_id AND wgpc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = wgpc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = wgpc.end_milestone_id AND ems.change_end_date IS NULL
                    LEFT JOIN diwi.woningblok_grondpositie_changelog_value wgpcv ON wgpc.id = wgpcv.woningblok_grondpositie_changelog_id
            WHERE
                sms.date <= NOW() AND NOW() < ems.date
            GROUP BY aw.woningblok_id
        ),
        active_woningbloks_programming AS (
            SELECT
                aw.woningblok_id,
                wpc.programmering AS programming
            FROM
                active_woningbloks aw
                    JOIN diwi.woningblok_programmering_changelog wpc ON aw.woningblok_id = wpc.woningblok_id AND wpc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = wpc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = wpc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= NOW() AND NOW() < ems.date
        ),
        active_woningbloks_ownership_values AS (
            SELECT
                aw.woningblok_id,
                array_agg(jsonb_build_object('ownership_type', wewc.eigendom_soort,
                    'property_value_id', COALESCE(wewc.ownership_property_value_id, wewc.rental_property_value_id),
                    'amount', wewc.amount)) AS ownership_value_options
            FROM
                active_woningbloks aw
                    JOIN diwi.woningblok_eigendom_en_waarde_changelog wewc ON aw.woningblok_id = wewc.woningblok_id AND wewc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = wewc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = wewc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= NOW() AND NOW() < ems.date
                AND (wewc.ownership_property_value_id IS NOT NULL OR wewc.rental_property_value_id IS NOT NULL)
            GROUP BY aw.woningblok_id
        )




SELECT
    aw.project_id      AS project_id,
    aw.woningblok_id   AS houseblock_id,
    awv.total_value    AS no_of_houses,
    awd.delivery_date  AS delivery_date,
    awc.category_values || awpc.category_values AS property_category_options,
    awbp.boolean_values || awpbp.boolean_values AS property_boolean_options,
    awhp.house_type AS house_type,
    awhp.physical_appearance AS physical_appearance,
    awtg.target_group   AS target_group,
    awgp.ground_position AS ground_position,
    awp.programming     AS programming,
    awov.ownership_value_options AS ownership_value_options
FROM
    active_woningbloks aw
        LEFT JOIN active_woningbloks_totalvalue awv ON awv.woningblok_id = aw.woningblok_id
        LEFT JOIN active_woningbloks_deliverydate awd ON awd.woningblok_id = aw.woningblok_id
        LEFT JOIN active_woningbloks_property_categories awc ON awc.woningblok_id = aw.woningblok_id
        LEFT JOIN active_woningbloks_project_property_categories awpc ON awpc.project_id = aw.project_id
        LEFT JOIN active_woningbloks_boolean_properties awbp ON awbp.woningblok_id = aw.woningblok_id
        LEFT JOIN active_woningbloks_project_boolean_properties awpbp ON awpbp.project_id = aw.project_id
        LEFT JOIN active_woningbloks_housetypes_physical_app awhp ON awhp.woningblok_id = aw.woningblok_id
        LEFT JOIN active_woningbloks_targetgroup awtg ON awtg.woningblok_id = aw.woningblok_id
        LEFT JOIN active_woningbloks_ground_positions awgp ON awgp.woningblok_id = aw.woningblok_id
        LEFT JOIN active_woningbloks_programming awp ON awp.woningblok_id = aw.woningblok_id
        LEFT JOIN active_woningbloks_ownership_values awov ON awov.woningblok_id = aw.woningblok_id
;

END;$$


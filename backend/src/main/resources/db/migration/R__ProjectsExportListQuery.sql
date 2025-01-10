DROP FUNCTION IF EXISTS diwi.get_projects_export_list;

CREATE OR REPLACE FUNCTION diwi.get_projects_export_list (
  _export_date_ date,
  _user_role_ text,
  _user_uuid_ uuid,
  _allowed_projects_ UUID[],
  _allowed_confidentialities_ TEXT[]
)
	RETURNS TABLE (
        projectId UUID,
        name TEXT,
        confidentiality diwi.confidentiality,
        startDate DATE,
        endDate DATE,
        planType TEXT[],
        projectPhase diwi.project_phase,
        planningPlanStatus TEXT[],
        realizationPhaseDate DATE,
        planStatusPhase1Date DATE,
        textProperties JSONB,
        numericProperties JSONB,
        booleanProperties JSONB,
        categoryProperties JSONB,
        geometries TEXT[],
        houseblocks JSONB
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

SELECT  q.projectId,
        q.projectName AS name,
        q.confidentiality,
        q.startDate,
        q.endDate,
        q.planType,
        q.projectPhase,
        q.planningPlanStatus,
        q.realizationPhaseDate,
        q.planStatusPhase1Date,
        q.textProperties,
        q.numericProperties,
        q.booleanProperties,
        q.categoryProperties,
        q.geometries,
        q.houseblocks
FROM (

    WITH
        project_owners AS (
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
        ),
        active_projects AS (
            SELECT
                p.id, ps.confidentiality_level AS confidentiality, sms.date AS startDate, ems.date AS endDate
            FROM
                diwi.project p
                    JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_state ps ON ps.project_id = p.id AND ps.change_end_date IS NULL
                    JOIN project_owners po ON po.project_id = p.id
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
                AND
                (   -- general project visibility rules
                    (_user_uuid_ = ANY(po.users)) OR
                    (_user_role_ IN ('User', 'UserPlus') AND ps.confidentiality_level != 'PRIVATE') OR
                    (_user_role_ = 'Management' AND ps.confidentiality_level NOT IN ('PRIVATE', 'INTERNAL_CIVIL')) OR
                    (_user_role_ = 'Council' AND ps.confidentiality_level NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT'))
                )
                AND
                    CASE
                        WHEN _allowed_confidentialities_ IS NOT NULL THEN ps.confidentiality_level::TEXT = ANY(_allowed_confidentialities_)
                        WHEN _allowed_projects_ IS NOT NULL THEN p.id = ANY(_allowed_projects_)
                    END
        ),
        active_project_names AS (
            SELECT
                pnc.project_id, pnc.name
            FROM
                active_projects ap
                    JOIN diwi.project_name_changelog pnc ON pnc.project_id = ap.id AND pnc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
        ),
        active_project_fases AS (
            SELECT
                pfc.project_id, pfc.project_fase
            FROM
                active_projects ap
                    JOIN diwi.project_fase_changelog pfc ON pfc.project_id = ap.id AND pfc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pfc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pfc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
        ),
        active_project_plan_types AS (
            SELECT
                pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
            FROM
                active_projects ap
                    JOIN diwi.project_plan_type_changelog pptc ON pptc.project_id = ap.id AND pptc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pptc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pptc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
            GROUP BY pptc.project_id
        ),
        active_project_planologische_planstatus AS (
            SELECT
                pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
            FROM
                active_projects ap
                    JOIN diwi.project_planologische_planstatus_changelog pppc ON ap.id = pppc.project_id AND pppc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pppc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pppc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
            GROUP BY pppc.project_id
        ),
        active_project_geometries AS (
            SELECT
                prlc.project_id, array_agg(prlcv.plot_feature::TEXT) AS geometries
            FROM
                active_projects ap
                    JOIN diwi.project_registry_link_changelog prlc ON ap.id = prlc.project_id AND prlc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = prlc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = prlc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_registry_link_changelog_value prlcv ON prlc.id = prlcv.project_registry_link_changelog_id
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
            GROUP BY prlc.project_id
        ),
        active_project_realization_phase AS (
            SELECT
                pfc.project_id, MIN(sms.date) AS realization_phase_start_date, pfc.project_fase
            FROM
                active_projects ap
                    JOIN diwi.project_fase_changelog pfc ON ap.id = pfc.project_id AND pfc.change_end_date IS NULL AND pfc.project_fase = '_6_REALIZATION'
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pfc.start_milestone_id AND sms.change_end_date IS NULL
            GROUP BY pfc.project_id, pfc.project_fase
        ),
        active_project_planstatus_phase1 AS (
            SELECT
                pppc.project_id, MIN(sms.date) AS planstatus_phase1_date
            FROM
                active_projects ap
                    JOIN diwi.project_planologische_planstatus_changelog pppc ON ap.id = pppc.project_id AND pppc.change_end_date IS NULL
                    JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
                        AND pppcv.planologische_planstatus IN ('_1A_ONHERROEPELIJK', '_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG', '_1C_ONHERROEPELIJK_MET_BW_NODIG')
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pppc.start_milestone_id AND sms.change_end_date IS NULL
            GROUP BY pppc.project_id
        ),
        active_project_textCP AS (
            SELECT
                ptc.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', ptc.property_id, 'textValue', ptc.value))) AS text_properties
            FROM
                active_projects ap
                    JOIN diwi.project_text_changelog ptc ON ap.id = ptc.project_id AND ptc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = ptc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = ptc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
            GROUP BY ptc.project_id
        ),
        active_project_numericCP AS (
            SELECT
                pnc.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', pnc.eigenschap_id, 'value', pnc.value, 'min', LOWER(pnc.value_range),
                                                   'max', UPPER(pnc.value_range)))) AS numeric_properties
            FROM
                active_projects ap
                    JOIN diwi.project_maatwerk_numeriek_changelog pnc ON ap.id = pnc.project_id AND pnc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
            GROUP BY pnc.project_id
        ),
        active_project_booleanCP AS (
            SELECT
                pbc.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', pbc.eigenschap_id, 'booleanValue', pbc.value))) AS boolean_properties
            FROM
                active_projects ap
                    JOIN diwi.project_maatwerk_boolean_changelog pbc ON ap.id = pbc.project_id AND pbc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pbc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pbc.end_milestone_id AND ems.change_end_date IS NULL
            WHERE
                sms.date <= _export_date_ AND _export_date_ < ems.date
            GROUP BY pbc.project_id
        ),
        active_project_categoryCP AS (
            WITH prj_cat_props AS (
                SELECT
                    pcc.project_id, pcc.property_id, array_agg(pccv.property_value_id) AS category_options
                FROM
                    active_projects ap
                        JOIN diwi.project_category_changelog pcc ON ap.id = pcc.project_id AND pcc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = pcc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = pcc.end_milestone_id AND ems.change_end_date IS NULL
                        JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                WHERE
                    sms.date <= _export_date_ AND _export_date_ < ems.date
                GROUP BY pcc.project_id, pcc.property_id
            )
            SELECT
                pcp.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', pcp.property_id, 'optionValues', pcp.category_options))) AS category_properties
            FROM prj_cat_props pcp
            GROUP BY pcp.project_id
        ),
        active_project_houseblocks AS (
            WITH
                active_project_active_woningbloks AS (
                    SELECT
                         w.id, w.project_id, sms.date AS startDate, ems.date AS endDate
                    FROM
                        active_projects ap
                            JOIN diwi.woningblok w ON w.project_id = ap.id
                            JOIN diwi.woningblok_state ws ON ws.woningblok_id = w.id AND ws.change_end_date IS NULL
                            JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                            JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                            JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                    WHERE sms.date <= _export_date_ AND _export_date_ < ems.date
                ),
                active_project_active_woningbloks_delivery AS (
                    SELECT
                        apaw.id, EXTRACT(YEAR FROM wdc.latest_deliverydate)::INTEGER AS deliveryYear
                    FROM
                        active_project_active_woningbloks apaw
                        JOIN diwi.woningblok_deliverydate_changelog wdc ON apaw.id = wdc.woningblok_id AND wdc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                    WHERE
                        sms.date <= _export_date_ AND _export_date_ < ems.date
                ),
                active_project_active_woningbloks_mutation AS (
                    SELECT
                        apaw.id, wmc.mutation_kind AS mutationKind, wmc.amount AS mutationAmount
                    FROM
                        active_project_active_woningbloks apaw
                        JOIN diwi.woningblok_mutatie_changelog wmc ON apaw.id = wmc.woningblok_id AND wmc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = wmc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = wmc.end_milestone_id AND ems.change_end_date IS NULL
                    WHERE
                        sms.date <= _export_date_ AND _export_date_ < ems.date
                ),
                active_project_active_woningbloks_housetypes AS (
                    SELECT
                        apaw.id, meer.amount AS meergezinswoning, eeng.amount AS eengezinswoning
                    FROM
                        active_project_active_woningbloks apaw
                        JOIN diwi.woningblok_type_en_fysiek_changelog wtfc ON apaw.id = wtfc.woningblok_id AND wtfc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = wtfc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = wtfc.end_milestone_id AND ems.change_end_date IS NULL
                        LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value eeng ON eeng.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND eeng.woning_type = 'EENGEZINSWONING'
                        LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value meer ON meer.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND meer.woning_type = 'MEERGEZINSWONING'
                    WHERE
                        sms.date <= _export_date_ AND _export_date_ < ems.date
                ),
                active_project_active_ownership_value AS (
                    SELECT
                        apaw.id, to_jsonb(array_agg(jsonb_build_object('ownershipId', wewc.id, 'ownershipType', wewc.eigendom_soort, 'ownershipAmount', wewc.amount,
                                    'ownershipValue', wewc.waarde_value, 'ownershipRentalValue', wewc.huurbedrag_value,
                                    'ownershipValueRangeMin', lower(wewc.waarde_value_range), 'ownershipRentalValueRangeMin', lower(huurbedrag_value_range),
                                    'ownershipValueRangeMax', upper(wewc.waarde_value_range) - 1, 'ownershipRentalValueRangeMax', upper(huurbedrag_value_range) - 1,
                                    'ownershipRangeCategoryId', wewc.ownership_property_value_id, 'ownershipRentalRangeCategoryId', wewc.rental_property_value_id))) AS ownershipValue
                    FROM
                        active_project_active_woningbloks apaw
                        JOIN diwi.woningblok_eigendom_en_waarde_changelog wewc ON apaw.id = wewc.woningblok_id AND wewc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = wewc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = wewc.end_milestone_id AND ems.change_end_date IS NULL
                    WHERE
                        sms.date <= _export_date_ AND _export_date_ < ems.date
                    GROUP BY apaw.id
                ),

                active_project_future_woningbloks AS (
                    SELECT
                         w.id, w.project_id, sms.date AS startDate, sms.milestone_id AS start_milestone_id
                    FROM
                        active_projects ap
                            JOIN diwi.woningblok w ON w.project_id = ap.id
                            JOIN diwi.woningblok_state ws ON ws.woningblok_id = w.id AND ws.change_end_date IS NULL
                            JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                            JOIN diwi.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                    WHERE _export_date_ < sms.date
                ),
                active_project_future_woningbloks_delivery AS (
                    SELECT
                        apfw.id, EXTRACT(YEAR FROM wdc.latest_deliverydate)::INTEGER AS deliveryYear
                    FROM
                        active_project_future_woningbloks apfw
                        JOIN diwi.woningblok_deliverydate_changelog wdc ON apfw.id = wdc.woningblok_id AND wdc.change_end_date IS NULL
                            AND wdc.start_milestone_id = apfw.start_milestone_id
                ),
                active_project_future_woningbloks_mutation AS (
                    SELECT
                        apfw.id, wmc.mutation_kind AS mutationKind, wmc.amount AS mutationAmount
                    FROM
                        active_project_future_woningbloks apfw
                        JOIN diwi.woningblok_mutatie_changelog wmc ON apfw.id = wmc.woningblok_id AND wmc.change_end_date IS NULL
                            AND wmc.start_milestone_id = apfw.start_milestone_id
                ),
                active_project_future_woningbloks_housetypes AS (
                    SELECT
                        apfw.id, meer.amount AS meergezinswoning, eeng.amount AS eengezinswoning
                    FROM
                        active_project_future_woningbloks apfw
                        JOIN diwi.woningblok_type_en_fysiek_changelog wtfc ON apfw.id = wtfc.woningblok_id AND wtfc.change_end_date IS NULL
                            AND wtfc.start_milestone_id = apfw.start_milestone_id
                        LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value eeng ON eeng.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND eeng.woning_type = 'EENGEZINSWONING'
                        LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value meer ON meer.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND meer.woning_type = 'MEERGEZINSWONING'
                ),
                active_project_future_ownership_value AS (
                    SELECT
                        apfw.id, to_jsonb(array_agg(jsonb_build_object('ownershipId', wewc.id, 'ownershipType', wewc.eigendom_soort, 'ownershipAmount', wewc.amount,
                                    'ownershipValue', wewc.waarde_value, 'ownershipRentalValue', wewc.huurbedrag_value,
                                    'ownershipValueRangeMin', lower(wewc.waarde_value_range), 'ownershipRentalValueRangeMin', lower(huurbedrag_value_range),
                                    'ownershipValueRangeMax', upper(wewc.waarde_value_range) - 1, 'ownershipRentalValueRangeMax', upper(huurbedrag_value_range) - 1,
                                    'ownershipRangeCategoryId', wewc.ownership_property_value_id, 'ownershipRentalRangeCategoryId', wewc.rental_property_value_id))) AS ownershipValue
                    FROM
                        active_project_future_woningbloks apfw
                        JOIN diwi.woningblok_eigendom_en_waarde_changelog wewc ON apfw.id = wewc.woningblok_id AND wewc.change_end_date IS NULL
                            AND wewc.start_milestone_id = apfw.start_milestone_id
                    GROUP BY apfw.id
                ),

                active_project_past_woningbloks AS (
                    SELECT
                         w.id, w.project_id, ems.date AS endDate, ems.milestone_id AS end_milestone_id
                    FROM
                        active_projects ap
                            JOIN diwi.woningblok w ON w.project_id = ap.id
                            JOIN diwi.woningblok_state ws ON ws.woningblok_id = w.id AND ws.change_end_date IS NULL
                            JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                            JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                    WHERE  ems.date <= _export_date_
                ),
                active_project_past_woningbloks_delivery AS (
                    SELECT
                        appw.id, EXTRACT(YEAR FROM wdc.latest_deliverydate)::INTEGER AS deliveryYear
                    FROM
                        active_project_past_woningbloks appw
                        JOIN diwi.woningblok_deliverydate_changelog wdc ON appw.id = wdc.woningblok_id AND wdc.change_end_date IS NULL
                            AND wdc.end_milestone_id = appw.end_milestone_id
                ),
                active_project_past_woningbloks_mutation AS (
                    SELECT
                        appw.id, wmc.mutation_kind AS mutationKind, wmc.amount AS mutationAmount
                    FROM
                        active_project_past_woningbloks appw
                        JOIN diwi.woningblok_mutatie_changelog wmc ON appw.id = wmc.woningblok_id AND wmc.change_end_date IS NULL
                            AND wmc.end_milestone_id = appw.end_milestone_id
                ),
                active_project_past_woningbloks_housetypes AS (
                    SELECT
                        appw.id, meer.amount AS meergezinswoning, eeng.amount AS eengezinswoning
                    FROM
                        active_project_past_woningbloks appw
                        JOIN diwi.woningblok_type_en_fysiek_changelog wtfc ON appw.id = wtfc.woningblok_id AND wtfc.change_end_date IS NULL
                            AND wtfc.end_milestone_id = appw.end_milestone_id
                        LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value eeng ON eeng.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND eeng.woning_type = 'EENGEZINSWONING'
                        LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value meer ON meer.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND meer.woning_type = 'MEERGEZINSWONING'
                ),
                active_project_past_ownership_value AS (
                    SELECT
                        appw.id, to_jsonb(array_agg(jsonb_build_object('ownershipId', wewc.id, 'ownershipType', wewc.eigendom_soort, 'ownershipAmount', wewc.amount,
                                    'ownershipValue', wewc.waarde_value, 'ownershipRentalValue', wewc.huurbedrag_value,
                                    'ownershipValueRangeMin', lower(wewc.waarde_value_range), 'ownershipRentalValueRangeMin', lower(huurbedrag_value_range),
                                    'ownershipValueRangeMax', upper(wewc.waarde_value_range) - 1, 'ownershipRentalValueRangeMax', upper(huurbedrag_value_range) - 1,
                                    'ownershipRangeCategoryId', wewc.ownership_property_value_id, 'ownershipRentalRangeCategoryId', wewc.rental_property_value_id))) AS ownershipValue
                    FROM
                        active_project_past_woningbloks appw
                        JOIN diwi.woningblok_eigendom_en_waarde_changelog wewc ON appw.id = wewc.woningblok_id AND wewc.change_end_date IS NULL
                            AND wewc.end_milestone_id = appw.end_milestone_id
                    GROUP BY appw.id
                ),

                houseblocks AS (
                    SELECT
                        apaw.project_id,
                        jsonb_build_object('houseblockId', apaw.id, 'deliveryYear', apawd.deliveryYear, 'mutationKind', apawm.mutationKind, 'mutationAmount', apawm.mutationAmount,
                         'meergezinswoning', apawh.meergezinswoning, 'eengezinswoning', apawh.eengezinswoning, 'ownershipValueList', apaov.ownershipValue) AS houseblocks
                    FROM
                        active_project_active_woningbloks apaw
                            JOIN active_project_active_woningbloks_mutation apawm ON apaw.id = apawm.id
                            LEFT JOIN active_project_active_woningbloks_delivery apawd ON apawd.id = apaw.id
                            LEFT JOIN active_project_active_woningbloks_housetypes apawh ON apawh.id = apaw.id
                            LEFT JOIN active_project_active_ownership_value apaov ON apaov.id = apaw.id

                    UNION

                    SELECT
                        apfw.project_id,
                        jsonb_build_object('houseblockId', apfw.id, 'deliveryYear', apfwd.deliveryYear, 'mutationKind', apfwm.mutationKind, 'mutationAmount', apfwm.mutationAmount,
                         'meergezinswoning', apfwh.meergezinswoning, 'eengezinswoning', apfwh.eengezinswoning, 'ownershipValueList', apfov.ownershipValue) AS houseblocks
                    FROM
                        active_project_future_woningbloks apfw
                            JOIN active_project_future_woningbloks_mutation apfwm ON apfw.id = apfwm.id
                            LEFT JOIN active_project_future_woningbloks_delivery apfwd ON apfwd.id = apfw.id
                            LEFT JOIN active_project_future_woningbloks_housetypes apfwh ON apfwh.id = apfw.id
                            LEFT JOIN active_project_future_ownership_value apfov ON apfov.id = apfw.id

                    UNION

                    SELECT
                        appw.project_id,
                        jsonb_build_object('houseblockId', appw.id, 'deliveryYear', appwd.deliveryYear, 'mutationKind', appwm.mutationKind, 'mutationAmount', appwm.mutationAmount,
                         'meergezinswoning', appwh.meergezinswoning, 'eengezinswoning', appwh.eengezinswoning, 'ownershipValueList', appov.ownershipValue) AS houseblocks
                    FROM
                        active_project_past_woningbloks appw
                            JOIN active_project_past_woningbloks_mutation appwm ON appw.id = appwm.id
                            LEFT JOIN active_project_past_woningbloks_delivery appwd ON appwd.id = appw.id
                            LEFT JOIN active_project_past_woningbloks_housetypes appwh ON appwh.id = appw.id
                            LEFT JOIN active_project_past_ownership_value appov ON appov.id = appw.id
                )

                SELECT
                    hb.project_id, to_jsonb(array_agg(hb.houseblocks)) AS houseblocks
                FROM houseblocks hb
                GROUP BY hb.project_id
        ),
        past_projects AS (
            SELECT
                p.id, ps.confidentiality_level AS confidentiality, sms.date AS startDate, ems.date AS endDate, ems.milestone_id AS end_milestone_id
            FROM
                diwi.project p
                    JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                    JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                    JOIN diwi.project_state ps ON ps.project_id = p.id AND ps.change_end_date IS NULL
                    JOIN project_owners po ON po.project_id = p.id
            WHERE
                ems.date <= _export_date_
                AND
                (   -- general project visibility rules
                    (_user_uuid_ = ANY(po.users)) OR
                    (_user_role_ IN ('User', 'UserPlus') AND ps.confidentiality_level != 'PRIVATE') OR
                    (_user_role_ = 'Management' AND ps.confidentiality_level NOT IN ('PRIVATE', 'INTERNAL_CIVIL')) OR
                    (_user_role_ = 'Council' AND ps.confidentiality_level NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT'))
                )
                AND
                    CASE
                        WHEN _allowed_confidentialities_ IS NOT NULL THEN ps.confidentiality_level::TEXT = ANY(_allowed_confidentialities_)
                        WHEN _allowed_projects_ IS NOT NULL THEN p.id = ANY(_allowed_projects_)
                    END
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
        past_project_geometries AS (
            SELECT
                prlc.project_id, array_agg(prlcv.plot_feature::TEXT) AS geometries
            FROM
                past_projects pp
                    JOIN diwi.project_registry_link_changelog prlc ON pp.id = prlc.project_id
                        AND prlc.end_milestone_id = pp.end_milestone_id AND prlc.change_end_date IS NULL
                    JOIN diwi.project_registry_link_changelog_value prlcv ON prlc.id = prlcv.project_registry_link_changelog_id
            GROUP BY prlc.project_id
        ),
        past_project_realization_phase AS (
            SELECT
                pfc.project_id, MIN(sms.date) AS realization_phase_start_date, pfc.project_fase
            FROM
                past_projects pp
                    JOIN diwi.project_fase_changelog pfc ON pp.id = pfc.project_id AND pfc.change_end_date IS NULL AND pfc.project_fase = '_6_REALIZATION'
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pfc.start_milestone_id AND sms.change_end_date IS NULL
            GROUP BY pfc.project_id, pfc.project_fase
        ),
        past_project_planstatus_phase1 AS (
            SELECT
                pppc.project_id, MIN(sms.date) AS planstatus_phase1_date
            FROM
                past_projects pp
                    JOIN diwi.project_planologische_planstatus_changelog pppc ON pp.id = pppc.project_id AND pppc.change_end_date IS NULL
                    JOIN diwi.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
                        AND pppcv.planologische_planstatus IN ('_1A_ONHERROEPELIJK', '_1B_ONHERROEPELIJK_MET_UITWERKING_NODIG', '_1C_ONHERROEPELIJK_MET_BW_NODIG')
                    JOIN diwi.milestone_state sms ON sms.milestone_id = pppc.start_milestone_id AND sms.change_end_date IS NULL
            GROUP BY pppc.project_id
        ),
        past_project_textCP AS (
            SELECT
                ptc.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', ptc.property_id, 'textValue', ptc.value))) AS text_properties
            FROM
                past_projects pp
                    JOIN diwi.project_text_changelog ptc ON pp.id = ptc.project_id AND ptc.end_milestone_id = pp.end_milestone_id AND ptc.change_end_date IS NULL
            GROUP BY ptc.project_id
        ),
        past_project_numericCP AS (
            SELECT
                pnc.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', pnc.eigenschap_id, 'value', pnc.value, 'min', LOWER(pnc.value_range),
                                                                      'max', UPPER(pnc.value_range)))) AS numeric_properties
            FROM
                past_projects pp
                    JOIN diwi.project_maatwerk_numeriek_changelog pnc ON pp.id = pnc.project_id AND pnc.end_milestone_id = pp.end_milestone_id AND pnc.change_end_date IS NULL
            GROUP BY pnc.project_id
        ),
        past_project_booleanCP AS (
            SELECT
                pbc.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', pbc.eigenschap_id, 'booleanValue', pbc.value))) AS boolean_properties
            FROM
                past_projects pp
                    JOIN diwi.project_maatwerk_boolean_changelog pbc ON pp.id = pbc.project_id AND pbc.end_milestone_id = pp.end_milestone_id AND pbc.change_end_date IS NULL
            GROUP BY pbc.project_id
        ),
        past_project_categoryCP AS (
            WITH prj_cat_props AS (
                SELECT
                    pcc.project_id, pcc.property_id, array_agg(pccv.property_value_id) AS category_options
                FROM
                    past_projects pp
                        JOIN diwi.project_category_changelog pcc ON pp.id = pcc.project_id AND pcc.end_milestone_id = pp.end_milestone_id  AND pcc.change_end_date IS NULL
                        JOIN diwi.project_category_changelog_value pccv ON pccv.project_category_changelog_id = pcc.id
                GROUP BY pcc.project_id, pcc.property_id
            )
            SELECT
                pcp.project_id, to_jsonb(array_agg(jsonb_build_object('propertyId', pcp.property_id, 'optionValues', pcp.category_options))) AS category_properties
            FROM prj_cat_props pcp
            GROUP BY pcp.project_id
        ),
        past_project_houseblocks AS (
            WITH
                past_project_past_woningbloks AS (
                    SELECT
                         w.id, w.project_id, ems.date AS endDate, ems.milestone_id AS end_milestone_id
                    FROM
                        past_projects pp
                            JOIN diwi.woningblok w ON w.project_id = pp.id
                            JOIN diwi.woningblok_state ws ON ws.woningblok_id = w.id AND ws.change_end_date IS NULL
                            JOIN diwi.woningblok_duration_changelog wdc ON wdc.woningblok_id = w.id AND wdc.change_end_date IS NULL
                            JOIN diwi.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                    WHERE  ems.date <= _export_date_
                ),
                past_project_past_woningbloks_delivery AS (
                    SELECT
                        pppw.id, EXTRACT(YEAR FROM wdc.latest_deliverydate)::INTEGER AS deliveryYear
                    FROM
                        past_project_past_woningbloks pppw
                        JOIN diwi.woningblok_deliverydate_changelog wdc ON pppw.id = wdc.woningblok_id AND wdc.change_end_date IS NULL
                            AND wdc.end_milestone_id = pppw.end_milestone_id
                ),
                past_project_past_woningbloks_mutation AS (
                    SELECT
                        pppw.id, wmc.mutation_kind AS mutationKind, wmc.amount AS mutationAmount
                    FROM
                        past_project_past_woningbloks pppw
                        JOIN diwi.woningblok_mutatie_changelog wmc ON pppw.id = wmc.woningblok_id AND wmc.change_end_date IS NULL
                            AND wmc.end_milestone_id = pppw.end_milestone_id
                ),
                past_project_past_woningbloks_housetypes AS (
                    SELECT
                        pppw.id, meer.amount AS meergezinswoning, eeng.amount AS eengezinswoning
                    FROM
                        past_project_past_woningbloks pppw
                        JOIN diwi.woningblok_type_en_fysiek_changelog wtfc ON pppw.id = wtfc.woningblok_id AND wtfc.change_end_date IS NULL
                            AND wtfc.end_milestone_id = pppw.end_milestone_id
                        LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value eeng ON eeng.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND eeng.woning_type = 'EENGEZINSWONING'
                        LEFT JOIN diwi.woningblok_type_en_fysiek_changelog_type_value meer ON meer.woningblok_type_en_fysiek_voorkomen_changelog_id = wtfc.id
                            AND meer.woning_type = 'MEERGEZINSWONING'
                ),
                past_project_past_ownership_value AS (
                    SELECT
                        pppw.id, to_jsonb(array_agg(jsonb_build_object('ownershipId', wewc.id, 'ownershipType', wewc.eigendom_soort, 'ownershipAmount', wewc.amount,
                                    'ownershipValue', wewc.waarde_value, 'ownershipRentalValue', wewc.huurbedrag_value,
                                    'ownershipValueRangeMin', lower(wewc.waarde_value_range), 'ownershipRentalValueRangeMin', lower(huurbedrag_value_range),
                                    'ownershipValueRangeMax', upper(wewc.waarde_value_range) - 1, 'ownershipRentalValueRangeMax', upper(huurbedrag_value_range) - 1,
                                    'ownershipRangeCategoryId', wewc.ownership_property_value_id, 'ownershipRentalRangeCategoryId', wewc.rental_property_value_id))) AS ownershipValue
                    FROM
                        past_project_past_woningbloks pppw
                        JOIN diwi.woningblok_eigendom_en_waarde_changelog wewc ON pppw.id = wewc.woningblok_id AND wewc.change_end_date IS NULL
                            AND wewc.end_milestone_id = pppw.end_milestone_id
                    GROUP BY pppw.id
                ),

                houseblocks AS (
                    SELECT
                        pppw.project_id,
                        jsonb_build_object('houseblockId', pppw.id, 'deliveryYear', pppwd.deliveryYear, 'mutationKind', pppwm.mutationKind, 'mutationAmount', pppwm.mutationAmount,
                         'meergezinswoning', pppwh.meergezinswoning, 'eengezinswoning', pppwh.eengezinswoning, 'ownershipValueList', pppov.ownershipValue) AS houseblocks
                    FROM
                        past_project_past_woningbloks pppw
                            JOIN past_project_past_woningbloks_mutation pppwm ON pppw.id = pppwm.id
                            LEFT JOIN past_project_past_woningbloks_delivery pppwd ON pppwd.id = pppw.id
                            LEFT JOIN past_project_past_woningbloks_housetypes pppwh ON pppwh.id = pppw.id
                            LEFT JOIN past_project_past_ownership_value pppov ON pppov.id = pppw.id
                )

                SELECT
                    hb.project_id, to_jsonb(array_agg(hb.houseblocks)) AS houseblocks
                FROM houseblocks hb
                GROUP BY hb.project_id
        )

    SELECT ap.id                    AS projectId,
           apn.name                 AS projectName,
           ap.confidentiality       AS confidentiality,
           ap.startDate             AS startDate,
           ap.endDate               AS endDate,
           appt.plan_types          AS planType,
           apf.project_fase         AS projectPhase,
           appp.planning_planstatus AS planningPlanStatus,
           aprp.realization_phase_start_date AS realizationPhaseDate,
           appp1.planstatus_phase1_date AS planStatusPhase1Date,
           apt.text_properties      AS textProperties,
           apnp.numeric_properties  AS numericProperties,
           apb.boolean_properties   AS booleanProperties,
           apc.category_properties  AS categoryProperties,
           apg.geometries           AS geometries,
           aph.houseblocks          AS houseblocks
    FROM
        active_projects ap
            LEFT JOIN active_project_names apn ON apn.project_id = ap.id
            LEFT JOIN active_project_plan_types appt ON appt.project_id = ap.id
            LEFT JOIN active_project_fases apf ON apf.project_id = ap.id
            LEFT JOIN active_project_planologische_planstatus appp ON appp.project_id = ap.id
            LEFT JOIN active_project_textCP apt ON apt.project_id = ap.id
            LEFT JOIN active_project_numericCP apnp ON apnp.project_id = ap.id
            LEFT JOIN active_project_booleanCP apb ON apb.project_id = ap.id
            LEFT JOIN active_project_categoryCP apc ON apc.project_id = ap.id
            LEFT JOIN active_project_geometries apg ON apg.project_id = ap.id
            LEFT JOIN active_project_houseblocks aph ON aph.project_id = ap.id
            LEFT JOIN active_project_realization_phase aprp ON aprp.project_id = ap.id
            LEFT JOIN active_project_planstatus_phase1 appp1 ON appp1.project_id = ap.id

    UNION

    SELECT pp.id                    AS projectId,
           ppn.name                 AS projectName,
           pp.confidentiality       AS confidentiality,
           pp.startDate             AS startDate,
           pp.endDate               AS endDate,
           pppt.plan_types          AS planType,
           ppf.project_fase         AS projectPhase,
           pppp.planning_planstatus AS planningPlanStatus,
           pprp.realization_phase_start_date AS realizationPhaseDate,
           pppp1.planstatus_phase1_date AS planStatusPhase1Date,
           ppt.text_properties      AS textProperties,
           ppnp.numeric_properties  AS numericProperties,
           ppb.boolean_properties   AS booleanProperties,
           ppc.category_properties  AS categoryProperties,
           ppg.geometries           AS geometries,
           pph.houseblocks          AS houseblocks
    FROM
        past_projects pp
            LEFT JOIN past_project_names ppn ON ppn.project_id = pp.id
            LEFT JOIN past_project_plan_types pppt ON pppt.project_id = pp.id
            LEFT JOIN past_project_fases ppf ON ppf.project_id = pp.id
            LEFT JOIN past_project_planologische_planstatus pppp ON pppp.project_id = pp.id
            LEFT JOIN past_project_textCP ppt ON ppt.project_id = pp.id
            LEFT JOIN past_project_numericCP ppnp ON ppnp.project_id = pp.id
            LEFT JOIN past_project_booleanCP ppb ON ppb.project_id = pp.id
            LEFT JOIN past_project_categoryCP ppc ON ppc.project_id = pp.id
            LEFT JOIN past_project_geometries ppg ON ppg.project_id = pp.id
            LEFT JOIN past_project_houseblocks pph ON pph.project_id = pp.id
            LEFT JOIN past_project_realization_phase pprp ON pprp.project_id = pp.id
            LEFT JOIN past_project_planstatus_phase1 pppp1 ON pppp1.project_id = pp.id

) AS q

ORDER BY q.projectName COLLATE "diwi_numeric" ASC;

END;$$

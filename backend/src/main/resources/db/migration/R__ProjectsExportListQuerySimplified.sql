DROP FUNCTION IF EXISTS diwi.get_projects_export_list_simplified;

CREATE OR REPLACE FUNCTION diwi.get_projects_export_list_simplified (
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
        houseblocks JSONB,
        status TEXT
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
        q.houseblocks,
        q.status
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
                ),
                past_project_past_woningbloks_name AS (
                    SELECT
                        pppw.id, wdc.naam as "name"
                    FROM
                        past_project_past_woningbloks pppw
                        JOIN diwi.woningblok_naam_changelog wdc ON pppw.id = wdc.woningblok_id AND wdc.change_end_date IS NULL
                            AND wdc.end_milestone_id = pppw.end_milestone_id
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
                        jsonb_build_object(
                            'houseblockId', pppw.id,
                            'deliveryYear', pppwd.deliveryYear,
                            'mutationKind', pppwm.mutationKind,
                            'mutationAmount', pppwm.mutationAmount,
                            'meergezinswoning', pppwh.meergezinswoning,
                            'eengezinswoning', pppwh.eengezinswoning,
                            'ownershipValueList', pppov.ownershipValue,
                            'name', pppn.name) AS houseblocks
                    FROM
                        past_project_past_woningbloks pppw
                            JOIN past_project_past_woningbloks_mutation pppwm ON pppw.id = pppwm.id
                            LEFT JOIN past_project_past_woningbloks_delivery pppwd ON pppwd.id = pppw.id
                            LEFT JOIN past_project_past_woningbloks_housetypes pppwh ON pppwh.id = pppw.id
                            LEFT JOIN past_project_past_ownership_value pppov ON pppov.id = pppw.id
                            LEFT JOIN past_project_past_woningbloks_name pppn ON pppn.id = pppw.id
                )

                SELECT
                    hb.project_id, to_jsonb(array_agg(hb.houseblocks)) AS houseblocks
                FROM houseblocks hb
                GROUP BY hb.project_id
        )

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
           pph.houseblocks          AS houseblocks,
           'REALIZED'               AS "status"
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

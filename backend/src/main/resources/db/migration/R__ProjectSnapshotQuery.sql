DROP FUNCTION IF EXISTS get_active_or_future_project_snapshot;

CREATE OR REPLACE FUNCTION get_active_or_future_project_snapshot (
  _project_uuid_ uuid,
  _now_ date
)
	RETURNS TABLE (
        projectId UUID,
        projectStateId UUID,
        projectName TEXT,
        projectOwnersArray TEXT[][],
        projectLeadersArray TEXT[][],
        projectColor TEXT,
        confidentialityLevel diwi_testset.confidentiality,
        startDate DATE,
        endDate DATE,
        planType TEXT[],
        priority TEXT[][],
        projectPhase diwi_testset.project_phase,
        planningPlanStatus TEXT[],
        municipalityRole TEXT[][],
        totalValue BIGINT,
        municipality TEXT[][],
        wijk TEXT[][],
        buurt TEXT[][]
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

SELECT  q.projectId,
        q.projectStateId,
        q.projectName,
        q.projectOwners            AS projectOwnersArray,
        q.projectLeaders           AS projectLeadersArray,
        q.projectColor,
        q.confidentialityLevel,
        q.startDate,
        q.endDate,
        q.planType,
        q.priorityModel            AS priority,
        q.projectPhase,
        q.planningPlanStatus,
        q.municipalityRoleModel    AS municipalityRole,
        q.totalValue,
        q.municipalityModel        AS municipality,
        q.wijkModel                AS wijk,
        q.buurtModel               AS burrt
FROM (

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
                     sms.date <= _now_ AND _now_ < ems.date AND p.id = _project_uuid_
             ),
             active_project_names AS (
                 SELECT
                     pnc.project_id, pnc.name
                 FROM
                     diwi_testset.project_name_changelog pnc
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pnc.change_end_date IS NULL AND pnc.project_id = _project_uuid_
             ),
             active_project_fases AS (
                 SELECT
                     pfc.project_id, pfc.project_fase
                 FROM
                     diwi_testset.project_fase_changelog pfc
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pfc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pfc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pfc.change_end_date IS NULL AND pfc.project_id = _project_uuid_
             ),
             active_project_plan_types AS (
                 SELECT
                     pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
                 FROM
                     diwi_testset.project_plan_type_changelog pptc
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pptc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pptc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi_testset.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pptc.change_end_date IS NULL AND pptc.project_id = _project_uuid_
                 GROUP BY pptc.project_id
             ),
             active_project_planologische_planstatus AS (
                 SELECT
                     pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
                 FROM
                     diwi_testset.project_planologische_planstatus_changelog pppc
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pppc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pppc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi_testset.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pppc.change_end_date IS NULL AND pppc.project_id = _project_uuid_
                 GROUP BY pppc.project_id
             ),
             active_project_priorities AS (
                 SELECT ppc.project_id,
                        CASE
                            WHEN ppc.value_type = 'SINGLE_VALUE' THEN array_agg(vs.ordinal_level || ' ' || vs.value_label)
                            WHEN ppc.value_type = 'RANGE' THEN array_agg(vsMin.ordinal_level || ' ' || vsMin.value_label) || array_agg(vsMax.ordinal_level || ' ' || vsMax.value_label)
                            END AS project_priorities,
                        CASE
                            WHEN ppc.value_type = 'SINGLE_VALUE' THEN  array_agg(array[vs.project_priorisering_value_id::text, vs.ordinal_level || ' ' || vs.value_label])
                            WHEN ppc.value_type = 'RANGE' THEN array_agg(array[vsMin.project_priorisering_value_id::text, vsMin.ordinal_level || ' ' || vsMin.value_label]) ||
                                                               array_agg(array[vsMax.project_priorisering_value_id::text, vsMax.ordinal_level || ' ' || vsMax.value_label])
                            END AS project_prioritiesModel
                 FROM
                     diwi_testset.project_priorisering_changelog ppc
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = ppc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = ppc.end_milestone_id AND ems.change_end_date IS NULL
                         LEFT JOIN diwi_testset.project_priorisering_value_state vs
                                   ON ppc.project_priorisering_value_id = vs.project_priorisering_value_id AND vs.change_end_date IS NULL
                         LEFT JOIN diwi_testset.project_priorisering_value_state vsMin
                                   ON ppc.project_priorisering_min_value_id = vsMin.project_priorisering_value_id AND vsMin.change_end_date IS NULL
                         LEFT JOIN diwi_testset.project_priorisering_value_state vsMax
                                   ON ppc.project_priorisering_max_value_id = vsMax.project_priorisering_value_id AND vsMax.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND ppc.change_end_date IS NULL AND ppc.project_id = _project_uuid_
                 GROUP BY ppc.project_id, ppc.value_type
             ),
             active_project_gemeenterol AS (
                 SELECT
                     pgc.project_id,
                     array_agg(array[pgvs.project_gemeenterol_value_id::TEXT, pgvs.value_label]) AS municipality_roleModel
                 FROM
                     diwi_testset.project_gemeenterol_changelog pgc
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pgc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pgc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi_testset.project_gemeenterol_value_state pgvs
                              ON pgvs.project_gemeenterol_value_id = pgc.project_gemeenterol_value_id AND pgvs.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pgc.change_end_date IS NULL AND pgc.project_id = _project_uuid_
                 GROUP BY pgc.project_id
             ),
             active_project_woningblok_totalvalue AS (
                 SELECT
                     w.project_id, SUM(COALESCE(wmc.netto_plancapaciteit, 0)) AS total_value
                 FROM
                     diwi_testset.woningblok_mutatie_changelog wmc
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wmc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wmc.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi_testset.woningblok w ON wmc.woningblok_id = w.id
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND wmc.change_end_date IS NULL AND w.project_id = _project_uuid_
                 GROUP BY w.project_id
             ),
             active_project_wijk AS (
                 SELECT
                     pgic.project_id,
                     array_agg(array[wijks.wijk_id::TEXT, wijks.waarde_label]) AS wijkModel
                 FROM
                     diwi_testset.project_gemeente_indeling_changelog pgic
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pgic.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pgic.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi_testset.project_gemeente_indeling_changelog_wijk pgicw ON pgicw.project_gemeente_indeling_changelog_id = pgic.id
                         JOIN diwi_testset.wijk_state wijks ON wijks.wijk_id = pgicw.wijk_id AND wijks.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pgic.change_end_date IS NULL AND pgic.project_id = _project_uuid_
                 GROUP BY pgic.project_id
             ),
             active_project_buurt AS (
                 SELECT
                     pgic.project_id,
                     array_agg(array[buurts.buurt_id::TEXT, buurts.waarde_label]) AS buurtModel
                 FROM
                     diwi_testset.project_gemeente_indeling_changelog pgic
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pgic.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pgic.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi_testset.project_gemeente_indeling_changelog_buurt pgicb ON pgicb.project_gemeente_indeling_changelog_id = pgic.id
                         JOIN diwi_testset.buurt_state buurts ON buurts.buurt_id = pgicb.buurt_id AND buurts.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pgic.change_end_date IS NULL AND pgic.project_id = _project_uuid_
                 GROUP BY pgic.project_id
             ),
             active_project_municipality AS (
                 SELECT
                     pgic.project_id,
                     array_agg(array[gemeentes.gemeente_id::TEXT, gemeentes.waarde_label]) AS municipalityModel
                 FROM
                     diwi_testset.project_gemeente_indeling_changelog pgic
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pgic.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pgic.end_milestone_id AND ems.change_end_date IS NULL
                         JOIN diwi_testset.project_gemeente_indeling_changelog_gemeente pgicg ON pgicg.project_gemeente_indeling_changelog_id = pgic.id
                         JOIN diwi_testset.gemeente_state gemeentes ON gemeentes.gemeente_id = pgicg.gemeente_id AND gemeentes.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date AND pgic.change_end_date IS NULL AND pgic.project_id = _project_uuid_
                 GROUP BY pgic.project_id
             ),
             future_projects AS (
                 SELECT
                     p.id, sms.date AS startDate, ems.date AS endDate
                 FROM
                     diwi_testset.project p
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = p.id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date > _now_ AND p.id = _project_uuid_
             ),
             future_project_names AS (
                 SELECT
                     pnc.project_id, pnc.name
                 FROM
                     diwi_testset.project_name_changelog pnc
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = pnc.project_id AND pdc.start_milestone_id = pnc.start_milestone_id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                 WHERE
                     sms.date > _now_ AND pnc.change_end_date IS NULL AND pnc.project_id = _project_uuid_
             ),
             future_project_fases AS (
                 SELECT
                     pfc.project_id, pfc.project_fase
                 FROM
                     diwi_testset.project_fase_changelog pfc
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = pfc.project_id AND pdc.start_milestone_id = pfc.start_milestone_id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pfc.start_milestone_id AND sms.change_end_date IS NULL
                 WHERE
                     sms.date > _now_ AND pfc.change_end_date IS NULL AND pfc.project_id = _project_uuid_
             ),
             future_project_plan_types AS (
                 SELECT
                     pptc.project_id, array_agg(pptcv.plan_type::TEXT ORDER BY pptcv.plan_type::TEXT ASC) AS plan_types
                 FROM
                     diwi_testset.project_plan_type_changelog pptc
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = pptc.project_id AND pdc.start_milestone_id = pptc.start_milestone_id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pptc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.project_plan_type_changelog_value pptcv ON pptc.id = pptcv.changelog_id
                 WHERE
                     sms.date > _now_ AND pptc.change_end_date IS NULL AND pptc.project_id = _project_uuid_
                 GROUP BY  pptc.project_id
             ),
             future_project_planologische_planstatus AS (
                 SELECT
                     pppc.project_id, array_agg(pppcv.planologische_planstatus::TEXT ORDER BY pppcv.planologische_planstatus::TEXT ASC) AS planning_planstatus
                 FROM
                     diwi_testset.project_planologische_planstatus_changelog pppc
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = pppc.project_id AND pdc.start_milestone_id = pppc.start_milestone_id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pppc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.project_planologische_planstatus_changelog_value pppcv ON pppc.id = pppcv.planologische_planstatus_changelog_id
                 WHERE
                     sms.date > _now_ AND pppc.change_end_date IS NULL AND pppc.project_id = _project_uuid_
                 GROUP BY  pppc.project_id
             ),
             future_project_priorities AS (
                 SELECT
                     ppc.project_id,
                     CASE
                         WHEN ppc.value_type = 'SINGLE_VALUE' THEN array_agg(vs.ordinal_level || ' ' || vs.value_label)
                         WHEN ppc.value_type = 'RANGE' THEN array_agg(vsMin.ordinal_level || ' ' || vsMin.value_label) || array_agg(vsMax.ordinal_level || ' ' || vsMax.value_label)
                         END AS project_priorities,
                     CASE
                         WHEN ppc.value_type = 'SINGLE_VALUE' THEN  array_agg(array[vs.project_priorisering_value_id::text, vs.ordinal_level || ' ' || vs.value_label])
                         WHEN ppc.value_type = 'RANGE' THEN array_agg(array[vsMin.project_priorisering_value_id::text, vsMin.ordinal_level || ' ' || vsMin.value_label]) ||
                                                            array_agg(array[vsMax.project_priorisering_value_id::text, vsMax.ordinal_level || ' ' || vsMax.value_label])
                         END AS project_prioritiesModel
                 FROM
                     diwi_testset.project_priorisering_changelog ppc
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = ppc.project_id AND pdc.start_milestone_id = ppc.start_milestone_id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = ppc.start_milestone_id AND sms.change_end_date IS NULL
                         LEFT JOIN diwi_testset.project_priorisering_value_state vs
                                   ON ppc.project_priorisering_value_id = vs.project_priorisering_value_id AND vs.change_end_date IS NULL
                         LEFT JOIN diwi_testset.project_priorisering_value_state vsMin
                                   ON ppc.project_priorisering_min_value_id = vsMin.project_priorisering_value_id AND vsMin.change_end_date IS NULL
                         LEFT JOIN diwi_testset.project_priorisering_value_state vsMax
                                   ON ppc.project_priorisering_max_value_id = vsMax.project_priorisering_value_id AND vsMax.change_end_date IS NULL
                 WHERE
                     sms.date > _now_ AND ppc.change_end_date IS NULL AND ppc.project_id = _project_uuid_
                 GROUP BY ppc.project_id, ppc.value_type
             ),
             future_project_gemeenterol AS (
                 SELECT
                     pgc.project_id,
                     array_agg(array[pgvs.project_gemeenterol_value_id::TEXT, pgvs.value_label]) AS municipality_roleModel
                 FROM
                     diwi_testset.project_gemeenterol_changelog pgc
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = pgc.project_id AND pdc.start_milestone_id = pgc.start_milestone_id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pgc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.project_gemeenterol_value_state pgvs
                              ON pgvs.project_gemeenterol_value_id = pgc.project_gemeenterol_value_id AND pgvs.change_end_date IS NULL
                 WHERE
                     sms.date > _now_ AND pgc.change_end_date IS NULL AND pgc.project_id = _project_uuid_
                 GROUP BY  pgc.project_id
             ),
             future_project_woningblok_totalvalue AS (
                 SELECT
                     w.project_id, SUM(COALESCE(wmc.netto_plancapaciteit, 0)) AS total_value
                 FROM
                     diwi_testset.woningblok_mutatie_changelog wmc
                         JOIN diwi_testset.woningblok w ON wmc.woningblok_id = w.id
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = w.project_id AND pdc.start_milestone_id = wmc.start_milestone_id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wmc.start_milestone_id AND sms.change_end_date IS NULL
                 WHERE
                     sms.date > _now_ AND wmc.change_end_date IS NULL AND w.project_id = _project_uuid_
                 GROUP BY w.project_id
             ),
             future_project_municipality AS (
                 SELECT
                     pgic.project_id,
                     array_agg(array[gemeentes.gemeente_id::TEXT, gemeentes.waarde_label]) AS municipalityModel
                 FROM
                     diwi_testset.project_gemeente_indeling_changelog pgic
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = pgic.project_id AND pdc.start_milestone_id = pgic.start_milestone_id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pgic.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.project_gemeente_indeling_changelog_gemeente pgicg ON pgicg.project_gemeente_indeling_changelog_id = pgic.id
                         JOIN diwi_testset.gemeente_state gemeentes ON gemeentes.gemeente_id = pgicg.gemeente_id AND gemeentes.change_end_date IS NULL
                 WHERE
                     sms.date > _now_ AND pgic.change_end_date IS NULL AND pgic.project_id = _project_uuid_
                 GROUP BY pgic.project_id
             ),
             future_project_wijk AS (
                 SELECT
                     pgic.project_id,
                     array_agg(array[wijks.wijk_id::TEXT, wijks.waarde_label]) AS wijkModel
                 FROM
                     diwi_testset.project_gemeente_indeling_changelog pgic
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = pgic.project_id AND pdc.start_milestone_id = pgic.start_milestone_id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pgic.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.project_gemeente_indeling_changelog_wijk pgick ON pgick.project_gemeente_indeling_changelog_id = pgic.id
                         JOIN diwi_testset.wijk_state wijks ON wijks.wijk_id = pgick.wijk_id AND wijks.change_end_date IS NULL
                 WHERE
                     sms.date > _now_ AND pgic.change_end_date IS NULL AND pgic.project_id = _project_uuid_
                 GROUP BY pgic.project_id
             ),
             future_project_buurt AS (
                 SELECT
                     pgic.project_id,
                     array_agg(array[buurts.buurt_id::TEXT, buurts.waarde_label]) AS buurtModel
                 FROM
                     diwi_testset.project_gemeente_indeling_changelog pgic
                         JOIN diwi_testset.project_duration_changelog pdc ON pdc.project_id = pgic.project_id AND pdc.start_milestone_id = pgic.start_milestone_id AND pdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = pgic.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.project_gemeente_indeling_changelog_buurt pgicb ON pgicb.project_gemeente_indeling_changelog_id = pgic.id
                         JOIN diwi_testset.buurt_state buurts ON buurts.buurt_id = pgicb.buurt_id AND buurts.change_end_date IS NULL
                 WHERE
                     sms.date > _now_ AND pgic.change_end_date IS NULL AND pgic.project_id = _project_uuid_
                 GROUP BY pgic.project_id
             ),
             project_users AS (
                 SELECT
                     q.project_id    AS project_id,
                     q.project_rol   AS project_rol,
                     array_agg(array[q.organization_id::TEXT, q.organization_name, q.user_id::TEXT, q.user_initials, q.user_last_name, q.user_first_name]) AS users
                 FROM (
                          SELECT DISTINCT
                              ps.project_id as project_id,
                              otp.project_rol AS project_rol,
                              us.user_id AS user_id,
                              LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS user_initials,
                              us.last_name AS user_last_name,
                              us.first_name AS user_first_name,
                              os.organization_id AS organization_id,
                              os.naam AS organization_name
                          FROM diwi_testset.project_state ps
                              JOIN diwi_testset.organization_to_project otp ON ps.project_id = otp.project_id AND otp.change_end_date IS NULL
                              JOIN diwi_testset.organization_state os ON otp.organization_id = os.organization_id AND os.change_end_date IS NULL
                              JOIN diwi_testset.user_to_organization uto ON otp.organization_id = uto.organization_id
                              JOIN diwi_testset.user_state us ON uto.user_id = us.user_id AND us.change_end_date IS NULL
                          WHERE
                              ps.change_end_date IS NULL AND ps.project_id = _project_uuid_
                      ) AS q
                 GROUP BY q.project_id, q.project_rol
             )

         SELECT ap.id                    AS projectId,
                ps.id                    AS projectStateId,
                apn.name                 AS projectName,
                ps.project_colour        AS projectColor,
                ps.confidentiality_level AS confidentialityLevel,
                owners.users             AS projectOwners,
                ap.startDate             AS startDate,
                ap.endDate               AS endDate,
                appt.plan_types          AS planType,
                app.project_priorities   AS priority,
                app.project_prioritiesModel  AS priorityModel,
                apf.project_fase         AS projectPhase,
                appp.planning_planstatus AS planningPlanStatus,
                apg.municipality_roleModel    AS municipalityRoleModel,
                apwv.total_value         AS totalValue,
                apwm.municipalityModel   AS municipalityModel,
                apww.wijkModel           AS wijkModel,
                apwb.buurtModel          AS buurtModel,
                leaders.users            AS projectLeaders
         FROM
             active_projects ap
                 LEFT JOIN diwi_testset.project_state ps ON ps.project_id = ap.id AND ps.change_end_date IS NULL
                 LEFT JOIN active_project_names apn ON apn.project_id = ap.id
                 LEFT JOIN active_project_plan_types appt ON appt.project_id = ap.id
                 LEFT JOIN active_project_fases apf ON apf.project_id = ap.id
                 LEFT JOIN active_project_planologische_planstatus appp ON appp.project_id = ap.id
                 LEFT JOIN active_project_priorities app ON app.project_id = ap.id
                 LEFT JOIN active_project_gemeenterol apg ON apg.project_id = ap.id
                 LEFT JOIN active_project_woningblok_totalvalue apwv ON apwv.project_id = ap.id
                 LEFT JOIN active_project_municipality apwm ON apwm.project_id = ap.id
                 LEFT JOIN active_project_buurt apwb ON apwb.project_id = ap.id
                 LEFT JOIN active_project_wijk apww ON apww.project_id = ap.id
                 LEFT JOIN project_users leaders ON ps.project_id = leaders.project_id AND leaders.project_rol = 'PROJECT_LEIDER'
                 LEFT JOIN project_users owners ON ps.project_id = owners.project_id AND owners.project_rol = 'OWNER'

         UNION

         SELECT fp.id                    AS projectId,
                ps.id                    AS projectStateId,
                fpn.name                 AS projectName,
                ps.project_colour        AS projectColor,
                ps.confidentiality_level AS confidentialityLevel,
                owners.users             AS projectOwners,
                fp.startDate             AS startDate,
                fp.endDate               AS endDate,
                fppt.plan_types          AS planType,
                fpp.project_priorities   AS priority,
                fpp.project_prioritiesModel  AS priorityModel,
                fpf.project_fase         AS projectPhase,
                fppp.planning_planstatus AS planningPlanStatus,
                fpg.municipality_roleModel    AS municipalityRoleModel,
                fpwv.total_value         AS totalValue,
                fpwm.municipalityModel   AS municipalityModel,
                fpww.wijkModel           AS wijkModel,
                fpwb.buurtModel          AS buurtModel,
                leaders.users            AS projectLeaders
         FROM
             future_projects fp
                 LEFT JOIN diwi_testset.project_state ps ON ps.project_id = fp.id AND ps.change_end_date IS NULL
                 LEFT JOIN future_project_names fpn ON fpn.project_id = fp.id
                 LEFT JOIN future_project_plan_types fppt ON fppt.project_id = fp.id
                 LEFT JOIN future_project_fases fpf ON fpf.project_id = fp.id
                 LEFT JOIN future_project_planologische_planstatus fppp ON fppp.project_id = fp.id
                 LEFT JOIN future_project_priorities fpp ON fpp.project_id = fp.id
                 LEFT JOIN future_project_gemeenterol fpg ON fpg.project_id = fp.id
                 LEFT JOIN future_project_woningblok_totalvalue fpwv ON fpwv.project_id = fp.id
                 LEFT JOIN future_project_municipality fpwm ON fpwm.project_id = fp.id
                 LEFT JOIN future_project_buurt fpwb ON fpwb.project_id = fp.id
                 LEFT JOIN future_project_wijk fpww ON fpww.project_id = fp.id
                 LEFT JOIN project_users leaders ON ps.project_id = leaders.project_id AND leaders.project_rol = 'PROJECT_LEIDER'
                 LEFT JOIN project_users owners ON ps.project_id = owners.project_id AND owners.project_rol = 'OWNER'

     ) AS q
WHERE q.projectId = _project_uuid_ LIMIT 1;

END;$$

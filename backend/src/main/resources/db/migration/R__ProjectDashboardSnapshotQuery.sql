DROP FUNCTION IF EXISTS diwi.get_project_dashboard_snapshot;

CREATE OR REPLACE FUNCTION diwi.get_project_dashboard_snapshot (
  _project_uuid_ uuid,
  _snapshot_date_ date,
  _user_role_ text,
  _user_uuid_ uuid
)
	RETURNS TABLE (
        projectId          UUID,
        physicalAppearance JSONB
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

SELECT
    q.projectId          AS projectId,
    q.physicalAppearance AS physicalAppearance

FROM (

         WITH
             projects AS (
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
             project_users AS (
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
                          FROM diwi.project_state ps
                              JOIN diwi.usergroup_to_project ugtp ON ps.project_id = ugtp.project_id AND ugtp.change_end_date IS NULL
                              JOIN diwi.usergroup_state ugs ON ugtp.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                              LEFT JOIN diwi.user_to_usergroup utug ON ugtp.usergroup_id = utug.usergroup_id
                              LEFT JOIN diwi.user_state us ON utug.user_id = us.user_id AND us.change_end_date IS NULL
                          WHERE
                              ps.change_end_date IS NULL AND ps.project_id = _project_uuid_
                      ) AS q
                 GROUP BY q.project_id
             )

         SELECT
                p.id                               AS projectId,
                ps.confidentiality_level           AS confidentialityLevel,
                owners.users                       AS projectOwners,
                wpa.physicalAppearance             AS physicalAppearance
         FROM
             projects p
                 LEFT JOIN diwi.project_state ps ON ps.project_id = p.id AND ps.change_end_date IS NULL
                 LEFT JOIN LATERAL (SELECT to_jsonb(array_agg(jsonb_build_object('name', label, 'amount', amount))) AS physicalAppearance
                                    FROM woningbloks_physical_appearance) AS wpa ON true
                 LEFT JOIN project_users owners ON ps.project_id = owners.project_id

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

DROP FUNCTION IF EXISTS diwi.get_project_auditlog;

CREATE OR REPLACE FUNCTION diwi.get_project_auditlog (
  _now_ date,
  _project_uuid_ uuid,
  _start_time_ TIMESTAMP,
  _end_time_ TIMESTAMP,
  _user_role_ text,
  _user_uuid_ uuid
)
	RETURNS TABLE (
        id BIGINT,
        projectId UUID,
        actionType TEXT,
        projectName TEXT,
        propertyType TEXT,
        oldValues TEXT[],
        newValues TEXT[],
        changeUser TEXT,
        changeDate TIMESTAMP WITH TIME ZONE
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

WITH projectDetails AS (

    SELECT  q.projectId AS id,
            q.projectName AS name
    FROM (

        WITH
            active_projects AS (
                SELECT
                    p.id
                FROM
                    diwi.project p
                        JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id
                        JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                WHERE
                    CASE WHEN _project_uuid_ IS NOT NULL THEN p.id = _project_uuid_
                         WHEN _project_uuid_ IS NULL THEN 1 = 1 END
                    AND sms.date <= _now_ AND _now_ < ems.date
            ),
            active_project_names AS (
                SELECT
                    pnc.project_id, pnc.name
                FROM
                    active_projects ap
                        JOIN diwi.project_name_changelog pnc ON ap.id = pnc.project_id AND pnc.change_end_date IS NULL
                        JOIN diwi.milestone_state sms ON sms.milestone_id = pnc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = pnc.end_milestone_id AND ems.change_end_date IS NULL
                WHERE
                    sms.date <= _now_ AND _now_ < ems.date
            ),
            future_projects AS (
                SELECT
                    p.id, sms.milestone_id AS start_milestone_id
                FROM
                    diwi.project p
                        JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id
                        JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                WHERE
                    CASE WHEN _project_uuid_ IS NOT NULL THEN p.id = _project_uuid_
                         WHEN _project_uuid_ IS NULL THEN 1 = 1 END
                    AND sms.date > _now_
            ),
            future_project_names AS (
                SELECT
                    pnc.project_id, pnc.name
                FROM
                    future_projects fp
                        JOIN diwi.project_name_changelog pnc ON fp.id = pnc.project_id
                            AND pnc.start_milestone_id = fp.start_milestone_id AND pnc.change_end_date IS NULL
            ),
            past_projects AS (
                SELECT
                    p.id, ems.milestone_id AS end_milestone_id
                FROM
                    diwi.project p
                        JOIN diwi.project_duration_changelog pdc ON pdc.project_id = p.id
                        JOIN diwi.milestone_state sms ON sms.milestone_id = pdc.start_milestone_id AND sms.change_end_date IS NULL
                        JOIN diwi.milestone_state ems ON ems.milestone_id = pdc.end_milestone_id AND ems.change_end_date IS NULL
                WHERE
                    CASE WHEN _project_uuid_ IS NOT NULL THEN p.id = _project_uuid_
                         WHEN _project_uuid_ IS NULL THEN 1 = 1 END
                    AND ems.date <= _now_
            ),
            past_project_names AS (
                SELECT
                    pnc.project_id, pnc.name
                FROM
                    past_projects pp
                        JOIN diwi.project_name_changelog pnc ON pp.id = pnc.project_id
                            AND pnc.end_milestone_id = pp.end_milestone_id AND pnc.change_end_date IS NULL
            ),
            project_users AS (
                SELECT
                    ps.project_id as project_id,
                    array_agg(utug.user_id) AS users
                FROM diwi.project_state ps
                    JOIN diwi.usergroup_to_project ugtp ON ps.project_id = ugtp.project_id AND ugtp.change_end_date IS NULL
                    JOIN diwi.usergroup_state ugs ON ugtp.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                    LEFT JOIN diwi.user_to_usergroup utug ON ugtp.usergroup_id = utug.usergroup_id
                WHERE
                    ps.change_end_date IS NULL
                GROUP BY ps.project_id
            )

        SELECT ap.id                    AS projectId,
               apn.name                 AS projectName,
               ps.confidentiality_level AS confidentialityLevel,
               owners.users                  AS projectOwners
        FROM
            active_projects ap
                LEFT JOIN diwi.project_state ps ON ps.project_id = ap.id AND ps.change_end_date IS NULL
                LEFT JOIN active_project_names apn ON apn.project_id = ap.id
                LEFT JOIN project_users owners ON ps.project_id = owners.project_id

        UNION

        SELECT fp.id                    AS projectId,
               fpn.name                 AS projectName,
               ps.confidentiality_level AS confidentialityLevel,
               owners.users                  AS projectOwners
        FROM
            future_projects fp
                LEFT JOIN diwi.project_state ps ON ps.project_id = fp.id AND ps.change_end_date IS NULL
                LEFT JOIN future_project_names fpn ON fpn.project_id = fp.id
                LEFT JOIN project_users owners ON ps.project_id = owners.project_id

        UNION

        SELECT pp.id                    AS projectId,
               ppn.name                 AS projectName,
               ps.confidentiality_level AS confidentialityLevel,
               owners.users                  AS projectOwners
        FROM
            past_projects pp
                LEFT JOIN diwi.project_state ps ON ps.project_id = pp.id AND ps.change_end_date IS NULL
                LEFT JOIN past_project_names ppn ON ppn.project_id = pp.id
                LEFT JOIN project_users owners ON ps.project_id = owners.project_id

    ) AS q
      WHERE
        (
          ( _user_uuid_ = ANY(q.projectOwners) ) OR
          ( _user_role_ IN ('User', 'UserPlus') AND q.confidentialityLevel != 'PRIVATE') OR
          ( _user_role_ = 'Management' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL') ) OR
          ( _user_role_ = 'Council' AND q.confidentialityLevel NOT IN ('PRIVATE', 'INTERNAL_CIVIL', 'INTERNAL_MANAGEMENT') )
        )
),

auditlog AS (
    SELECT
        duration.project_id,
        pd.name AS projectName,
        'CREATE' AS actionType,
        'project' AS property,
        ARRAY[]::TEXT[] AS oldPropertyValues,
        null::TIMESTAMP AS oldPropertyStartDate,
        ARRAY[]::TEXT[] AS newPropertyValues,
        duration.change_start_date AS newPropertyStartDate,
        duration.create_user_id AS changeUser
        FROM projectDetails pd
            JOIN  diwi.project_duration_changelog duration ON duration.project_id = pd.id
    WHERE
        duration.change_start_date >= _start_time_ AND duration.change_start_date <= _end_time_

    UNION

    SELECT
        duration.project_id,
        pd.name AS projectName,
        'DELETE' AS actionType,
        'project' AS property,
        ARRAY[]::TEXT[] AS oldPropertyValues,
        null::TIMESTAMP AS oldPropertyStartDate,
        ARRAY[]::TEXT[] AS newPropertyValues,
        duration.change_end_date AS newPropertyStartDate,
        duration.change_user_id AS changeUser
        FROM projectDetails pd
            JOIN  diwi.project_duration_changelog duration ON duration.project_id = pd.id
    WHERE
        duration.change_end_date IS NOT NULL AND duration.change_end_date >= _start_time_ AND duration.change_end_date <= _end_time_

    UNION

    SELECT
        newValue.project_id,
        pd.name AS projectName,
        'UPDATE' AS actionType,
        'projectName' AS property,
        ARRAY_REMOVE(ARRAY[oldValue.name], null) AS oldPropertyValues,
        oldValue.change_start_date AS oldPropertyStartDate,
        ARRAY_REMOVE(ARRAY[newValue.name], null) AS newPropertyValues,
        newValue.change_start_date AS newPropertyStartDate,
        newValue.create_user_id AS changeUser
        FROM projectDetails pd
            JOIN diwi.project_name_changelog newValue ON newValue.project_id = pd.id
            LEFT JOIN diwi.project_name_changelog oldValue ON newValue.project_id = oldValue.project_id AND newValue.change_start_date = oldValue.change_end_date
    WHERE
        newValue.change_start_date >= _start_time_ AND newValue.change_start_date <= _end_time_
        AND ( oldValue.name IS NULL OR newValue.name != oldValue.name )

    UNION

    SELECT
        newValue.project_id,
        pd.name AS projectName,
        'UPDATE' AS actionType,
        'projectConfidentiality' AS property,
        ARRAY_REMOVE(ARRAY[oldValue.confidentiality_level::TEXT], null) AS oldPropertyValues,
        oldValue.change_start_date AS oldPropertyStartDate,
        ARRAY_REMOVE(ARRAY[newValue.confidentiality_level::TEXT], null) AS newPropertyValues,
        newValue.change_start_date AS newPropertyStartDate,
        newValue.create_user_id AS changeUser
        FROM projectDetails pd
            JOIN diwi.project_state newValue ON newValue.project_id = pd.id
            LEFT JOIN diwi.project_state oldValue ON newValue.project_id = oldValue.project_id AND newValue.change_start_date = oldValue.change_end_date
    WHERE
        newValue.change_start_date >= _start_time_ AND newValue.change_start_date <= _end_time_
        AND ( oldValue.confidentiality_level IS NULL OR newValue.confidentiality_level != oldValue.confidentiality_level )

    UNION

    SELECT
        newValue.project_id,
        pd.name AS projectName,
        'UPDATE' AS actionType,
        'projectColor' AS property,
        ARRAY_REMOVE(ARRAY[oldValue.project_colour::TEXT], null) AS oldPropertyValues,
        oldValue.change_start_date AS oldPropertyStartDate,
        ARRAY_REMOVE(ARRAY[newValue.project_colour::TEXT], null) AS newPropertyValues,
        newValue.change_start_date AS newPropertyStartDate,
        newValue.create_user_id AS changeUser
        FROM projectDetails pd
            JOIN diwi.project_state newValue ON newValue.project_id = pd.id
            LEFT JOIN diwi.project_state oldValue ON newValue.project_id = oldValue.project_id AND newValue.change_start_date = oldValue.change_end_date
    WHERE
        newValue.change_start_date >= _start_time_ AND newValue.change_start_date <= _end_time_
        AND ( oldValue.project_colour IS NULL OR newValue.project_colour != oldValue.project_colour )

    UNION

    SELECT
        pdc.project_id,
        pd.name AS projectName,
        'UPDATE' AS actionType,
        'projectStartDate' AS property,
        ARRAY_REMOVE(ARRAY[oldValue.date::TEXT], null) AS oldPropertyValues,
        oldValue.change_start_date AS oldPropertyStartDate,
        ARRAY_REMOVE(ARRAY[newValue.date::TEXT], null) AS newPropertyValues,
        newValue.change_start_date AS newPropertyStartDate,
        newValue.create_user_id AS changeUser
        FROM projectDetails pd
            JOIN diwi.project_duration_changelog pdc ON pdc.project_id = pd.id
            JOIN diwi.milestone_state newValue ON newValue.milestone_id = pdc.start_milestone_id AND newValue.change_end_date IS NULL
            LEFT JOIN diwi.milestone_state oldValue ON newValue.milestone_id = oldValue.milestone_id AND newValue.change_start_date = oldValue.change_end_date
    WHERE
        newValue.change_start_date >= _start_time_ AND newValue.change_start_date <= _end_time_
        AND ( oldValue.date IS NULL OR newValue.date != oldValue.date )

        UNION

    SELECT
        pdc.project_id,
        pd.name AS projectName,
        'UPDATE' AS actionType,
        'projectEndDate' AS property,
        ARRAY_REMOVE(ARRAY[oldValue.date::TEXT], null) AS oldPropertyValues,
        oldValue.change_start_date AS oldPropertyStartDate,
        ARRAY_REMOVE(ARRAY[newValue.date::TEXT], null) AS newPropertyValues,
        newValue.change_start_date AS newPropertyStartDate,
        newValue.create_user_id AS changeUser
        FROM projectDetails pd
            JOIN diwi.project_duration_changelog pdc ON pdc.project_id = pd.id
            JOIN diwi.milestone_state newValue ON newValue.milestone_id = pdc.end_milestone_id AND newValue.change_end_date IS NULL
            LEFT JOIN diwi.milestone_state oldValue ON newValue.milestone_id = oldValue.milestone_id AND newValue.change_start_date = oldValue.change_end_date
    WHERE
        newValue.change_start_date >= _start_time_ AND newValue.change_start_date <= _end_time_
        AND ( oldValue.date IS NULL OR newValue.date != oldValue.date )

)

SELECT row_number() OVER () AS id,
    auditlog.project_id AS projectId,
    auditlog.actionType AS actionType,
    auditlog.projectName AS projectName,
    auditlog.property AS property,
    auditlog.oldPropertyValues AS oldValues,
    auditlog.newPropertyValues AS newValues,
    us.email AS changeUser,
    auditlog.newPropertyStartDate AS changeDate

FROM auditlog
    LEFT JOIN diwi.user_state us ON auditlog.changeuser = us.user_id AND us.change_end_date IS NULL
ORDER BY auditlog.newPropertyStartDate, auditlog.property;

END;$$

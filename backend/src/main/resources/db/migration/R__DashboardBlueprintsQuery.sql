DROP FUNCTION IF EXISTS diwi.get_dashboard_blueprints;

CREATE
OR REPLACE FUNCTION diwi.get_dashboard_blueprints (
  _bp_uuid_ UUID,
  _bp_user_uuid_ UUID
)
	RETURNS TABLE (
        id UUID,
        name TEXT,
        elements TEXT[],
        users JSONB
	)
	LANGUAGE plpgsql

AS $$
BEGIN
RETURN QUERY

WITH active_blueprints AS (
    SELECT
        bs.blueprint_id AS blueprint_id,
        bs.id AS blueprint_state_id,
        bs.name AS name
    FROM
        diwi.blueprint_state bs
    WHERE
        bs.change_end_date IS NULL
            AND
        CASE
            WHEN _bp_uuid_ IS NOT NULL THEN bs.blueprint_id = _bp_uuid_
            WHEN _bp_uuid_ IS NULL THEN 1 = 1
        END
    ),

    blueprint_users AS (
        SELECT
            q.blueprint_id    AS blueprint_id,
            to_jsonb(array_agg(jsonb_build_object('userGroupUuid', q.usergroup_id::TEXT, 'userGroupName', q.usergroup_name, 'uuid', q.user_id::TEXT,
                'initials', q.user_initials, 'lastName', q.user_last_name, 'firstName', q.user_first_name))) AS users
        FROM (
            SELECT DISTINCT
                ab.blueprint_id as blueprint_id,
                us.user_id AS user_id,
                LEFT(us.last_name, 1) || LEFT(us.first_name,1) AS user_initials,
                us.last_name AS user_last_name,
                us.first_name AS user_first_name,
                ugs.usergroup_id AS usergroup_id,
                ugs.naam AS usergroup_name
            FROM
                active_blueprints ab
                    JOIN diwi.blueprint_to_usergroup btug ON ab.blueprint_state_id = btug.blueprint_state_id
                    JOIN diwi.usergroup_state ugs ON btug.usergroup_id = ugs.usergroup_id AND ugs.change_end_date IS NULL
                    LEFT JOIN diwi.user_to_usergroup utug ON ugs.usergroup_id = utug.usergroup_id
                    LEFT JOIN diwi.user_state us ON utug.user_id = us.user_id AND us.change_end_date IS NULL
        ) AS q
        GROUP BY q.blueprint_id
    ),

    blueprint_elements AS (
        SELECT
            ab.blueprint_id    AS blueprint_id,
            array_agg(bte.element::TEXT) FILTER (WHERE bte.element IS NOT NULL) AS elements
        FROM
            active_blueprints ab
                LEFT JOIN diwi.blueprint_to_element bte ON ab.blueprint_state_id = bte.blueprint_state_id
        GROUP BY ab.blueprint_id
    )

SELECT
    ab.blueprint_id,
    ab.name,
    be.elements,
    bu.users
FROM
    active_blueprints ab
        LEFT JOIN blueprint_users bu ON bu.blueprint_id = ab.blueprint_id
        LEFT JOIN blueprint_elements be ON be.blueprint_id = ab.blueprint_id

ORDER BY ab.name;

END;$$

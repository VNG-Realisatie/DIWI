DO $$
DECLARE
    system_user_id uuid;
    temp_usergroup_id uuid;
BEGIN
    SELECT
        id INTO system_user_id
    FROM
        diwi_testset."user"
    WHERE
        "system_user" = TRUE
    LIMIT 1;

    INSERT INTO diwi_testset.usergroup (id, single_user)
        VALUES (gen_random_uuid(), false)
        RETURNING id INTO temp_usergroup_id;
    INSERT INTO diwi_testset.usergroup_state (id, usergroup_id, naam, change_start_date, create_user_id)
        VALUES (gen_random_uuid(), temp_usergroup_id, 'Tijdelijke groep', now(), system_user_id);

    INSERT INTO diwi_testset.usergroup_to_project (id, usergroup_id, project_id, change_start_date, create_user_id)
        SELECT gen_random_uuid(), temp_usergroup_id, q.no_group_project_id, now(), system_user_id
        FROM
            (SELECT DISTINCT ps.project_id AS no_group_project_id FROM diwi_testset.project_state ps
                WHERE ps.change_end_date IS NULL
                    AND ps.project_id NOT IN (
                        SELECT distinct ps.project_id FROM diwi_testset.project_state ps
                            LEFT JOIN diwi_testset.usergroup_to_project ugtp ON ps.project_id = ugtp.project_id AND ugtp.change_end_date IS NULL
                            LEFT JOIN diwi_testset.usergroup_state ugs ON ugs.usergroup_id = ugtp.usergroup_id AND ugs.change_end_date IS NULL
                            WHERE ps.change_end_date IS NULL and ugs.id IS NOT NULL)
            ) AS q;

END
$$;

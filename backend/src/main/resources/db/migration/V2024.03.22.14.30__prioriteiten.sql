DROP FUNCTION IF EXISTS create_project_priorisering_value;

CREATE FUNCTION create_project_priorisering_value(id uuid, system_user_id uuid, value_label text, ordinal_level int4)
    RETURNS VOID
    AS $$
BEGIN
    INSERT INTO diwi_testset."project_priorisering_value"(id)
        VALUES(id);
    INSERT INTO diwi_testset."project_priorisering_value_state"(id, project_priorisering_value_id, create_user_id, change_start_date, value_label, ordinal_level)
        VALUES(id, id, system_user_id, now(), value_label, ordinal_level);
END;
$$
LANGUAGE plpgsql;

DO $$
DECLARE
    system_user_id uuid;
BEGIN
    SELECT
        id INTO system_user_id
    FROM
        diwi_testset."user"
    WHERE
        "system_user" = TRUE
    LIMIT 1;
    PERFORM
        create_project_priorisering_value(gen_random_uuid(), system_user_id, 'Laag', 1);
    PERFORM
        create_project_priorisering_value(gen_random_uuid(), system_user_id, 'Middel', 2);
    PERFORM
        create_project_priorisering_value(gen_random_uuid(), system_user_id, 'Hoog', 3);
END
$$;

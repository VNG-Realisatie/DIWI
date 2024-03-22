DROP FUNCTION IF EXISTS create_rol_gemeente;

CREATE FUNCTION create_rol_gemeente(id uuid, system_user_id uuid, value_label text)
    RETURNS VOID
    AS $$
BEGIN
    INSERT INTO diwi_testset."project_gemeenterol_value"(id)
        VALUES(id);
    INSERT INTO diwi_testset."project_gemeenterol_value_state"(id, project_gemeenterol_value_id, create_user_id, change_start_date, value_label)
        VALUES(id, id, system_user_id, now(), value_label);
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
        create_rol_gemeente(gen_random_uuid(), system_user_id, 'Opdrachtgever');
    PERFORM
        create_rol_gemeente(gen_random_uuid(), system_user_id, 'Vergunningverlener');
END
$$;

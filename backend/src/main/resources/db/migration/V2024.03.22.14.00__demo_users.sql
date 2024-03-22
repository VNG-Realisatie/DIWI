DROP FUNCTION IF EXISTS create_demo_user_org;

CREATE FUNCTION create_demo_user_org(id uuid, system_user_id uuid, first_name text, last_name text)
    RETURNS VOID
    AS $$
BEGIN
    INSERT INTO diwi_testset."user"(id)
        VALUES(id);
    INSERT INTO diwi_testset."user_state"(id, user_id, create_user_id, change_start_date, identity_provider_id, "first_name", "last_name")
        VALUES(id, id, system_user_id, now(), '', first_name, last_name);
    INSERT INTO diwi_testset."organization"(id)
        VALUES(id);
    INSERT INTO diwi_testset."organization_state"(id, organization_id, create_user_id, change_start_date, naam)
        VALUES(id, id, system_user_id, now(), first_name || ' ' || last_name);
    INSERT INTO diwi_testset."user_to_organization"(id, organization_id, user_id, create_user_id, change_start_date)
        VALUES(id, id, id, system_user_id, now());
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
        create_demo_user_org(gen_random_uuid(), system_user_id, 'Demo user', '1');
    PERFORM
        create_demo_user_org(gen_random_uuid(), system_user_id, 'Demo user', '2');
END
$$;

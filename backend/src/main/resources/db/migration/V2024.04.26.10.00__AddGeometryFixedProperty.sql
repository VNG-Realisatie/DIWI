DO $$
DECLARE
    system_user_id uuid;
    prop_id uuid;
BEGIN
    SELECT
        id INTO system_user_id
    FROM
        diwi_testset."user"
    WHERE
        "system_user" = TRUE
    LIMIT 1;

    INSERT INTO diwi_testset.property (id, type)
        VALUES (gen_random_uuid(), 'FIXED')
        RETURNING id INTO prop_id;

    INSERT INTO diwi_testset.property_state (id, property_name, property_type, create_user_id, change_start_date, property_id, property_object_type)
        VALUES (gen_random_uuid(), 'geometry', 'TEXT', system_user_id, NOW(), prop_id, 'PROJECT');

END
$$;


ALTER TABLE diwi_testset.project_maatwerk_text_changelog RENAME TO project_text_changelog;
ALTER TABLE diwi_testset.project_text_changelog
    RENAME COLUMN eigenschap_id TO property_id;

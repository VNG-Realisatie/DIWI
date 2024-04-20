DO $$
DECLARE
    system_user_id uuid;
    prop_id uuid;
    prop_cat_id uuid;
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
        VALUES (gen_random_uuid(), 'municipalityRole', 'CATEGORY', system_user_id, NOW(), prop_id, 'PROJECT');

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), prop_id)
        RETURNING id INTO prop_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), prop_cat_id, 'Opdrachtgever', system_user_id, NOW());

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), prop_id)
        RETURNING id INTO prop_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), prop_cat_id, 'Vergunningverlener', system_user_id, NOW());

END
$$;

DROP FUNCTION IF EXISTS create_rol_gemeente;
DROP TABLE diwi_testset.project_gemeenterol_changelog;
DROP TABLE diwi_testset.project_gemeenterol_value_state;
DROP TABLE diwi_testset.project_gemeenterol_value;


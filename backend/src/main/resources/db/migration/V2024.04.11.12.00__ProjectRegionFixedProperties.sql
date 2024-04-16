DO $$
DECLARE
    system_user_id uuid;
    property_id uuid;
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
            RETURNING id INTO property_id;
    INSERT INTO diwi_testset.property_state (id, property_name, property_type, create_user_id, change_start_date, property_id, property_object_type)
        VALUES (gen_random_uuid(), 'municipality', 'CATEGORY', system_user_id, NOW(), property_id, 'PROJECT');

    INSERT INTO diwi_testset.property (id, type)
        VALUES (gen_random_uuid(), 'FIXED')
            RETURNING id INTO property_id;
    INSERT INTO diwi_testset.property_state (id, property_name, property_type, create_user_id, change_start_date, property_id, property_object_type)
        VALUES (gen_random_uuid(), 'district', 'CATEGORY', system_user_id, NOW(), property_id, 'PROJECT');

    INSERT INTO diwi_testset.property (id, type)
        VALUES (gen_random_uuid(), 'FIXED')
            RETURNING id INTO property_id;
    INSERT INTO diwi_testset.property_state (id, property_name, property_type, create_user_id, change_start_date, property_id, property_object_type)
        VALUES (gen_random_uuid(), 'neighbourhood', 'CATEGORY', system_user_id, NOW(), property_id, 'PROJECT');

END
$$;

DROP TABLE IF EXISTS diwi_testset.project_gemeente_indeling_changelog_buurt;
DROP TABLE IF EXISTS diwi_testset.project_gemeente_indeling_changelog_gemeente;
DROP TABLE IF EXISTS diwi_testset.project_gemeente_indeling_changelog_wijk;
DROP TABLE IF EXISTS diwi_testset.project_gemeente_indeling_changelog;

DROP TABLE IF EXISTS diwi_testset.plan_conditie_gemeente_indeling_buurt_value;
DROP TABLE IF EXISTS diwi_testset.plan_conditie_gemeente_indeling_wijk_value;
DROP TABLE IF EXISTS diwi_testset.plan_conditie_gemeente_indeling_gemeente_value;
DROP TABLE diwi_testset.plan_conditie_gemeente_indeling;

DROP TABLE IF EXISTS diwi_testset.buurt_state;
DROP TABLE IF EXISTS diwi_testset.buurt;
DROP TABLE IF EXISTS diwi_testset.wijk_state;
DROP TABLE IF EXISTS diwi_testset.wijk;
DROP TABLE IF EXISTS diwi_testset.gemeente_state;
DROP TABLE IF EXISTS diwi_testset.gemeente;

ALTER TABLE diwi_testset.project_maatwerk_categorie_changelog RENAME TO project_category_changelog;
ALTER TABLE diwi_testset.project_maatwerk_categorie_changelog_value RENAME TO project_category_changelog_value;

ALTER TABLE diwi_testset.project_category_changelog
    RENAME COLUMN eigenschap_id TO property_id;
ALTER TABLE diwi_testset.project_category_changelog_value
    RENAME COLUMN eigenschap_waarde_id TO property_value_id;
ALTER TABLE diwi_testset.project_category_changelog_value
    RENAME COLUMN project_maatwerk_categorie_changelog_id TO project_category_changelog_id;

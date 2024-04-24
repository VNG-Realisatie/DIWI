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
        VALUES (gen_random_uuid(), 'priority', 'ORDINAL', system_user_id, NOW(), prop_id, 'PROJECT');

    INSERT INTO diwi_testset.property_ordinal_value (id, property_id)
        SELECT p.id, prop_id
            FROM diwi_testset.project_priorisering_value p;
    INSERT INTO diwi_testset.property_ordinal_value_state (id, ordinal_value_id, value_label, ordinal_level, create_user_id, change_start_date, change_end_date, change_user_id)
        SELECT gen_random_uuid(), ps.project_priorisering_value_id, ps.value_label, ps.ordinal_level, ps.create_user_id, ps.change_start_date, ps.change_end_date, ps.change_user_id
            FROM diwi_testset.project_priorisering_value_state ps;

    INSERT INTO diwi_testset.project_maatwerk_ordinaal_changelog (id, start_milestone_id, end_milestone_id, project_id, value_id, create_user_id, change_start_date,
                                                     change_end_date, value_type, min_value_id, max_value_id, change_user_id, eigenschap_id)
    SELECT gen_random_uuid(), c.start_milestone_id, c.end_milestone_id, c.project_id, c.project_priorisering_value_id, c.create_user_id, c.change_start_date,
           c.change_end_date, c.value_type, c.project_priorisering_min_value_id, c.project_priorisering_max_value_id, c.change_user_id, prop_id
        FROM diwi_testset.project_priorisering_changelog c;

END
$$;

ALTER TABLE diwi_testset.project_maatwerk_ordinaal_changelog RENAME TO project_ordinal_changelog;
ALTER TABLE diwi_testset.project_ordinal_changelog RENAME COLUMN eigenschap_id TO property_id;

DROP TABLE diwi_testset.project_priorisering_changelog;
DROP TABLE diwi_testset.project_priorisering_value_state;
DROP TABLE diwi_testset.project_priorisering_value;

DROP FUNCTION IF EXISTS create_project_priorisering_value;

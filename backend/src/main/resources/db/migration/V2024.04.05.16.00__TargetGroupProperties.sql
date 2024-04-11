ALTER TABLE diwi_testset.woningblok_doelgroep_changelog_value
    ADD COLUMN IF NOT EXISTS property_value_id UUID;

ALTER TABLE ONLY diwi_testset.woningblok_doelgroep_changelog_value
    ADD CONSTRAINT fk_woningblok_doelgroep_changelog_value__property_value FOREIGN KEY ("property_value_id") REFERENCES diwi_testset.property_category_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


DO $$
DECLARE
    system_user_id uuid;
    property_id uuid;
    property_cat_id uuid;
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
        VALUES (gen_random_uuid(), 'targetGroup', 'CATEGORY', system_user_id, NOW(), property_id, 'WONINGBLOK');

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), property_id)
        RETURNING id INTO property_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), property_cat_id, 'Regulier', system_user_id, NOW());
    UPDATE diwi_testset.woningblok_doelgroep_changelog_value
        SET property_value_id = property_cat_id
        WHERE doelgroep = 'REGULIER';

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), property_id)
        RETURNING id INTO property_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), property_cat_id, 'Jongeren', system_user_id, NOW());
    UPDATE diwi_testset.woningblok_doelgroep_changelog_value
        SET property_value_id = property_cat_id
        WHERE doelgroep = 'JONGEREN';

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), property_id)
        RETURNING id INTO property_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), property_cat_id, 'Student', system_user_id, NOW());
    UPDATE diwi_testset.woningblok_doelgroep_changelog_value
        SET property_value_id = property_cat_id
        WHERE doelgroep = 'STUDENTEN';

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), property_id)
        RETURNING id INTO property_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), property_cat_id, 'Ouderen', system_user_id, NOW());
    UPDATE diwi_testset.woningblok_doelgroep_changelog_value
        SET property_value_id = property_cat_id
        WHERE doelgroep = 'OUDEREN';

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), property_id)
        RETURNING id INTO property_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), property_cat_id, 'GHZ', system_user_id, NOW());
    UPDATE diwi_testset.woningblok_doelgroep_changelog_value
        SET property_value_id = property_cat_id
        WHERE doelgroep = 'GEHANDICAPTEN_EN_ZORG';

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), property_id)
        RETURNING id INTO property_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), property_cat_id, 'Grote gezinnen', system_user_id, NOW());
    UPDATE diwi_testset.woningblok_doelgroep_changelog_value
        SET property_value_id = property_cat_id
        WHERE doelgroep = 'GROTE_GEZINNEN';

END
$$;

ALTER TABLE diwi_testset.woningblok_doelgroep_changelog_value
    ALTER COLUMN property_value_id SET NOT NULL;

ALTER TABLE diwi_testset.woningblok_doelgroep_changelog_value
    DROP COLUMN doelgroep;

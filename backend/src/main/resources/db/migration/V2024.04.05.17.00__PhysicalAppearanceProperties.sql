ALTER TABLE diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
    ADD COLUMN IF NOT EXISTS property_value_id UUID;

ALTER TABLE ONLY diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
    ADD CONSTRAINT fk_woningblok_type_en_fysiek_changelog_fysiek_value__property_value FOREIGN KEY ("property_value_id") REFERENCES diwi_testset.property_category_value("id") MATCH FULL ON UPDATE CASCADE ON DELETE CASCADE;


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
        VALUES (gen_random_uuid(), 'physicalAppearance', 'CATEGORY', system_user_id, NOW(), property_id, 'WONINGBLOK');

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), property_id)
        RETURNING id INTO property_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), property_cat_id, 'Tussenwoning', system_user_id, NOW());
    UPDATE diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
        SET property_value_id = property_cat_id
        WHERE fysiek_voorkomen = 'TUSSENWONING';

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), property_id)
        RETURNING id INTO property_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), property_cat_id, 'Hoekwoning', system_user_id, NOW());
    UPDATE diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
        SET property_value_id = property_cat_id
        WHERE fysiek_voorkomen = 'HOEKWONING';

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), property_id)
        RETURNING id INTO property_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), property_cat_id, 'Twee onder een kap', system_user_id, NOW());
    UPDATE diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
        SET property_value_id = property_cat_id
        WHERE fysiek_voorkomen = 'TWEE_ONDER_EEN_KAP';

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), property_id)
        RETURNING id INTO property_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), property_cat_id, 'Vrijstaand', system_user_id, NOW());
    UPDATE diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
        SET property_value_id = property_cat_id
        WHERE fysiek_voorkomen = 'VRIJSTAAND';

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), property_id)
        RETURNING id INTO property_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), property_cat_id, 'Portiekflat', system_user_id, NOW());
    UPDATE diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
        SET property_value_id = property_cat_id
        WHERE fysiek_voorkomen = 'PORTIEKFLAT';

    INSERT INTO diwi_testset.property_category_value (id, property_id)
        VALUES (gen_random_uuid(), property_id)
        RETURNING id INTO property_cat_id;
    INSERT INTO diwi_testset.property_category_value_state (id, category_value_id, value_label, create_user_id, change_start_date)
        VALUES (gen_random_uuid(), property_cat_id, 'Gallerijflat', system_user_id, NOW());
    UPDATE diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
        SET property_value_id = property_cat_id
        WHERE fysiek_voorkomen = 'GALLERIJFLAT';

END
$$;

ALTER TABLE diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
    ALTER COLUMN property_value_id SET NOT NULL;

ALTER TABLE diwi_testset.woningblok_type_en_fysiek_changelog_fysiek_value
    DROP COLUMN fysiek_voorkomen;

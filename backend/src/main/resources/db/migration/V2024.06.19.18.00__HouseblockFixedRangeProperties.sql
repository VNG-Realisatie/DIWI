DO $$
DECLARE
    system_user_id uuid;
    prop_id uuid;
BEGIN
    SELECT
        id INTO system_user_id
    FROM
        diwi."user"
    WHERE
            "system_user" = TRUE
        LIMIT 1;

    INSERT INTO diwi.property (id, type)
        VALUES (gen_random_uuid(), 'FIXED')
        RETURNING id INTO prop_id;

    INSERT INTO diwi.property_state (id, property_name, property_type, create_user_id, change_start_date, property_id, property_object_type)
        VALUES (gen_random_uuid(), 'priceRangeBuy', 'RANGE_CATEGORY', system_user_id, NOW(), prop_id, 'WONINGBLOK');

    INSERT INTO diwi.property (id, type)
        VALUES (gen_random_uuid(), 'FIXED')
        RETURNING id INTO prop_id;

    INSERT INTO diwi.property_state (id, property_name, property_type, create_user_id, change_start_date, property_id, property_object_type)
        VALUES (gen_random_uuid(), 'priceRangeRent', 'RANGE_CATEGORY', system_user_id, NOW(), prop_id, 'WONINGBLOK');

END
$$;


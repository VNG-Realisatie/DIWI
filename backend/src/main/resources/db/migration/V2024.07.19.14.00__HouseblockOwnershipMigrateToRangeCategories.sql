DO $$
DECLARE
    system_user_id uuid;
    prop_id uuid;
    temprow RECORD;
    temp_id uuid;
    now TIMESTAMP;
BEGIN

    SELECT NOW() INTO now;

    SELECT id INTO system_user_id
        FROM diwi."user"
        WHERE "system_user" = TRUE LIMIT 1;

    SELECT property_id INTO prop_id
        FROM diwi.property_state
        WHERE property_name = 'priceRangeBuy' LIMIT 1;

    FOR temprow IN
        SELECT DISTINCT lower(waarde_value_range) AS min_value, upper(waarde_value_range) AS max_value, array_agg(id) AS ids
            FROM diwi.woningblok_eigendom_en_waarde_changelog
        WHERE waarde_value_range IS NOT NULL
        GROUP BY waarde_value_range
    LOOP
        INSERT INTO diwi.property_range_category_value (id, property_id)
            VALUES (gen_random_uuid(), prop_id)
            RETURNING id INTO temp_id;
        INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_start_date)
            VALUES (gen_random_uuid(), temp_id, 'Koop: ' || temprow.min_value || ' - ' || COALESCE(temprow.max_value::TEXT, ''), temprow.min_value, temprow.max_value, system_user_id, now);
        UPDATE diwi.woningblok_eigendom_en_waarde_changelog
            SET ownership_property_value_id = temp_id, waarde_value_range = null
        WHERE id = ANY(temprow.ids);
    END LOOP;

    FOR temprow IN
        SELECT DISTINCT waarde_value AS value, array_agg(id) AS ids
            FROM diwi.woningblok_eigendom_en_waarde_changelog
        WHERE waarde_value IS NOT NULL
        GROUP BY waarde_value
    LOOP
        INSERT INTO diwi.property_range_category_value (id, property_id)
            VALUES (gen_random_uuid(), prop_id)
            RETURNING id INTO temp_id;
        INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_start_date)
            VALUES (gen_random_uuid(), temp_id, 'Koop: ' || temprow.value, temprow.value, temprow.value, system_user_id, now);
        UPDATE diwi.woningblok_eigendom_en_waarde_changelog
            SET ownership_property_value_id = temp_id, waarde_value = null
        WHERE id = ANY(temprow.ids);
    END LOOP;


    SELECT property_id INTO prop_id
        FROM diwi.property_state
        WHERE property_name = 'priceRangeRent' LIMIT 1;

    FOR temprow IN
        SELECT DISTINCT lower(huurbedrag_value_range) AS min_value, upper(huurbedrag_value_range) AS max_value, array_agg(id) AS ids
            FROM diwi.woningblok_eigendom_en_waarde_changelog
        WHERE huurbedrag_value_range IS NOT NULL
        GROUP BY huurbedrag_value_range
    LOOP
        INSERT INTO diwi.property_range_category_value (id, property_id)
            VALUES (gen_random_uuid(), prop_id)
            RETURNING id INTO temp_id;
        INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_start_date)
            VALUES (gen_random_uuid(), temp_id, 'Huur: ' || temprow.min_value || ' - ' || COALESCE(temprow.max_value::TEXT, ''), temprow.min_value, temprow.max_value, system_user_id, now);
        UPDATE diwi.woningblok_eigendom_en_waarde_changelog
            SET rental_property_value_id = temp_id, huurbedrag_value_range = null
        WHERE id = ANY(temprow.ids);
    END LOOP;

    FOR temprow IN
        SELECT DISTINCT huurbedrag_value AS value, array_agg(id) AS ids
            FROM diwi.woningblok_eigendom_en_waarde_changelog
        WHERE huurbedrag_value IS NOT NULL
        GROUP BY huurbedrag_value
    LOOP
        INSERT INTO diwi.property_range_category_value (id, property_id)
            VALUES (gen_random_uuid(), prop_id)
            RETURNING id INTO temp_id;
        INSERT INTO diwi.property_range_category_value_state (id, range_category_value_id, name, min, max, create_user_id, change_start_date)
            VALUES (gen_random_uuid(), temp_id, 'Huur: ' || temprow.value, temprow.value, temprow.value, system_user_id, now);
        UPDATE diwi.woningblok_eigendom_en_waarde_changelog
            SET rental_property_value_id = temp_id, huurbedrag_value = null
        WHERE id = ANY(temprow.ids);
    END LOOP;

END
$$;


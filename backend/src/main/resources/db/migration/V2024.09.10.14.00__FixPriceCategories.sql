UPDATE diwi.property_range_category_value_state AS vs
SET
    ("name", min, max) = (
        SELECT
            CASE
                WHEN u."system_user"
                AND vs.min = vs.max THEN substring(
                    vs.name
                    FROM
                        0 FOR 7
                ) || TRIM(to_char (vs.min, '9999999999999999999,00'))
                WHEN u."system_user"
                AND vs.max IS NULL THEN substring(
                    vs.name
                    FROM
                        0 FOR 7
                ) || TRIM(to_char (vs.min, '9999999999999999999,00')) || ' en hoger'
                WHEN u."system_user" THEN substring(
                    vs.name
                    FROM
                        0 FOR 7
                ) || TRIM(to_char (vs.min, '9999999999999999999,00')) || ' - ' || TRIM(to_char (vs.max - 1, '9999999999999999999,00'))
                ELSE vs.name
            END,
            CASE
                WHEN u."system_user" THEN vs.min
                ELSE vs.min * 100
            END,
            CASE
                WHEN u."system_user"
                AND vs.min != vs.max THEN vs.max - 1
                WHEN u."system_user"
                AND vs.min = vs.max THEN vs.max
                ELSE vs.max * 100
            END
        FROM
            diwi.property_range_category_value_state AS vs2
            INNER JOIN diwi.user AS u ON u.id = vs2.create_user_id
            INNER JOIN diwi.property_range_category_value AS prcv ON prcv.id = vs.range_category_value_id
            INNER JOIN diwi.property_state AS ps ON ps.property_id = prcv.property_id
        WHERE
            vs.id = vs2.id
            AND ps.property_name IN ('priceRangeBuy', 'priceRangeRent')
    );

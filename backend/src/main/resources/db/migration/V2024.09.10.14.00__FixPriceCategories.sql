UPDATE diwi.property_range_category_value_state vs
SET
    ("name", min, max) = (
        SELECT
            CASE
                WHEN u.system_user
                AND min = max THEN substring(
                    vs.name
                    FROM
                        0 FOR 7
                ) || TRIM(
                    to_char (
                        CAST(
                            split_part (
                                substring(
                                    vs.name
                                    FROM
                                        6
                                ),
                                '-',
                                1
                            ) AS numeric(16, 6)
                        ) / 100,
                        '9999999999999999999.00'
                    )
                )
                WHEN u.system_user THEN substring(
                    vs.name
                    FROM
                        0 FOR 7
                ) || TRIM(
                    to_char (
                        min / 100,
                        '9999999999999999999.00'
                    )
                ) || ' - ' || TRIM(
                    to_char (
                        max / 100,
                        '9999999999999999999.00'
                    )
                )
                ELSE vs.name
            END,
            CASE
                WHEN u.system_user THEN min
                ELSE min * 100
            END,
            CASE
                WHEN u.system_user THEN max
                ELSE max * 100
            END
        FROM
            diwi.user AS u
        WHERE
            u.id = vs.create_user_id
            -- and vs.id = vs2.id
    );

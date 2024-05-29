DROP FUNCTION IF EXISTS get_customproperty_definitions;
DROP FUNCTION IF EXISTS get_property_definitions;

CREATE
OR REPLACE FUNCTION get_property_definitions (
  _cp_uuid_ UUID,
  _cp_object_type_ VARCHAR,
  _cp_disabled_ BOOL,
  _cp_type_ VARCHAR
)
	RETURNS TABLE (
        id UUID,
        name TEXT,
        type diwi.property_type,
        objectType diwi.maatwerk_object_soort,
        propertyType diwi.maatwerk_eigenschap_type,
        disabled BOOL,
        categories JSONB,
        ordinals JSONB
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

SELECT cp.id                                                                                                                          AS id,
       cpState.property_name                                                                                                          AS name,
       cp.type                                                                                                                        AS type,
       cpState.property_object_type                                                                                                   AS objectType,
       cpState.property_type                                                                                                          AS propertyType,
       CASE WHEN cpState.change_end_date IS NULL THEN false ELSE TRUE END                                                             AS disabled,
       to_jsonb(array_agg(
           jsonb_build_object('id', catState.category_value_id, 'name', catState.value_label, 'disabled',
                              catState.change_end_date IS NOT NULL)) FILTER (WHERE catState.category_value_id IS NOT NULL))            AS categories,
       to_jsonb(array_agg(
           jsonb_build_object('id', ordState.ordinal_value_id, 'name', ordState.value_label, 'level', ordState.ordinal_level,
                              'disabled', ordState.change_end_date IS NOT NULL)) FILTER (WHERE ordState.ordinal_value_id IS NOT NULL)) AS ordinals

FROM diwi.property cp
    LEFT JOIN LATERAL (
        SELECT *
            FROM diwi.property_state cps
            WHERE cps.property_id = cp.id
            ORDER BY cps.change_start_date DESC
        LIMIT 1) cpState ON TRUE

    LEFT JOIN diwi.property_category_value cat ON cat.property_id = cp.id
    LEFT JOIN LATERAL (
        SELECT cs.category_value_id, cs.value_label, cs.change_end_date
            FROM diwi.property_category_value_state cs
            WHERE cs.category_value_id = cat.id
            ORDER BY cs.change_start_date DESC
        LIMIT 1) catState ON TRUE

    LEFT JOIN diwi.property_ordinal_value ord ON ord.property_id = cp.id
    LEFT JOIN LATERAL (
        SELECT os.ordinal_value_id, os.value_label, os.ordinal_level, os.change_end_date
            FROM diwi.property_ordinal_value_state os
            WHERE os.ordinal_value_id = ord.id
            ORDER BY os.change_start_date DESC
        LIMIT 1) ordState ON TRUE


WHERE
    CASE
        WHEN _cp_uuid_ IS NOT NULL THEN cp.id = _cp_uuid_
        WHEN _cp_object_type_ IS NOT NULL THEN cpState.property_object_type = CAST (_cp_object_type_ AS diwi.maatwerk_object_soort)
        ELSE 1 = 1
    END

    AND

    CASE
        WHEN _cp_disabled_ IS NULL THEN 1 = 1
        WHEN _cp_disabled_ IS TRUE THEN cpState.change_end_date IS NOT NULL
        WHEN _cp_disabled_ IS FALSE THEN cpState.change_end_date IS NULL
    END

    AND

    CASE
        WHEN _cp_type_ IS NOT NULL THEN cp.type = CAST (_cp_type_ AS diwi.property_type)
        ELSE 1 = 1
    END

GROUP BY cp.id, cpState.property_name, cp.type, cpState.property_object_type, cpState.property_type, disabled

ORDER BY cpState.property_name;

END;$$

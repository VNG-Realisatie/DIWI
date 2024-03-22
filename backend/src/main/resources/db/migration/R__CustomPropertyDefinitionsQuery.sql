DROP FUNCTION IF EXISTS get_customproperty_definitions;

CREATE
OR REPLACE FUNCTION get_customproperty_definitions (
  _cp_uuid_ UUID,
  _cp_object_type_ VARCHAR,
  _cp_disabled_ BOOL
)
	RETURNS TABLE (
        id UUID,
        name TEXT,
        objectType diwi_testset.maatwerk_object_soort,
        propertyType diwi_testset.maatwerk_eigenschap_type,
        disabled BOOL,
        categories JSONB,
        ordinals JSONB
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

SELECT cp.id                                                                                                                             AS id,
       cpState.eigenschap_naam                                                                                                           AS name,
       cpState.eigenschap_object_soort                                                                                                   AS objectType,
       cpState.eigenschap_type                                                                                                           AS propertyType,
       CASE WHEN cpState.change_end_date IS NULL THEN false ELSE TRUE END                                                                AS disabled,
       to_jsonb(array_agg(
           jsonb_build_object('id', catState.categorie_waarde_id, 'name', catState.waarde_label, 'disabled',
                              catState.change_end_date IS NOT NULL)) FILTER (WHERE catState.categorie_waarde_id IS NOT NULL))            AS categories,
       to_jsonb(array_agg(
           jsonb_build_object('id', ordState.ordinaal_waarde_id, 'name', ordState.waarde_label, 'level', ordState.ordinaal_niveau,
                              'disabled', ordState.change_end_date IS NOT NULL)) FILTER (WHERE ordState.ordinaal_waarde_id IS NOT NULL)) AS ordinals

FROM diwi_testset.maatwerk_eigenschap cp
    LEFT JOIN LATERAL (
        SELECT *
            FROM diwi_testset.maatwerk_eigenschap_state cps
            WHERE cps.eigenschap_id = cp.id
            ORDER BY cps.change_start_date DESC
        LIMIT 1) cpState ON TRUE

    LEFT JOIN diwi_testset.maatwerk_categorie_waarde cat ON cat.maatwerk_eigenschap_id = cp.id
    LEFT JOIN LATERAL (
        SELECT cs.categorie_waarde_id, cs.waarde_label, cs.change_end_date
            FROM diwi_testset.maatwerk_categorie_waarde_state cs
            WHERE cs.categorie_waarde_id = cat.id
            ORDER BY cs.change_start_date DESC
        LIMIT 1) catState ON TRUE

    LEFT JOIN diwi_testset.maatwerk_ordinaal_waarde ord ON ord.maatwerk_eigenschap_id = cp.id
    LEFT JOIN LATERAL (
        SELECT os.ordinaal_waarde_id, os.waarde_label, os.ordinaal_niveau, os.change_end_date
            FROM diwi_testset.maatwerk_ordinaal_waarde_state os
            WHERE os.ordinaal_waarde_id = ord.id
            ORDER BY os.change_start_date DESC
        LIMIT 1) ordState ON TRUE


WHERE
    CASE
        WHEN _cp_uuid_ IS NOT NULL THEN cp.id = _cp_uuid_
        WHEN _cp_object_type_ IS NOT NULL THEN cpState.eigenschap_object_soort = CAST (_cp_object_type_ AS diwi_testset.maatwerk_object_soort)
        ELSE 1 = 1
    END

    AND

    CASE
        WHEN _cp_disabled_ IS NULL THEN 1 = 1
        WHEN _cp_disabled_ IS TRUE THEN cpState.change_end_date IS NOT NULL
        WHEN _cp_disabled_ IS FALSE THEN cpState.change_end_date IS NULL
    END

GROUP BY cp.id, cpState.eigenschap_naam, cpState.eigenschap_object_soort, cpState.eigenschap_type, disabled

ORDER BY cpState.eigenschap_naam;

END;$$

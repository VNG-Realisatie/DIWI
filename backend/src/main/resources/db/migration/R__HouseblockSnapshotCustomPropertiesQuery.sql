DROP FUNCTION IF EXISTS get_houseblock_custom_properties;

CREATE OR REPLACE FUNCTION get_houseblock_custom_properties (
  _woningblok_uuid_ uuid,
  _now_ date
)
	RETURNS TABLE (
        customPropertyId UUID,
        booleanValue BOOL,
        numericValue FLOAT8,
        numericValueRange NUMRANGE,
        numericValueType diwi_testset.value_type,
        textValue TEXT,
        categories UUID[],
        ordinalValueId UUID,
        ordinalMinValueId UUID,
        ordinalMaxValueId UUID,
        propertyType diwi_testset.maatwerk_eigenschap_type
	)
	LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY

SELECT
    q.customPropertyId,
    q.booleanValue,
    q.numericValue,
    q.numericValueRange,
    q.numericValueType,
    q.textValue,
    q.categories,
    q.ordinalValueId,
    q.ordinalMinValueId,
    q.ordinalMaxValueId,
    q.propertyType

FROM (

         WITH
             active_woningbloks AS (
                 SELECT
                     p.id, sms.date AS startDate, ems.date AS endDate
                 FROM
                     diwi_testset.woningblok p
                         JOIN diwi_testset.woningblok_duration_changelog wdc ON wdc.woningblok_id = p.id AND wdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE  sms.date <= _now_ AND _now_ < ems.date AND p.id = _woningblok_uuid_
             ),
             active_woningbloks_booleanCP AS (
                 SELECT
                     ap.id, wbc.eigenschap_id, wbc.value
                 FROM
                     active_woningbloks ap
                         JOIN diwi_testset.woningblok_maatwerk_boolean_changelog wbc ON ap.id = wbc.woningblok_id AND wbc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wbc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wbc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_numericCP AS (
                 SELECT
                     ap.id, wnc.eigenschap_id, wnc.value, wnc.value_range, wnc.value_type
                 FROM
                     active_woningbloks ap
                         JOIN diwi_testset.woningblok_maatwerk_numeriek_changelog wnc ON ap.id = wnc.woningblok_id AND wnc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wnc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wnc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_textCP AS (
                 SELECT
                     ap.id, wtc.eigenschap_id, wtc.value
                 FROM
                     active_woningbloks ap
                         JOIN diwi_testset.woningblok_maatwerk_text_changelog wtc ON ap.id = wtc.woningblok_id AND wtc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wtc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wtc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             active_woningbloks_categoriesCP AS (
                 SELECT
                     ap.id, wcc.eigenschap_id, array_agg(wccv.eigenschap_waarde_id) AS categories
                 FROM
                     active_woningbloks ap
                         JOIN diwi_testset.woningblok_maatwerk_categorie_changelog wcc ON ap.id = wcc.woningblok_id AND wcc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wcc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wcc.end_milestone_id AND ems.change_end_date IS NULL
                         LEFT JOIN diwi_testset.woningblok_maatwerk_categorie_changelog_value wccv ON wccv.woningblok_maatwerk_categorie_changelog_id = wcc.id
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
                 GROUP BY ap.id, wcc.eigenschap_id
             ),
             active_woningbloks_ordinalCP AS (
                 SELECT
                     ap.id, woc.eigenschap_id, woc.value_id AS ordinal_value_id, woc.min_value_id AS ordinal_min_value_id, woc.max_value_id AS ordinal_max_value_id
                 FROM
                     active_woningbloks ap
                         JOIN diwi_testset.woningblok_maatwerk_ordinaal_changelog woc ON ap.id = woc.woningblok_id AND woc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = woc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = woc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE
                     sms.date <= _now_ AND _now_ < ems.date
             ),
             future_woningbloks AS (
                 SELECT
                     p.id, sms.milestone_id AS start_milestone_id
                 FROM
                     diwi_testset.woningblok p
                         JOIN diwi_testset.woningblok_duration_changelog wdc ON wdc.woningblok_id = p.id AND wdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE sms.date > _now_  AND p.id = _woningblok_uuid_
             ),
             future_woningbloks_numericCP AS (
                 SELECT
                     fp.id, wnc.eigenschap_id, wnc.value, wnc.value_range, wnc.value_type
                 FROM
                     future_woningbloks fp
                         JOIN diwi_testset.woningblok_maatwerk_numeriek_changelog wnc ON fp.id = wnc.woningblok_id
                            AND wnc.start_milestone_id = fp.start_milestone_id AND wnc.change_end_date IS NULL
             ),
             future_woningbloks_booleanCP AS (
                 SELECT
                     fp.id, wbc.eigenschap_id, wbc.value
                 FROM
                     future_woningbloks fp
                         JOIN diwi_testset.woningblok_maatwerk_boolean_changelog wbc ON fp.id = wbc.woningblok_id
                         AND wbc.start_milestone_id = fp.start_milestone_id AND wbc.change_end_date IS NULL
             ),
             future_woningbloks_textCP AS (
                 SELECT
                     fp.id, wtc.eigenschap_id, wtc.value
                 FROM
                     future_woningbloks fp
                         JOIN diwi_testset.woningblok_maatwerk_text_changelog wtc ON fp.id = wtc.woningblok_id
                         AND wtc.start_milestone_id = fp.start_milestone_id AND wtc.change_end_date IS NULL
             ),
             future_woningbloks_categoriesCP AS (
                 SELECT
                     fp.id, wcc.eigenschap_id, array_agg(wccv.eigenschap_waarde_id) AS categories
                 FROM
                     future_woningbloks fp
                         JOIN diwi_testset.woningblok_maatwerk_categorie_changelog wcc ON fp.id = wcc.woningblok_id
                            AND wcc.start_milestone_id = fp.start_milestone_id AND wcc.change_end_date IS NULL
                         LEFT JOIN diwi_testset.woningblok_maatwerk_categorie_changelog_value wccv ON wccv.woningblok_maatwerk_categorie_changelog_id = wcc.id
                 GROUP BY fp.id, wcc.eigenschap_id
             ),
             future_woningbloks_ordinalCP AS (
                 SELECT
                     fp.id, woc.eigenschap_id, woc.value_id AS ordinal_value_id, woc.min_value_id AS ordinal_min_value_id, woc.max_value_id AS ordinal_max_value_id
                 FROM
                     future_woningbloks fp
                         JOIN diwi_testset.woningblok_maatwerk_ordinaal_changelog woc ON fp.id = woc.woningblok_id
                         AND woc.start_milestone_id = fp.start_milestone_id AND woc.change_end_date IS NULL

             ),
             past_woningbloks AS (
                 SELECT
                     p.id, ems.milestone_id AS end_milestone_id
                 FROM
                     diwi_testset.woningblok p
                         JOIN diwi_testset.woningblok_duration_changelog wdc ON wdc.woningblok_id = p.id AND wdc.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state sms ON sms.milestone_id = wdc.start_milestone_id AND sms.change_end_date IS NULL
                         JOIN diwi_testset.milestone_state ems ON ems.milestone_id = wdc.end_milestone_id AND ems.change_end_date IS NULL
                 WHERE ems.date <= _now_  AND p.id = _woningblok_uuid_
             ),
             past_woningbloks_numericCP AS (
                 SELECT
                     pw.id, wnc.eigenschap_id, wnc.value, wnc.value_range, wnc.value_type
                 FROM
                     past_woningbloks pw
                         JOIN diwi_testset.woningblok_maatwerk_numeriek_changelog wnc ON pw.id = wnc.woningblok_id
                            AND wnc.end_milestone_id = pw.end_milestone_id AND wnc.change_end_date IS NULL
             ),
             past_woningbloks_booleanCP AS (
                 SELECT
                     pw.id, wbc.eigenschap_id, wbc.value
                 FROM
                     past_woningbloks pw
                         JOIN diwi_testset.woningblok_maatwerk_boolean_changelog wbc ON pw.id = wbc.woningblok_id
                         AND wbc.end_milestone_id = pw.end_milestone_id AND wbc.change_end_date IS NULL
             ),
             past_woningbloks_textCP AS (
                 SELECT
                     pw.id, wtc.eigenschap_id, wtc.value
                 FROM
                     past_woningbloks pw
                         JOIN diwi_testset.woningblok_maatwerk_text_changelog wtc ON pw.id = wtc.woningblok_id
                         AND wtc.end_milestone_id = pw.end_milestone_id AND wtc.change_end_date IS NULL
             ),
             past_woningbloks_categoriesCP AS (
                 SELECT
                     pw.id, wcc.eigenschap_id, array_agg(wccv.eigenschap_waarde_id) AS categories
                 FROM
                     past_woningbloks pw
                         JOIN diwi_testset.woningblok_maatwerk_categorie_changelog wcc ON pw.id = wcc.woningblok_id
                            AND wcc.end_milestone_id = pw.end_milestone_id AND wcc.change_end_date IS NULL
                         LEFT JOIN diwi_testset.woningblok_maatwerk_categorie_changelog_value wccv ON wccv.woningblok_maatwerk_categorie_changelog_id = wcc.id
                 GROUP BY pw.id, wcc.eigenschap_id
             ),
             past_woningbloks_ordinalCP AS (
                 SELECT
                     pw.id, woc.eigenschap_id, woc.value_id AS ordinal_value_id, woc.min_value_id AS ordinal_min_value_id, woc.max_value_id AS ordinal_max_value_id
                 FROM
                     past_woningbloks pw
                         JOIN diwi_testset.woningblok_maatwerk_ordinaal_changelog woc ON pw.id = woc.woningblok_id
                         AND woc.end_milestone_id = pw.end_milestone_id AND woc.change_end_date IS NULL
             )

         SELECT
             ap.id AS woningblokId,
             apb.value AS booleanValue,
             CAST (null AS FLOAT8) AS numericValue,
             CAST (null AS NUMRANGE) AS numericValueRange,
             CAST (null AS diwi_testset.value_type) AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apb.eigenschap_id AS customPropertyId,
             'BOOLEAN'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM active_woningbloks ap
                JOIN active_woningbloks_booleanCP apb ON ap.id = apb.id

         UNION

         SELECT
             ap.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             apn.value AS numericValue,
             apn.value_range AS numericValueRange,
             apn.value_type AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apn.eigenschap_id AS customPropertyId,
             'NUMERIC'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM active_woningbloks ap
                JOIN active_woningbloks_numericCP apn ON ap.id = apn.id

         UNION

         SELECT
             ap.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             apt.value AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apt.eigenschap_id AS customPropertyId,
             'TEXT'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM active_woningbloks ap
                JOIN active_woningbloks_textCP apt ON ap.id = apt.id

         UNION

         SELECT
             ap.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             apc.categories AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             apc.eigenschap_id AS customPropertyId,
             'CATEGORY'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM active_woningbloks ap
             JOIN active_woningbloks_categoriesCP apc ON ap.id = apc.id

         UNION

         SELECT
             ap.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[])  AS categories,
             apo.ordinal_value_id AS ordinalValueId,
             apo.ordinal_min_value_id AS ordinalMinValueId,
             apo.ordinal_max_value_id AS ordinalMaxValueId,
             apo.eigenschap_id AS customPropertyId,
             'ORDINAL'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM active_woningbloks ap
             JOIN active_woningbloks_ordinalCP apo ON ap.id = apo.id

         UNION

         SELECT
             fp.id AS woningblokId,
             fpb.value AS booleanValue,
             CAST (null AS FLOAT8) AS numericValue,
             CAST (null AS NUMRANGE) AS numericValueRange,
             CAST (null AS diwi_testset.value_type) AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpb.eigenschap_id AS customPropertyId,
             'BOOLEAN'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM future_woningbloks fp
                  JOIN future_woningbloks_booleanCP fpb ON fp.id = fpb.id

         UNION

         SELECT
             fp.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             fpn.value AS numericValue,
             fpn.value_range AS numericValueRange,
             fpn.value_type AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpn.eigenschap_id AS customPropertyId,
             'NUMERIC'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM future_woningbloks fp
                  JOIN future_woningbloks_numericCP fpn ON fp.id = fpn.id

         UNION

         SELECT
             fp.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             fpt.value AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpt.eigenschap_id AS customPropertyId,
             'TEXT'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM future_woningbloks fp
                  JOIN future_woningbloks_textCP fpt ON fp.id = fpt.id

         UNION

         SELECT
             fp.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             fpc.categories AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             fpc.eigenschap_id AS customPropertyId,
             'CATEGORY'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM future_woningbloks fp
             JOIN future_woningbloks_categoriesCP fpc ON fp.id = fpc.id

         UNION

         SELECT
             fp.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[])  AS categories,
             fpo.eigenschap_id AS customPropertyId,
             fpo.ordinal_value_id AS ordinalValueId,
             fpo.ordinal_min_value_id AS ordinalMinValueId,
             fpo.ordinal_max_value_id AS ordinalMaxValueId,
             'ORDINAL'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM future_woningbloks fp
             JOIN future_woningbloks_ordinalCP fpo ON fp.id = fpo.id

         UNION

         SELECT
             pw.id AS woningblokId,
             pwb.value AS booleanValue,
             CAST (null AS FLOAT8) AS numericValue,
             CAST (null AS NUMRANGE) AS numericValueRange,
             CAST (null AS diwi_testset.value_type) AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             pwb.eigenschap_id AS customPropertyId,
             'BOOLEAN'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM past_woningbloks pw
             JOIN past_woningbloks_booleanCP pwb ON pw.id = pwb.id

         UNION

         SELECT
             pw.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             pwn.value AS numericValue,
             pwn.value_range AS numericValueRange,
             pwn.value_type AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             pwn.eigenschap_id AS customPropertyId,
             'NUMERIC'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM past_woningbloks pw
             JOIN past_woningbloks_numericCP pwn ON pw.id = pwn.id

         UNION

         SELECT
             pw.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             pwt.value AS textValue,
             CAST (null AS UUID[]) AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             pwt.eigenschap_id AS customPropertyId,
             'TEXT'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM past_woningbloks pw
             JOIN past_woningbloks_textCP pwt ON pw.id = pwt.id

         UNION

         SELECT
             pw.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             pwc.categories AS categories,
             CAST (null AS UUID) AS ordinalValueId,
             CAST (null AS UUID) AS ordinalMinValueId,
             CAST (null AS UUID) AS ordinalMaxValueId,
             pwc.eigenschap_id AS customPropertyId,
             'CATEGORY'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM past_woningbloks pw
             JOIN past_woningbloks_categoriesCP pwc ON pw.id = pwc.id

         UNION

         SELECT
             pw.id AS woningblokId,
             CAST(null AS BOOL) AS booleanValue,
             null AS numericValue,
             null AS numericValueRange,
             null AS numericValueType,
             null AS textValue,
             CAST (null AS UUID[])  AS categories,
             pwo.eigenschap_id AS customPropertyId,
             pwo.ordinal_value_id AS ordinalValueId,
             pwo.ordinal_min_value_id AS ordinalMinValueId,
             pwo.ordinal_max_value_id AS ordinalMaxValueId,
             'ORDINAL'::"diwi_testset"."maatwerk_eigenschap_type" AS propertyType
         FROM past_woningbloks pw
             JOIN past_woningbloks_ordinalCP pwo ON pw.id = pwo.id

     ) AS q

        JOIN diwi_testset.maatwerk_eigenschap_state cps ON cps.eigenschap_id = q.customPropertyId AND cps.change_end_date IS NULL

WHERE q.woningblokId = _woningblok_uuid_;


END;$$
